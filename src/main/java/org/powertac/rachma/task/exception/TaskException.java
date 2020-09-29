package org.powertac.rachma.task.exception;

@Deprecated
public class TaskException extends Exception {

    public TaskException() {
        super();
    }

    public TaskException(Throwable cause) {
        super(cause);
    }

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

}
