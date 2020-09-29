package org.powertac.rachma.job;

import org.powertac.rachma.runner.Runner;
import org.powertac.rachma.runner.SerialRunnerGroup;

import java.util.List;

public class SerialJobRunnerGroup extends SerialRunnerGroup implements JobRunner {

    private final Job job;

    public SerialJobRunnerGroup(Job job, List<Runner> runners) {
        super(runners);
        this.job = job;
    }

    @Override
    public Job getJob() {
        return job;
    }

}
