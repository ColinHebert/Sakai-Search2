package uk.ac.ox.oucs.search2.compatibility.backward.service;

import org.sakaiproject.search.api.SearchIndexBuilder;
import uk.ac.ox.oucs.search2.service.AbstractIndexService;

/**
 * @author Colin Hebert
 */
public class Search2IndexService extends AbstractIndexService {
    private SearchIndexBuilder searchIndexBuilder;

    @Override
    public void indexDocument(String documentReference, boolean now) {
        throw new UnsupportedOperationException("The previous indexing system doesn't support manual indexation");
    }

    @Override
    public void unindexDocument(String documentReference, boolean now) {
        throw new UnsupportedOperationException("The previous indexing system doesn't support manual unindexation");
    }

    @Override
    public void indexSiteDocuments(String siteId, boolean now) {
        searchIndexBuilder.rebuildIndex(siteId);
    }

    @Override
    public void unindexSiteDocuments(String siteId, boolean now) {
        throw new UnsupportedOperationException("The previous indexing system doesn't support site unindexation");
    }

    @Override
    public void indexEveryDocuments(boolean now) {
        searchIndexBuilder.rebuildIndex();
    }

    @Override
    public void unindexEveryDocuments(boolean now) {
        throw new UnsupportedOperationException("The previous indexing system doesn't support complete unindexation");
    }

    public void setSearchIndexBuilder(SearchIndexBuilder searchIndexBuilder) {
        this.searchIndexBuilder = searchIndexBuilder;
    }
}
