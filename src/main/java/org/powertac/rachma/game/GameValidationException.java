package org.powertac.rachma.game;

public class GameValidationException extends Exception {

    public GameValidationException(String message) {
        super(message);
    }

    public GameValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
