package uk.ac.ox.oucs.search2.solr;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.util.ContentStreamBase;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.content.*;
import uk.ac.ox.oucs.search2.solr.request.ReaderUpdateRequest;
import uk.ac.ox.oucs.search2.solr.task.SolrTask;
import uk.ac.ox.oucs.search2.task.DefaultTask;
import uk.ac.ox.oucs.search2.task.Task;
import uk.ac.ox.oucs.search2.task.TaskHandler;
import uk.ac.ox.oucs.search2.task.exception.MultipleTasksException;
import uk.ac.ox.oucs.search2.task.exception.TaskException;
import uk.ac.ox.oucs.search2.task.exception.TemporaryTaskException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static uk.ac.ox.oucs.search2.solr.task.SolrTask.Type.COMMIT;
import static uk.ac.ox.oucs.search2.solr.task.SolrTask.Type.OPTIMISE;
import static uk.ac.ox.oucs.search2.task.DefaultTask.Type.*;

/**
 * @author Colin Hebert
 */
public class SolrTaskHandler implements TaskHandler {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();
    private static final Logger logger = LoggerFactory.getLogger(SolrTaskHandler.class);
    private SolrServer solrServer;
    private ContentProducerRegistry contentProducerRegistry;

    @Override
    public void executeTask(Task task) {
        SolrServer solrServer = this.solrServer;

        try {
            String type = task.getType();
            DateTime taskCreationDate = new DateTime(task.getCreationDate());

            if (INDEX_DOCUMENT.getTypeName().equals(type)) {
                String documentReference = task.getProperty(DefaultTask.DOCUMENT_REFERENCE);
                ContentProducer contentProducer = contentProducerRegistry.getContentProducer(documentReference);
                indexDocument(contentProducer.getContent(documentReference), taskCreationDate, solrServer);
            } else if (UNINDEX_DOCUMENT.getTypeName().equals(type)) {
                unindexDocument(task.getProperty(DefaultTask.DOCUMENT_REFERENCE), taskCreationDate, solrServer);
            } else if (INDEX_SITE.getTypeName().equals(type)) {
                indexSite(task.getProperty(DefaultTask.SITE_ID), taskCreationDate, solrServer);
            } else if (UNINDEX_SITE.getTypeName().equals(type)) {
                unindexSite(task.getProperty(DefaultTask.SITE_ID), taskCreationDate, solrServer);
            } else if (INDEX_ALL.getTypeName().equals(type)) {
                indexAll(taskCreationDate, solrServer);
            } else if (UNINDEX_ALL.getTypeName().equals(type)) {
                unindexAll(taskCreationDate, solrServer);
            } else if (OPTIMISE.getTypeName().equals(type)) {
                optimise(solrServer);
            } else if (COMMIT.getTypeName().equals(type)) {
                commit(solrServer);
            } else if (IGNORE.getTypeName().equals(type)) {
                logger.debug("Task '" + task + "', was ignored as expected");
            } else {
                throw new TaskException("Unknown task '" + task + "'");
            }

            commit(solrServer);

        } catch (Exception e) {
            throw handleException(e, "An exception occurred while handling the task '" + task + "'", task);
        }
    }

    private void indexDocument(Content document, DateTime taskCreationDate, SolrServer solrServer) {
        try {
            SolrInputDocument solrDocument = generateSolrBaseDocument(document, taskCreationDate);
            SolrRequest indexRequest;

            if (document instanceof StreamContent) {
                indexRequest = getStreamIndexRequest(solrDocument, ((StreamContent) document).getContent());
            } else if (document instanceof ReaderContent) {
                solrDocument.addField(SolrSchemaConstants.CONTENT_FIELD, ((ReaderContent) document).getContent());
                indexRequest = new ReaderUpdateRequest().add(solrDocument);
            } else if (document instanceof StringContent) {
                solrDocument.addField(SolrSchemaConstants.CONTENT_FIELD, ((StringContent) document).getContent());
                indexRequest = new UpdateRequest().add(solrDocument);
            } else {
                throw new TaskException("Impossible to index '" + document + "'");
            }
            solrServer.request(indexRequest);
        } catch (Exception e) {
            Task task = new DefaultTask(INDEX_DOCUMENT, taskCreationDate).setProperty(DefaultTask.DOCUMENT_REFERENCE, document.getReference());
            throw handleException(e, "An exception occurred while indexing the document '" + document + "'", task);
        }
    }

