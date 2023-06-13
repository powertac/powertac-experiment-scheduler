package org.powertac.rachma.docker;

import java.util.Optional;

public interface DockerContainerRepository {

    Optional<DockerContainer> find(String id);

}
