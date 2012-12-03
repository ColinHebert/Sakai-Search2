package uk.ac.ox.oucs.search2.result.filter;

import uk.ac.ox.oucs.search2.result.SearchResult;

import java.util.Iterator;

/**
 * Chain of filters to apply on a unique Result.
 * <p>
 * Each chain is an iteration on multiple {@link ResultFilter} calling the next filter.<br />
 * A FilterChain can be applied on only one {@link SearchResult} and shouldn't be reused.
 * </p>
 *
 * @author Colin Hebert
 */
public class FilterChain {
    private final Iterator<ResultFilter> filterIterator;

    /**
     * Creates a FilterChain based on a an iteration of filters.
     *
     * @param resultFilters filters to apply.
     */
    public FilterChain(Iterable<ResultFilter> resultFilters) {
        filterIterator = resultFilters.iterator();
    }

    /**
     * Applies the next filter on the SearchResult.
     * <p>
     * If there is no more filters to apply returns the current value as is.
     * </p>
     *
     * @param searchResult Result to filter.
     * @return A filtered result or the current value if there is no more filters to apply.
     */
    public SearchResult filter(SearchResult searchResult) {
        if (filterIterator.hasNext())
            return filterIterator.next().filter(searchResult, this);
        else
            return searchResult;
    }
}
