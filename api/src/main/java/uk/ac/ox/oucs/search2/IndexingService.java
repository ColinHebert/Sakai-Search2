package uk.ac.ox.oucs.search2;

import uk.ac.ox.oucs.search2.content.Content;

import java.util.Queue;

/**
 * This service will be used to add new content to the index once indexing {@link org.sakaiproject.event.api.Event} have
 * been triggered and then intercepted by the {@link uk.ac.ox.oucs.search2.event.IndexEventManager}.
 * <p>
 * As different handlers could generate content for different tools or sites, it's preferable to keep the {@link uk.ac.ox.oucs.search2.event.IndexEventHandler} name
 * to clean the index later. This is why each method interacting will require an eventHandlerName.
 * </p>
 *
 * @author Colin Hebert
 */
public interface IndexingService {
    /**
     * Add a simple content to the search index.
     * <p>
     * As some contents may contain other elements (folder which contain files, etc.) a {@link Queue} is given instead of a single content.
     * If only one entry is useful, use {@link java.util.Collections#singleton(Object)}
     * </p>
     *
     * @param eventHandlerName Name of the {@link uk.ac.ox.oucs.search2.event.IndexEventHandler} responsible for this call
     * @param contents         content to index in a {@link Queue}, usually there will be one element,
     *                         but for some cases it's possible to have more than one element.
     */
    void indexContent(String eventHandlerName, Queue<Content> contents);

    /**
     * Remove a simple content from the search index.
     * <p>
     * As some contents may contain other elements (folder which contain files, etc.) a {@link Queue} is given instead of a single content.
     * </p>
     *
     * @param eventHandlerName Name of the {@link uk.ac.ox.oucs.search2.event.IndexEventHandler} responsible for this call
     * @param contents         content to remove from the index in a {@link Queue}, usually there will be one element,
     *                         but for some cases it's possible to have more than one element.
     */
    void unindexContent(String eventHandlerName, Queue<Content> contents);

    /**
     * Add content from a specific site to the search index.
     * <p>
     * This method should be similar to {@link #indexContent(String, Queue)} in most implementations.
     * </p>
     *
     * @param eventHandlerName Name of the {@link uk.ac.ox.oucs.search2.event.IndexEventHandler} responsible for this call
     * @param contents         content to index in a {@link Queue}.
     * @param site             indexed site.
     */
    void indexSite(String eventHandlerName, Queue<Content> contents, String site);

    /**
     * Remove content related to a site from the search index.
     *
     * @param eventHandlerName Name of the {@link uk.ac.ox.oucs.search2.event.IndexEventHandler} responsible for this call
     * @param site             indexed site.
     */
    void unindexSite(String eventHandlerName, String site);

    /**
     * Add every content available to the search index.
     * <p>
     * This method should be similar to {@link #indexContent(String, Queue)} in most implementations.
     * </p>
     *
     * @param eventHandlerName Name of the {@link uk.ac.ox.oucs.search2.event.IndexEventHandler} responsible for this call
     * @param contents         content to index in a {@link Queue}.
     */
    void indexAll(String eventHandlerName, Queue<Content> contents);

    /**
     * Remove every content available from the search index
     *
     * @param eventHandlerName Name of the {@link uk.ac.ox.oucs.search2.event.IndexEventHandler} responsible for this call
     */
    void unindexAll(String eventHandlerName);
}
