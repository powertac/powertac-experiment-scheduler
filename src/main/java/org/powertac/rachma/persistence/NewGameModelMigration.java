package org.powertac.rachma.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.broker.*;
import org.powertac.rachma.docker.DockerImageRepository;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileRepository;
import org.powertac.rachma.file.FileRole;
import org.powertac.rachma.paths.PathProvider;
import org.powertac.rachma.game.*;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobRepository;
import org.powertac.rachma.job.JobState;
import org.powertac.rachma.job.SimulationJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
@ConditionalOnProperty(value = "persistence.legacy.enable-mongo", havingValue = "true")
public class NewGameModelMigration implements Migration {

    @Value("${persistence.migration.new-model-migration.enabled}")
    private boolean enabled;

    @Value("${directory.local.jobs}")
    private String jobBaseDir;

    private final GameRepository gameRepository;
    private final JobRepository jobRepository;
    private final BrokerRepository brokerRepository;
    private final FileRepository fileRepository;
    private final DockerImageRepository imageRepository;
    private final PathProvider paths;
    private final Map<Game, SimulationJob> jobsByGame;
    private final Logger logger;

    public NewGameModelMigration(GameRepository gameRepository, JobRepository jobRepository, BrokerRepository brokerRepository, FileRepository fileRepository, DockerImageRepository imageRepository, PathProvider paths) {
        this.gameRepository = gameRepository;
        this.jobRepository = jobRepository;
        this.brokerRepository = brokerRepository;
        this.fileRepository = fileRepository;
        this.imageRepository = imageRepository;
        this.paths = paths;
        jobsByGame = new HashMap<>();
        logger = LogManager.getLogger(NewGameModelMigration.class);
    }

    @Override
    public String getName() {
        return "new-game-model";
    }

    @Override
    public void run() throws MigrationException {
        buildGames();
        persistChanges();
    }

    @Override
    public void rollback() {
        for (Map.Entry<Game, SimulationJob> entry : jobsByGame.entrySet()) {
            Game game = entry.getKey();
            SimulationJob job = entry.getValue();
            try {
                PathProvider.OrchestratorPaths.GamePaths gamePaths = paths.local().game(game);
                moveFileIfExists(gamePaths.bootstrap(), intermediateBootstrapPath(game, job.getId()));
                moveFileIfExists(gamePaths.properties(), intermediateSimulationPropertiesPath(game, job.getId()));
                moveFile(gamePaths.dir(), jobDirPath(job.getId()));
                gameRepository.delete(game);
            } catch (RuntimeException|IOException e) {
                logger.error(String.format("Could not roll back changes for job[%s]", job.getId()));
            }
        }
    }

    @Override
    public boolean shouldRun() {
        return enabled;
    }

    private void buildGames() throws MigrationException {
        for (Job job : jobRepository.findAll()) {
            Game game = createGameFromJob((SimulationJob) job);
            game.setRuns(createRuns(game, (SimulationJob) job));
            jobsByGame.put(game, (SimulationJob) job);
        }
        for (Map.Entry<Game, SimulationJob> entry : jobsByGame.entrySet()) {
            Game game = entry.getKey();
            SimulationJob job = entry.getValue();
            game.setBootstrap(resolveBootstrapFile(job));
            game.setSeed(resolveSeedFile(job));
        }
    }

    private Game createGameFromJob(SimulationJob job) throws MigrationException {
        return new Game(
            UUID.randomUUID().toString(),
            job.getName(),
            getBrokersForBrokerTypes(job.getSimulationTask().getBrokers()),
            job.getSimulationTask().getParameters(),
            job.getStatus().getStart(),
            getCancelledStatus(job));
    }

    private BrokerSet getBrokersForBrokerTypes(Collection<BrokerType> types) throws MigrationException {
        Set<Broker> brokers = new HashSet<>();
        for (BrokerType type : types) {
            try {
                Broker broker = brokerRepository.findByNameAndVersion(type.getName(), "latest");
                brokers.add(null == broker ? createBrokerFromType(type) : broker);
            } catch (BrokerConflictException e) {
                throw new MigrationException(String.format("failed to create new broker '%s' due to conflict with existing one", type.getName()), e);
            }
        }
        return new BrokerSet(
            UUID.randomUUID().toString(),
            brokers);
    }

