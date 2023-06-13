package org.powertac.rachma.docker;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.powertac.rachma.exec.PersistentTask;

import javax.persistence.Convert;

@SuperBuilder
@NoArgsConstructor
public abstract class ContainerTask extends PersistentTask {

    @Getter
    @Setter
    @Convert(converter = DockerContainerConverter.class)
    private DockerContainer container;

}
