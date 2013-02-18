package uk.ac.ox.oucs.search2.solr.indexation;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.util.ContentStreamBase;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.document.Document;
import uk.ac.ox.oucs.search2.document.ReaderDocument;
import uk.ac.ox.oucs.search2.document.StreamDocument;
import uk.ac.ox.oucs.search2.document.StringDocument;
import uk.ac.ox.oucs.search2.indexation.AbstractTaskHandler;
import uk.ac.ox.oucs.search2.indexation.DefaultTask;
import uk.ac.ox.oucs.search2.indexation.Task;
import uk.ac.ox.oucs.search2.indexation.exception.MultipleTasksException;
import uk.ac.ox.oucs.search2.indexation.exception.TaskException;
import uk.ac.ox.oucs.search2.indexation.exception.TemporaryTaskException;
import uk.ac.ox.oucs.search2.indexation.exception.UnsupportedTaskException;
import uk.ac.ox.oucs.search2.solr.SolrSchemaConstants;
import uk.ac.ox.oucs.search2.solr.request.ReaderUpdateRequest;
import uk.ac.ox.oucs.search2.tika.document.TikaDocument;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import static uk.ac.ox.oucs.search2.indexation.DefaultTask.Type.*;
import static uk.ac.ox.oucs.search2.solr.indexation.SolrTask.Type.COMMIT;
import static uk.ac.ox.oucs.search2.solr.indexation.SolrTask.Type.OPTIMISE;

/**
 * TaskHandler in charge of making Solr requests.
 * <p>
 * The supported {@link Task} are listed in {@link DefaultTask.Type} and {@link SolrTask.Type}.<br />
 * The SolrTaskHandler handles {@link StreamDocument} either with an embedded Tika, or with
 * <a href="http://wiki.apache.org/solr/ExtractingRequestHandler">SolrCell</a>.
 * </p>
 *
 * @author Colin Hebert
 */
public class SolrTaskHandler extends AbstractTaskHandler {
    private static final Logger logger = LoggerFactory.getLogger(SolrTaskHandler.class);
    /**
     * DateTime format used in Solr requests.
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();
    /**
     * Solr server used for indexation requests.
     */
    private SolrServer solrServer;
    /**
     * Status of SolrCell, if enabled, the TaskHandler will send binary streams directly to the Solr server.
     * If it is disabled, Tika will be run directly by the TaskHandler to obtain a {@link StringDocument}.
     */
    private boolean solrCellEnabled;

    @Override
    public void executeTask(Task task) {
        try {
            super.executeTask(task);
        } catch (UnsupportedTaskException e) {
            String type = task.getType();
            try {
                if (OPTIMISE.getTypeName().equals(type)) {
                    optimise();
                } else if (COMMIT.getTypeName().equals(type)) {
                    // Don't do anything, the commit is done automatically in the finally.
                } else if (IGNORE.getTypeName().equals(type)) {
                    logger.debug("Task '" + task + "', was ignored as expected");
                } else {
                    throw e;
                }
            } catch (Exception ex) {
                throw wrapException(ex, "An exception occurred while handling the task '" + task + "'", task);
            }
        } finally {
            commit();
        }
    }

    /**
     * Indexes a single document.
     * <p>
     * The document is indexed only if the task was created <b>after</b> the last indexation of the document.
     * </p>
     *
     * @param document         the document to index.
     * @param taskCreationDate creation date of the {@link Task}.
     */
    @Override
    protected void indexDocument(Document document, DateTime taskCreationDate) {
        if (logger.isDebugEnabled())
            logger.debug("Add '" + document.getReference() + "' to the index");

        try {
            if (isAlreadyUpToDate(document, taskCreationDate)) {
                if (logger.isDebugEnabled())
                    logger.debug("Indexation not useful as the document was updated earlier");
                return;
            }

            SolrInputDocument solrDocument = generateSolrBaseDocument(document, taskCreationDate);
            SolrRequest indexRequest = createSolrRequest(document, solrDocument);
            logger.debug("Executing the following request '" + indexRequest + "'");
            solrServer.request(indexRequest);
        } catch (Exception e) {
            Task task = new DefaultTask(INDEX_DOCUMENT, taskCreationDate)
                    .setProperty(DefaultTask.DOCUMENT_REFERENCE, document.getReference());
            throw wrapException(e,
                    "An exception occurred while indexing the document '" + document.getReference() + "'", task);
        }
    }

