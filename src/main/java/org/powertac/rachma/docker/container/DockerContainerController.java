package org.powertac.rachma.docker.container;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.exception.ContainerStartException;
import org.powertac.rachma.docker.exception.KillContainerException;

public interface DockerContainerController {

    void start(DockerContainer container) throws ContainerStartException;
    void kill(DockerContainer container) throws KillContainerException;
    boolean isRunning(DockerContainer container) throws DockerException;

}
