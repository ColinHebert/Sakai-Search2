package uk.ac.ox.oucs.search2.indexation.exception;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Exception thrown when a {@link uk.ac.ox.oucs.search2.indexation.Task} composed of multiple subTasks failed.
 * <p>
 * Some Tasks (such as a complete reindexation) can be composed of multiple smaller Tasks.<br />
 * When one of the smaller subTasks fails, the system is expected to continue to process the subTasks left.<br />
 * In order to keep track of failed subTasks, a MultipleTasksException can be generated, filled with the
 * {@link TaskException}s and thrown.
 * </p>
 *
 * @author Colin Hebert
 */
public class MultipleTasksException extends TaskException {
    /**
     * Exceptions thrown during the execution of a task.
     */
    private final Collection<TaskException> thrownExceptions = new LinkedList<TaskException>();

    public MultipleTasksException() {
    }

    public MultipleTasksException(String message) {
        super(message);
    }

    public MultipleTasksException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultipleTasksException(Throwable cause) {
        super(cause);
    }

    /**
     * Adds an exception to the list of recorded ones.
     * <p>
     * If the exception is a MultipleTasksException itself, the list of exceptions of the provided exception is
     * copied into this exception.
     * </p>
     *
     * @param taskException Caught exception stored for later.
     */
    public void addTaskException(TaskException taskException) {
        if (taskException instanceof MultipleTasksException) {
            thrownExceptions.addAll(((MultipleTasksException) taskException).getThrownExceptions());
        } else {
            thrownExceptions.add(taskException);
        }
    }

    /**
     * Gets every exception stored.
     *
     * @return an unmodifiable collection of exceptions.
     */
    public Collection<TaskException> getThrownExceptions() {
        return Collections.unmodifiableCollection(thrownExceptions);
    }

    /**
     * Checks if an exception has been stored.
     * <p>
     * Usually an empty MultipleTasksException should not be thrown.
     * </p>
     *
     * @return true if at least one exception has been added, false otherwise.
     */
    public boolean isEmpty() {
        return thrownExceptions.isEmpty();
    }
}
