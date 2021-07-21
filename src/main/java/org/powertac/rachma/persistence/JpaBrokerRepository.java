package org.powertac.rachma.persistence;

import org.powertac.rachma.broker.Broker;
import org.springframework.data.repository.CrudRepository;

public interface JpaBrokerRepository extends CrudRepository<Broker, String> {

    boolean existsByNameAndVersion(String name, String version);
    Broker findByName(String name);

}
