package uk.ac.ox.oucs.search2.filter;

import uk.ac.ox.oucs.search2.result.SearchResult;

import java.util.Iterator;

/**
 * Result filter chain, it allows to apply {@link SearchFilter} on every {@link SearchResult} for security or diverse
 * reasons.
 *
 * @author Colin Hebert
 */
public class FilterChain {
    private final Iterator<SearchFilter> searchFilterIterator;

    /**
     * Create a FilterChain based on a list of filters
     *
     * @param searchFilters filters to apply
     */
    public FilterChain(Iterable<SearchFilter> searchFilters) {
        searchFilterIterator = searchFilters.iterator();
    }

    /**
     * Apply the next available filter in the list or return the current value if there is no filters left.
     *
     * @param searchResult Result to filter
     * @return filtered result or the given SearchResult if there is no filters left.
     */
    public SearchResult filter(SearchResult searchResult) {
        if (searchFilterIterator.hasNext())
            return searchFilterIterator.next().filter(searchResult, this);
        else
            return searchResult;
    }
}
