package uk.ac.ox.oucs.search2.solr.result;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import uk.ac.ox.oucs.search2.document.Document;
import uk.ac.ox.oucs.search2.result.AbstractSearchResultList;
import uk.ac.ox.oucs.search2.result.SearchResult;
import uk.ac.ox.oucs.search2.result.filter.FilterChain;
import uk.ac.ox.oucs.search2.result.filter.ResultFilter;
import uk.ac.ox.oucs.search2.solr.SolrSchemaConstants;
import uk.ac.ox.oucs.search2.solr.document.SolrDocument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Colin Hebert
 */
public class SolrSearchResultList extends AbstractSearchResultList<QueryResponse> {
    private long numberResultsFound;
    private long startCurrentSelection;
    private String suggestion;

    public SolrSearchResultList(QueryResponse queryResponse) {
        this(queryResponse, Collections.<ResultFilter>emptyList());
    }

    public SolrSearchResultList(QueryResponse queryResponse, Iterable<ResultFilter> searchFilters) {
        super(queryResponse, searchFilters);
        numberResultsFound = queryResponse.getResults().getNumFound();
        startCurrentSelection = queryResponse.getResults().getStart();
        suggestion = extractSuggestion(queryResponse);
    }

    @Override
    protected List<? extends SearchResult> getSearchResults(QueryResponse queryResponse,
                                                            Iterable<ResultFilter> filters) {
        List<SearchResult> searchResults = new ArrayList<SearchResult>(queryResponse.getResults().size());
        long index = 0;

        for (org.apache.solr.common.SolrDocument document : queryResponse.getResults()) {
            String reference = (String) document.getFieldValue(SolrSchemaConstants.REFERENCE_FIELD);
            SolrSearchResult solrResult = extractResult(index++, document,
                    queryResponse.getHighlighting().get(reference));
            SearchResult searchResult = new FilterChain(filters).filter(solrResult);
            searchResults.add(searchResult);
        }
        return searchResults;
    }

    private SolrSearchResult extractResult(long index, org.apache.solr.common.SolrDocument solrDocument,
                                           Map<String, List<String>> highlights) {
        Document document = new SolrDocument(solrDocument);
        double score = (Double) solrDocument.getFieldValue(SolrSchemaConstants.SCORE_FIELD);
        String highlightedText = getText(highlights.get(SolrSchemaConstants.CONTENT_FIELD));
        return new SolrSearchResult(document, score, index, highlightedText);
    }

    @Override
    public long getNumberResultsFound() {
        return numberResultsFound;
    }

    @Override
    public long getStartCurrentSelection() {
        return startCurrentSelection;
    }

    @Override
    public String getSpellCheck() {
        return suggestion;
    }

    private String extractSuggestion(QueryResponse queryResponse) {
        SpellCheckResponse spellCheckResponse = queryResponse.getSpellCheckResponse();
        if (spellCheckResponse == null || !spellCheckResponse.isCorrectlySpelled())
            return null;
        else
            return spellCheckResponse.getCollatedResult();
    }

    private String getText(Iterable<String> highlights) {
        StringBuilder sb = new StringBuilder();
        for (String highlight : highlights)
            sb.append(highlight).append("... ");
        return sb.toString();
    }
}
