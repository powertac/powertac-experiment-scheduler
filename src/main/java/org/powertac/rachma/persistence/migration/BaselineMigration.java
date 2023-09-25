package org.powertac.rachma.persistence.migration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.baseline.*;
import org.powertac.rachma.broker.*;
import org.powertac.rachma.docker.DockerImageRepository;
import org.powertac.rachma.file.action.FileActions;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.game.GameRunPhase;
import org.powertac.rachma.job.JobState;
import org.powertac.rachma.job.MongoJobRepository;
import org.powertac.rachma.job.SimulationJob;
import org.powertac.rachma.paths.PathProvider;
import org.powertac.rachma.util.ID;
import org.powertac.rachma.validation.exception.ValidationException;
import org.powertac.rachma.weather.WeatherConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@ConditionalOnProperty(value = "persistence.legacy.enable-mongo", havingValue = "true")
public class BaselineMigration implements Migration {

    enum BaselineBroker {
        IS3,
        TUC_TAC,
        SPOT19,
        AgentUDE17
    }

    @Value("${persistence.migration.baseline.directory.jobs}")
    private String jobSourceDir;

    private final static String baselineName = "IS3 broker baseline";
    private final static DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault());
    private final static Map<BaselineBroker, BiPredicate<Broker, BrokerType>> brokerMatchers =
        Stream.<Pair<BaselineBroker, BiPredicate<Broker, BrokerType>>>of(
            Pair.of(BaselineBroker.IS3, (b, t) -> b.getName().equals("IS3") // EWIIS3
                && b.getVersion().equals("tmt-finals_2020_11")
                && t.getName().equals("EWIIS3")
                && t.getImage().equals("ewiis3-fat:storage-tariff")),
            Pair.of(BaselineBroker.TUC_TAC, (b, t) -> b.getName().equals(t.getName()) // TUC_TAC_2020
                && b.getVersion().equals("2020")
                && t.getImage().equals("tuc-tac-2020:latest")),
            Pair.of(BaselineBroker.SPOT19, (b, t) -> b.getName().equals(t.getName()) // SPOT19
                && b.getVersion().equals("latest")
                && t.getImage().equals("spot19:latest")),
            Pair.of(BaselineBroker.AgentUDE17, (b, t) -> b.getName().equals(t.getName()) // AgentUDE17
                && b.getVersion().equals("latest")
                && t.getImage().equals("agentude17:latest"))
    ).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

    private final MongoJobRepository jobRepository;
    private final BrokerRepository brokerRepository;
    private final BaselineFactory baselineFactory;
    private final BaselineGameFactory gameFactory;
    private final BaselineRepository baselineRepository;
    private final DockerImageRepository imageRepository;
    private final PathProvider paths;
    private final Logger logger;

    private List<SimulationJob> cachedJobs;
    private Map<BaselineBroker, Broker> baselineBrokers;

    @Autowired
    public BaselineMigration(MongoJobRepository jobRepository, BrokerRepository brokerRepository,
                             BaselineFactory baselineFactory, BaselineGameFactory gameFactory,
                             BaselineRepository baselineRepository, DockerImageRepository imageRepository, PathProvider paths) {
        this.jobRepository = jobRepository;
        this.brokerRepository = brokerRepository;
        this.baselineFactory = baselineFactory;
        this.gameFactory = gameFactory;
        this.baselineRepository = baselineRepository;
        this.imageRepository = imageRepository;
        this.paths = paths;
        this.logger = LogManager.getLogger(BaselineMigration.class);
    }

    @Override
    public String getName() {
        return "baseline";
    }

    @Override
    public void run() throws MigrationException {
        try {
            Baseline baseline = baselineFactory.createFromSpec(getSpec());
            baselineRepository.save(baseline);
            baseline.setGames(createGames(baseline));
            baseline.setCreatedAt(getCreatedAt(baseline));
            FileActions actions = prepareFileActions(baseline);
            actions.commit(); // copy files to new locations
            baselineRepository.save(baseline);
        } catch (ValidationException e) {
            throw new MigrationException("validation failed", e);
        } catch (IOException e) {
            throw new MigrationException("file operation failed", e);
        }
    }

    @Override
    public void rollback() throws MigrationException {
        Optional<Baseline> baseline = baselineRepository.findByName(baselineName);
        baseline.ifPresent(baselineRepository::delete);
    }

    private void seedBrokers() throws MigrationException {
        // FIXME : how to deal with this during rollback?
        createBrokerIfNotExists("IS3", "tmt-finals_2020_11", "ewiis3-fat:storage-tariff");
        createBrokerIfNotExists("TUC_TAC", "2020", "tuc_tac:2020");
        createBrokerIfNotExists("AgentUDE17", "latest", "agentude17:new");
        createBrokerIfNotExists("SPOT19", "latest", "spot19:latest");
    }

    private void createBrokerIfNotExists(String name, String version, String imageTag) throws MigrationException {
        if (null == brokerRepository.findByNameAndVersion(name, version)) {
            try {
                brokerRepository.save(new Broker(
                    ID.gen(),
                    name,
                    version,
                    imageTag,
                    imageRepository.exists(imageTag)
                ));
            } catch (BrokerConflictException|BrokerValidationException e) {
                throw new MigrationException(String.format(
                    "could not create new broker '%s' due to conflict with existing one", name), e);
            }
        }
    }

    private BaselineSpec getSpec() throws MigrationException {
        return new BaselineSpec(
            baselineName,
            new HashMap<>(),
            getBrokerSets(),
            getWeatherConfigs());
    }

    private List<BrokerSet> getBrokerSets() throws MigrationException {
        BrokerSet set1 = new BrokerSet(
            UUID.randomUUID().toString(),
            Stream.of(getBaselineBroker(BaselineBroker.IS3)).collect(Collectors.toSet()));
        BrokerSet set2 = new BrokerSet(
            UUID.randomUUID().toString(),
            Stream.of(
                    getBaselineBroker(BaselineBroker.IS3),
                    getBaselineBroker(BaselineBroker.TUC_TAC))
                .collect(Collectors.toSet()));
        BrokerSet set3 = new BrokerSet(
            UUID.randomUUID().toString(),
            Stream.of(
                    getBaselineBroker(BaselineBroker.IS3),
                    getBaselineBroker(BaselineBroker.TUC_TAC),
                    getBaselineBroker(BaselineBroker.SPOT19),
                    getBaselineBroker(BaselineBroker.AgentUDE17))
                .collect(Collectors.toSet()));
        return Stream.of(set1, set2, set3).collect(Collectors.toList());
    }

    private Broker getBaselineBroker(BaselineBroker broker) throws MigrationException {
        return getBaselineBrokers().get(broker);
    }

    private Map<BaselineBroker, Broker> getBaselineBrokers() throws MigrationException {
        if (null == baselineBrokers) {
            seedBrokers();
            baselineBrokers = new HashMap<>();
            baselineBrokers.put(BaselineBroker.IS3, brokerRepository.findByNameAndVersion("IS3", "tmt-finals_2020_11"));
            baselineBrokers.put(BaselineBroker.TUC_TAC, brokerRepository.findByNameAndVersion("TUC_TAC", "2020"));
            baselineBrokers.put(BaselineBroker.SPOT19, brokerRepository.findByNameAndVersion("SPOT19", "latest"));
            baselineBrokers.put(BaselineBroker.AgentUDE17, brokerRepository.findByNameAndVersion("AgentUDE17", "latest"));
        }
        return baselineBrokers;
    }

    private List<WeatherConfiguration> getWeatherConfigs() {
        List<WeatherConfiguration> configs = new ArrayList<>();
        configs.add(new WeatherConfiguration("rotterdam", Instant.parse("2010-04-01T12:00:00Z")));
        configs.add(new WeatherConfiguration("rotterdam", Instant.parse("2010-07-01T12:00:00Z")));
        configs.add(new WeatherConfiguration("rotterdam", Instant.parse("2010-10-01T12:00:00Z")));
        configs.add(new WeatherConfiguration("rotterdam", Instant.parse("2011-01-01T12:00:00Z")));
        configs.add(new WeatherConfiguration("cheyenne", Instant.parse("2014-04-01T12:00:00Z")));
        configs.add(new WeatherConfiguration("cheyenne", Instant.parse("2014-07-01T12:00:00Z")));
        configs.add(new WeatherConfiguration("cheyenne", Instant.parse("2014-10-01T12:00:00Z")));
        configs.add(new WeatherConfiguration("cheyenne", Instant.parse("2015-01-01T12:00:00Z")));
        return configs;
    }

    private Optional<SimulationJob> findMatch(Game game) {
        for (SimulationJob job : getBaselineJobs()) {
            String location = job.getSimulationTask().getParameters().get("server.weatherService.weatherLocation");
            if (!location.equals(game.getWeatherConfiguration().getLocation()))
                continue;
            String jobWeatherDate = job.getSimulationTask().getParameters().get("common.competition.simulationBaseTime");
            String gameWeatherDate = formatter.format(game.getWeatherConfiguration().getStartTime());
            if (!gameWeatherDate.equals(jobWeatherDate))
                continue;
            if (!brokerSetsMatch(game.getBrokers(), job.getSimulationTask().getBrokers()))
                continue;
            return Optional.of(job);
        }
        return Optional.empty();
    }

    private List<SimulationJob> getBaselineJobs() {
        if (null == cachedJobs) {
            cachedJobs = jobRepository.findAll().stream()
                .filter(job -> job.getName().startsWith("Baseline"))
                .filter(job -> job.getStatus().getState().equals(JobState.COMPLETED))
                .filter(job -> job.getStatus().getDurationMillis() > (110 * 60 * 1000))
                .sorted((a,b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(job -> (SimulationJob) job)
                .collect(Collectors.toList());
        }
        return cachedJobs;
    }

    private boolean brokerSetsMatch(Set<Broker> gameBrokers, Set<BrokerType> jobBrokers) {
        Set<Predicate<BrokerType>> brokerMatchers = gameBrokers.stream()
            .map(this::brokerMatcher)
            .collect(Collectors.toSet());
        return jobBrokers.stream()
            .allMatch(t -> brokerMatchers.stream()
                .anyMatch(m -> m.test(t)));
    }

    private Predicate<BrokerType> brokerMatcher(Broker broker) {
        return t -> brokerMatchers.values().stream()
            .anyMatch(matcher -> matcher.test(broker, t));
    }

    private List<Game> createGames(Baseline baseline) throws MigrationException {
        List<Game> games = new ArrayList<>();
        for (Game game : gameFactory.createGames(baseline)) {
            Optional<SimulationJob> job = findMatch(game);
            if (job.isPresent()) {
                GameRun run = createRun(job.get());
                run.setGame(game);
                game.getRuns().add(run);
                game.setCreatedAt(run.getStart());
                games.add(game);
            } else {
                throw new MigrationException(String.format("could not find match for game '%s'", game.getName()));
            }
        }
        return games;
    }

    private GameRun createRun(SimulationJob job) {
        return GameRun.builder()
            .id(ID.gen())
            .start(job.getStatus().getStart())
            .end(job.getStatus().getEnd())
            .phase(GameRunPhase.DONE)
            .failed(false)
            .build();
    }

    private Instant getCreatedAt(Baseline baseline) throws MigrationException {
        Optional<Instant> createdAt = baseline.getGames().stream()
            .map(Game::getCreatedAt)
            .min(Instant::compareTo);
        if (createdAt.isEmpty()) {
            throw new MigrationException("could not determine baseline creation time");
        }
        return createdAt.get();
    }

    private FileActions prepareFileActions(Baseline baseline) throws MigrationException {
        FileActions actions = new FileActions();
        for (Game game : baseline.getGames()) {
            Optional<SimulationJob> job = findMatch(game);
            if (job.isPresent()) {
                actions.append(getFileActions(game, job.get()));
            } else {
                throw new MigrationException(String.format("could not find match for game with id='%s'", game.getId()));
            }
        }
        return actions;
    }

    private FileActions getFileActions(Game game, SimulationJob job) throws MigrationException {
        String jobDir = Paths.get(jobSourceDir, job.getId()).toString();
        // game scope
        FileActions actions = FileActions.create()
            .mkdir(paths.local().game(game).dir()) // game dir
            .copy(Paths.get(jobDir, String.format("%s.bootstrap.xml", job.getId())), // bootstrap
                paths.local().game(game).bootstrap())
            .copy(Paths.get(jobDir, String.format("%s.simulation.properties", job.getId())), // server props
                paths.local().game(game).properties());
        for (BrokerType type : job.getSimulationTask().getBrokers()) { // broker props
            Broker broker = findBroker(type);
            actions.copy(Paths.get(jobDir, String.format("broker.%s.properties", type.getName())),
                paths.local().game(game).broker(broker).properties());
        }
        // run scope
        GameRun run = game.getLatestSuccessfulRun();
        actions.mkdir(paths.local().run(run).dir()) // run dir
            .mkdir(paths.local().run(run).serverLogs()) // server log dir
            .copy(Paths.get(jobDir, "log/powertac-sim-0.state"), paths.local().run(run).state()) // state log
            .copy(Paths.get(jobDir, "log/powertac-sim-0.trace"), paths.local().run(run).trace()); // trace log
        return actions;
    }

    private Broker findBroker(BrokerType type) throws MigrationException {
        for (Map.Entry<BaselineBroker, Broker> entry : getBaselineBrokers().entrySet()) {
            if (brokerMatchers.get(entry.getKey()).test(entry.getValue(), type)) {
                return entry.getValue();
            }
        }
        throw new MigrationException(String.format("no broker matches type '%s'", type.getName()));
    }

}
