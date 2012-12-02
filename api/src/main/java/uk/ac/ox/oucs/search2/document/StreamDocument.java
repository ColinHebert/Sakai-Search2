package uk.ac.ox.oucs.search2.document;

import java.io.InputStream;

/**
 * Document for which the content is accessible through an {@link InputStream}
 *
 * @author Colin Hebert
 */
public interface StreamDocument extends Document {
    /**
     * Gets the document's content
     */
    InputStream getContent();
}
