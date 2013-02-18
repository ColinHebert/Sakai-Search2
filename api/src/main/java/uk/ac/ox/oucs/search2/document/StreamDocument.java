package uk.ac.ox.oucs.search2.document;

import java.io.InputStream;

/**
 * Document for which the content is accessible through an {@link InputStream}.
 * <p>
 * A StreamDocument also provides optional methods for content type detection to help the content parser to determine
 * how the content should be interpreted.
 * </p>
 *
 * @author Colin Hebert
 */
public interface StreamDocument extends Document {
    /**
     * Gets the document's content.
     *
     * @return the content of the document as a Stream.
     */
    InputStream getContent();

    /**
     * Gets the name of the streamed content (optional).
     * <p>
     * The name should be similar to a file name; the extension may help to determine the content type.
     * </p>
     *
     * @return a file name for the streamed content, or null if not provided.
     */
    String getContentName();

    /**
     * Gets the mime type of the streamed content (optional).
     *
     * @return the mime type of the streamed content, or null if not provided.
     */
    String getContentType();
}
