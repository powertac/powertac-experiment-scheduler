package org.powertac.rachma.job;

import org.powertac.rachma.job.exception.JobNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface JobRepository {

    Job find(String id) throws JobNotFoundException;
    Set<Job> findQueuedJobs();
    Set<Job> findRunningJobs();
    List<Job> findAll();
    void add(Job job);
    void addAll(Collection<Job> jobs);
    void update(Job job);
    void remove(Job job);

}
