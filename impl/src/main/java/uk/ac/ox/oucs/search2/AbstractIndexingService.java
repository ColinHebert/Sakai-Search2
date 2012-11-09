package uk.ac.ox.oucs.search2;

import uk.ac.ox.oucs.search2.content.Content;

import java.util.Queue;

/**
 * @author Colin Hebert
 */
public abstract class AbstractIndexingService implements IndexingService {
    @Override
    public void indexSite(String eventHandlerName, Queue<Content> contents, String site) {
        indexContent(eventHandlerName, contents);
    }

    @Override
    public void indexAll(String eventHandlerName, Queue<Content> contents) {
        indexContent(eventHandlerName, contents);
    }

    @Override
    public void reindexSite(String eventHandlerName, Queue<Content> contents, String site) {
        unindexSite(eventHandlerName, site);
        indexSite(eventHandlerName, contents, site);
    }

    @Override
    public void reindexAll(String eventHandlerName, Queue<Content> contents) {
        unindexAll(eventHandlerName);
        indexAll(eventHandlerName, contents);
    }
}
