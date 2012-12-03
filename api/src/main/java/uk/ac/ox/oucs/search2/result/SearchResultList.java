package uk.ac.ox.oucs.search2.result;

import java.util.List;

/**
 * List of results returned by a search query.
 * <p>
 * This list contains a portion of {@link SearchResult} returned during the last search query.<br />
 * This list isn't expected to be exhaustive, and may contain only one page of results.
 * </p>
 *
 * @author Colin Hebert
 */
public interface SearchResultList extends List<SearchResult> {
    /**
     * Gets the number of results found overall.
     * <p>
     * This number ISN'T the number of results in this list but the number of results matching the search query overall.
     * </p>
     *
     * @return number of results.
     */
    long getNumberResultsFound();

    /**
     * Gets the position of the first result in this selection.
     *
     * @return the index overall of the first entry of the current selection.
     */
    long getStartCurrentSelection();

    /**
     * Gets a spellCheck on the current search query.
     * <p>
     * This spellCheck is usually the one used to create the "did you mean: ..." section.<br />
     * The spellchecking system is optional, if it isn't provided this method should always return null.<br />
     * Usually the spellCheck will be the same as the one provided by {@link uk.ac.ox.oucs.search2.SearchService#getSpellCheck(String)}.
     * </p>
     *
     * @return a spellcheck for a better search query, null if it doesn't need to be modified.
     */
    String getSpellCheck();
}
