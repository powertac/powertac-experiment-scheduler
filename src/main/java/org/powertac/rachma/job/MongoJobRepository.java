package org.powertac.rachma.job;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;

@ConditionalOnProperty(value = "persistence.legacy.enable-mongo", havingValue = "true")
public interface MongoJobRepository extends MongoRepository<Job, String> {}
