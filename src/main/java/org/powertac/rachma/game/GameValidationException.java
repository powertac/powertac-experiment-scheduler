package org.powertac.rachma.game;

import java.io.IOException;

public class GameValidationException extends Exception {

    public GameValidationException(String message) {
        super(message);
    }

    public GameValidationException(String message, IOException cause) {
        super(message, cause);
    }
}
