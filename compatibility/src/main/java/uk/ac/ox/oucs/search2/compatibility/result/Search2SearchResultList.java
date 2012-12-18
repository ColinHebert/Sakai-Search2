package uk.ac.ox.oucs.search2.compatibility.backward.result;

import org.sakaiproject.search.api.SearchList;
import uk.ac.ox.oucs.search2.result.AbstractSearchResultList;
import uk.ac.ox.oucs.search2.result.SearchResult;
import uk.ac.ox.oucs.search2.result.filter.ResultFilter;

import java.util.*;

/**
 * @author Colin Hebert
 */
public class Search2SearchResultList extends AbstractSearchResultList<SearchList> {
    private SearchList results;

    public Search2SearchResultList(SearchList result) {
        super(result);
        results = result;
    }

    @Override
    protected List<? extends SearchResult> getSearchResults(final SearchList result, Iterable<ResultFilter> filters) {
        return new AbstractList<SearchResult>() {
            @Override
            public int size() {
                return result.size();
            }

            @Override
            public SearchResult get(int index) {
                return new Search2SearchResult(result.get(index));
            }
        };
    }

    @Override
    public long getNumberResultsFound() {
        return results.getFullSize();
    }

    @Override
    public long getStartCurrentSelection() {
        return results.getStart();
    }

    @Override
    public String getSpellCheck() {
        return null;
    }
}
