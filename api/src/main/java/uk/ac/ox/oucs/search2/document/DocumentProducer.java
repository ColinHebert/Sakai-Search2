package uk.ac.ox.oucs.search2.document;

import java.util.Queue;

/**
 * Generate indexable {@link Document} based on the unique reference.
 *
 * @author Colin Hebert
 */
public interface DocumentProducer {
    /**
     * Creates the Document for the given reference.
     *
     * @param reference Reference for a specific element in Sakai.
     * @return a Document containing the content and metadata of the referenced element.
     */
    Document getDocument(String reference);

    /**
     * Obtains all handled Documents within a site.
     * <p>
     * The purpose of this method is to iterate through the Documents of a site with a minimal memory footprint.<br />
     * The only required operations for the resulting {@link Queue} are {@link Queue#peek()} and {@link Queue#poll()()}.
     * </p>
     *
     * @param siteId Site containing various Documents.
     * @return Documents in a Queue.
     */
    Queue<Document> getSiteDocuments(String siteId);

    /**
     * Checks if the reference can be associated with a Document by this DocumentProducer.
     *
     * @param reference Reference to check.
     * @return true if the reference is handled by the current ContentProducer, false otherwise.
     */
    boolean isHandled(String reference);

    /**
     * Checks if the current user is able to read the referenced document.
     *
     * @param reference Reference of the document to check.
     * @return true if the current user can read the document, false otherwise
     */
    boolean isReadable(String reference);
}
