package uk.ac.ox.oucs.search2.indexation;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;

/**
 * Default queuing system, based on {@link ExecutorService}.
 * <p>
 * This implementations uses two different ExecutorService, one for heavy tasks such as a complete reindexation,
 * the other one for simpler tasks such as the indexation of a simple file.<br />
 * This separation in two executors allows to avoid heavy tasks splitting in other heavy tasks before being split in
 * simple tasks, to fill the queue.<br />
 * The use case would be a "reindex everything" task, splitting itself into a lot of "reindex site siteId", and those
 * "reindex site siteId" would split themselves into "index document documentReference".<br />
 * In this scenario, expanding every task into a single Executor would mean that at some point the queue contains
 * every possible documents. The second executor is here to take the load from the first one, avoiding a queue with
 * too many elements.<br />
 * For this reason, the {@link #simpleTasksExecutor} must have a bigger queue and work faster than
 * {@link #heavyTasksExecutor}
 * </p>
 *
 * @author Colin Hebert
 */
public class DefaultTaskQueuing extends AbstractTaskRunner implements TaskQueuing {
    private Collection<String> simpleTaskTypes = Collections.emptyList();
    /**
     * Executor with a large backlog handling very simple tasks (index document/remove document).
     * <p>
     * This executor is heavily used when some operations fail temporarily and are re-queued.
     * </p>
     */
    private ExecutorService simpleTasksExecutor;
    /**
     * Executor used for heavy tasks that will probably be split in sub-tasks.
     */
    private ExecutorService heavyTasksExecutor;

    public void destroy() {
        simpleTasksExecutor.shutdownNow();
        heavyTasksExecutor.shutdownNow();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The Task is added to the right executor depending on the type of of Task.
     * </p>
     *
     * @param task task to add to the queue.
     */
    @Override
    public void addTaskToQueue(Task task) {
        RunnableTask rt = new RunnableTask(task);
        if (simpleTaskTypes.contains(task.getType()))
            simpleTasksExecutor.execute(rt);
        else
            heavyTasksExecutor.execute(rt);
    }

    public void setSimpleTasksExecutor(ExecutorService simpleTasksExecutor) {
        this.simpleTasksExecutor = simpleTasksExecutor;
    }

    public void setHeavyTasksExecutor(ExecutorService heavyTasksExecutor) {
        this.heavyTasksExecutor = heavyTasksExecutor;
    }

    public void setSimpleTaskTypes(Collection<String> simpleTaskTypes) {
        this.simpleTaskTypes = simpleTaskTypes;
    }

    /**
     * Runnable class generated for each task and queued in the {@link ExecutorService}.
     */
    private final class RunnableTask implements Runnable {
        private final Task task;

        private RunnableTask(Task task) {
            this.task = task;
        }

        @Override
        public void run() {
            runTask(task);
        }
    }
}
