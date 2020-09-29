package org.powertac.rachma.powertac.broker.exception;

public class BrokerNotFoundException extends Exception {

    public BrokerNotFoundException(String message) {
        super(message);
    }

    public BrokerNotFoundException(String message, Throwable throwable) {
        super(message,throwable);
    }

}
