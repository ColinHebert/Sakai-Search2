package uk.ac.ox.oucs.search2.indexation;

/**
 * Simple implementation of {@link TaskQueuing} executing directly the {@link Task} in the current thread.
 * <p>
 * This implementation also provides a {@link TaskRunner} (itself).<br />
 * This implementation should not be used outside a development environment as it causes the event system to hang
 * until the task has been executed.
 * </p>
 *
 * @author Colin Hebert
 */
public class SimpleQueuingSystem extends AbstractTaskRunner implements TaskQueuing {
    public void init() {
        setTaskQueuing(this);
    }

    @Override
    public void addTaskToQueue(Task task) {
        runTask(task);
    }
}
