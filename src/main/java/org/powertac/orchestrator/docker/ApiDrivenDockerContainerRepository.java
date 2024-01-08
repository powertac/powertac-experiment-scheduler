package org.powertac.orchestrator.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApiDrivenDockerContainerRepository implements DockerContainerRepository {

    private final DockerClient docker;

    public ApiDrivenDockerContainerRepository(DockerClient docker) {
        this.docker = docker;
    }

    @Override
    public Optional<DockerContainer> find(String id) throws DockerException {
        try {
            InspectContainerResponse response = docker.inspectContainerCmd(id).exec();
            return Optional.of(new DockerContainer(id, response.getName()));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

}
