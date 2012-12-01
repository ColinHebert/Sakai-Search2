package uk.ac.ox.oucs.search2.task;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

/**
 * @author Colin Hebert
 */
public class InMemoryQueuing extends BlockingTaskRunner implements TaskQueuing {
    private Collection<String> simpleTaskTypes = new LinkedList<String>();
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

    private class RunnableTask implements Runnable {
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
