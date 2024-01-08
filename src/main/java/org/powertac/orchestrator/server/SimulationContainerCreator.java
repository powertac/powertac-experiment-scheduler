package org.powertac.orchestrator.server;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.orchestrator.docker.DockerContainer;
import org.powertac.orchestrator.docker.DockerNetwork;
import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.game.GameRun;

public interface SimulationContainerCreator {

    DockerContainer create(GameRun run, DockerNetwork network) throws DockerException;
    String getSimulationContainerName(Game game);

}
