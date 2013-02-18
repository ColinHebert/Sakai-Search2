package uk.ac.ox.oucs.search2.tika.document;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.document.StreamDocument;
import uk.ac.ox.oucs.search2.document.StringDocument;

import java.util.*;

/**
 * This {@link uk.ac.ox.oucs.search2.document.Document} is a wrapper for {@link StreamDocument} allowing to parse the
 * binary document on the fly.
 * <p>
 * Using {@link Tika} parsers, the {@code Document} provided as an {@code java.io.InputStream}
 * is automatically parsed.<br />
 * The {@link #getProperties()} contains the original properties and the metadata extracted from the {@code Document}.
 * </p>
 *
 * @author Colin Hebert
 */
public class TikaDocument implements StringDocument {
    private static final Logger logger = LoggerFactory.getLogger(TikaDocument.class);
    private static final Tika TIKA = new Tika();
    private final StreamDocument streamDocument;
    private final String documentContent;
    private final Map<String, Collection<String>> properties;

    /**
     * Creates a TikaDocument based on a {@link StreamDocument}.
     *
     * @param streamDocument original document to wrap.
     */
    public TikaDocument(StreamDocument streamDocument) {
        Metadata metadata = new Metadata();
        this.streamDocument = streamDocument;
        this.documentContent = getStreamDocumentContent(streamDocument, metadata);
        this.properties = extractProperties(streamDocument, metadata);
    }

    /**
     * Attempts to extract the content of the document.
     * <p>
     * Using the {@link Tika}, this method will try to parse the document and its metadata.<br />
     * To make the type detection easier, Tika will rely on the document name that can be provided with
     * {@link StreamDocument#getContentName()} and on the type content provided b
     * {@link StreamDocument#getContentType()}.<br />
     * Those methods are optional, if nothing is provided, Tika will try to figure out the content type itself.<br />
     * If Tika can't parse the document, the content will be an empty {@code String}.
     * </p>
     *
     * @param streamDocument original document that should be parsed by Tika.
     * @param metadata       metadata, automatically filled by Tika when the document content is parsed.
     * @return the parsed content, or an empty String if the document can't be parsed.
     */
    private static String getStreamDocumentContent(StreamDocument streamDocument, Metadata metadata) {
        String documentContent = "";
        try {
            // Sets the document name to attempt to determine the content type based on the file extension
            String contentName = streamDocument.getContentName();
            if (contentName != null)
                metadata.add(Metadata.RESOURCE_NAME_KEY, contentName);

            // Sets the content type to simplify type detection
            String contentType = streamDocument.getContentType();
            if (contentType != null)
                metadata.add(Metadata.CONTENT_TYPE, contentType);

            documentContent = TIKA.parseToString(streamDocument.getContent(), metadata);
        } catch (Exception e) {
            logger.warn("Couldn't parse the content of the document", e);
        }
        return documentContent;
    }

    /**
     * Extracts properties of the document and merge with the metadata.
     * <p>
     * The properties of the document are now the properties of the previous document plus the potential metadata
     * extracted during the call to {@link #getStreamDocumentContent(StreamDocument, Metadata)}.
     * </p>
     * TODO: The properties in the parent are expected to be immutable, but the content is expected to be modifiable.
     * TODO: This is probably a mistake. Either both should be immutable or neither.
     * TODO: Make a decision, and either make a deep copy of the existing properties
     * TODO: or make the collections immutable too.
     *
     * @param streamDocument original document containing some properties.
     * @param metadata       metadata obtained during {@link #getStreamDocumentContent(StreamDocument, Metadata)}.
     * @return
     */
    private static Map<String, Collection<String>> extractProperties(StreamDocument streamDocument, Metadata metadata) {
        // The original properties map could be unmodifiable, it would be better to make a copy of it.
        // We assume that the Collection is still mutable.
        Map<String, Collection<String>> properties =
                new HashMap<String, Collection<String>>(streamDocument.getProperties());
        for (String metadataName : metadata.names()) {
            for (String metadataValue : metadata.getValues(metadataName)) {
                Collection<String> property = properties.get(metadataName);
                if (property == null) {
                    property = new LinkedList<String>();
                    properties.put(metadataName, property);
                }
                property.add(metadataValue);
            }
        }

        return properties;
    }

    @Override
    public Map<String, Collection<String>> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public String getContent() {
        return documentContent;
    }

    // Delegated methods

    @Override
    public String getReference() {
        return streamDocument.getReference();
    }

    @Override
    public String getId() {
        return streamDocument.getId();
    }

    @Override
    public String getTitle() {
        return streamDocument.getTitle();
    }

    @Override
    public String getUrl() {
        return streamDocument.getUrl();
    }

    @Override
    public boolean isPortalUrl() {
        return streamDocument.isPortalUrl();
    }

    @Override
    public String getTool() {
        return streamDocument.getTool();
    }

    @Override
    public String getType() {
        return streamDocument.getType();
    }

    @Override
    public String getSiteId() {
        return streamDocument.getSiteId();
    }

    @Override
    public String getContainer() {
        return streamDocument.getContainer();
    }
}
