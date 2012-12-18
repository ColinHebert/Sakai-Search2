package uk.ac.ox.oucs.search2.compatibility.result;

import org.sakaiproject.search.api.SearchResult;
import org.sakaiproject.search.api.TermFrequency;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Transforms a result from search2 into a result for search1
 *
 * @author Colin Hebert
 */
public class Search1SearchResult implements SearchResult {
    private final uk.ac.ox.oucs.search2.result.SearchResult searchResult;

    public Search1SearchResult(uk.ac.ox.oucs.search2.result.SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    @Override
    public float getScore() {
        return (float) searchResult.getScore();
    }

    @Override
    public String getId() {
        return searchResult.getDocument().getId();
    }

    @Override
    public String[] getFieldNames() {
        return searchResult.getDocument().getProperties().keySet().toArray(new String[0]);
    }

    @Override
    public String[] getValues(String string) {
        return searchResult.getDocument().getProperties().get(string).toArray(new String[0]);
    }

    @Override
    public Map<String, String[]> getValueMap() {
        Map<String, String[]> valueMap = new HashMap<String, String[]>();
        for (Map.Entry<String, Collection<String>> entry : searchResult.getDocument().getProperties().entrySet()) {
            valueMap.put(entry.getKey(), entry.getValue().toArray(new String[0]));
        }
        return valueMap;
    }

    @Override
    public String getUrl() {
        return searchResult.getDocument().getUrl();
    }

    @Override
    public String getTitle() {
        return searchResult.getDocument().getTitle();
    }

    @Override
    public int getIndex() {
        return (int) searchResult.getIndex();
    }

    @Override
    public String getSearchResult() {
        return searchResult.getDisplayedText();
    }

    @Override
    public String getReference() {
        return searchResult.getDocument().getReference();
    }

    @Override
    public TermFrequency getTerms() throws IOException {
        return new TermFrequency() {
            @Override
            public String[] getTerms() {
                return new String[0];
            }

            @Override
            public int[] getFrequencies() {
                return new int[0];
            }
        };
    }

    @Override
    public String getTool() {
        return searchResult.getDocument().getTool();
    }

    @Override
    public boolean isCensored() {
        return searchResult.isCensored();
    }

    @Override
    public String getSiteId() {
        return searchResult.getDocument().getSiteId();
    }

    @Override
    public void toXMLString(StringBuilder sb) {
    }

    @Override
    public void setUrl(String newUrl) {
        //TODO: Check when this method is called and if it's relevant to change the URL.
    }

    @Override
    public boolean hasPortalUrl() {
        return searchResult.getDocument().isPortalUrl();
    }
}
