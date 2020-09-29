package org.powertac.rachma.docker.container;

import org.powertac.rachma.runner.Runner;

public interface DockerContainerRunner extends Runner {

    DockerContainer getContainer();

}
