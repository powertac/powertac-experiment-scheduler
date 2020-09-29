package org.powertac.rachma.docker.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.exception.ContainerStartException;
import org.powertac.rachma.docker.exception.KillContainerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DockerContainerControllerImpl implements DockerContainerController {

    private final DockerClient dockerClient;

    @Autowired
    public DockerContainerControllerImpl(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public void start(DockerContainer container) throws ContainerStartException {
        try {
            dockerClient.startContainerCmd(container.getId()).exec();
        }
        catch (DockerException e) {
            throw new ContainerStartException("could not start container with name=" + container.getName());
        }
    }

    @Override
    public void kill(DockerContainer container) throws KillContainerException {
        try {
            dockerClient.killContainerCmd(container.getId()).exec();
        }
        catch (DockerException e) {
            throw new KillContainerException("could not kill container with name=" + container.getName());
        }
    }

    @Override
    public boolean isRunning(DockerContainer container) throws DockerException {
        return getState(container).getRunning().booleanValue();
    }

    private InspectContainerResponse.ContainerState getState(DockerContainer container) throws DockerException {
        return inspect(container).getState();
    }

    private InspectContainerResponse inspect(DockerContainer container)  throws DockerException {
        return dockerClient.inspectContainerCmd(container.getId()).exec();
    }

}
