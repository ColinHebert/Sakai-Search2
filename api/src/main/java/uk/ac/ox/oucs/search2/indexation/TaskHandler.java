package uk.ac.ox.oucs.search2.indexation;

/**
 * A TaskHandler is in charge of executing a Task.
 * <p>
 * The main usage of the TaskHandler is directly interpret and execute a {@link Task} in order to modify or update the index.
 * </p>
 *
 * @author Colin Hebert
 */
public interface TaskHandler {
    /**
     * Executes a Task.
     *
     * @param task task to execute.
     * @throws uk.ac.ox.oucs.search2.indexation.exception.TaskException
     *          if the execution wasn't successful.
     * @throws uk.ac.ox.oucs.search2.indexation.exception.UnsupportedTaskException
     *          if the {@link Task} can't be handled.
     */
    void executeTask(Task task);
}
