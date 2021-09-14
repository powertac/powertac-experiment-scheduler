package org.powertac.rachma.task;

import java.time.Instant;

@Deprecated
public interface TaskStatus {

    Instant getStart();
    Instant getEnd();
    long getDurationMillis();

    TaskState getState();
    void setRunning();
    void setCompleted();
    void setFailed();

}
