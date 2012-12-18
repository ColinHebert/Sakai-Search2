package uk.ac.ox.oucs.search2.compatibility.backward.service;

import org.sakaiproject.search.api.InvalidSearchQueryException;
import org.sakaiproject.search.api.SearchService;
import uk.ac.ox.oucs.search2.compatibility.backward.result.Search2SearchResultList;
import uk.ac.ox.oucs.search2.result.EmptySearchResultList;
import uk.ac.ox.oucs.search2.result.SearchResultList;
import uk.ac.ox.oucs.search2.result.filter.ResultFilter;
import uk.ac.ox.oucs.search2.service.AbstractSearchService;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Colin Hebert
 */
public class Search2SearchService extends AbstractSearchService {
    private SearchService searchService;

    @Override
    protected SearchResultList search(String searchQuery, Collection<String> contexts, long start, long length, Iterable<ResultFilter> filterChain) {
        try {
            return new Search2SearchResultList(searchService.search(searchQuery, new ArrayList<String>(contexts), (int) start, (int) (start+length)));
        } catch (InvalidSearchQueryException e) {
            return EmptySearchResultList.instance;
        }
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
}
