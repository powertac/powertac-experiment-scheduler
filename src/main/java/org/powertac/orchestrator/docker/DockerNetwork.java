package org.powertac.orchestrator.docker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DockerNetwork {

    @Getter
    private String id;

    @Getter
    private String name;

}
