package uk.ac.ox.oucs.search2.task;

/**
 * Fail fast queueing system
 * <p>
 * Runs the {@link Task} in the current thread and let exceptions propagate.
 * </p>
 *
 * @author Colin Hebert
 */
public class DefaultQueueingSystem implements TaskQueuing, TaskRunner {
    private TaskHandler taskHandler;

    @Override
    public void addTaskToQueue(Task task) {
        runTask(task);
    }

    @Override
    public void runTask(Task task) {
        taskHandler.executeTask(task);
    }

    public void setTaskHandler(TaskHandler taskHandler) {
        this.taskHandler = taskHandler;
    }
}
