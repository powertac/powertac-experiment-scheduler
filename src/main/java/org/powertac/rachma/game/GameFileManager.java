package org.powertac.rachma.game;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.file.FileRole;

import java.io.IOException;
import java.util.Map;

public interface GameFileManager {

    void removeExisting(Game game) throws IOException;
    void createGameDirectories(Game game) throws IOException;
    void createSimulationProperties(Game game) throws IOException;
    void createLogs(Game game) throws IOException;
    void createBrokerProperties(Game game, Broker broker) throws IOException;
    void createBootstrap(Game game) throws IOException;
    boolean bootstrapExists(Game game);
    boolean seedExists(Game game);
    Map<FileRole, String> getFiles(Game game);

}
