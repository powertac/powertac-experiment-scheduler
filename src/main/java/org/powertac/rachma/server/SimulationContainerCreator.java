package org.powertac.rachma.server;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.DockerContainer;
import org.powertac.rachma.docker.DockerNetwork;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRun;

public interface SimulationContainerCreator {

    DockerContainer create(GameRun run, DockerNetwork network) throws DockerException;
    String getSimulationContainerName(Game game);

}
