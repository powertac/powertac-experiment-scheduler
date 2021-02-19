package org.powertac.rachma.powertac.bootstrap;

import org.powertac.rachma.docker.container.DockerContainerSpec;
import org.powertac.rachma.docker.exception.NotFoundException;
import org.powertac.rachma.powertac.server.AbstractServerContainerSpecificationFactory;
import org.powertac.rachma.powertac.server.ServerContainerCommandBuilder;
import org.powertac.rachma.powertac.server.ServerDockerContainerCommand;
import org.powertac.rachma.resource.SharedFile;
import org.powertac.rachma.resource.SharedFileDriver;
import org.powertac.rachma.task.ContainerSpecificationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BootstrapContainerSpecificationFactory extends AbstractServerContainerSpecificationFactory<BootstrapTask>
    implements ContainerSpecificationFactory<BootstrapTask> {

    private SharedFileDriver sharedFileDriver;

    @Autowired
    public BootstrapContainerSpecificationFactory(SharedFileDriver sharedFileDriver) {
        this.sharedFileDriver = sharedFileDriver;
    }

    @Override
    public Set<DockerContainerSpec> createSpecifications(BootstrapTask task) throws NotFoundException, IOException {
        return Stream.of(createSpecification(task)).collect(Collectors.toSet());
    }

    @Override
    protected SharedFile createSharedBootstrapFile(BootstrapTask task) throws IOException {
        SharedFile sharedBootstrapFile = super.createSharedBootstrapFile(task);
        sharedFileDriver.create(sharedBootstrapFile);
        return sharedBootstrapFile;
    }

    private DockerContainerSpec createSpecification(BootstrapTask task) throws IOException {
        SharedFile bootstrapFile = createSharedBootstrapFile(task);
        SharedFile propertiesFile = createSharedPropertiesFile(task, getPropertiesFileName(task));
        ServerDockerContainerCommand command = createCommand(bootstrapFile, propertiesFile);
        return DockerContainerSpec.builder()
            .image(defaultServerImageTag)
            .name(getContainerName(task))
            .file(bootstrapFile)
            .file(propertiesFile)
            .command(command)
            .build();
    }

    private ServerDockerContainerCommand createCommand(SharedFile bootstrapFile, SharedFile propertiesFile) {
        return ServerContainerCommandBuilder.boot()
            .withOutputFile(bootstrapFile.getContainerPath())
            .withPropertyFile(propertiesFile.getContainerPath())
            .build();
    }

    private String getPropertiesFileName(BootstrapTask task) {
        return String.format("%s.bootstrap.properties", task.getJob().getId());
    }

    private String getContainerName(BootstrapTask task) {
        return String.format("boot.%s", task.getJob().getId());
    }

}
