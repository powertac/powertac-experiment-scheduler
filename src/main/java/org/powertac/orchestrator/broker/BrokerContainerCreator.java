package org.powertac.orchestrator.broker;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.orchestrator.docker.DockerContainer;
import org.powertac.orchestrator.docker.DockerNetwork;
import org.powertac.orchestrator.game.GameRun;

public interface BrokerContainerCreator {

    DockerContainer create(GameRun run, Broker broker, DockerNetwork network) throws DockerException;
    String getBrokerContainerName(GameRun game, Broker broker);

}