    private void unindexDocument(String documentReference, DateTime taskCreationDate, SolrServer solrServer) {
        try {
            solrServer.deleteByQuery(SolrSchemaConstants.REFERENCE_FIELD + ":" + ClientUtils.escapeQueryChars(documentReference) + " AND " +
                    SolrSchemaConstants.TIMESTAMP_FIELD + ":{* TO " + DATE_TIME_FORMATTER.print(taskCreationDate) + "}");
        } catch (Exception e) {
            Task task = new DefaultTask(UNINDEX_DOCUMENT, taskCreationDate).setProperty(DefaultTask.DOCUMENT_REFERENCE, documentReference);
            throw handleException(e, "An exception occurred while unindexing the document '" + documentReference + "'", task);
        }
    }

    private void indexSite(String siteId, DateTime taskCreationDate, SolrServer solrServer) {
        Queue<Content> documents = getSiteDocuments(siteId);
        MultipleTasksException multipleTasksException = new MultipleTasksException();
        while (documents.peek() != null) {
            try {
                indexDocument(documents.poll(), taskCreationDate, solrServer);
            } catch (TaskException e) {
                multipleTasksException.addTaskException(e);
            }
        }

        try {
            unindexSite(siteId, taskCreationDate, solrServer);
        } catch (TaskException e) {
            multipleTasksException.addTaskException(e);
        }

        if (!multipleTasksException.isEmpty())
            throw multipleTasksException;
    }

    private void unindexSite(String siteId, DateTime taskCreationDate, SolrServer solrServer) {
        try {
            solrServer.deleteByQuery(SolrSchemaConstants.SITE_ID_FIELD + ":" + ClientUtils.escapeQueryChars(siteId) + " AND " +
                    SolrSchemaConstants.TIMESTAMP_FIELD + ":{* TO " + DATE_TIME_FORMATTER.print(taskCreationDate) + "}");
        } catch (Exception e) {
            Task task = new DefaultTask(UNINDEX_SITE, taskCreationDate).setProperty(DefaultTask.SITE_ID, siteId);
            throw handleException(e, "An exception occurred while unindexing the site '" + siteId + "'", task);
        }
    }

    private void indexAll(DateTime taskCreationDate, SolrServer solrServer) {
        //TODO :get sitesIds
        Queue<String> siteIds = null;
        MultipleTasksException multipleTasksException = new MultipleTasksException();
        while (siteIds.peek() != null) {
            try {
                indexSite(siteIds.poll(), taskCreationDate, solrServer);
            } catch (TaskException e) {
                multipleTasksException.addTaskException(e);
            }
        }

        try {
            unindexAll(taskCreationDate, solrServer);
        } catch (TaskException e) {
            multipleTasksException.addTaskException(e);
        }

        if (!multipleTasksException.isEmpty())
            throw multipleTasksException;
    }

    private void unindexAll(DateTime taskCreationDate, SolrServer solrServer) {
        try {
            solrServer.deleteByQuery(SolrSchemaConstants.TIMESTAMP_FIELD + ":{* TO " + DATE_TIME_FORMATTER.print(taskCreationDate) + "}");
        } catch (Exception e) {
            Task task = new DefaultTask(UNINDEX_ALL, taskCreationDate);
            throw handleException(e, "An exception occurred while unindexing everything", task);
        }
    }

    private void optimise(SolrServer solrServer) {
        try {
            solrServer.optimize();
        } catch (Exception e) {
            Task task = new SolrTask(OPTIMISE);
            throw handleException(e, "An exception occurred while optimising the index", task);
        }
    }

    private void commit(SolrServer solrServer) {
        try {
            solrServer.commit();
        } catch (Exception e) {
            Task task = new SolrTask(SolrTask.Type.COMMIT);
            throw handleException(e, "An exception occurred while committing", task);
        }
    }

    private TaskException handleException(Exception e, String message, Task newTask) {
        Throwable rootException = e;
        if (e instanceof SolrServerException) {
            rootException = ((SolrServerException) e).getRootCause();
        }

        if (rootException instanceof IOException)
            return new TemporaryTaskException(message, e, newTask);
        else if (e instanceof TaskException)
            return (TaskException) e;
        else
            return new TaskException(message, e);
    }

