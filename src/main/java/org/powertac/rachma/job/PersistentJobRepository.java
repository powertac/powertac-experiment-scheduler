package org.powertac.rachma.job;

import org.powertac.rachma.job.exception.JobNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Component
@Deprecated
public class PersistentJobRepository implements JobRepository {

    private final MongoRepository<Job, String> mongoRepository;
    private final MongoTemplate mongoTemplate;

    public PersistentJobRepository(MongoRepository<Job, String> mongoRepository, MongoTemplate mongoTemplate) {
        this.mongoRepository = mongoRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Job find(String id) throws JobNotFoundException {
        Optional<Job> job = mongoRepository.findById(id);
        if (job.isEmpty()) {
            throw new JobNotFoundException(String.format("could not find job with id=%s", id));
        }
        return job.get();
    }

    @Override
    public Set<Job> findQueuedJobs() {
        Query query = new Query();
        query.addCriteria(Criteria.where("status.state").is(JobState.QUEUED.toString()));
        List<Job> queuedJobs = mongoTemplate.find(query, Job.class, "job");
        return new HashSet<>(queuedJobs);
    }

    @Override
    public Set<Job> findRunningJobs() {
        Query query = new Query();
        query.addCriteria(Criteria.where("status.state").is(JobState.RUNNING.toString()));
        List<Job> queuedJobs = mongoTemplate.find(query, Job.class, "job");
        return new HashSet<>(queuedJobs);
    }

    @Override
    public List<Job> findAll() {
        return mongoRepository.findAll();
    }

    @Override
    public void add(Job job) {
        mongoRepository.insert(job);
    }

    @Override
    public void addAll(Collection<Job> jobs) {
        for (Job job : jobs) {
            add(job);
        }
    }

    @Override
    public void update(Job job) {
        mongoRepository.save(job);
    }

    @Override
    public void remove(Job job) {
        mongoRepository.delete(job);
    }

}
