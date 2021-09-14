package org.powertac.rachma.broker;

import org.powertac.rachma.powertac.broker.BrokerNotFoundException;

import java.util.Map;

public interface BrokerTypeRepository {

    BrokerType findByName(String name) throws BrokerNotFoundException;

    Map<String, BrokerType> findAll();

    boolean has(String name);

}
