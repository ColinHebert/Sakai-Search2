package uk.ac.ox.oucs.search2.indexation;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.thread_local.api.ThreadLocalManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.indexation.exception.MultipleTasksException;
import uk.ac.ox.oucs.search2.indexation.exception.TaskException;
import uk.ac.ox.oucs.search2.indexation.exception.TemporaryTaskException;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Task runner putting the polling system on hold if a TemporaryTaskException has been caught and requeuing the failed task.
 * <p>
 * Assuming that every {@link Task} should be successfully executed or completely fail, a {@link TemporaryTaskException}
 * could mean that the {@link TaskHandler} can't process new tasks at the moment.
 * </p>
 * <p>
 * This TaskRunner will put every thread that attempt to run a indexation on hold each time a TemporaryTaskHandlingException is caught.<br />
 * The waiting time is doubled each time a Task fails with a TemporaryTaskHandlingException until it reaches the {@link #maximumWaitingTime}.<br />
 * The waiting time is reset each time a indexation is successfully executed.
 * </p>
 *
 * @author Colin Hebert
 */
public abstract class AbstractTaskRunner implements TaskRunner {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTaskRunner.class);
    private static final SecurityAdvisor GRANT_ALL_SECURITY_ADVISOR = new SecurityAdvisor() {
        @Override
        public SecurityAdvice isAllowed(String userId, String function, String reference) {
            return SecurityAdvice.ALLOWED;
        }
    };
    /**
     * Waiting time when the first {@link TemporaryTaskException} occurs. (1 second)
     */
    private static final int BASE_WAITING_TIME = 1000;
    private final ReentrantLock taskRunnerLock = new ReentrantLock();
    /**
     * Maximum wait.
     * Set to 5 minutes by default.
     */
    private int maximumWaitingTime = 5 * 60 * 1000;
    private int waitingTime = BASE_WAITING_TIME;
    private SecurityService securityService;
    private ThreadLocalManager threadLocalManager;
    private TaskQueuing taskQueuing;
    private TaskHandler taskHandler;

    public void runTask(Task task) {
        try {
            checkLockdown();
            //Unlock permissions so every resource is accessible
            unlockPermissions();

            try {
                taskHandler.executeTask(task);
                //The indexation was successful, reset the waiting time
                waitingTime = BASE_WAITING_TIME;
            } catch (MultipleTasksException e) {
                logger.warn("Some exceptions happened during the execution of '" + task + "'.", e);
                unfoldMultipleTasksException(e);
            } catch (TemporaryTaskException e) {
                taskRunnerLock.tryLock();
                logger.warn("The task '" + task + "' couldn't be executed, try again later.", e);
                taskQueuing.addTaskToQueue(task);
            } catch (Exception e) {
                logger.error("Couldn't execute '" + task + "'.", e);
            }

            if (taskRunnerLock.isHeldByCurrentThread()) {
                lockdown();
            }
        } finally {
            //Empties the content of the localThread
            cleanLocalThread();
            //Lock permissions as they're not used anymore
            lockPermissions();
        }
    }

    /**
     * Checks if the TaskRunner is in a lockdown, waits for it to finish if it's the case.
     */
    private void checkLockdown() {
        synchronized (taskRunnerLock) {
            if (logger.isDebugEnabled())
                logger.debug("Check if the taskRunnerLock is open");
            try {
                while (taskRunnerLock.isLocked()) {
                    //Stop for a while because some tasks failed and should be run again.
                    logger.info("TaskRunner on lockdown, waiting for a while.");
                    taskRunnerLock.wait();
                }
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while the system was on lockdown.", e);
            }
        }
    }

    /**
     * Starts and stop a lockdown.
     * <p>
     * A lockdown is often started when a {@link TemporaryTaskException} occured.<br />
     * Prevents other threads from polling from the queue for a while, leaving some time to recover from the TemporaryTaskException.
     * </p>
     */
    private void lockdown() {
        try {
            logger.info("Tasks runner on lockdown, waiting " + waitingTime + "ms");
            Thread.sleep(waitingTime);
            //Multiply the waiting time by two
            if (waitingTime <= maximumWaitingTime)
                waitingTime <<= 1;
        } catch (InterruptedException e) {
            logger.error("Thread interrupted while the system was on lockdown.", e);
        } finally {
            synchronized (taskRunnerLock) {
                logger.info("Lockdown finished");
                taskRunnerLock.notifyAll();
                taskRunnerLock.unlock();
            }
        }
    }

    /**
     * Clean the local thread by removing elements that could be stored in {@link ThreadLocal}.
     */
    private void cleanLocalThread() {
        if (logger.isDebugEnabled())
            logger.debug("Cleanup the localThread from leftover content.");
        threadLocalManager.clear();
    }

    /**
     * Handles every exception captured by a {@link MultipleTasksException}.
     * <p>
     * {@link TemporaryTaskException} will cause a lockdown and queues a new task. Anything else will simply be logged.
     * </p>
     *
     * @param mte MultipleTasksException to unfold.
     */
    private void unfoldMultipleTasksException(MultipleTasksException mte) {
        for (TaskException te : mte.getThrownExceptions()) {
            if (te instanceof TemporaryTaskException) {
                taskRunnerLock.tryLock();
                TemporaryTaskException tte = (TemporaryTaskException) te;
                logger.warn("A task couldn't be executed, will try '" + tte.getNewTask() + "' later.", te);
                taskQueuing.addTaskToQueue(tte.getNewTask());
            } else {
                logger.error("An exception occurred during the task execution.", te);
            }
        }
    }

    /**
     * Gives every right to the current user.
     */
    private void unlockPermissions() {
        logger.info("Add an advisor allowing the access to everything");
        securityService.pushAdvisor(GRANT_ALL_SECURITY_ADVISOR);
    }

    /**
     * Removes the rights given with {@link #unlockPermissions()}.
     */
    private void lockPermissions() {
        logger.info("Remove the advisor allowing the access to everything");
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

    public void setThreadLocalManager(ThreadLocalManager threadLocalManager) {
        this.threadLocalManager = threadLocalManager;
    }

    public void setMaximumWaitingTime(int maximumWaitingTime) {
        this.maximumWaitingTime = maximumWaitingTime;
    }
}