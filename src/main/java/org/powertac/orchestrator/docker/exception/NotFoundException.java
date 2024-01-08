package org.powertac.orchestrator.docker.exception;

public class NotFoundException extends ContainerException {

    public NotFoundException(String message) {
        super(message);
    }

}
