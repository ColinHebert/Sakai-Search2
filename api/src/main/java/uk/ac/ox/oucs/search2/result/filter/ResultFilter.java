package uk.ac.ox.oucs.search2.result.filter;

import uk.ac.ox.oucs.search2.result.SearchResult;

/**
 * Filter intercepting SearchResults and wrapping them to change their behaviour if necessary.
 * <p>
 * Multiple filters are applied on the result, each of them applied in a specific order thanks to the {@link FilterChain}.
 * Each filter can intercept the {@link SearchResult} wrap it or interact with it before it's returned to the user.
 * </p>
 *
 * @author Colin Hebert
 */
public interface ResultFilter {
    /**
     * Applies the filter on a result, and pass the result to the next filter in the chain.
     * <p>
     * Call {@link FilterChain#filter(SearchResult)} to continue the chain of filters.<br />
     * Not every filter on the chain must be called, it's the responsibility of the filter to either transfer the
     * result to the next filter or directly return the result.
     * </p>
     *
     * @param searchResult Result to filter.
     * @param filterChain  Chain of filters containing the next filters to apply.
     * @return The filtered result.
     */
    SearchResult filter(SearchResult searchResult, FilterChain filterChain);
}
