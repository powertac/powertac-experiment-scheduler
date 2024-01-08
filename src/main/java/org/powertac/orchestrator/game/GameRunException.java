package org.powertac.orchestrator.game;

public class GameRunException extends Exception {

    public GameRunException(String message) {
        super(message);
    }

    public GameRunException(String message, Throwable cause) {
        super(message, cause);
    }

}
