package uk.ac.ox.oucs.search2.indexation.exception;

/**
 * Exception thrown whenever a {@link uk.ac.ox.oucs.search2.indexation.Task} not supported by a {@link uk.ac.ox.oucs.search2.indexation.TaskHandler}.
 *
 * @author Colin Hebert
 */
public class UnsupportedTaskException extends TaskException {
    public UnsupportedTaskException() {
    }

    public UnsupportedTaskException(String message) {
        super(message);
    }

    public UnsupportedTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedTaskException(Throwable cause) {
        super(cause);
    }
}
