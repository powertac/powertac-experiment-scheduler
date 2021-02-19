package org.powertac.rachma.job;

import java.time.Instant;

public interface JobStatus {

    Instant getStart();
    Instant getEnd();
    long getDurationMillis();

    JobState getState();

    void setQueued();
    void setRunning();
    void setCompleted();
    void setFailed();

    boolean isFinished();

}
