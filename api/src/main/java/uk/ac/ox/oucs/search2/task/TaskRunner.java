package uk.ac.ox.oucs.search2.task;

/**
 * Class in charge of polling the queuing system in order to execute the {@link Task}
 *
 * @author Colin Hebert
 */
public interface TaskRunner {
    void runTask(Task task);
}