    /**
     * Removes a single document from the index.
     * <p>
     * The document is only removed if the task was created <b>after</b> the last indexation of the document.
     * </p>
     *
     * @param documentReference reference of the document to remove.
     * @param taskCreationDate  creation date of the {@link Task}.
     */
    @Override
    protected void unindexDocument(String documentReference, DateTime taskCreationDate) {
        if (logger.isDebugEnabled())
            logger.debug("Remove '" + documentReference + "' from the index");

        try {
            solrServer.deleteByQuery(SolrSchemaConstants.REFERENCE_FIELD + ":" + ClientUtils.escapeQueryChars(documentReference) + " AND " +
                    SolrSchemaConstants.TIMESTAMP_FIELD + ":{* TO " + DATE_TIME_FORMATTER.print(taskCreationDate) + "}");
        } catch (Exception e) {
            Task task = new DefaultTask(UNINDEX_DOCUMENT, taskCreationDate)
                    .setProperty(DefaultTask.DOCUMENT_REFERENCE, documentReference);
            throw wrapException(e,
                    "An exception occurred while unindexing the document '" + documentReference + "'", task);
        }
    }

    /**
     * Indexes every document available in a site.
     *
     * @param siteId           identifier of the site to index.
     * @param taskCreationDate creation date of the {@link Task}.
     */
    @Override
    protected void indexSite(String siteId, DateTime taskCreationDate) {
        MultipleTasksException mte = new MultipleTasksException(
                "An exception occurred while indexing the site '" + siteId + "'");
        try {
            super.indexSite(siteId, taskCreationDate);
        } catch (TaskException e) {
            mte.addTaskException(e);
        }

        try {
            unindexSite(siteId, taskCreationDate);
        } catch (TaskException e) {
            mte.addTaskException(e);
        }

        if (!mte.isEmpty()) throw mte;
    }

    /**
     * Removes every document in a site.
     * <p>
     * Only documents indexed <b>before</b> the {@link Task} creation date will be removed.
     * </p>
     *
     * @param siteId           identifier of the site to unindex.
     * @param taskCreationDate creation date of the {@code Task}.
     */
    @Override
    protected void unindexSite(String siteId, DateTime taskCreationDate) {
        logger.info("Remove old documents from '" + siteId + "'");

        try {
            solrServer.deleteByQuery(SolrSchemaConstants.SITE_ID_FIELD + ":" + ClientUtils.escapeQueryChars(siteId) + " AND " +
                    SolrSchemaConstants.TIMESTAMP_FIELD + ":{* TO " + DATE_TIME_FORMATTER.print(taskCreationDate) + "}");
        } catch (Exception e) {
            Task task = new DefaultTask(UNINDEX_SITE, taskCreationDate).setProperty(DefaultTask.SITE_ID, siteId);
            throw wrapException(e, "An exception occurred while unindexing the site '" + siteId + "'", task);
        }
    }

    /**
     * Indexes every available documents in a site.
     * <p>
     * This {@link Task} is quite heavy, it's recommended to split it in sub-tasks.
     * </p>
     *
     * @param taskCreationDate creation date of the {@code Task}.
     */
    @Override
    protected void indexAll(DateTime taskCreationDate) {
        MultipleTasksException mte = new MultipleTasksException();
        try {
            super.indexAll(taskCreationDate);
        } catch (TaskException e) {
            mte.addTaskException(e);
        }

        try {
            unindexAll(taskCreationDate);
        } catch (TaskException e) {
            mte.addTaskException(e);
        }

        try {
            optimise();
        } catch (TaskException e) {
            mte.addTaskException(e);
        }

        if (!mte.isEmpty()) throw mte;
    }

