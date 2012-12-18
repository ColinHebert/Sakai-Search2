package uk.ac.ox.oucs.search2.compatibility.service;

import org.sakaiproject.search.api.*;
import org.sakaiproject.search.model.SearchBuilderItem;
import uk.ac.ox.oucs.search2.compatibility.event.Search2EventHandler;
import uk.ac.ox.oucs.search2.compatibility.result.Search1SearchList;
import uk.ac.ox.oucs.search2.result.SearchResultList;
import uk.ac.ox.oucs.search2.service.SearchService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Intercepts calls to the Search1 API and redirects them to the Search2 API
 *
 * @author Colin Hebert
 */
public class Search1SearchService implements org.sakaiproject.search.api.SearchService {
    private SearchService actualSearchService;
    private Search2EventHandler search2EventHandler = new Search2EventHandler();
    private SearchIndexBuilder searchIndexBuilder;

    public void init() {

    }

    @Override
    public SearchList search(String searchTerms, List<String> contexts, int searchStart, int searchEnd) throws InvalidSearchQueryException {
        SearchResultList searchResults = actualSearchService.search(searchTerms, contexts, searchStart, searchEnd - searchStart);
        return new Search1SearchList(searchResults);
    }

    @Override
    public SearchList search(String searchTerms, List<String> contexts, int start, int end, String filterName, String sorterName) throws InvalidSearchQueryException {
        //Filters and sorters don't apply here.
        return search(searchTerms, contexts, start, end);
    }

    @Override
    public void registerFunction(String function) {
        search2EventHandler.addEventType(function);
    }

    @Override
    public void reload() {
    }

    @Override
    public void refreshInstance() {
        searchIndexBuilder.refreshIndex();
    }

    @Override
    public void rebuildInstance() {
        searchIndexBuilder.rebuildIndex();
    }

    @Override
    public void refreshSite(String currentSiteId) {
        searchIndexBuilder.refreshIndex(currentSiteId);
    }

    @Override
    public void rebuildSite(String currentSiteId) {
        searchIndexBuilder.rebuildIndex(currentSiteId);
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public int getNDocs() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getPendingDocs() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<SearchBuilderItem> getAllSearchItems() {
        return null;
    }

    @Override
    public List<SearchBuilderItem> getSiteMasterSearchItems() {
        return null;
    }

    @Override
    public List<SearchBuilderItem> getGlobalMasterSearchItems() {
        return null;
    }

    @Override
    public SearchStatus getSearchStatus() {
        return null;
    }

    @Override
    public boolean removeWorkerLock() {
        return false;
    }

    @Override
    public List getSegmentInfo() {
        return null;
    }

    @Override
    public void forceReload() {
    }

    @Override
    public TermFrequency getTerms(int documentId) throws IOException {
        return null;
    }

    @Override
    public String searchXML(Map parameterMap) {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getDigestStoragePath() {
        return null;
    }

    @Override
    public String getSearchSuggestion(String searchString) {
        return actualSearchService.getSpellCheck(searchString);
    }

    @Override
    public void enableDiagnostics() {
    }

    @Override
    public void disableDiagnostics() {
    }

    @Override
    public boolean hasDiagnostics() {
        return false;
    }
}
