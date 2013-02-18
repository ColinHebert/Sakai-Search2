package uk.ac.ox.oucs.search2.result;

import uk.ac.ox.oucs.search2.result.filter.ResultFilter;

import java.util.Collections;
import java.util.List;

/**
 * @author Colin Hebert
 */
public final class EmptySearchResultList extends AbstractSearchResultList<Object> {
    private static SearchResultList instance = new EmptySearchResultList();

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

    public static SearchResultList getInstance() {
        return instance;
    }
}
