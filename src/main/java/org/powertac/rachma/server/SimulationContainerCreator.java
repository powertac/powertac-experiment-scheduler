package org.powertac.rachma.server;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.DockerContainer;
import org.powertac.rachma.docker.DockerNetwork;
import org.powertac.rachma.game.Game;

public interface SimulationContainerCreator {

    DockerContainer create(Game game, DockerNetwork network) throws DockerException;

}
