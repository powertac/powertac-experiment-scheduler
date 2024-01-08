package org.powertac.orchestrator.docker.exception;

public class InterruptedBySignalException extends InterruptedException {

    public InterruptedBySignalException(String message) {
        super(message);
    }

}
