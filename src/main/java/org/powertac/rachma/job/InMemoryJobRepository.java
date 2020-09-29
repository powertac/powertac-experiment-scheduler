package org.powertac.rachma.job;

import org.apache.commons.lang.NotImplementedException;
import org.powertac.rachma.job.exception.JobNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InMemoryJobRepository implements JobRepository {

    private Map<String, Job> jobs = new HashMap<>();

    @Override
    public Job find(String id) throws JobNotFoundException {
        if (jobs.containsKey(id)) {
            return jobs.get(id);
        }
        throw new JobNotFoundException("job with id=" + id + " could not be found");
    }

    @Override
    public Set<Job> findAllQueuedJobs() {
        throw new NotImplementedException("This method should not be called!");
    }

    @Override
    public List<Job> list() {
        return new ArrayList<>(jobs.values());
    }

    @Override
    public void add(Job job) {
        jobs.put(job.getId(), job);
    }

    @Override
    public void addAll(Collection<Job> jobs) {
        for (Job job : jobs) {
            add(job);
        }
    }

    @Override
    public void update(Job job) {
        if (jobs.containsKey(job.getId())) {
            jobs.replace(job.getId(), job);
        }
    }

    @Override
    public void remove(Job job) {
        if (jobs.containsKey(job.getId())) {
            jobs.remove(job.getId(), job);
        }
    }
}
