package uk.ac.ox.oucs.search2.indexation;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.document.Document;
import uk.ac.ox.oucs.search2.document.DocumentProducer;
import uk.ac.ox.oucs.search2.document.DocumentProducerRegistry;
import uk.ac.ox.oucs.search2.indexation.exception.MultipleTasksException;
import uk.ac.ox.oucs.search2.indexation.exception.TaskException;
import uk.ac.ox.oucs.search2.indexation.exception.UnsupportedTaskException;
import uk.ac.ox.oucs.search2.service.IndexService;

import java.util.Collection;

import static uk.ac.ox.oucs.search2.indexation.DefaultTask.Type.*;

/**
 * Abstract implementation of a {@link TaskHandler} handling any {@link DefaultTask}.
 *
 * @author Colin Hebert
 */
public abstract class AbstractTaskHandler implements TaskHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTaskHandler.class);
    private IndexService indexService;
    private DocumentProducerRegistry documentProducerRegistry;

    @Override
    public void executeTask(Task task) {
        if (logger.isDebugEnabled())
            logger.debug("Execute the task '" + task + "'");

        try {
            String type = task.getType();
            DateTime taskCreationDate = new DateTime(task.getCreationDate());

            if (INDEX_DOCUMENT.getTypeName().equals(type)) {
                String documentReference = task.getProperty(DefaultTask.DOCUMENT_REFERENCE);
                DocumentProducer documentProducer = documentProducerRegistry.getDocumentProducer(documentReference);
                indexDocument(documentProducer.getDocument(documentReference), taskCreationDate);
            } else if (UNINDEX_DOCUMENT.getTypeName().equals(type)) {
                unindexDocument(task.getProperty(DefaultTask.DOCUMENT_REFERENCE), taskCreationDate);
            } else if (INDEX_SITE.getTypeName().equals(type)) {
                indexSite(task.getProperty(DefaultTask.SITE_ID), taskCreationDate);
            } else if (UNINDEX_SITE.getTypeName().equals(type)) {
                unindexSite(task.getProperty(DefaultTask.SITE_ID), taskCreationDate);
            } else if (INDEX_ALL.getTypeName().equals(type)) {
                indexAll(taskCreationDate);
            } else if (UNINDEX_ALL.getTypeName().equals(type)) {
                unindexAll(taskCreationDate);
            } else {
                throw new UnsupportedTaskException("Couldn't handle the task '" + task + "'");
            }
        } catch (TaskException e) {
            throw e;
        } catch (Exception e) {
            throw new TaskException("An exception occurred while handling the task '" + task + "'", e);
        }
    }

    /**
     * Indexes a single document.
     *
     * @param document         document to index.
     * @param taskCreationDate creation date of the original {@link Task}.
     */
    protected abstract void indexDocument(Document document, DateTime taskCreationDate);

    /**
     * Removes a single document from the index.
     *
     * @param documentReference reference of the document to unindex.
     * @param taskCreationDate  creation date of the original {@link Task}.
     */
    protected abstract void unindexDocument(String documentReference, DateTime taskCreationDate);

    /**
     * Reindexes an entire site.
     * <p>
     * It's expected that after a call to {@code indexSite()} the entire site is completely up to date in the index and
     * there shouldn't be any deprecated document left in the index.
     * </p>
     *
     * @param siteId           unique identifier of the site to index.
     * @param taskCreationDate creation date of the original {@link Task}.
     */
    protected void indexSite(String siteId, DateTime taskCreationDate) {
        logger.info("Rebuilding the index for '" + siteId + "'");
        MultipleTasksException nthe = new MultipleTasksException("An exception occurred while indexing the site '" + siteId + "'");

        for (Document document : indexService.getIndexableDocumentsForSite(siteId)) {
            indexDocument(document, taskCreationDate);
        }

        if (!nthe.isEmpty()) throw nthe;
    }

    /**
     * Removes an entire site from the index.
     * <p>
     * It's expected that after a call to {@code unindexSite()} the index has been emptied of every document related
     * to the site, even deprecated documents should be removed.
     * </p>
     *
     * @param siteId           unique identifier of the site to unindex.
     * @param taskCreationDate creation date of the original {@link Task}.
     */
    protected void unindexSite(String siteId, DateTime taskCreationDate) {
        logger.info("Removing documents from the index for the site '" + siteId + "'");
        MultipleTasksException nthe = new MultipleTasksException("An exception occurred while unindexing the site '" + siteId + "'");

        for (Document document : indexService.getIndexableDocumentsForSite(siteId)) {
            unindexDocument(document.getReference(), taskCreationDate);
        }

        if (!nthe.isEmpty()) throw nthe;
    }

    /**
     * Rebuilds the entire index.
     * <p>
     * It's expected that after a call to {@code indexAll()} the index has been entirely rebuild and is free from
     * deprecated documents.
     * </p>
     *
     * @param taskCreationDate creation date of the original {@link Task}.
     */
    protected void indexAll(DateTime taskCreationDate) {
        logger.info("Rebuilding the entire index");
        MultipleTasksException nthe = new MultipleTasksException("An exception occurred while rebuilding the index");
        Collection<String> indexableSites = indexService.getIndexableSiteIds();

        for (String siteId : indexableSites) {
            indexSite(siteId, taskCreationDate);
        }

        if (!nthe.isEmpty()) throw nthe;
    }

    /**
     * Empties the entire index.
     *
     * @param taskCreationDate creation date of the original {@link Task}.
     */
    protected void unindexAll(DateTime taskCreationDate) {
        logger.info("Emptying the entire index");
        MultipleTasksException nthe = new MultipleTasksException("An exception occurred while emptying the index");
        Collection<String> indexableSites = indexService.getIndexableSiteIds();

        for (String siteId : indexableSites) {
            unindexSite(siteId, taskCreationDate);
        }

        if (!nthe.isEmpty()) throw nthe;
    }

    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    public void setDocumentProducerRegistry(DocumentProducerRegistry documentProducerRegistry) {
        this.documentProducerRegistry = documentProducerRegistry;
    }
}
