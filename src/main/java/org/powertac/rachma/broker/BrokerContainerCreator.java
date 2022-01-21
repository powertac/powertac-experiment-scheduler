package org.powertac.rachma.broker;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.DockerContainer;
import org.powertac.rachma.docker.DockerNetwork;
import org.powertac.rachma.game.Game;

public interface BrokerContainerCreator {

    DockerContainer create(Game game, Broker broker, DockerNetwork network) throws DockerException;
    String getBrokerContainerName(Game game, Broker broker);

}
