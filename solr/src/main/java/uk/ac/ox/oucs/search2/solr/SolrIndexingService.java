package uk.ac.ox.oucs.search2.solr;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.AbstractIndexingService;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.content.ReaderContent;
import uk.ac.ox.oucs.search2.content.StreamContent;
import uk.ac.ox.oucs.search2.content.StringContent;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * @author Colin Hebert
 */
public class SolrIndexingService extends AbstractIndexingService {
    private final SolrServer solrServer;
    private static final Logger logger = LoggerFactory.getLogger(AbstractIndexingService.class);

    public SolrIndexingService(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    @Override
    public void indexContent(String eventHandlerName, Iterable<Content> contents) {
        try {
            for (Content content : contents) {
                SolrRequest indexRequest;

                SolrInputDocument document = new SolrInputDocument();
                document.addField(SolrSchemaConstants.ID_FIELD, content.getId());
                document.addField(SolrSchemaConstants.TITLE_FIELD, content.getTitle());
                document.addField(SolrSchemaConstants.REFERENCE_FIELD, content.getReference());
                document.addField(SolrSchemaConstants.SITEID_FIELD, content.getSiteId());
                document.addField(SolrSchemaConstants.TOOL_FIELD, content.getTool());
                document.addField(SolrSchemaConstants.CONTAINER_FIELD, content.getContainer());
                document.addField(SolrSchemaConstants.TYPE_FIELD, content.getType());
                document.addField(SolrSchemaConstants.SUBTYPE_FIELD, content.getSubtype());
                document.addField(SolrSchemaConstants.EVENTHANDLER_FIELD, eventHandlerName);
                document.addField(SolrSchemaConstants.TIMESTAMP_FIELD, System.currentTimeMillis());

                //Add the custom properties
                for (Map.Entry<String, Collection<String>> entry : content.getProperties().entrySet()) {
                    document.addField(SolrSchemaConstants.PROPERTY_PREFIX + toSolrFieldName(entry.getKey()), entry.getValue());
                }

                if (content instanceof StreamContent) {
                    indexRequest = null;
                } else if (content instanceof ReaderContent) {
                    document.addField(SolrSchemaConstants.CONTENT_FIELD, ((ReaderContent) content).getContent());
                    //indexRequest = new ReaderUpdateRequest().add(document);
                    indexRequest = null;
                } else if (content instanceof StringContent) {
                    document.addField(SolrSchemaConstants.CONTENT_FIELD, ((StringContent) content).getContent());
                    indexRequest = new UpdateRequest().add(document);
                } else {
                    //TODO: Log/exception??
                    continue;
                }
                solrServer.request(indexRequest);

            }
            solrServer.commit();
        } catch (IOException e) {
            logger.warn("Couldn't execute the request", e);
        } catch (SolrServerException e) {
            logger.error("Can't contact the search server", e);
        }
    }

    @Override
    public void unindexContent(String eventHandlerName, Iterable<Content> contents) {
        try {
            UpdateRequest unindexRequest = new UpdateRequest();
            for (Content content : contents) {
                unindexRequest.deleteById(content.getId());
            }
            solrServer.request(unindexRequest);
            solrServer.commit();
        } catch (IOException e) {
            logger.warn("Couldn't execute the request", e);
        } catch (SolrServerException e) {
            logger.error("Can't contact the search server", e);
        }
    }

    @Override
    public void indexSite(String eventHandlerName, Iterable<Content> contents, String site) {
        indexContent(eventHandlerName, contents);
    }

    @Override
    public void unindexSite(String eventHandlerName, String siteId) {
        logger.info("Removing content for eventHandler '" + eventHandlerName + "' and siteId '" + siteId + "'");
        try {
            solrServer.deleteByQuery(SolrSchemaConstants.EVENTHANDLER_FIELD + ':' + eventHandlerName +
                    " AND " + SolrSchemaConstants.SITEID_FIELD + ':' + siteId);
        } catch (SolrServerException e) {
            logger.warn("Couldn't clean the index for eventHandler '" + eventHandlerName + "' and siteId '" + siteId + "'", e);
        } catch (IOException e) {
            logger.error("Couln't access the solr server", e);
        }
    }

    @Override
    public void indexAll(String eventHandlerName, Iterable<Content> contents) {
        indexContent(eventHandlerName, contents);
    }

    @Override
    public void unindexAll(String eventHandlerName) {
        logger.info("Removing content for eventHandler '" + eventHandlerName + "'");
        try {
            solrServer.deleteByQuery(SolrSchemaConstants.EVENTHANDLER_FIELD + ':' + eventHandlerName);
        } catch (SolrServerException e) {
            logger.warn("Couldn't clean the index for eventHandler '" + eventHandlerName + "'", e);
        } catch (IOException e) {
            logger.error("Couln't access the solr server", e);
        }
    }

    /**
     * Replace special characters, turn to lower case and avoid repetitive '_'
     *
     * @param propertyName String to filter
     * @return a filtered name more appropriate to use with solr
     */
    private static String toSolrFieldName(String propertyName) {
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
}
