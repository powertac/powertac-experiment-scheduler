package org.powertac.rachma.docker.container;

import org.powertac.rachma.runner.Runner;
import org.powertac.rachma.runner.RunnerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DockerContainerRunnerFactory implements RunnerFactory<DockerContainerSpec, Runner> {

    @Value("${container.task.inspectionInterval}")
    protected int inspectionInterval;

    @Value("${container.task.inspectionRetryTimeout}")
    protected int inspectionRetryTimeout;

    @Value("${container.task.inspectionRetryLimit}")
    protected int inspectionRetryLimit;

    private final DockerContainerCreator containerCreator;
    private final DockerContainerController containerController;
    private final DockerContainerRepository containerRepository;

    public DockerContainerRunnerFactory(DockerContainerCreator containerCreator,
                                        DockerContainerController containerController,
                                        DockerContainerRepository containerRepository) {
        this.containerCreator = containerCreator;
        this.containerController = containerController;
        this.containerRepository = containerRepository;
    }

    @Override
    public Runner createRunner(DockerContainerSpec containerSpec) {
        DockerContainer container = containerCreator.createContainer(containerSpec);
        containerRepository.add(container);
        ContainerInspectionConfig inspectionConfig = ContainerInspectionConfig.builder()
            .interval(inspectionInterval)
            .retryTimeout(inspectionRetryTimeout)
            .retryLimit(inspectionRetryLimit)
            .build();
        return new DockerContainerRunnerImpl(containerController, containerRepository, container, inspectionConfig);
    }

}
