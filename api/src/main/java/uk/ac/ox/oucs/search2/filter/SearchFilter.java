package uk.ac.ox.oucs.search2.filter;

import uk.ac.ox.oucs.search2.result.SearchResult;

/**
 * Filter a {@link SearchResult} in order to change its behaviour if necessary
 *
 * @author Colin Hebert
 */
public interface SearchFilter {
    /**
     * Apply the filter on a {@link SearchResult}, and pass the result to the next filter in the chain.
     * <p>
     * Call {@link FilterChain#filter(SearchResult)} to continue the chain of filters
     * </p>
     *
     * @param searchResult Result to filter
     * @param filterChain  Chain of filters containing the next filters to apply
     * @return The filtered result
     */
    SearchResult filter(SearchResult searchResult, FilterChain filterChain);
}
