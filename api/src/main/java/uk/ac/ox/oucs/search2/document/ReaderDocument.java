package uk.ac.ox.oucs.search2.document;

import java.io.Reader;

/**
 * Document for which the content is accessible through a {@link Reader}
 *
 * @author Colin Hebert
 */
public interface ReaderDocument extends Document {
    /**
     * Gets the document's content
     */
    Reader getContent();
}
