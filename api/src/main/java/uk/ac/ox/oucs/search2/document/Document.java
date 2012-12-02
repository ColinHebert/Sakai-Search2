package uk.ac.ox.oucs.search2.document;

import java.util.Collection;
import java.util.Map;

/**
 * Element stored in a search index or about to be indexed.
 * <p>
 * A basic Document only contains metadata about an element available in Sakai. Sub interfaces provide an access to the
 * content of the document in different ways.
 * </p>
 *
 * @author Colin Hebert
 */
public interface Document {
    /**
     * Gets a unique reference for the element in Sakai.
     * <p>
     * This reference is considered as unique across the entire Sakai instance.
     * It could and should be used as the primary key of the indexed document.
     * </p>
     */
    String getReference();

    /**
     * Gets the identifier of the indexed element.
     * <p>
     * This identifier is the one provided by the service in charge of those elements and might not be unique accross
     * the Sakai instance.
     * </p>
     */
    String getId();

    /**
     * Gets the title of the indexed element.
     */
    String getTitle();

    /**
     * Gets an URL for a direct access to the document.
     * <p>
     * The URL might be a portal URL, meaning that the document is accessible through Sakai, or an external URL,
     * meaning that the document is accessible through another site. This status is accessible through {@link #isPortalUrl()}.
     * </p>
     */
    String getUrl();

    /**
     * Gets the status of the URL provided by {@link #getUrl()}.
     *
     * @return true if the URL is a portal URL (internal), false otherwise.
     */
    boolean isPortalUrl();

    /**
     * Gets the tool in which the document is managed.
     */
    String getTool();

    /**
     * Gets the type of the document.
     * <p>
     * Usually the type of document is related to the tool in which the document is.
     * </p>
     */
    String getType();

    /**
     * Gets the document subtype, optional.
     */
    String getSubtype();

    /**
     * Gets the site hosting the document.
     */
    String getSiteId();

    /**
     * Gets the container in which the document can be found.
     */
    String getContainer();

    /**
     * Gets all the additional properties of the document.
     * <p>
     * Those properties are expected to be unmodifiable.
     * </p>
     */
    Map<String, Collection<String>> getProperties();
}
