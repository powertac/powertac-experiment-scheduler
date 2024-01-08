package org.powertac.orchestrator.docker;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.powertac.orchestrator.exec.PersistentTask;

import jakarta.persistence.Convert;

@SuperBuilder
@NoArgsConstructor
public abstract class ContainerTask extends PersistentTask {

    @Getter
    @Setter
    @Convert(converter = DockerContainerConverter.class)
    private DockerContainer container;

}
