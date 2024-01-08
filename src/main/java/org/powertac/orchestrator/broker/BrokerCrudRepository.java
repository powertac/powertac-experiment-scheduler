package org.powertac.orchestrator.broker;

import org.springframework.data.repository.CrudRepository;

interface BrokerCrudRepository extends CrudRepository<Broker, String> {

    boolean existsByNameAndVersion(String name, String version);
    Broker findByNameAndVersion(String name, String version);

}
