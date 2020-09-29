package org.powertac.rachma.job;

import org.powertac.rachma.job.exception.JobSchedulingException;

import java.util.Collection;

public interface JobScheduler {

    void runNextJob();
    void schedule(Job job) throws JobSchedulingException;
    void schedule(Collection<Job> jobs) throws JobSchedulingException;

}
