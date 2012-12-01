package uk.ac.ox.oucs.search2.service;

import uk.ac.ox.oucs.search2.task.DefaultTask;
import uk.ac.ox.oucs.search2.task.Task;
import uk.ac.ox.oucs.search2.task.TaskHandler;
import uk.ac.ox.oucs.search2.task.TaskQueuing;

import static uk.ac.ox.oucs.search2.task.DefaultTask.DOCUMENT_REFERENCE;
import static uk.ac.ox.oucs.search2.task.DefaultTask.SITE_ID;
import static uk.ac.ox.oucs.search2.task.DefaultTask.Type.*;

/**
 * @author Colin Hebert
 */
public class DefaultIndexService implements IndexService {
    private TaskHandler taskHandler;
    private TaskQueuing taskQueuing;

    @Override
    public void indexDocument(String documentReference) {
        Task task = new DefaultTask(INDEX_DOCUMENT).setProperty(DOCUMENT_REFERENCE, documentReference);
        taskHandler.executeTask(task);
    }

    @Override
    public void unindexDocument(String documentReference) {
        Task task = new DefaultTask(UNINDEX_DOCUMENT).setProperty(DOCUMENT_REFERENCE, documentReference);
        taskHandler.executeTask(task);
    }

    @Override
    public void queueIndexDocument(String documentReference) {
        Task task = new DefaultTask(INDEX_DOCUMENT).setProperty(DOCUMENT_REFERENCE, documentReference);
        taskQueuing.addTaskToQueue(task);
    }

    @Override
    public void queueUnindexDocument(String documentReference) {
        Task task = new DefaultTask(UNINDEX_DOCUMENT).setProperty(DOCUMENT_REFERENCE, documentReference);
        taskQueuing.addTaskToQueue(task);
    }

    @Override
    public void indexSiteDocuments(String siteId) {
        Task task = new DefaultTask(INDEX_SITE).setProperty(SITE_ID, siteId);
        taskHandler.executeTask(task);
    }

    @Override
    public void reindexSiteDocuments(String siteId) {
        Task task = new DefaultTask(REINDEX_SITE).setProperty(SITE_ID, siteId);
        taskHandler.executeTask(task);
    }

    @Override
    public void unindexSiteDocuments(String siteId) {
        Task task = new DefaultTask(UNINDEX_SITE).setProperty(SITE_ID, siteId);
        taskHandler.executeTask(task);
    }

    @Override
    public void queueIndexSiteDocuments(String siteId) {
        Task task = new DefaultTask(INDEX_SITE).setProperty(SITE_ID, siteId);
        taskQueuing.addTaskToQueue(task);
    }

    @Override
    public void queueReindexSiteDocuments(String siteId) {
        Task task = new DefaultTask(REINDEX_SITE).setProperty(SITE_ID, siteId);
        taskQueuing.addTaskToQueue(task);
    }

    @Override
    public void queueUnindexSiteDocuments(String siteId) {
        Task task = new DefaultTask(UNINDEX_SITE).setProperty(SITE_ID, siteId);
        taskQueuing.addTaskToQueue(task);
    }

    @Override
    public void indexEveryDocuments() {
        Task task = new DefaultTask(INDEX_ALL);
        taskHandler.executeTask(task);
    }

    @Override
    public void reindexEveryDocuments() {
        Task task = new DefaultTask(REINDEX_ALL);
        taskHandler.executeTask(task);
    }

    @Override
    public void unindexEveryDocuments() {
        Task task = new DefaultTask(UNINDEX_ALL);
        taskHandler.executeTask(task);
    }

    @Override
    public void queueIndexEveryDocuments() {
        Task task = new DefaultTask(INDEX_ALL);
        taskQueuing.addTaskToQueue(task);
    }

    @Override
    public void queueReindexEveryDocuments() {
        Task task = new DefaultTask(REINDEX_ALL);
        taskQueuing.addTaskToQueue(task);
    }

    @Override
    public void queueUnindexEveryDocuments() {
        Task task = new DefaultTask(UNINDEX_ALL);
        taskQueuing.addTaskToQueue(task);
    }

    public void setTaskHandler(TaskHandler taskHandler) {
        this.taskHandler = taskHandler;
    }

    public void setTaskQueuing(TaskQueuing taskQueuing) {
        this.taskQueuing = taskQueuing;
    }
}
