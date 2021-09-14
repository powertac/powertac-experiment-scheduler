package org.powertac.rachma.task;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@NoArgsConstructor
@Deprecated
public class GenericTaskStatus implements TaskStatus {

    @Getter
    private Instant start;

    @Getter
    private Instant end;

    @Getter
    private TaskState state = TaskState.CREATED;

    @Override
    public void setRunning() {
        start();
        state = TaskState.RUNNING;
    }

    @Override
    public void setCompleted() {
        end();
        state = TaskState.COMPLETED;
    }

    @Override
    public void setFailed() {
        end();
        state = TaskState.FAILED;
    }

    @Override
    public long getDurationMillis() {
        if (null == start) {
            return 0;
        }
        if (null == end) {
            return Duration.between(start, Instant.now()).abs().toMillis();
        }
        return Duration.between(start, end).abs().toMillis();
    }

    private void start() {
        start = Instant.now();
    }

    private void end() {
        end = Instant.now();
    }

}
