package uk.ac.ox.oucs.search2.result;

import uk.ac.ox.oucs.search2.result.filter.ResultFilter;

import java.util.Collections;
import java.util.List;

/**
 * @author Colin Hebert
 */
public class EmptySearchResultList extends AbstractSearchResultList<Object> {
    public static SearchResultList instance = new EmptySearchResultList();

    private EmptySearchResultList() {
        super(null);
    }

    @Override
    protected List<? extends SearchResult> getSearchResults(Object result, Iterable<ResultFilter> filters) {
        return Collections.emptyList();
    }

    @Override
    public long getNumberResultsFound() {
        return 0;
    }

    @Override
    public long getStartCurrentSelection() {
        return 0;
    }

    @Override
    public String getSpellCheck() {
        return null;
    }
}
