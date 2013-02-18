package uk.ac.ox.oucs.search2.solr.service;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.exception.InvalidSearchQueryException;
import uk.ac.ox.oucs.search2.result.SearchResultList;
import uk.ac.ox.oucs.search2.result.filter.ResultFilter;
import uk.ac.ox.oucs.search2.service.AbstractSearchService;
import uk.ac.ox.oucs.search2.solr.SolrSchemaConstants;
import uk.ac.ox.oucs.search2.solr.result.SolrSearchResultList;

import java.util.Collection;
import java.util.Iterator;

/**
 * Search implementation using solr as an index.
 *
 * @author Colin Hebert
 */
public class SolrSearchService extends AbstractSearchService {
    private static final Logger logger = LoggerFactory.getLogger(SolrSearchService.class);
    private SolrServer solrServer;

    @Override
    protected SearchResultList search(String searchQuery, Collection<String> contexts, long start, long length,
                                      Iterable<ResultFilter> filterChain) {
        try {
            SolrQuery query = new SolrQuery();

            query.setStart((int) start);
            query.setRows((int) length);
            query.setFields("*", "score");

            query.setHighlight(true).setHighlightSnippets(5);
            query.setParam("hl.useFastVectorHighlighter", true);
            query.setParam("hl.mergeContiguous", true);
            query.setParam("hl.fl", SolrSchemaConstants.CONTENT_FIELD);

            if (!contexts.isEmpty()) {
                query.setFilterQueries(createSitesFilterQuery(contexts));
            }

            if (logger.isDebugEnabled())
                logger.debug("Searching with Solr : " + searchQuery);
            query.setQuery(searchQuery);
            QueryResponse rsp = solrServer.query(query);
            return new SolrSearchResultList(rsp, filterChain);
        } catch (SolrServerException e) {
            throw new InvalidSearchQueryException("Failed to parse Query ", e);
        }
    }

    /**
     * Creates a solr filter query containing every site id in the current context.
     *
     * @param contexts site ids of the current search context.
     * @return a solr filter query.
     */
    private String createSitesFilterQuery(Collection<String> contexts) {
        StringBuilder sb = new StringBuilder();
        sb.append('+').append(SolrSchemaConstants.SITE_ID_FIELD).append(":");
        sb.append('(');
        for (Iterator<String> contextIterator = contexts.iterator(); contextIterator.hasNext(); ) {
            sb.append(ClientUtils.escapeQueryChars(contextIterator.next()));
            if (contextIterator.hasNext())
                sb.append(" OR ");
        }
        sb.append(')');
        if (logger.isDebugEnabled())
            logger.debug("Create filter query " + sb);
        return sb.toString();
    }

    public void setSolrServer(SolrServer solrServer) {
        this.solrServer = solrServer;
    }
}
