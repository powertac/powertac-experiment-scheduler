package org.powertac.orchestrator.broker;

public interface BrokerValidator {

    void validate(Broker broker) throws BrokerValidationException;

}
