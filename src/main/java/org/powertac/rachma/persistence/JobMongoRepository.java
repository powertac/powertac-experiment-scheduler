package org.powertac.rachma.persistence;

import org.powertac.rachma.job.Job;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobMongoRepository extends MongoRepository<Job, String> {}
