package org.powertac.orchestrator.docker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DockerContainer {

    @Getter
    private final String id;

    @Getter
    private final String name;

}