    /**
     * Removes every document in the index.
     * <p>
     * Only documents indexed <b>before</b> the {@link Task} creation date will be removed.
     * </p>
     *
     * @param taskCreationDate creation date of the {@code Task}.
     */
    @Override
    protected void unindexAll(DateTime taskCreationDate) {
        logger.info("Remove old documents from every sites");
        try {
            solrServer.deleteByQuery(SolrSchemaConstants.TIMESTAMP_FIELD + ":{* TO " + DATE_TIME_FORMATTER.print(taskCreationDate) + "}");
            optimise();
        } catch (Exception e) {
            Task task = new DefaultTask(UNINDEX_ALL, taskCreationDate);
            throw wrapException(e, "An exception occurred while unindexing everything", task);
        }
    }

    /**
     * Optimises the Solr index.
     * <p>
     * Forces an
     * <a href="http://wiki.apache.org/solr/UpdateXmlMessages#A.22commit.22_and_.22optimize.22">optimisation</a>
     * of the Solr index.
     * </p>
     */
    private void optimise() {
        logger.info("Optimise the index");
        try {
            solrServer.optimize();
        } catch (Exception e) {
            Task task = new SolrTask(OPTIMISE);
            throw wrapException(e, "An exception occurred while optimising the index", task);
        }
    }

    /**
     * Commits the current Solr transaction.
     * <p>
     * Forces a <a href="http://wiki.apache.org/solr/UpdateXmlMessages#A.22commit.22_and_.22optimize.22">commit</a>
     * in the Solr index.
     * </p>
     */
    private void commit() {
        if (logger.isDebugEnabled())
            logger.debug("Commit the current transaction");
        try {
            solrServer.commit();
        } catch (Exception e) {
            Task task = new SolrTask(SolrTask.Type.COMMIT);
            throw wrapException(e, "An exception occurred while committing", task);
        }
    }

    /**
     * Wraps any type of exception in a {@link TaskException}.
     * <p>
     * Some exceptions can be considered as temporary and are wrapped in a {@link TemporaryTaskException}
     * with a {@link Task} to run later.<br />
     * Such exceptions are :
     * <ul>
     * <li>{@link IOException} which are due to a connection problem between the Solr server and Sakai</li>
     * <li>{@link SolrServerException} containing an {@code IOException} (same as above)</li>
     * <li>{@link SolrException} when due to a 503 - Service unavailable</li>
     * </ul>
     * </p>
     * <p>
     * Exceptions that are already considered as {@link TaskException} are simply returned.<br />
     * Other exceptions are wrapped in a {@link TaskException}.
     * </p>
     *
     * @param e       Exception to wrap.
     * @param message Message given to the exception (if a new one is created).
     * @param newTask New task
     * @return
     */
    private TaskException wrapException(Exception e, String message, Task newTask) {
        if (e instanceof SolrServerException && ((SolrServerException) e).getRootCause() instanceof IOException) {
            return new TemporaryTaskException(message, e, newTask);
        } else if (e instanceof SolrException
                && ((SolrException) e).code() == SolrException.ErrorCode.SERVICE_UNAVAILABLE.code) {
            return new TemporaryTaskException(message, e, newTask);
        } else if (e instanceof IOException) {
            return new TemporaryTaskException(message, e, newTask);
        } else if (e instanceof TaskException) {
            return (TaskException) e;
        } else {
            return new TaskException(message, e);
        }
    }

