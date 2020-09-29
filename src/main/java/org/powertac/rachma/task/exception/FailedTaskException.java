package org.powertac.rachma.task.exception;

@Deprecated
public class FailedTaskException extends TaskException {

    public FailedTaskException(String message) {
        super(message);
    }

    public FailedTaskException(Throwable cause) {
        super(cause);
    }

    public FailedTaskException(String message, Throwable cause) {
        super(message, cause);
    }

}
