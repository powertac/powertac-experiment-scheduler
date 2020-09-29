package org.powertac.rachma.experiment;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoFacadeExperimentRepository implements ExperimentRepository {

    private static final String collectionName = "experiment";

    private final MongoTemplate mongoTemplate;

    public MongoFacadeExperimentRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Experiment> findAll() {
        List<Experiment> experiments = mongoTemplate.findAll(Experiment.class);
        return experiments;
    }

    @Override
    public void add(Experiment experiment) {
        mongoTemplate.save(experiment, collectionName);
    }

}
