package org.powertac.rachma.job.exception;

public class JobSchedulingException extends Exception {

    public JobSchedulingException(String message) {
        super(message);
    }

    public JobSchedulingException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
