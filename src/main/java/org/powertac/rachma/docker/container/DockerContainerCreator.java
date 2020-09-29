package org.powertac.rachma.docker.container;

import com.github.dockerjava.api.exception.DockerException;

public interface DockerContainerCreator {

    DockerContainer createContainer(DockerContainerSpec spec) throws DockerException;

}
