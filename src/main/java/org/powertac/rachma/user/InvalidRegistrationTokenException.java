package org.powertac.rachma.user;

public class InvalidRegistrationTokenException extends Exception {

    public InvalidRegistrationTokenException(String message) {
        super(message);
    }

    public InvalidRegistrationTokenException(String message, Throwable cause) {
        super(message, cause);
    }

}
