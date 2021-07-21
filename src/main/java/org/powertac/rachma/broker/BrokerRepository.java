package org.powertac.rachma.broker;

import java.util.Collection;

public interface BrokerRepository {

    Collection<Broker> findAll();
    Broker findById(String id);
    Broker findByName(String name);
    boolean exists(String name, String version);
    void save(Broker broker);

}
