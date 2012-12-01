package uk.ac.ox.oucs.search2.service;

/**
 * @author Colin Hebert
 */
public interface IndexService {
    void indexDocument(String documentReference);

    void unindexDocument(String documentReference);

    void queueIndexDocument(String documentReference);

    void queueUnindexDocument(String documentReference);

    void indexSiteDocuments(String siteId);

    void reindexSiteDocuments(String siteId);

    void unindexSiteDocuments(String siteId);

    void queueIndexSiteDocuments(String siteId);

    void queueReindexSiteDocuments(String siteId);

    void queueUnindexSiteDocuments(String siteId);

    void indexEveryDocuments();

    void reindexEveryDocuments();

    void unindexEveryDocuments();

    void queueIndexEveryDocuments();

    void queueReindexEveryDocuments();

    void queueUnindexEveryDocuments();
}
