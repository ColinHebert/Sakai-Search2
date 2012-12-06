package uk.ac.ox.oucs.search2.service;

import uk.ac.ox.oucs.search2.result.SearchResultList;
import uk.ac.ox.oucs.search2.result.filter.ResultFilter;

import java.util.Collection;
import java.util.List;

/**
 * Service used to do full text search in the Sakai instance.
 *
 * @author Colin Hebert
 */
public interface SearchService {
    /**
     * Obtains a list of results matching a search query in every sites accessible by the current user.
     * <p>
     * The context of the search is {@link Context#EVERY_SITES}.<br />
     * The number of results returned depend on the implementation, but the list starts with the first
     * available result overall.<br />
     * It is expected that the implementation block results that are not accessible by the current user.
     * </p>
     *
     * @param searchQuery a full text query.
     * @return a list of results (empty if there is no results).
     */
    SearchResultList search(String searchQuery);

    /**
     * Obtains a list of results matching a search query within limited context.
     * <p>
     * It is expected that the implementation block results that are not accessible by the current user.
     * </p>
     *
     * @param searchQuery a full text query.
     * @param contexts    contexts (Site Ids) in which the search is done.
     * @return a list of results (empty if there is no results).
     */
    SearchResultList search(String searchQuery, Collection<String> contexts);

    /**
     * Obtains a list of results matching a search query within a limited context.
     * <p>
     * Similar to {@link #search(String, java.util.Collection)} but pre existing {@link Context} can be provided instead.
     * </p>
     *
     * @param searchQuery a full text query.
     * @param context     context (scope) in which the search is done.
     * @return a list of results (empty if there is no results).
     */
    SearchResultList search(String searchQuery, Context context);

    /**
     * Obtains a list of results matching a search query delimited by a start and a number of entries.
     * <p>
     * If the number of results from the {@code start} is smaller than the expected {@code length}, the returned list
     * will have a smaller {@link SearchResultList#size()} than {@code length}.<br />
     * The context of the search os {@link Context#EVERY_SITES}.<br />
     * It is expected that the implementation block results that are not accessible by the current user.
     * </p>
     *
     * @param searchQuery a full text query.
     * @param start       index of the first expected result.
     * @param length      number of results expected.
     * @return a list of results (empty if there is no results).
     */
    SearchResultList search(String searchQuery, long start, long length);

    /**
     * Obtains a list of results matching a search query within a specific context and delimited by a start and a number of entries.
     * <p>
     * It is expected that the implementation block results that are not accessible by the current user.
     * </p>
     *
     * @param searchQuery full text query.
     * @param contexts    contexts (site Ids) in which the search is done.
     * @param start       index of the first expected result.
     * @param length      number of results expected.
     * @return a list of results (empty if there is no results).
     */
    SearchResultList search(String searchQuery, Collection<String> contexts, long start, long length);

    /**
     * Obtains a list of results matching a search query within a specific context and delimited by a start and a number of entries.
     *
     * @param searchQuery full text query.
     * @param context     context (scope) in which the search is done.
     * @param start       index of the first expected result.
     * @param length      number of results expected.
     * @return a list of results (empty if there is no results).
     */
    SearchResultList search(String searchQuery, Context context, long start, long length);

    /**
     * Checks the query against the search engine spellchecker to determine a "did you mean ..." query.
     *
     * @param searchQuery current search query.
     * @return a new search query as a {@code String}, or null if there is no correction available.
     */
    String getSpellCheck(String searchQuery);

    /**
     * Obtains search suggestions provided by the search engine, usually for auto-completion purposes.
     *
     * @param searchString current search query.
     * @return a list of suggestions ordered by relevancy or an empty {@code List} if there is no suggestions available.
     */
    List<String> getSuggestions(String searchString);

    /**
     * Sets  filters applied on results before being returned.
     * <p>
     * For obvious reasons, it is recommended to have an {@link Iterable} able to provide multiple
     * {@link java.util.Iterator} in the same order. ie: {@link List} or {@link java.util.SortedSet}.
     * </p>
     *
     * @param searchFilters filters to apply on each result.
     */
    void setSearchFilters(Iterable<ResultFilter> searchFilters);

    /**
     * Automatically generated search contexts.
     * <p>
     * It is preferable to use automatically an automatically generated context for some search operation.
     * </p>
     */
    public enum Context {
        /**
         * Every site in which the current user is a member.
         */
        SUBSCRIBED_SITES,
        /**
         * Every site accessible to the user, public sites included.
         */
        EVERY_SITES
    }
}
