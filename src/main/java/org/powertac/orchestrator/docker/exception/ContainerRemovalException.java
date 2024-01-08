package org.powertac.orchestrator.docker.exception;

public class ContainerRemovalException extends ContainerException {

    public ContainerRemovalException(String message) {
        super(message);
    }

    public ContainerRemovalException(String message, Throwable cause) {
        super(message, cause);
    }

}
