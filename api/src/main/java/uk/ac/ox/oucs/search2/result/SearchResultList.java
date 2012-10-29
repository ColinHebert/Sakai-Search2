package uk.ac.ox.oucs.search2.result;

import java.util.List;
import java.util.Map;

/**
 * Result of a search query, consisting in a list of {@link SearchResult}
 *
 * @author Colin Hebert
 */
public interface SearchResultList extends List<SearchResult> {
    /**
     * Get the number of entries found overall even if paging is done
     *
     * @return number of entries
     */
    long getNumberResultsFound();

    /**
     * Get the position of the first result in the current selection
     *
     * @return the index of the first entry of the current selection
     */
    long getStartCurrentSelection();

    /**
     * Get a suggested search query
     *
     * @return a suggestion for another search query, null if none can be provided
     */
    String getSuggestion();

    /**
     * Get the term frequencies of the current results
     * TODO: The term frequencies on a list of result is deprecated and should be removed
     *
     * @return term frequency for each term in the currently selected results
     */
    @Deprecated
    Map<String, Long> getTermFrequencies();
}
