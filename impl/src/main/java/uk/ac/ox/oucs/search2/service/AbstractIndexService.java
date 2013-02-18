package uk.ac.ox.oucs.search2.service;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import uk.ac.ox.oucs.search2.document.Document;
import uk.ac.ox.oucs.search2.document.DocumentProducerRegistry;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Colin Hebert
 */
public abstract class AbstractIndexService implements IndexService {
    public static final String SEARCH_TOOL_ID = "sakai.search";
    private SiteService siteService;
    private DocumentProducerRegistry documentProducerRegistry;
    private boolean indexSiteWithSearchToolOnly;
    private boolean excludeUserSites;

    @Override
    public boolean isSiteIndexable(Site site) {
        return !(siteService.isSpecialSite(site.getId())
                || (indexSiteWithSearchToolOnly && site.getToolForCommonId(SEARCH_TOOL_ID) == null)
                || (excludeUserSites && siteService.isUserSite(site.getId())));
    }

    @Override
    public Collection<String> getIndexableSiteIds() {
        Collection<String> indexableSiteIds = new LinkedList<String>();
        List<Site> siteList = siteService.getSites(SiteService.SelectionType.ANY, null, null, null,
                SiteService.SortType.NONE, null);
        for (Site s : siteList) {
            if (isSiteIndexable(s)) {
                indexableSiteIds.add(s.getId());
            }
        }
        return indexableSiteIds;
    }

    @Override
    public Collection<Document> getIndexableDocumentsForSite(String siteId) {
        return new SiteDocumentsQueue(siteId, documentProducerRegistry);
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setIndexSiteWithSearchToolOnly(boolean indexSiteWithSearchToolOnly) {
        this.indexSiteWithSearchToolOnly = indexSiteWithSearchToolOnly;
    }

    public void setExcludeUserSites(boolean excludeUserSites) {
        this.excludeUserSites = excludeUserSites;
    }

    public void setDocumentProducerRegistry(DocumentProducerRegistry documentProducerRegistry) {
        this.documentProducerRegistry = documentProducerRegistry;
    }
}
