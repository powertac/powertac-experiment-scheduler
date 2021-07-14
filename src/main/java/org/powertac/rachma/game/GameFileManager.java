package org.powertac.rachma.game;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.game.Game;

import java.io.IOException;

public interface GameFileManager {

    void createGameDirectories(Game game) throws IOException;
    void createSimulationProperties(Game game) throws IOException;
    void createBrokerProperties(Game game, Broker broker) throws IOException;
    void createBootstrap(Game game) throws IOException;
    boolean bootstrapExists(Game game);
    boolean seedExists(Game game);
}
