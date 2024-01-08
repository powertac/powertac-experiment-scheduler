package org.powertac.orchestrator.user.exception;

public class InvalidRegistrationTokenException extends Exception {

    public InvalidRegistrationTokenException(String message) {
        super(message);
    }

    public InvalidRegistrationTokenException(String message, Throwable cause) {
        super(message, cause);
    }

}
