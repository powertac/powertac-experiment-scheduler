package org.powertac.rachma.docker.network;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Set;

@Builder
public class DockerNetworkConfig {

    @Getter
    private String network;

    @Getter
    @Singular
    private Set<String> containerAliases;

    @Getter
    @Singular
    private Set<Integer> exposedPorts;

}
