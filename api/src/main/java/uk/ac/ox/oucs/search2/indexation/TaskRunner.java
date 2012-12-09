package uk.ac.ox.oucs.search2.indexation;

/**
 * A TaskRunner is in charge of receiving the next Task and prepare the execution environment.
 * <p>
 * The TaskRunner receives each {@link Task} and hand it to a {@link TaskHandler}.<br />
 * If an exception happens during the execution of the Task, the TaskRunner is expected to handle it.<br />
 * The TaskRunner is also responsible to modify the execution environment so the Task can be executed.
 * </p>
 *
 * @author Colin Hebert
 */
public interface TaskRunner {
    /**
     * Runs a Task after setting up the execution environment.
     * <p>
     * This method should not throw exceptions.
     * </p>
     *
     * @param task the recently dequeued Task, about to be executed.
     */
    void runTask(Task task);
}
