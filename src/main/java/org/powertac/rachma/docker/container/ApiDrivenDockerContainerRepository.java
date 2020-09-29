package org.powertac.rachma.docker.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApiDrivenDockerContainerRepository implements DockerContainerRepository {

    @Value("${dev.keepContainersAfterRun}")
    private boolean forceKeepContainers;

    private final DockerClient dockerClient;
    private final Set<DockerContainer> managedContainers = new HashSet<>();

    @Autowired
    public ApiDrivenDockerContainerRepository(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public void add(DockerContainer container) {
        if (exists(container)) {
            managedContainers.add(container);
        }
    }

    public Set<DockerContainer> findAll() {
        return managedContainers.stream()
            .filter(this::exists)
            .collect(Collectors.toSet());
    }

    @Override
    public void remove(DockerContainer container) throws DockerException {
        remove(container, false);
    }

    @Override
    public void remove(DockerContainer container, boolean force) throws DockerException {
        try {
            managedContainers.remove(container);
            if (!forceKeepContainers) {
                RemoveContainerCmd removeContainerCmd = createRemoveCommand(container, force);
                removeContainerCmd.exec();
            }
        }
        catch (NotFoundException e) {
            // This exception is ignored because the desired state is for this container not to exist anyway
        }
    }

    @Override
    public boolean exists(DockerContainer container) throws DockerException {
        try {
            // source of truth is always the docker daemon
            dockerClient.inspectContainerCmd(container.getId()).exec();
            return true;
        }
        catch (NotFoundException e) {
            managedContainers.remove(container);
            return false;
        }
    }

    @PreDestroy
    public void cleanUpContainers() {
        for (DockerContainer container : findAll()) {
            remove(container, true);
        }
    }

    private RemoveContainerCmd createRemoveCommand(DockerContainer container, boolean force) {
        return dockerClient.removeContainerCmd(container.getId())
            .withForce(force)
            .withRemoveVolumes(true);
    }

}
