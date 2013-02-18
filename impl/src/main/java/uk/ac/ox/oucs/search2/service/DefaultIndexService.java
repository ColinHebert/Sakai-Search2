package uk.ac.ox.oucs.search2.service;

import uk.ac.ox.oucs.search2.indexation.DefaultTask;
import uk.ac.ox.oucs.search2.indexation.Task;
import uk.ac.ox.oucs.search2.indexation.TaskHandler;
import uk.ac.ox.oucs.search2.indexation.TaskQueuing;

import static uk.ac.ox.oucs.search2.indexation.DefaultTask.DOCUMENT_REFERENCE;
import static uk.ac.ox.oucs.search2.indexation.DefaultTask.SITE_ID;
import static uk.ac.ox.oucs.search2.indexation.DefaultTask.Type.*;

/**
 * Default implementation of the indexService.
 * <p>
 * Relies on a {@link TaskHandler} to process a {@link Task} directly or sends it to a {@link TaskQueuing} if it can
 * be processed later.
 * </p>
 *
 * @author Colin Hebert
 */
public class DefaultIndexService extends AbstractIndexService {
    private TaskHandler taskHandler;
    private TaskQueuing taskQueuing;

    @Override
    public void indexDocument(String documentReference, boolean now) {
        Task task = new DefaultTask(INDEX_DOCUMENT).setProperty(DOCUMENT_REFERENCE, documentReference);
        addTask(task, now);
    }

    @Override
    public void unindexDocument(String documentReference, boolean now) {
        Task task = new DefaultTask(UNINDEX_DOCUMENT).setProperty(DOCUMENT_REFERENCE, documentReference);
        addTask(task, now);
    }

    @Override
    public void indexSiteDocuments(String siteId, boolean now) {
        Task task = new DefaultTask(INDEX_SITE).setProperty(SITE_ID, siteId);
        addTask(task, now);
    }

    @Override
    public void unindexSiteDocuments(String siteId, boolean now) {
        Task task = new DefaultTask(UNINDEX_SITE).setProperty(SITE_ID, siteId);
        addTask(task, now);
    }

    @Override
    public void indexEveryDocuments(boolean now) {
        Task task = new DefaultTask(INDEX_ALL);
        addTask(task, now);
    }

    @Override
    public void unindexEveryDocuments(boolean now) {
        Task task = new DefaultTask(UNINDEX_ALL);
        addTask(task, now);
    }

    /**
     * Adds a new {@link Task} by either sending it to a {@link TaskHandler} or a {@link TaskQueuing}.
     *
     * @param task Task to add to the system.
     * @param now  true if the task should be handler right away, false if it can wait in the queue.
     */
    private void addTask(Task task, boolean now) {
        if (now)
            taskHandler.executeTask(task);
        else
            taskQueuing.addTaskToQueue(task);
    }

    public void setTaskHandler(TaskHandler taskHandler) {
        this.taskHandler = taskHandler;
    }

    public void setTaskQueuing(TaskQueuing taskQueuing) {
        this.taskQueuing = taskQueuing;
    }

}