    /**
     * Checks if a newer version of the document has been indexed, based on the creation date of the {@link Task}.
     *
     * @param document         document checked.
     * @param taskCreationDate creation date of the current task.
     * @return true if the index is already up to date, false otherwise.
     * @throws SolrServerException thrown if the verification of the document status failed.
     */
    private boolean isAlreadyUpToDate(Document document, DateTime taskCreationDate) throws SolrServerException {
        if (logger.isDebugEnabled())
            logger.debug("Check if '" + document + "' is up to date");

        // Count the number of documents with an indexation date after the task creation date
        // and with the same reference.
        SolrQuery query = new SolrQuery()
                .setQuery(SolrSchemaConstants.REFERENCE_FIELD + ":" + ClientUtils.escapeQueryChars(document.getReference()) + " AND " +
                        SolrSchemaConstants.TIMESTAMP_FIELD + ":[" + DATE_TIME_FORMATTER.print(taskCreationDate) + " TO *]")
                .setRows(0);
        // If there is a result, then the document is already up to date.
        return solrServer.query(query).getResults().getNumFound() != 0;
    }

    /**
     * Creates the basic {@link SolrInputDocument} used for the indexation request.
     * <p>
     * This document is filled with every property available in the provided {@link Document} except for the actual
     * content.<br />
     * The content should be handled separately as it could come from a {@link java.io.Reader} or a {@link InputStream}.
     * </p>
     *
     * @param document         document to index
     * @param taskCreationDate creation date of the request (used to fill the timestamp)
     * @return a {@code SolrInputDocument} ready to be indexed.
     */
    private SolrInputDocument generateSolrBaseDocument(Document document, DateTime taskCreationDate) {
        SolrInputDocument solrDocument = new SolrInputDocument();
        solrDocument.addField(SolrSchemaConstants.ID_FIELD, document.getId());
        solrDocument.addField(SolrSchemaConstants.TITLE_FIELD, document.getTitle());
        solrDocument.addField(SolrSchemaConstants.REFERENCE_FIELD, document.getReference());
        solrDocument.addField(SolrSchemaConstants.SITE_ID_FIELD, document.getSiteId());
        solrDocument.addField(SolrSchemaConstants.TOOL_FIELD, document.getTool());
        solrDocument.addField(SolrSchemaConstants.CONTAINER_FIELD, document.getContainer());
        solrDocument.addField(SolrSchemaConstants.TYPE_FIELD, document.getType());
        solrDocument.addField(SolrSchemaConstants.SUBTYPE_FIELD, document.getSubtype());
        solrDocument.addField(SolrSchemaConstants.URL_FIELD, document.getUrl());
        solrDocument.addField(SolrSchemaConstants.PORTAL_URL_FIELD, document.isPortalUrl());
        solrDocument.addField(SolrSchemaConstants.TIMESTAMP_FIELD, DATE_TIME_FORMATTER.print(taskCreationDate));

        // Add the custom properties
        for (Map.Entry<String, Collection<String>> entry : document.getProperties().entrySet()) {
            solrDocument.addField(SolrSchemaConstants.PROPERTY_PREFIX + toSolrFieldName(entry.getKey()), entry.getValue());
        }
        return solrDocument;
    }

