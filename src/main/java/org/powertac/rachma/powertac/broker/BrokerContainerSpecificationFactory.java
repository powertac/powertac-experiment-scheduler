package org.powertac.rachma.powertac.broker;

import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.configuration.SharedPropertiesFileBuilder;
import org.powertac.rachma.docker.container.DockerContainerSpec;
import org.powertac.rachma.resource.SharedFile;
import org.powertac.rachma.resource.WorkDirectory;
import org.powertac.rachma.task.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Service
public class BrokerContainerSpecificationFactory {

    @Value("${broker.defaultPropertiesFile}")
    private String defaultPropertiesFile;

    @Value("${container.directory.base}")
    private String containerBaseDir;

    public DockerContainerSpec createSpecification(Task task, BrokerType brokerType)
        throws IOException {

        SharedFile sharedPropertiesFile = createSharedPropertiesFile(task.getWorkDirectory(), brokerType);
        BrokerDockerContainerCommand command = createCommand(sharedPropertiesFile);

        return DockerContainerSpec.builder()
            .image(brokerType.getImage())
            .file(sharedPropertiesFile)
            .name(getContainerName(task, brokerType))
            .network(getNetworkName(task))
            .command(command)
            .build();
    }

    private SharedFile createSharedPropertiesFile(WorkDirectory workDirectory, BrokerType brokerType) throws IOException {
        return SharedPropertiesFileBuilder.newFile()
            .workDirectory(workDirectory)
            .containerDirectory(containerBaseDir)
            .file(getPropertiesFileName(brokerType))
            .properties(getDefaultProperties())
            .property("samplebroker.core.powerTacBroker.username", brokerType.getName())
            .writeAndBuild();
    }

    private BrokerDockerContainerCommand createCommand(SharedFile propertiesFile) {
        return BrokerContainerCommandBuilder.builder()
            .withOption("--config", propertiesFile.getContainerPath())
            .build();
    }

    private Properties getDefaultProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newInputStream(Paths.get(defaultPropertiesFile)));
        return properties;
    }

    private String getContainerName(Task task, BrokerType brokerType) {
        return String.format("%s.%s", brokerType.getName(), task.getId());
    }

    private String getNetworkName(Task task) {
        return String.format("sim.%s", task.getId());
    }

    private String getPropertiesFileName(BrokerType brokerType) {
        return String.format("broker.%s.properties", brokerType.getName());
    }

}
