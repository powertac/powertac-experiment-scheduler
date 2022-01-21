package org.powertac.rachma.docker;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.exception.ContainerException;
import org.powertac.rachma.docker.exception.ContainerReflectionException;
import org.powertac.rachma.docker.exception.ContainerStartException;
import org.powertac.rachma.docker.exception.KillContainerException;

import java.util.Map;
import java.util.Set;

public interface DockerContainerController {

    void start(DockerContainer container) throws ContainerStartException;
    void kill(DockerContainer container) throws KillContainerException;
    DockerContainerExitState run(DockerContainer container) throws ContainerException;
    Map<DockerContainer, DockerContainerExitState> run(Set<DockerContainer> containers) throws ContainerException;
    boolean isRunning(DockerContainer container) throws ContainerReflectionException;
    void remove(DockerContainer container) throws DockerException;
    void forceRemove(DockerContainer container) throws DockerException;
    void forceRemove(String name) throws DockerException;
    boolean exists(String name) throws DockerException;

}
