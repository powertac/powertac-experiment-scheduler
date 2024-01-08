package org.powertac.orchestrator.broker;

import org.powertac.orchestrator.validation.exception.ValidationException;

import java.util.Set;

public interface BrokerSetFactory {

    BrokerSet create(Set<Broker> brokers) throws ValidationException;
    BrokerSet createFromIds(Set<String> ids) throws ValidationException;

}