    private Broker createBrokerFromType(BrokerType type) throws BrokerConflictException {
        Broker broker = new Broker(
            null,
            type.getName(),
            "latest",
            type.getImage(),
            imageRepository.exists(type.getImage()));
        brokerRepository.save(broker);
        return broker;
    }

    private List<GameRun> createRuns(Game game, SimulationJob job) {
        List<GameRun> runs = new ArrayList<>();
        JobState state = job.getStatus().getState();
        if (state.equals(JobState.COMPLETED)) {
            runs.add(createRun(game, job, false));
        } else if (state.equals(JobState.FAILED) || state.equals(JobState.RUNNING)) {
            runs.add(createRun(game, job, true));
        }
        return runs;
    }

    private GameRun createRun(Game game, SimulationJob job, boolean failed) {
        GameRun run = new GameRun(UUID.randomUUID().toString(), game);
        run.setStart(job.getStatus().getStart());
        run.setEnd(job.getStatus().getEnd());
        run.setPhase(GameRunPhase.DONE);
        run.setFailed(failed);
        return run;
    }

    private boolean getCancelledStatus(SimulationJob job) {
        return job.getStatus().getState().equals(JobState.RUNNING)
            || job.getStatus().getState().equals(JobState.FAILED);
    }

    private File resolveBootstrapFile(SimulationJob job) throws MigrationException {
        String bootstrapFilePath = job.getSimulationTask().getBootstrapFilePath();
        return null != bootstrapFilePath ? getOrCreateFile(bootstrapFilePath, FileRole.BOOTSTRAP) : null;
    }

    private File resolveSeedFile(SimulationJob job) throws MigrationException {
        String seedFilePath = job.getSimulationTask().getSeedFilePath();
        return null != seedFilePath ? getOrCreateFile(seedFilePath, FileRole.SEED) : null;
    }

    private File getOrCreateFile(String filePath, FileRole role) throws MigrationException {
        Game referencedGame = findReferencedGame(filePath, role);
        File file = fileRepository.findByRoleAndGame(role, referencedGame);
        if (null == file) {
            file = new File(UUID.randomUUID().toString(), role, referencedGame);
        }
        return file;
    }

    private Game findReferencedGame(String path, FileRole role) throws MigrationException {
        String[] pathParts = path.split(java.io.File.separator);
        String jobId = role.equals(FileRole.BOOTSTRAP) ? pathParts[pathParts.length - 2] : pathParts[pathParts.length - 3];
        for (Map.Entry<Game, SimulationJob> entry : jobsByGame.entrySet()) {
            if (entry.getValue().getId().equals(jobId)) {
                return entry.getKey();
            }
        }
        throw new MigrationException(String.format("could not resolve game for job[%s]", jobId));
    }

    private void persistChanges() {
        for (Game game : jobsByGame.keySet()) {
            SimulationJob job = jobsByGame.get(game);
            try {
                saveGame(game);
                moveFiles(game, job);
            } catch (IOException|RuntimeException e) {
                logger.error(String.format("failed to save game[%s] for job[%s]", game.getId(), job.getId()), e);
            }
        }
    }

    private void moveFiles(Game game, SimulationJob job) throws IOException {
        PathProvider.OrchestratorPaths.GamePaths gamePaths = paths.local().game(game);
        moveFile(jobDirPath(job.getId()), gamePaths.dir());
        moveFileIfExists(intermediateBootstrapPath(game, job.getId()), gamePaths.bootstrap());
        moveFileIfExists(intermediateSimulationPropertiesPath(game, job.getId()), gamePaths.properties());
    }

    private void saveGame(Game game) {
        if (null != game.getBootstrap()) {
            saveGame(game.getBootstrap().getGame());
        }
        if (null != game.getSeed()) {
            saveGame(game.getSeed().getGame());
        }
        gameRepository.save(game);
        logger.info(String.format("saved game[%s]", game.getId()));
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
        } else {
            Files.move(source, target);
        }
    }

    private Path jobDirPath(String jobId) {
        return Paths.get(String.format("%s/%s", jobBaseDir, jobId));
    }

    private Path intermediateBootstrapPath(Game game, String jobId) {
        return Paths.get(
            paths.local().game(game).dir().toString(),
            String.format("%s.bootstrap.xml", jobId));
    }

    private Path intermediateSimulationPropertiesPath(Game game, String jobId) {
        return Paths.get(
            paths.local().game(game).dir().toString(),
            String.format("%s.simulation.properties", jobId));
    }

}
