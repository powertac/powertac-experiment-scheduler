package org.powertac.rachma.runner.exception;

public class RunnerException extends Exception {

    RunnerException(String message) {
        super(message);
    }

    RunnerException(String message, Throwable cause) {
        super(message, cause);
    }

}
