package org.powertac.rachma.docker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DockerNetwork {

    @Getter
    private String id;

    @Getter
    private String name;

}
