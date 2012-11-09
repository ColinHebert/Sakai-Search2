package uk.ac.ox.oucs.search2.backwardcompatibility;

import org.sakaiproject.search.api.SearchIndexBuilder;
import uk.ac.ox.oucs.search2.AbstractIndexingService;
import uk.ac.ox.oucs.search2.content.Content;

import java.util.Queue;

/**
 * @author Colin Hebert
 */
public class BackIndexingService extends AbstractIndexingService {
    private SearchIndexBuilder searchIndexBuilder;

    @Override
    public void indexContent(String eventHandlerName, Queue<Content> contents) {
        throw new UnsupportedOperationException("The previous search service doesn't support manual indexation");
    }

    @Override
    public void unindexContent(String eventHandlerName, Queue<Content> contents) {
        throw new UnsupportedOperationException("The previous search service doesn't support manual removal");
    }

    @Override
    public void indexSite(String eventHandlerName, Queue<Content> contents, String site) {
        searchIndexBuilder.refreshIndex(site);
    }

    @Override
    public void reindexSite(String eventHandlerName, Queue<Content> contents, String site) {
        searchIndexBuilder.rebuildIndex(site);
    }

    @Override
    public void unindexSite(String eventHandlerName, String site) {
        throw new UnsupportedOperationException("The previous search service doesn't support manual site removal");
    }

    @Override
    public void indexAll(String eventHandlerName, Queue<Content> contents) {
        searchIndexBuilder.refreshIndex();
    }

    @Override
    public void reindexAll(String eventHandlerName, Queue<Content> contents) {
        searchIndexBuilder.rebuildIndex();
    }

    @Override
    public void unindexAll(String eventHandlerName) {
        throw new UnsupportedOperationException("The previous search service doesn't support manual index cleaning removal");
    }

    public void setSearchIndexBuilder(SearchIndexBuilder searchIndexBuilder) {
        this.searchIndexBuilder = searchIndexBuilder;
    }
}
