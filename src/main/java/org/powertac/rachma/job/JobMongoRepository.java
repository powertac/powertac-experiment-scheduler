package org.powertac.rachma.job;

import org.springframework.data.mongodb.repository.MongoRepository;

@Deprecated
public interface JobMongoRepository extends MongoRepository<Job, String> {}
