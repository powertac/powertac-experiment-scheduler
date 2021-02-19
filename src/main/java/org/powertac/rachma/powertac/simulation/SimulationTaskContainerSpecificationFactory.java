package org.powertac.rachma.powertac.simulation;

import org.powertac.rachma.docker.DockerContainerCommand;
import org.powertac.rachma.docker.container.DockerContainerSpec;
import org.powertac.rachma.docker.exception.NotFoundException;
import org.powertac.rachma.powertac.broker.BrokerContainerSpecificationFactory;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.powertac.server.AbstractServerContainerSpecificationFactory;
import org.powertac.rachma.powertac.server.ServerContainerCommandBuilder;
import org.powertac.rachma.powertac.server.ServerDockerContainerCommand;
import org.powertac.rachma.resource.SharedDirectory;
import org.powertac.rachma.resource.SharedFile;
import org.powertac.rachma.resource.SharedFileBuilder;
import org.powertac.rachma.task.ContainerSpecificationFactory;
import org.powertac.rachma.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SimulationTaskContainerSpecificationFactory
    extends AbstractServerContainerSpecificationFactory<SimulationTask>
    implements ContainerSpecificationFactory<SimulationTask> {

    @Value("${simulation.container.directory.base}")
    private String containerBaseDir;

    @Value("${simulation.container.defaultMessageBrokerPort}")
    private int defaultMessageBrokerPort;

    @Value("${simualtion.container.aliases}")
    private String[] serverAliases;

    private final BrokerContainerSpecificationFactory brokerSpecificationFactory;

    @Autowired
    public SimulationTaskContainerSpecificationFactory(BrokerContainerSpecificationFactory brokerSpecificationFactory) {
        this.brokerSpecificationFactory = brokerSpecificationFactory;
    }

    @Override
    public Set<DockerContainerSpec> createSpecifications(SimulationTask task) throws IOException, NotFoundException {
        Set<DockerContainerSpec> specifications = new HashSet<>();
        specifications.add(createServerSpecification(task));
        specifications.addAll(createBrokerSpecifications(task));
        return specifications;
    }

    private Set<DockerContainerSpec> createBrokerSpecifications(SimulationTask task) throws IOException {
        Set<DockerContainerSpec> specifications = new HashSet<>();
        for (BrokerType type : task.getBrokers()) {
            specifications.add(brokerSpecificationFactory.createSpecification(task, type));
        }
        return specifications;
    }

    private DockerContainerSpec createServerSpecification(SimulationTask task) throws IOException {
        SharedDirectory logDirectory = createLogDirectory(task);
        SharedFile bootstrapFile = createSharedBootstrapFile(task);
        SharedFile propertiesFile = createSharedPropertiesFile(task, getPropertiesFileName(task));
        List<String> brokerNames = getBrokerNames(task);
        DockerContainerCommand command = createCommand(bootstrapFile, propertiesFile, brokerNames);
        return  DockerContainerSpec.builder()
            .image(defaultServerImageTag)
            .name(getContainerName(task))
            .file(bootstrapFile)
            .file(propertiesFile)
            .directory(logDirectory)
            .command(command)
            .network(getNetworkName(task))
            .exposedPort(defaultMessageBrokerPort)
            .aliases(getAliases())
            .build();
    }

    private ServerDockerContainerCommand createCommand(SharedFile bootstrapFile, SharedFile propertiesFile,
                                                       List<String> brokerNames) {
        return ServerContainerCommandBuilder.sim()
            .withInputFile(bootstrapFile.getContainerPath())
            .withPropertyFile(propertiesFile.getContainerPath())
            .withBrokers(brokerNames)
            .build();
    }

    private SharedDirectory createLogDirectory(SimulationTask task) {
        return (SharedDirectory) SharedFileBuilder.create()
            .localDirectory(task.getJob().getWorkDirectory().getLocalDirectory())
            .hostDirectory(task.getJob().getWorkDirectory().getHostDirectory())
            .containerDirectory(containerBaseDir)
            .directory("log/")
            .build();
    }

    private List<String> getBrokerNames(SimulationTask task) {
        return task.getBrokers().stream().map(BrokerType::getName).collect(Collectors.toList());
    }

    private Set<String> getAliases() {
        return Stream.of(serverAliases).collect(Collectors.toSet());
    }

    private String getPropertiesFileName(Task task) {
        return String.format("%s.simulation.properties", task.getJob().getId());
    }

    // TODO : duplicate of BrokerContainerSpecificationFactory::getNetworkName()
    private String getNetworkName(Task task) {
        return String.format("sim.%s", task.getJob().getId());
    }

    private String getContainerName(Task task) {
        return String.format("sim.%s", task.getJob().getId());
    }

}
