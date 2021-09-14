package org.powertac.rachma.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerRepository;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileRole;
import org.powertac.rachma.file.PathProvider;
import org.powertac.rachma.game.*;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobRepository;
import org.powertac.rachma.job.JobState;
import org.powertac.rachma.job.SimulationJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class NewGameModelMigration implements Migration {

    @Value("${directory.local.base}")
    private String baseDir;

    @Value("${directory.local.jobs}")
    private String jobBaseDir;

    private final GameRepository gameRepository;
    private final JobRepository jobRepository;
    private final BrokerRepository brokerRepository;
    private final PathProvider paths;
    private final Map<Game, SimulationJob> jobsByGame;
    private final Logger logger;
    private final boolean dryRun;


    public NewGameModelMigration(GameRepository gameRepository, JobRepository jobRepository, BrokerRepository brokerRepository, PathProvider paths) {
        this.gameRepository = gameRepository;
        this.jobRepository = jobRepository;
        this.brokerRepository = brokerRepository;
        this.paths = paths;
        this.jobsByGame = new HashMap<>();
        this.logger = LogManager.getLogger(NewGameModelMigration.class);
        this.dryRun = false;
    }

    @Override
    public String getName() {
        return "new-game-model";
    }

    @Override
    public void run() throws MigrationException {
        try {
            buildGames();
            saveIdMap();
            resolveFileRelations();
            saveGames();
            moveFiles();
        } catch (MigrationException e) {
            rollback();
            throw e;
        }
    }

    @Override
    public void rollback() throws MigrationException {
        try {
            for (Job job : jobRepository.findAll()) {
                Game game = gameRepository.findOneByName(job.getName());
                if (null == game) {
                    logger.warn(String.format("cannot find game for job with name '%s'", job.getName()));
                    continue;
                }
                GamePathProvider gamePaths = paths.local().game(game);
                moveFileIfExists(gamePaths.bootstrap(), jobBootstrapPath(job.getId()));
                moveFileIfExists(gamePaths.properties(), jobSimulationPropertiesPath(job.getId()));
                moveFile(gamePaths.dir(), jobPath(job.getId()));
                if (!dryRun) {
                    gameRepository.delete(game);
                }
            }
        } catch (IOException e) {
            throw new MigrationException("file operation error during rollback", e);
        }
    }

    private void buildGames() throws MigrationException {
        for (Job job : jobRepository.findAll()) {
            jobsByGame.put(jobToGame((SimulationJob) job), (SimulationJob) job);
        }
    }

    private Game jobToGame(SimulationJob job) throws MigrationException {
        Game game = new Game(
            UUID.randomUUID().toString(),
            job.getName(),
            brokerTypesToBrokers(job.getSimulationTask().getBrokers()),
            job.getSimulationTask().getParameters(),
            null,
            null,
            job.getStatus().getStart(),
            null,
            getCancelled(job));
        game.setRuns(createRuns(job, game));
        return game;
    }

    private Set<Broker> brokerTypesToBrokers(Collection<BrokerType> types) throws MigrationException {
        Set<Broker> brokers = new HashSet<>();
        for (BrokerType type : types) {
            Broker broker = brokerRepository.findByName(type.getName());
            if (null == broker) {
                throw new MigrationException(String.format("could not find broker '%s'", type.getName()));
            }
            brokers.add(broker);
        }
        return brokers;
    }

    private List<GameRun> createRuns(SimulationJob job, Game game) {
        GameRun run = new GameRun(UUID.randomUUID().toString(), game);
        run.setStart(job.getStatus().getStart());
        run.setEnd(job.getStatus().getEnd());
        switch (job.getStatus().getState()) {
            case COMPLETED:
                run.setPhase(GameRunPhase.DONE);
                run.setFailed(false);
                break;
            case FAILED:
            case RUNNING: // set running games to be failed as well since they should not be running at this point anyway
                run.setPhase(GameRunPhase.DONE);
                run.setFailed(true);
                break;
            case CREATED:
            case QUEUED:
            default:
                return new ArrayList<>();
        }
        return Stream.of(run).collect(Collectors.toList());
    }

    private boolean getCancelled(SimulationJob job) {
        return job.getStatus().getState().equals(JobState.RUNNING)
            || job.getStatus().getState().equals(JobState.FAILED);
    }

    private void resolveFileRelations() throws MigrationException {
        for (Game game : jobsByGame.keySet()) {
            SimulationJob job = jobsByGame.get(game);
            String bootstrapFilePath = job.getSimulationTask().getBootstrapFilePath();
            if (null != bootstrapFilePath) {
                game.setBootstrap(getFile(bootstrapFilePath, FileRole.BOOTSTRAP));
            }
            String seedFilePath = job.getSimulationTask().getSeedFilePath();
            if (null != seedFilePath) {
                game.setSeed(getFile(seedFilePath, FileRole.SEED));
            }
        }
    }

    private File getFile(String filePath, FileRole role) throws MigrationException {
        for (Game game : jobsByGame.keySet()) {
            SimulationJob job = jobsByGame.get(game);
            if (role.equals(FileRole.BOOTSTRAP) && jobBootstrapPath(job.getId()).toString().equals(filePath)) {
                return new File(UUID.randomUUID().toString(), FileRole.BOOTSTRAP, game);
            } else if (role.equals(FileRole.SEED) && jobStateLogPath(job.getId()).toString().equals(filePath)) {
                return new File(UUID.randomUUID().toString(), FileRole.SEED, game);
            }
        }
        throw new MigrationException(String.format("cannot resolve file '%s' for path %s", role, filePath));
    }

    private void moveFiles() throws MigrationException {
        try {
            for (Game game : jobsByGame.keySet()) {
                SimulationJob job = jobsByGame.get(game);
                GamePathProvider gamePaths = paths.local().game(game);
                moveFile(jobPath(job.getId()), gamePaths.dir());
                moveFileIfExists(jobBootstrapPath(job.getId()), gamePaths.bootstrap());
                moveFileIfExists(jobSimulationPropertiesPath(job.getId()), gamePaths.properties());
            }
        } catch (IOException e) {
            throw new MigrationException("error while moving files", e);
        }
    }

    private void moveFile(Path source, Path target) throws IOException {
        moveFile(source, target, true);
    }

    private void moveFileIfExists(Path source, Path target) throws IOException {
        moveFile(source, target, false);
    }

    private void moveFile(Path source, Path target, boolean failOnMissingSource) throws IOException {
        if (!Files.exists(source)) {
            if (failOnMissingSource) {
                throw new IOException(String.format("source %s does not exist", source));
            } else {
                logger.info(String.format("skipping moving non-existent file %s", source));
            }
        } else if (dryRun) {
            logger.info(String.format("moving %s to %s", source, target));
        } else {
            Files.move(source, target);
        }
    }

    private Path jobPath(String jobId) {
        return Paths.get(String.format("%s/%s", jobBaseDir, jobId));
    }

    private Path jobBootstrapPath(String jobId) {
        return Paths.get(jobPath(jobId).toString(), String.format("%s.bootstrap.xml", jobId));
    }

    private Path jobStateLogPath(String jobId) {
        return Paths.get(jobPath(jobId).toString(), "log", "powertac-sim-0.state");
    }

    private Path jobSimulationPropertiesPath(String jobId) {
        return Paths.get(jobPath(jobId).toString(), String.format("%s.simulation.properties", jobId));
    }

    private void saveGames() {
        for (Game game : jobsByGame.keySet()) {
            if (!dryRun) {
                gameRepository.save(game);
            }
        }
    }

    private void saveIdMap() throws MigrationException {
        try {
            Map<String, String> idMap = new HashMap<>();
            for (Map.Entry<Game, SimulationJob> entry : jobsByGame.entrySet()) {
                idMap.put(entry.getKey().getId(), entry.getValue().getId());
            }
            Files.writeString(idMapPath(), new ObjectMapper().writeValueAsString(idMap));
        } catch (IOException e) {
            throw new MigrationException("could not write id map", e);
        }
    }

    private Path idMapPath() {
        return Paths.get(baseDir, "new-model-migration.idmap.json");
    }

}
