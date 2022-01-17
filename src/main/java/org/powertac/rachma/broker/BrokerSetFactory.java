package org.powertac.rachma.broker;

import org.powertac.rachma.validation.exception.ValidationException;

import java.util.Set;

public interface BrokerSetFactory {

    BrokerSet create(Set<Broker> brokers) throws ValidationException;

}
