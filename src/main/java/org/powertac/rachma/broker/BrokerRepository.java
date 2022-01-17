package org.powertac.rachma.broker;

import java.util.Collection;

public interface BrokerRepository {

    Collection<Broker> findAll();
    Broker findByNameAndVersion(String name, String version);
    boolean exists(String id);
    boolean exists(String name, String version);
    void save(Broker broker) throws BrokerConflictException;

}
