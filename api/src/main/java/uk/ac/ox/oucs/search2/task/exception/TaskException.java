package uk.ac.ox.oucs.search2.task.exception;

/**
 * @author Colin Hebert
 */
public class TaskException extends RuntimeException {
    public TaskException() {
    }

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskException(Throwable cause) {
        super(cause);
    }
}
