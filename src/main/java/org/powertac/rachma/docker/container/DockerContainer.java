package org.powertac.rachma.docker.container;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.powertac.rachma.runner.RunnableEntity;

@AllArgsConstructor
public class DockerContainer implements RunnableEntity {

    @Getter
    private final String id;

    @Getter
    private final String name;

}
