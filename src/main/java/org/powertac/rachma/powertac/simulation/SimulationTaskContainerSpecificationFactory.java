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
import java.nio.file.Files;
import java.nio.file.Path;
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

    @Value("${simulation.container.aliases}")
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
        SharedFile seedFile = createSharedSeedFile(task);
        List<String> brokerNames = getBrokerNames(task);
        DockerContainerCommand command = createCommand(bootstrapFile, propertiesFile, brokerNames, seedFile);
        DockerContainerSpec.DockerContainerSpecBuilder builder = DockerContainerSpec.builder()
            .image(defaultServerImageTag)
            .name(getContainerName(task))
            .command(command)
            .file(bootstrapFile)
            .file(propertiesFile)
            .directory(logDirectory)
            .network(getNetworkName(task))
            .exposedPort(defaultMessageBrokerPort)
            .aliases(getAliases());
        if (null != seedFile) {
            builder.file(seedFile);
        }
        return builder.build();
    }

    private ServerDockerContainerCommand createCommand(SharedFile bootstrapFile, SharedFile propertiesFile,
                                                       List<String> brokerNames, SharedFile seedFile) {
        ServerContainerCommandBuilder builder = ServerContainerCommandBuilder.sim()
            .withBootstrapFile(bootstrapFile.getContainerPath())
            .withPropertyFile(propertiesFile.getContainerPath())
            .withBrokers(brokerNames);
        if (null != seedFile) {
            builder.withSeedFile(seedFile.getContainerPath());
        }
        return builder.build();
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

    protected SharedFile createSharedBootstrapFile(SimulationTask task) throws IOException {
        if (null != task.getBootstrapFilePath()) {
            Path bootstrapFilePath = Path.of(task.getBootstrapFilePath());
            if (!Files.exists(bootstrapFilePath)) {
                throw new IOException(String.format("bootstrap file '%s' does not exist", bootstrapFilePath));
            }
            return SharedFileBuilder.create()
                .localDirectory(bootstrapFilePath.getParent().toString())  // FIXME : local path = host path
                .hostDirectory(bootstrapFilePath.getParent().toString())
                .containerDirectory(containerBaseDir)
                .file(bootstrapFilePath.getFileName().toString())
                .build();
        }
        return super.createSharedBootstrapFile(task);
    }

    private SharedFile createSharedSeedFile(SimulationTask task) throws IOException {
        if (null != task.getSeedFilePath()) {
            Path seedFilePath = Path.of(task.getSeedFilePath());
            if (!Files.exists(seedFilePath)) {
                throw new IOException(String.format("seed file '%s' does not exist", seedFilePath));
            }
            return SharedFileBuilder.create()
                .localDirectory(seedFilePath.getParent().toString()) // FIXME : local path = host path
                .hostDirectory(seedFilePath.getParent().toString())
                .containerDirectory(containerBaseDir)
                .file(seedFilePath.getFileName().toString())
                .build();
        }
        return null;
    }

    // TODO : duplicate of BrokerContainerSpecificationFactory::getNetworkName()
    private String getNetworkName(Task task) {
        return String.format("sim.%s", task.getJob().getId());
    }

    private String getContainerName(Task task) {
        return String.format("sim.%s", task.getJob().getId());
    }

}
