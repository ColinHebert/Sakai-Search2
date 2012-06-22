package uk.ac.ox.oucs.search2;

import uk.ac.ox.oucs.search2.content.Content;

/**
 * @author Colin Hebert
 */
public interface IndexingService {
    void indexContent(Content content);

    void unindexContent(Content content);

    void indexSite(String siteId);

    void unindexSite(String siteId);

    void indexSiteTool(String siteTool);

    void unindexSiteTool(String siteTool);

    void indexEverything();

    void unindexEverything();
}