    private SolrInputDocument generateSolrBaseDocument(Content content, DateTime taskCreationDate) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(SolrSchemaConstants.ID_FIELD, content.getId());
        document.addField(SolrSchemaConstants.TITLE_FIELD, content.getTitle());
        document.addField(SolrSchemaConstants.REFERENCE_FIELD, content.getReference());
        document.addField(SolrSchemaConstants.SITE_ID_FIELD, content.getSiteId());
        document.addField(SolrSchemaConstants.TOOL_FIELD, content.getTool());
        document.addField(SolrSchemaConstants.CONTAINER_FIELD, content.getContainer());
        document.addField(SolrSchemaConstants.TYPE_FIELD, content.getType());
        document.addField(SolrSchemaConstants.SUBTYPE_FIELD, content.getSubtype());
        document.addField(SolrSchemaConstants.URL_FIELD, content.getUrl());
        document.addField(SolrSchemaConstants.PORTAL_URL_FIELD, content.isPortalUrl());
        document.addField(SolrSchemaConstants.TIMESTAMP_FIELD, DATE_TIME_FORMATTER.print(taskCreationDate));

        //Add the custom properties
        for (Map.Entry<String, Collection<String>> entry : content.getProperties().entrySet()) {
            document.addField(SolrSchemaConstants.PROPERTY_PREFIX + toSolrFieldName(entry.getKey()), entry.getValue());
        }
        return document;
    }

    /**
     * Replace special characters, turn to lower case and avoid repetitive '_'
     *
     * @param propertyName String to filter
     * @return a filtered name more appropriate to use with solr
     */
    private String toSolrFieldName(String propertyName) {
        StringBuilder sb = new StringBuilder(propertyName.length());
        boolean lastUnderscore = false;
        for (Character c : propertyName.toLowerCase().toCharArray()) {
            if ((c < 'a' || c > 'z') && (c < '0' || c > '9'))
                c = '_';
            if (!lastUnderscore || c != '_')
                sb.append(c);
            lastUnderscore = (c == '_');
        }
        logger.debug("Transformed the '" + propertyName + "' property into: '" + sb + "'");
        return sb.toString();
    }

    private ContentStreamUpdateRequest getStreamIndexRequest(SolrInputDocument document, final InputStream contentStrean) {
        ContentStreamUpdateRequest contentStreamUpdateRequest = new ContentStreamUpdateRequest(SolrSchemaConstants.SOLR_CELL_PATH);
        contentStreamUpdateRequest.setParam("fmap.content", SolrSchemaConstants.CONTENT_FIELD);
        contentStreamUpdateRequest.setParam("uprefix", SolrSchemaConstants.SOLR_CELL_UPREFIX);
        ContentStreamBase contentStreamBase = new ContentStreamBase() {
            @Override
            public InputStream getStream() throws IOException {
                return ((StreamContent) contentStrean).getContent();
            }
        };
        contentStreamUpdateRequest.addContentStream(contentStreamBase);

        //Add params as sakai_paramname and add a fmap from sakai_paramname to paramname
        //This workaround is made to avoid field name collision between tika fields and manually set up fields
        //Eg: title generates a fmap.sakai_title=title and is sent over as "sakai_title"
        //    This way, the "title" field generated by tika can be safely prefixed with "tika_"
        for (SolrInputField field : document) {
            contentStreamUpdateRequest.setParam("fmap.sakai_" + field.getName(), field.getName());
            for (Object o : field) {
                contentStreamUpdateRequest.setParam(SolrSchemaConstants.SOLR_CELL_LITERAL + "sakai_" + field.getName(), o.toString());
            }
        }
        return contentStreamUpdateRequest;
    }

    private Queue<Content> getSiteDocuments(final String siteId) {
        return new AbstractQueue<Content>() {
            private Iterator<ContentProducer> contentProducerIterator = contentProducerRegistry.getContentProducers().iterator();
            private Queue<Content> contentQueue;
            private Content nextContent = popContent();

            @Override
            public Iterator<Content> iterator() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int size() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean offer(Content content) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Content poll() {
                Content content = nextContent;
                nextContent = popContent();
                return content;
            }

            @Override
            public Content peek() {
                return nextContent;
            }

            private Content popContent() {
                if (contentQueue == null) {
                    if (contentProducerIterator.hasNext()) {
                        contentQueue = contentProducerIterator.next().getSiteContents(siteId);
                    } else return null;
                }

                if (contentQueue.peek() == null) {
                    contentQueue = null;
                    return popContent();
                } else {
                    return contentQueue.poll();
                }
            }
        };
    }

    public void setSolrServer(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    public void setContentProducerRegistry(ContentProducerRegistry contentProducerRegistry) {
        this.contentProducerRegistry = contentProducerRegistry;
    }
}
