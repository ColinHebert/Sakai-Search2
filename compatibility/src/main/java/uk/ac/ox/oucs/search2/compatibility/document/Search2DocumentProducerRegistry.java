package uk.ac.ox.oucs.search2.compatibility.document;

import org.sakaiproject.search.api.SearchIndexBuilder;
import uk.ac.ox.oucs.search2.document.DefaultDocumentProducerRegistry;
import uk.ac.ox.oucs.search2.document.DocumentProducer;

/**
 * @author Colin Hebert
 */
public class Search2DocumentProducerRegistry extends DefaultDocumentProducerRegistry {
    private SearchIndexBuilder searchIndexBuilder;

    @Override
    public void registerDocumentProducer(DocumentProducer documentProducer) {
        super.registerDocumentProducer(documentProducer);
        searchIndexBuilder.registerEntityContentProducer(new Search1EntityContentProducer(documentProducer));
    }

    public void setSearchIndexBuilder(SearchIndexBuilder searchIndexBuilder) {
        this.searchIndexBuilder = searchIndexBuilder;
    }
}
