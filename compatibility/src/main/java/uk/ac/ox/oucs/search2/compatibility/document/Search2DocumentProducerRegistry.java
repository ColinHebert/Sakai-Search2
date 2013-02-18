package uk.ac.ox.oucs.search2.compatibility.document;

import org.sakaiproject.search.api.SearchIndexBuilder;
import uk.ac.ox.oucs.search2.compatibility.event.Search2EventManager;
import uk.ac.ox.oucs.search2.document.DefaultDocumentProducerRegistry;
import uk.ac.ox.oucs.search2.document.DocumentProducer;

/**
 * @author Colin Hebert
 */
public class Search2DocumentProducerRegistry extends DefaultDocumentProducerRegistry {
    private SearchIndexBuilder searchIndexBuilder;
    private Search2EventManager search2EventManager;

    @Override
    public void registerDocumentProducer(DocumentProducer documentProducer) {
        super.registerDocumentProducer(documentProducer);
        searchIndexBuilder.registerEntityContentProducer(
                new Search1EntityContentProducer(documentProducer, search2EventManager));
    }

    public void setSearchIndexBuilder(SearchIndexBuilder searchIndexBuilder) {
        this.searchIndexBuilder = searchIndexBuilder;
    }

    public void setSearch2EventManager(Search2EventManager search2EventManager) {
        this.search2EventManager = search2EventManager;
    }
}
