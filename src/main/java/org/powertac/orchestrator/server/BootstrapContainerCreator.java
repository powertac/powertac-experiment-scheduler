package org.powertac.orchestrator.server;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.orchestrator.docker.DockerContainer;
import org.powertac.orchestrator.game.Game;

public interface BootstrapContainerCreator {

    DockerContainer create(Game game, String networkId) throws DockerException;
    String getBootstrapContainerName(Game game);

}
