package org.powertac.rachma.broker;

import java.util.Collection;
import java.util.Optional;

public interface BrokerRepository {

    Collection<Broker> findAll();
    Optional<Broker> findById(String id);
    Broker findByNameAndVersion(String name, String version);
    boolean exists(String id);
    boolean exists(String name, String version);
    void save(Broker broker) throws BrokerConflictException;

}
