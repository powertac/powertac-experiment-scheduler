package org.powertac.rachma.docker.container;

import org.powertac.rachma.docker.exception.ContainerException;
import org.powertac.rachma.docker.exception.ContainerReflectionException;
import org.powertac.rachma.docker.exception.ContainerStartException;
import org.powertac.rachma.docker.exception.KillContainerException;

import java.util.Map;
import java.util.Set;

public interface DockerContainerController {

    void start(DockerContainer container) throws ContainerStartException;
    void kill(DockerContainer container) throws KillContainerException;
    ContainerExitState run(DockerContainer container) throws ContainerException;
    Map<DockerContainer, ContainerExitState> run(Set<DockerContainer> containers) throws ContainerException;
    boolean isRunning(DockerContainer container) throws ContainerReflectionException;

}
