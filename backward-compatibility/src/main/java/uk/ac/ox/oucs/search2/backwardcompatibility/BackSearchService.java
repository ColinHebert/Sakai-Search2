package uk.ac.ox.oucs.search2.backwardcompatibility;

import org.sakaiproject.search.api.InvalidSearchQueryException;
import org.sakaiproject.search.api.SearchList;
import org.sakaiproject.search.api.SearchService;
import uk.ac.ox.oucs.search2.AbstractSearchService;
import uk.ac.ox.oucs.search2.backwardcompatibility.result.BackSearchResultList;
import uk.ac.ox.oucs.search2.filter.SearchFilter;
import uk.ac.ox.oucs.search2.result.SearchResultList;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Colin Hebert
 */
public class BackSearchService extends AbstractSearchService {
    private SearchService searchService;

    @Override
    protected SearchResultList search(String searchQuery, Collection<String> contexts, long start, long length, Iterable<SearchFilter> filterChain) {
        try {
            SearchList searchList = searchService.search(searchQuery, new ArrayList<String>(contexts), (int) start, (int) (start + length), null, null);
            return new BackSearchResultList(searchList, filterChain);
        } catch (InvalidSearchQueryException e) {
            throw new uk.ac.ox.oucs.search2.exception.InvalidSearchQueryException(e);
        }
    }

    @Override
    public String getSpellCheck(String searchQuery) {
        return searchService.getSearchSuggestion(searchQuery);
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
}
