package uk.ac.ox.oucs.search2.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Simple implementation of {@link DocumentProducerRegistry}, it relies on a Collection of {@link DocumentProducer}.
 *
 * @author Colin Hebert
 */
public class DefaultDocumentProducerRegistry implements DocumentProducerRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDocumentProducerRegistry.class);
    private Collection<DocumentProducer> documentProducers = new ArrayList<DocumentProducer>();

    @Override
    public void registerDocumentProducer(DocumentProducer documentProducer) {
        logger.info("The documentProducer '" + documentProducer + "' has been registered");
        documentProducers.add(documentProducer);
    }

    @Override
    public DocumentProducer getDocumentProducer(String reference) {
        if (logger.isDebugEnabled())
            logger.debug("Looking for a DocumentProducer for '" + reference + "'");
        for (DocumentProducer documentProducer : documentProducers) {
            try {
                if (documentProducer.isHandled(reference)) {
                    if (logger.isDebugEnabled())
                        logger.debug("The documentProducer '" + documentProducer + "' matches '" + reference + "'");
                    return documentProducer;
                }
            } catch (Exception e) {
                logger.warn("The documentProducer '" + documentProducer + "' has thrown an exception", e);
            }
        }
        logger.error("Couldn't find a documentProducer for '" + reference + "'");
        return null;
    }

    @Override
    public Collection<DocumentProducer> getDocumentProducers() {
        return Collections.unmodifiableCollection(documentProducers);
    }
}