    /**
     * Creates a SolrRequest based on the type of {@link Document}.
     * <p>
     * The only supported documents are {@link StreamDocument}, {@link ReaderDocument} and {@link StringDocument}.<br />
     * If the document is a {@link StreamDocument}, the binary content can either be parsed through an embedded Tika or
     * with a configured Solr Cell.
     * </p>
     *
     * @param document     document to index.
     * @param solrDocument document indexable by solr.
     * @return a SolrRequest to index the document.
     */
    private SolrRequest createSolrRequest(Document document, SolrInputDocument solrDocument) {
        if (logger.isDebugEnabled())
            logger.debug("Create a solr request to add '" + document + "' to the index");

        if (document instanceof StreamDocument) {
            StreamDocument streamDocument = (StreamDocument) document;
            if (solrCellEnabled) {
                if (logger.isDebugEnabled())
                    logger.debug("Create a SolrCell request");
                return getStreamIndexRequest(streamDocument, solrDocument);
            } else {
                if (logger.isDebugEnabled())
                    logger.debug("Transform the document with tika");
                document = new TikaDocument(streamDocument);
            }
        }

        SolrRequest indexRequest;
        if (document instanceof ReaderDocument) {
            if (logger.isDebugEnabled())
                logger.debug("Create a request with a Reader");
            solrDocument.addField(SolrSchemaConstants.CONTENT_FIELD, ((ReaderDocument) document).getContent());
            indexRequest = new ReaderUpdateRequest().add(solrDocument);
        } else if (document instanceof StringDocument) {
            if (logger.isDebugEnabled())
                logger.debug("Create a request based on a String");
            solrDocument.addField(SolrSchemaConstants.CONTENT_FIELD, ((StringDocument) document).getContent());
            indexRequest = new UpdateRequest().add(solrDocument);
        } else {
            throw new TaskException("Impossible to index '" + document + "'");
        }
        return indexRequest;
    }

    /**
     * Replaces special characters with '_' and switch to lower case for a property name.
     * <p>
     * A property name is a field is Solr and has to respect a convention to work properly.<br />
     * To avoid mistakes, only alphanumerics and underscores are returned.
     * </p>
     *
     * @param propertyName String to filter.
     * @return a filtered name more appropriate to use within solr.
     */
    private String toSolrFieldName(String propertyName) {
        String newPropertyName = propertyName.replaceAll("\\W+", "_").toLowerCase();
        logger.debug("Transformed the '" + propertyName + "' property into: '" + newPropertyName + "'");
        return newPropertyName;
    }

    /**
     * Creates a SolrCell request based on a {@link StreamDocument}.
     * TODO: Is the solr document still required?
     *
     * @param streamDocument Document containing a binary stream.
     * @param solrDocument   solr document
     * @return
     */
    private ContentStreamUpdateRequest getStreamIndexRequest(final StreamDocument streamDocument, SolrInputDocument solrDocument) {
        ContentStreamUpdateRequest contentStreamUpdateRequest = new ContentStreamUpdateRequest(SolrSchemaConstants.SOLR_CELL_PATH);
        contentStreamUpdateRequest.setParam("fmap.content", SolrSchemaConstants.CONTENT_FIELD);
        contentStreamUpdateRequest.setParam("uprefix", SolrSchemaConstants.SOLR_CELL_UPREFIX);
        if (streamDocument.getContentName() != null)
            contentStreamUpdateRequest.setParam("resource.name", streamDocument.getContentName());
        if (streamDocument.getContentType() != null)
            contentStreamUpdateRequest.setParam("stream.contentType", streamDocument.getContentType());
        ContentStreamBase contentStreamBase = new ContentStreamBase() {
            @Override
            public InputStream getStream() throws IOException {
                return streamDocument.getContent();
            }
        };
        contentStreamUpdateRequest.addContentStream(contentStreamBase);

        // Add params as sakai_paramname and add a fmap from sakai_paramname to paramname
        // This workaround is made to avoid field name collision between tika fields and manually set up fields
        // Eg: title generates a fmap.sakai_title=title and is sent over as "sakai_title"
        //    This way, the "title" field generated by tika can be safely prefixed with "tika_"
        for (SolrInputField field : solrDocument) {
            contentStreamUpdateRequest.setParam("fmap.sakai_" + field.getName(), field.getName());
            for (Object o : field) {
                contentStreamUpdateRequest.setParam(SolrSchemaConstants.SOLR_CELL_LITERAL + "sakai_" + field.getName(), o.toString());
            }
        }
        return contentStreamUpdateRequest;
    }

    public void setSolrServer(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    public void setSolrCellEnabled(boolean solrCellEnabled) {
        this.solrCellEnabled = solrCellEnabled;
    }
}
