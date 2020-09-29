package org.powertac.rachma.docker.exception;

public class ContainerException extends Exception {

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }

}
