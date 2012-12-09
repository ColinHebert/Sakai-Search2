package uk.ac.ox.oucs.search2.indexation.exception;

import uk.ac.ox.oucs.search2.indexation.Task;

/**
 * Thrown when a temporary or easily recoverable problem was encountered during the execution of the {@link Task}.
 * <p>
 * In most cases, an exception due to network issues shouldn't prevent the Task to be executed at all and only
 * requires the system to try again later.<br />
 * Each TemporaryTaskException provides a new Task to execute. In most cases, the new Task will have the same properties
 * as the Task that just failed.
 * </p>
 *
 * @author Colin Hebert
 */
public class TemporaryTaskException extends TaskException {
    /**
     * New Task to execute.
     */
    private final Task newTask;

    public TemporaryTaskException(Task newTask) {
        this.newTask = newTask;
    }

    public TemporaryTaskException(String message, Task newTask) {
        super(message);
        this.newTask = newTask;
    }

    public TemporaryTaskException(String message, Throwable cause, Task newTask) {
        super(message, cause);
        this.newTask = newTask;
    }

    public TemporaryTaskException(Throwable cause, Task newTask) {
        super(cause);
        this.newTask = newTask;
    }

    /**
     * Gets the new {@link Task} to execute.
     *
     * @return the new Task to execute.
     */
    public Task getNewTask() {
        return newTask;
    }
}
