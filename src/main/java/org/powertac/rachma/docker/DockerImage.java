package org.powertac.rachma.docker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DockerImage {

    @Getter
    private final String id;

    @Getter
    private final String tag;

}
