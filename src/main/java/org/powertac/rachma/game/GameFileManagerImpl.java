package org.powertac.rachma.game;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.file.FileRole;
import org.powertac.rachma.file.PathProvider;
import org.powertac.rachma.util.BrokerCompatiblePropertiesWriter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GameFileManagerImpl implements GameFileManager {

    private final PathProvider paths;
    private final GamePropertiesProvider properties;

    public GameFileManagerImpl(PathProvider paths, GamePropertiesProvider properties) {
        this.paths = paths;
        this.properties = properties;
    }

    @Override
    public void removeExisting(Game game) throws IOException {
        Path gameDirectory = paths.local().game(game).dir();
        if (!Files.exists(gameDirectory)) {
            return;
        }
        List<Path> files = Files.walk(gameDirectory)
            .sorted(Comparator.reverseOrder()) // the order is important here; files must be removed before removing their parent directory
            .collect(Collectors.toList());
        for (Path file : files) {
            Files.deleteIfExists(file);
        }
    }

    @Override
    public void createGameDirectories(Game game) throws IOException {
        GamePathProvider gamePaths = paths.local().game(game);
        Files.createDirectories(gamePaths.dir());
        Files.createDirectories(gamePaths.brokers());
        Files.createDirectories(gamePaths.logs());
        for (Broker broker : game.getBrokers()) {
            Files.createDirectories(gamePaths.broker(broker).dir());
        }
    }

    @Override
    public void createSimulationProperties(Game game) throws IOException {
        Path propertiesPath = paths.local().game(game).properties();
        BrokerCompatiblePropertiesWriter.write(
            propertiesPath.toString(),
            properties.getServerProperties(game));
    }

    @Override
    public void createBrokerProperties(Game game, Broker broker) throws IOException {
        Path propertiesPath = paths.local().game(game).broker(broker).properties();
        BrokerCompatiblePropertiesWriter.write(
            propertiesPath.toString(),
            properties.getBrokerProperties(game, broker));
    }

    @Override
    public void createBootstrap(Game game) throws IOException {
        Path bootstrapPath = paths.local().game(game).bootstrap();
        Files.createFile(bootstrapPath);
    }

    @Override
    public boolean bootstrapExists(Game game) {
        Path bootstrapPath = paths.host().game(game).bootstrap();
        return Files.exists(bootstrapPath);
    }

    @Override
    public boolean seedExists(Game game) {
        Path seedPath = paths.host().game(game).seed();
        if (null == seedPath) {
            return false;
        }
        return Files.exists(seedPath);
    }

    @Override
    public Map<FileRole, String> getFiles(Game game) {
        Map<FileRole, String> files = new HashMap<>();
        if (Files.exists(paths.local().game(game).properties())) {
            files.put(FileRole.PROPERTIES, paths.host().game(game).properties().toString());
        }
        if (Files.exists(paths.local().game(game).bootstrap())) {
            files.put(FileRole.BOOTSTRAP, paths.host().game(game).bootstrap().toString());
        }
        if (null != game.getSeed() && Files.exists(paths.local().game(game).seed())) {
            files.put(FileRole.SEED, paths.host().game(game).seed().toString());
        }
        if (Files.exists(paths.local().game(game).stateLog())) {
            files.put(FileRole.STATE_LOG, paths.host().game(game).stateLog().toString());
        }
        if (Files.exists(paths.local().game(game).traceLog())) {
            files.put(FileRole.TRACE_LOG, paths.host().game(game).traceLog().toString());
        }
        return files;
    }

}
