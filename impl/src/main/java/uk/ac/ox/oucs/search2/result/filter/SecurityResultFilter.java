package uk.ac.ox.oucs.search2.result.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.document.Document;
import uk.ac.ox.oucs.search2.document.DocumentProducer;
import uk.ac.ox.oucs.search2.document.DocumentProducerRegistry;
import uk.ac.ox.oucs.search2.result.SearchResult;

/**
 * Result filter based on the user's rights to access a document.
 * <p>
 * This filter checks that a user as indeed the right to access a document, censoring every document
 * that shouldn't be displayed.
 * </p>
 *
 * @author Colin Hebert
 */
public class SecurityResultFilter implements ResultFilter {
    private static final Logger logger = LoggerFactory.getLogger(ResultFilter.class);
    private static final SearchResult censoredSearchResult = new SearchResult.CensoredSearchResult();
    private DocumentProducerRegistry documentProducerRegistry;

    @Override
    public SearchResult filter(SearchResult searchResult, FilterChain filterChain) {
        Document document = searchResult.getDocument();
        DocumentProducer documentProducer = documentProducerRegistry.getDocumentProducer(document.getReference());
        if (documentProducer == null) {
            logger.warn("Can't find a content producer for '" + document + "'.");
        }

        if (documentProducer == null || !documentProducer.isReadable(document.getReference())) {
            if (logger.isDebugEnabled())
                logger.debug("The document '" + document + "' has been censored.");
            return censoredSearchResult;
        } else {
            return filterChain.filter(searchResult);
        }
    }

    public void setDocumentProducerRegistry(DocumentProducerRegistry documentProducerRegistry) {
        this.documentProducerRegistry = documentProducerRegistry;
    }
}
