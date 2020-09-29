package org.powertac.rachma.docker.exception;

public class NetworkCreationException extends ContainerNetworkException {

    public NetworkCreationException(String message) {
        super(message);
    }

    public NetworkCreationException(String message, Throwable previousException) {
        super(message, previousException);
    }

}
