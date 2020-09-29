package org.powertac.rachma.docker.container;

import lombok.Builder;
import lombok.Getter;

@Builder
public class ContainerInspectionConfig {

    @Getter
    private final int interval;

    @Getter
    private final int retryLimit;

    @Getter
    private final int retryTimeout;

}
