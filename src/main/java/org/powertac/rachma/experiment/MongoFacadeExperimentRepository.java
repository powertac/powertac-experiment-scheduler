package org.powertac.rachma.experiment;

import org.apache.logging.log4j.LogManager;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobRepository;
import org.powertac.rachma.job.exception.JobNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoFacadeExperimentRepository implements ExperimentRepository {

    private static final String collectionName = "experiment";

    private final MongoTemplate mongoTemplate;
    private final JobRepository jobRepository;

    @Autowired
    public MongoFacadeExperimentRepository(MongoTemplate mongoTemplate, JobRepository jobRepository) {
        this.mongoTemplate = mongoTemplate;
        this.jobRepository = jobRepository;
    }

    @Override
    public List<Experiment> findAll() {
        List<Experiment> experiments = mongoTemplate.findAll(Experiment.class);
        for (Experiment experiment : experiments) {
            for (Instance instance : experiment.getInstances()) {
                addJobStatus(instance);
            }
        }
        return experiments;
    }

    public Experiment findByInstanceId(String instanceId) {
        Query query = new Query().addCriteria(Criteria.where("instances._id").is(instanceId));
        return mongoTemplate.findOne(query, Experiment.class);
    }

    @Override
    public void add(Experiment experiment) {
        mongoTemplate.save(experiment, collectionName);
    }

    private void addJobStatus(Instance instance) {
        try {
            Job job = jobRepository.find(instance.getId());
            instance.setStatus(job.getStatus());
        } catch (JobNotFoundException e) {
            LogManager.getLogger().debug("couldn't find job=" + instance.getId());
            // ignore case
        }
    }

}
