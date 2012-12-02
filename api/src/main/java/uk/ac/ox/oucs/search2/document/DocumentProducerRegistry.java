package uk.ac.ox.oucs.search2.document;

import java.util.Collection;

/**
 * Registry for every DocumentProducer.
 * <p>
 * This registry allows to keep track of all available {@link DocumentProducer} in the application.
 * </p>
 *
 * @author Colin Hebert
 */
public interface DocumentProducerRegistry {
    /**
     * Registers a DocumentProducer for future uses.
     *
     * @param documentProducer DocumentProducer to register.
     */
    void registerDocumentProducer(DocumentProducer documentProducer);

    /**
     * Obtains the DocumentProducer able to generate a new {@link Document} for the given reference.
     *
     * @param reference Reference to an element in Sakai.
     * @return the DocumentProducer able to create a Document from the given reference.
     */
    DocumentProducer getDocumentProducer(String reference);

    /**
     * Lists all registered DocumentProducer.
     *
     * @return an immutable Collection of {@link DocumentProducer}.
     */
    Collection<DocumentProducer> getDocumentProducers();
}
