package org.powertac.rachma.broker;

public interface BrokerValidator {

    void validate(Broker broker) throws BrokerValidationException;

}
