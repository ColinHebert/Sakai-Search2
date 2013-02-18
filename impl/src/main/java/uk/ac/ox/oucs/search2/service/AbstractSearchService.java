package uk.ac.ox.oucs.search2.service;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.result.SearchResultList;
import uk.ac.ox.oucs.search2.result.filter.ResultFilter;

import java.util.*;

/**
 * Abstract implementation of the search service.
 * <p>
 * Provides a basic search system and handling the queries context.
 * </p>
 * TODO: Rethink the context system. It may work with a context containing a few sites, but with many it will cause some problems.
 *
 * @author Colin Hebert
 */
public abstract class AbstractSearchService implements SearchService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSearchService.class);
    private Iterable<ResultFilter> searchFilters;
    private int defaultLength = 10;
    private SiteService siteService;

    @Override
    public SearchResultList search(String searchQuery) {
        // TODO: Think of using an infinite searchResultList, creating solr requests on the fly if needed?
        return search(searchQuery, getContextSiteIds(Context.EVERY_SITES), 0, defaultLength, searchFilters);
    }

    @Override
    public SearchResultList search(String searchQuery, Collection<String> contexts) {
        return search(searchQuery, contexts, 0, defaultLength, searchFilters);
    }

    @Override
    public SearchResultList search(String searchQuery, Context context) {
        return search(searchQuery, getContextSiteIds(context), 0, defaultLength, searchFilters);
    }

    @Override
    public SearchResultList search(String searchQuery, long start, long length) {
        return search(searchQuery, getContextSiteIds(Context.EVERY_SITES), start, length, searchFilters);
    }

    @Override
    public SearchResultList search(String searchQuery, Collection<String> contexts, long start, long length) {
        return search(searchQuery, contexts, start, length, searchFilters);
    }

    @Override
    public SearchResultList search(String searchQuery, Context context, long start, long length) {
        return search(searchQuery, getContextSiteIds(context), start, length, searchFilters);
    }

    protected abstract SearchResultList search(String searchQuery, Collection<String> contexts, long start, long length, Iterable<ResultFilter> filterChain);

    @Override
    public String getSpellCheck(String searchQuery) {
        return null;
    }

    @Override
    public List<String> getSuggestions(String searchString) {
        return null;
    }

    /**
     * Obtains the identifiers of sites available withing a specific context.
     *
     * @param context context within the sites are available
     * @return Every site ID in the content.
     */
    private Collection<String> getContextSiteIds(Context context) {
        switch (context) {
            case EVERY_SITES:
                return getAllViewableSites();
            case SUBSCRIBED_SITES:
            default:
                return getAllSubscribedSites();
        }
    }

    /**
     * Obtains the identifiers of every site viewable by the current user.
     *
     * @return a collection of every site identifier viewable by the current user.
     */
    private Collection<String> getAllViewableSites() {
        try {
            logger.info("Finding every site the current user can browse.");
            List<Site> publicSites = siteService.getSites(SiteService.SelectionType.PUBVIEW, null, null, null, null, null);
            // TODO: Check that PUBVIEW and ACCESS aren't redundant
            Collection<String> siteIds = new HashSet<String>(getAllSubscribedSites());
            for (Site site : publicSites) {
                siteIds.add(site.getId());
            }
            if (logger.isDebugEnabled())
                logger.debug("Found " + siteIds.size() + " userSites: " + siteIds);
            return siteIds;
        } catch (Exception e) {
            logger.warn("Couldn't get every site for the current user.", e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtains the identifier of every site in which the user is a member.
     *
     * @return a collection of every site identifier in which the current user is a member.
     */
    private Collection<String> getAllSubscribedSites() {
        try {
            logger.info("Finding every site in which the current user is a member.");
            List<Site> subscribedSites = siteService.getSites(SiteService.SelectionType.ACCESS, null, null, null, null, null);
            List<String> siteIds = new ArrayList<String>(subscribedSites.size() + 1);
            for (Site site : subscribedSites) {
                siteIds.add(site.getId());
            }
            if (logger.isDebugEnabled())
                logger.debug("Found " + siteIds.size() + " userSites: " + siteIds);

            return siteIds;
        } catch (Exception e) {
            logger.warn("Couldn't get every site for the current user.", e);
            return Collections.emptyList();
        }
    }

    @Override
    public void setSearchFilters(Iterable<ResultFilter> searchFilters) {
        this.searchFilters = searchFilters;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setDefaultLength(int defaultLength) {
        this.defaultLength = defaultLength;
    }
}
