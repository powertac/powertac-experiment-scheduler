package org.powertac.rachma.game;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.file.PathProvider;
import org.powertac.rachma.util.BrokerCompatiblePropertiesWriter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class GameFileManagerImpl implements GameFileManager {

    private final PathProvider paths;
    private final GamePropertiesProvider properties;

    public GameFileManagerImpl(PathProvider paths, GamePropertiesProvider properties) {
        this.paths = paths;
        this.properties = properties;
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

}
