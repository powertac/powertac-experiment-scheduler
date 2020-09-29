package org.powertac.rachma.instance;

import lombok.Getter;

import java.time.Instant;

public class InstanceStatusImpl implements InstanceStatus {

    @Getter
    private Instant queuedAt;

    @Override
    public boolean isQueued() {
        return null != queuedAt;
    }

}
