package uk.ac.ox.oucs.search2.task.exception;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Exception thrown when multiple {@link uk.ac.ox.oucs.search2.task.Task} failed in the same process.
 * <p>
 * This allows to have a fine grained exception for heavy tasks such as rebuilding the entire index.
 * </p>
 *
 * @author Colin Hebert
 */
public class MultipleTasksException extends TaskException {
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

    public void addTaskException(TaskException te) {
        if (te instanceof MultipleTasksException) {
            thrownExceptions.addAll(((MultipleTasksException) te).getThrownExceptions());
        } else {
            thrownExceptions.add(te);
        }
    }

    public Collection<TaskException> getThrownExceptions() {
        return Collections.unmodifiableCollection(thrownExceptions);
    }

    public boolean isEmpty(){
        return thrownExceptions.isEmpty();
    }
}
