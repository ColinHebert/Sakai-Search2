package uk.ac.ox.oucs.search2.indexation;

/**
 * Queuing system allowing Tasks execution to be deferred.
 * <p>
 * Each queuing system also provides a {@link TaskRunner} implementation which will be in charge of
 * handling the next {@link Task}.
 * </p>
 *
 * @author Colin Hebert
 */
public interface TaskQueuing {
    /**
     * Adds the Task to the queuing system.
     *
     * @param task task to queue.
     */
    void addTaskToQueue(Task task);
}
