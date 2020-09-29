package org.powertac.rachma.runner.exception;

public class RunnerCreationFailedException extends RunnerException {

    public RunnerCreationFailedException(String message) {
        super(message);
    }

    public RunnerCreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
