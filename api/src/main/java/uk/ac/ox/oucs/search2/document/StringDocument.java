package uk.ac.ox.oucs.search2.document;

/**
 * Document for which the content is accessible through a {@link String}
 *
 * @author Colin Hebert
 */
public interface StringDocument extends Document {
    /**
     * Gets the document's content
     */
    String getContent();
}
