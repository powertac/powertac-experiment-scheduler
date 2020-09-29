package org.powertac.rachma.job;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.powertac.rachma.job.serialization.JobStatusSerializer;

import java.time.Duration;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = JobStatusSerializer.class)
public class JobStatusImpl implements JobStatus {

    @Getter
    private Instant start;

    @Getter
    private Instant end;

    @Getter
    private JobState state = JobState.CREATED;

    @Override
    public void setQueued() {
        state = JobState.QUEUED;
    }

    @Override
    public void setRunning() {
        start();
        state = JobState.RUNNING;
    }

    @Override
    public void setFailed() {
        end();
        state = JobState.FAILED;
    }

    @Override
    public void setCompleted() {
        end();
        state = JobState.COMPLETED;
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
