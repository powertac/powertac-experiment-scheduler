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
import org.powertac.rachma.util.ID;
import org.powertac.rachma.validation.exception.ValidationException;
import org.powertac.rachma.weather.WeatherConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    private final static String baselineName = "IS3 broker baseline";
    private final static DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault());
    private final static Set<BiPredicate<Broker, BrokerType>> brokerMatchers = Stream.<BiPredicate<Broker, BrokerType>>of(
        (b, t) -> b.getName().equals("IS3") // EWIIS3
            && b.getVersion().equals("tmt-finals_2020_11")
            && t.getName().equals("EWIIS3")
            && t.getImage().equals("ewiis3-fat:storage-tariff"),
        (b, t) -> b.getName().equals(t.getName()) // TUC_TAC_2020
            && b.getVersion().equals("2020")
            && t.getImage().equals("tuc-tac-2020:latest"),
        (b, t) -> b.getName().equals(t.getName()) // SPOT19
            && b.getVersion().equals("latest")
            && t.getImage().equals("spot19:latest"),
        (b, t) -> b.getName().equals(t.getName()) // AgentUDE17
            && b.getVersion().equals("latest")
            && t.getImage().equals("agentude17:latest")
    ).collect(Collectors.toSet());

    private final MongoJobRepository jobRepository;
    private final BrokerRepository brokerRepository;
    private final BaselineFactory baselineFactory;
    private final BaselineGameFactory gameFactory;
    private final BaselineRepository baselineRepository;
    private final DockerImageRepository imageRepository;
    private final Logger logger;

    @Autowired
    public BaselineMigration(MongoJobRepository jobRepository, BrokerRepository brokerRepository,
                             BaselineFactory baselineFactory, BaselineGameFactory gameFactory,
                             BaselineRepository baselineRepository, DockerImageRepository imageRepository) {
        this.jobRepository = jobRepository;
        this.brokerRepository = brokerRepository;
        this.baselineFactory = baselineFactory;
        this.gameFactory = gameFactory;
        this.baselineRepository = baselineRepository;
        this.imageRepository = imageRepository;
        this.logger = LogManager.getLogger(BaselineMigration.class);
    }

    @Override
    public String getName() {
        return "baseline";
    }

    @Override
    public void run() throws MigrationException {
        try {
            // create brokers in case they don't exist
            seedBrokers();
            // create baseline and merged games
            Baseline baseline = baselineFactory.createFromSpec(getSpec());
            baselineRepository.save(baseline);
            List<Game> games = new ArrayList<>();
            for (Game game : gameFactory.createGames(baseline)) {
                game.getRuns().add(getRun(game));
                games.add(game);
                // FIXME : set created at
            }
            baseline.setGames(games);
            Optional<Instant> createdAt = games.stream()
                .map(Game::getCreatedAt)
                .min(Instant::compareTo);
            if (createdAt.isEmpty()) {
                throw new MigrationException("could not determine baseline creation time");
            }
            baseline.setCreatedAt(createdAt.get());
            // copy files to new locations
            FileActions fileActions = new FileActions();
            games.stream()
                .map(this::getFileActions)
                .forEach(fileActions::append);
            fileActions.commit();
            // persist games in database
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
        // TODO : remove file changes; use baseline games for that...
    }

    private void seedBrokers() throws MigrationException {
        // FIXME : how to deal with this during rollback
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
            } catch (BrokerConflictException e) {
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
        Broker ewiis3 = brokerRepository.findByNameAndVersion("IS3", "tmt-finals_2020_11");
        Broker tuctac2020 = brokerRepository.findByNameAndVersion("TUC_TAC", "2020");
        Broker agentude17 = brokerRepository.findByNameAndVersion("AgentUDE17", "latest");
        Broker spot19 = brokerRepository.findByNameAndVersion("SPOT19", "latest");
        if (null == ewiis3 || null == tuctac2020 || null == agentude17 || null == spot19) {
            throw new MigrationException("one of the brokers does not exist");
        }
        BrokerSet set1 = new BrokerSet(
            UUID.randomUUID().toString(),
            Stream.of(ewiis3).collect(Collectors.toSet()));
        BrokerSet set2 = new BrokerSet(
            UUID.randomUUID().toString(),
            Stream.of(ewiis3, tuctac2020).collect(Collectors.toSet()));
        BrokerSet set3 = new BrokerSet(
            UUID.randomUUID().toString(),
            Stream.of(ewiis3, tuctac2020, agentude17, spot19).collect(Collectors.toSet()));
        return Stream.of(set1, set2, set3).collect(Collectors.toList());
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

    private GameRun getRun(Game game) throws MigrationException {
        Optional<SimulationJob> job = findMatch(game);
        if (job.isPresent()) {
            GameRun run = new GameRun(ID.gen(), game);
            run.setStart(job.get().getStatus().getStart());
            run.setEnd(job.get().getStatus().getEnd());
            run.setPhase(GameRunPhase.DONE);
            run.setFailed(false);
            return run;
        } else {
            throw new MigrationException(String.format("could not find match for game '%s'", game.getName()));
        }
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
        return jobRepository.findAll().stream()
            .filter(job -> job.getName().startsWith("Baseline"))
            .filter(job -> job.getStatus().getState().equals(JobState.COMPLETED))
            .filter(job -> job.getStatus().getDurationMillis() > (110 * 60 * 1000))
            .sorted((a,b) -> a.getName().compareToIgnoreCase(b.getName()))
            .map(job -> (SimulationJob) job)
            .collect(Collectors.toList());
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
        return t -> brokerMatchers.stream().anyMatch(matcher -> matcher.test(broker, t));
    }

    private FileActions getFileActions(Game game) {
        return new FileActions();
    }

}
