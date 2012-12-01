package uk.ac.ox.oucs.search2.task;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.task.exception.MultipleTasksException;
import uk.ac.ox.oucs.search2.task.exception.TaskException;
import uk.ac.ox.oucs.search2.task.exception.TemporaryTaskException;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Task runner putting the polling system on hold if a TemporaryTaskException has been caught.
 * <p>
 * Assuming that every {@link Task} should be successfully executed or completely fail, a {@link TemporaryTaskException}
 * could mean that the {@link TaskHandler} can't process new tasks at the moment.
 * </p>
 * <p>
 * This TaskRunner will put every thread that attempt to run a task on hold each time a TemporaryTaskHandlingException is caught.<br />
 * The waiting time is doubled each time a Task fails with a TemporaryTaskHandlingException until it reaches the {@link #maximumWaitingTime}.<br />
 * The waiting time is reset each time a task is successfully executed.
 * </p>
 *
 * @author Colin Hebert
 */
public abstract class BlockingTaskRunner implements TaskRunner {
    public static final int BASE_WAITING_TIME = 1000;
    private static final Logger logger = LoggerFactory.getLogger(BlockingTaskRunner.class);
    public static final SecurityAdvisor GRANT_ALL_SECURITY_ADVISOR = new SecurityAdvisor() {
        @Override
        public SecurityAdvice isAllowed(String userId, String function, String reference) {
            return SecurityAdvice.ALLOWED;
        }
    };
    private final ReentrantLock taskRunnerLock = new ReentrantLock();
    /**
     * Maximum wait
     * Set to 5 minutes by default
     */
    private int maximumWaitingTime = 5 * 60 * BASE_WAITING_TIME;
    private int waitingTime = BASE_WAITING_TIME;
    //private TaskHandler taskHandler;
    private SecurityService securityService;
    private TaskQueuing taskQueuing;
    private TaskHandler taskHandler;

    public void runTask(Task task) {
        try {
            //Stop for a while because some tasks failed and should be run again.
            while (taskRunnerLock.isLocked())
                taskRunnerLock.wait();

            //Unlock permissions so every resource is accessible
            unlockPermissions();

            try {
                taskHandler.executeTask(task);
                //The task was successful, reset the waiting time
                waitingTime = BASE_WAITING_TIME;
            } catch (MultipleTasksException e) {
                logger.warn("Some exceptions happened during the execution of '" + task + "'.", e);
                unfoldNestedTaskException(e);
            } catch (TemporaryTaskException e) {
                taskRunnerLock.tryLock();
                logger.warn("The task '" + task + "' couldn't be executed, try again later.", e);
                taskQueuing.addTaskToQueue(task);
            } catch (Exception e) {
                logger.error("Couldn't execute task '" + task + "'.", e);
            }

            // A TemporaryTaskException occurred, stop everything for a while (so the search server can recover)
            if (taskRunnerLock.isHeldByCurrentThread()) {
                Thread.sleep(waitingTime);
                //Multiply the waiting time by two
                if (waitingTime <= maximumWaitingTime)
                    waitingTime <<= 1;
            }
        } catch (InterruptedException e) {
            logger.error("Thread interrupted while trying to do '" + task + "'.", e);
            taskQueuing.addTaskToQueue(task);
        } finally {
            // A TemporaryTaskException occurred and the waiting time is now passed (or an exception killed it)
            // unlock everything and get back to work
            if (taskRunnerLock.isHeldByCurrentThread()) {
                taskRunnerLock.notifyAll();
                taskRunnerLock.unlock();
            }

            removePermissions();
        }
    }

    private void unfoldNestedTaskException(MultipleTasksException e) {
        for (TaskException t : e.getThrownExceptions()) {
            if (t instanceof TemporaryTaskException) {
                taskRunnerLock.tryLock();
                TemporaryTaskException tthe = (TemporaryTaskException) t;
                logger.warn("A task failed '" + tthe.getNewTask() + "' will be tried again later.", t);
                taskQueuing.addTaskToQueue(tthe.getNewTask());
            } else {
                logger.error("An exception occured during the task execution.", t);
            }
        }
    }

    private void unlockPermissions() {
        securityService.pushAdvisor(GRANT_ALL_SECURITY_ADVISOR);
    }

    private void removePermissions() {
        securityService.popAdvisor(GRANT_ALL_SECURITY_ADVISOR);
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setTaskHandler(TaskHandler taskHandler) {
        this.taskHandler = taskHandler;
    }

    public void setTaskQueuing(TaskQueuing taskQueuing) {
        this.taskQueuing = taskQueuing;
    }
}