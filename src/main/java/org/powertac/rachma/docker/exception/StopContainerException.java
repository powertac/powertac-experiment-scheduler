package org.powertac.rachma.docker.exception;

public class StopContainerException extends ContainerException {

    public StopContainerException(String message) {
        super(message);
    }

    public StopContainerException(String message, Throwable cause) {
        super(message, cause);
    }

}
