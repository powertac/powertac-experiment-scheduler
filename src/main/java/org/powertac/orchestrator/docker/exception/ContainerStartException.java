package org.powertac.orchestrator.docker.exception;

public class ContainerStartException extends ContainerException {

    public ContainerStartException(String message) {
        super(message);
    }

    public ContainerStartException(String message, Throwable cause) {
        super(message, cause);
    }

}
