package uk.ac.ox.oucs.search2.content;

import java.util.Collection;

/**
 * Class to which every {@link ContentProducer} has to register.
 * <p>
 * This allows to obtain a {@link ContentProducer} for a specific reference
 * </p>
 *
 * @author Colin Hebert
 */
public interface ContentProducerRegistry {
    /**
     * Register a new {@link ContentProducer}
     *
     * @param contentProducer contentProducer to register
     */
    void registerContentProducer(ContentProducer contentProducer);

    /**
     * Obtain the content producer able to generate a new {@link Content} for the given reference
     *
     * @param reference reference to an element
     * @return the first contentProducer able to handle the given reference
     */
    ContentProducer getContentProducer(String reference);

    /**
     * Lists all registered ContentProducer
     *
     * @return an immutable Collection of {@link ContentProducer}
     */
    Collection<ContentProducer> getContentProducers();
}
