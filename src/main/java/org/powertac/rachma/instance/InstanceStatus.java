package org.powertac.rachma.instance;

import java.time.Instant;

public interface InstanceStatus {

    boolean isQueued();

    Instant getQueuedAt();

}
