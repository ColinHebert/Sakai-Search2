package uk.ac.ox.oucs.search2.task.exception;

import uk.ac.ox.oucs.search2.task.Task;

/**
 * Exception thrown during the execution of a {@link Task} when the problem was temporary and the task should be run again.
 * <p>
 * Provides a new Task to run (usually the exact same task)
 * </p>
 *
 * @author Colin Hebert
 */
public class TemporaryTaskException extends TaskException {
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

    public Task getNewTask() {
        return newTask;
    }
}
