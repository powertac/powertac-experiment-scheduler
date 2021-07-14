package org.powertac.rachma.server;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.container.DockerContainer;
import org.powertac.rachma.docker.network.DockerNetwork;
import org.powertac.rachma.game.Game;

public interface SimulationContainerCreator {

    DockerContainer create(Game game, DockerNetwork network) throws DockerException;

}
