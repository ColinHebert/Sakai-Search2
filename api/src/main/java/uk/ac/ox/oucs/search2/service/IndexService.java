package uk.ac.ox.oucs.search2.service;

import org.sakaiproject.site.api.Site;
import uk.ac.ox.oucs.search2.document.Document;

import java.util.Collection;

/**
 * Point of entry for every user order related to the indexation.
 * <p>
 * Each index order must be able to be executed right away or deferred to later.
 * </p>
 *
 * @author Colin Hebert
 */
public interface IndexService {
    /**
     * Indexes a single document based on its reference.
     * <p>
     * If the document was already indexed, replace it with the newer version.
     * </p>
     *
     * @param documentReference reference identifying a document.
     * @param now               indexes the document on the spot if true, queue it for later otherwise.
     */
    void indexDocument(String documentReference, boolean now);

    /**
     * Removes a a single document from the index based on its reference.
     * <p>
     * This call doesn't fail if the document isn't in the index.
     * </p>
     *
     * @param documentReference reference identifying a document.
     * @param now               removes the document on the spot if true, later otherwise.
     */
    void unindexDocument(String documentReference, boolean now);

    /**
     * Indexes an entire site.
     * <p>
     * If the site or a part of the site was already indexed, it will be updated.<br />
     * Obsolete documents belonging to the site will be automatically removed from the index.
     * </p>
     *
     * @param siteId unique identifier of the site.
     * @param now    indexes the site on the spot if true, queue it for later otherwise.
     */
    void indexSiteDocuments(String siteId, boolean now);

    /**
     * Removes every document from a site.
     * <p>
     * Like {@link #unindexDocument(String, boolean)}, removing a site that isn't in the index doesn't fail.
     * </p>
     *
     * @param siteId unique identifier of the site.
     * @param now    removes the site on the spot if true, queue it for later otherwise.
     */
    void unindexSiteDocuments(String siteId, boolean now);

    /**
     * Index every possible document on every sites.
     * <p>
     * If some document were already indexed, they will be updated.<br />
     * Obsolete documents will be automatically removed.
     * </p>
     *
     * @param now reindex every document right away if true, later otherwise.
     */
    void indexEveryDocuments(boolean now);

    /**
     * Remove everything from the index.
     *
     * @param now clean the index on the spot if true, queue it for later otherwise.
     */
    void unindexEveryDocuments(boolean now);

    /**
     * Checks whether a site is indexable or not.
     *
     * @param site site to check.
     * @return true if the site is indexable, false otherwise.
     */
    boolean isSiteIndexable(Site site);

    /**
     * Obtains the identifier for every indexable site.
     *
     * @return every id for indexable sites.
     */
    Collection<String> getIndexableSiteIds();

    /**
     * Obtains the indexable documents present in a site.
     *
     * @param siteId site in which the documents are.
     * @return a collection of indexable documents in a site. An empty collection if the site isn't indexable.
     */
    Collection<Document> getIndexableDocumentsForSite(String siteId);
}
