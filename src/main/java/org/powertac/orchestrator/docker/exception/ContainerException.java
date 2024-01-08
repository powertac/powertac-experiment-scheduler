package org.powertac.orchestrator.docker.exception;

public class ContainerException extends Exception {

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }

}
