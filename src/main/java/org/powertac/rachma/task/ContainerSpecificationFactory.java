package org.powertac.rachma.task;

import org.powertac.rachma.docker.container.DockerContainerSpec;
import org.powertac.rachma.docker.exception.NotFoundException;

import java.io.IOException;
import java.util.Set;

public interface ContainerSpecificationFactory<T extends ContainerTask> {

    Set<DockerContainerSpec> createSpecifications(T task) throws IOException, NotFoundException;

}
