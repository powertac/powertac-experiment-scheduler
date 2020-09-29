package org.powertac.rachma.job.exception;

public class JobNotFoundException extends Exception {

    public JobNotFoundException(String message) {
        super(message);
    }

    public JobNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
