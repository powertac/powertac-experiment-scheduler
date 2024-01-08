package org.powertac.orchestrator.docker;

import java.util.Optional;

public interface DockerContainerRepository {

    Optional<DockerContainer> find(String id);

}
