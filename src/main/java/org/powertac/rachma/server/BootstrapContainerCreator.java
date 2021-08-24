package org.powertac.rachma.server;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.container.DockerContainer;
import org.powertac.rachma.game.Game;

public interface BootstrapContainerCreator {

    DockerContainer create(Game game) throws DockerException;
    String getBootstrapContainerName(Game game);

}
