package uk.ac.ox.oucs.search2.compatibility.service;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.Notification;
import org.sakaiproject.search.api.EntityContentProducer;
import org.sakaiproject.search.api.SearchIndexBuilder;
import org.sakaiproject.search.model.SearchBuilderItem;
import uk.ac.ox.oucs.search2.compatibility.document.Search1EntityContentProducer;
import uk.ac.ox.oucs.search2.compatibility.document.Search2DocumentProducer;
import uk.ac.ox.oucs.search2.compatibility.event.Search2EventHandler;
import uk.ac.ox.oucs.search2.document.DocumentProducer;
import uk.ac.ox.oucs.search2.document.DocumentProducerRegistry;
import uk.ac.ox.oucs.search2.event.IndexEventHandler;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Colin Hebert
 */
public class Search1SearchIndexBuilder implements SearchIndexBuilder {
    private Search2EventHandler search2EventHandler;
    private DocumentProducerRegistry documentProducerRegistry;
    private EventTrackingService eventTrackingService;
    private boolean excludeUserSites;
    private boolean onlyIndexSearchToolSites;

    @Override
    public void addResource(Notification notification, Event event) {
        //Events are captured by Search2
    }

    @Override
    public void registerEntityContentProducer(EntityContentProducer ecp) {
        search2EventHandler.addEntityContentProducer(ecp);
        documentProducerRegistry.registerDocumentProducer(new Search2DocumentProducer(ecp));
    }

    @Override
    public void refreshIndex() {
        rebuildIndex();
    }

    @Override
    public void rebuildIndex() {
        eventTrackingService.post(eventTrackingService.newEvent(IndexEventHandler.INDEX_ALL_EVENT, null, false));
    }

    @Override
    public boolean isBuildQueueEmpty() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<EntityContentProducer> getContentProducers() {
        return new AbstractList<EntityContentProducer>() {
            private List<DocumentProducer> documentProducers = new ArrayList<DocumentProducer>(documentProducerRegistry.getDocumentProducers());

            @Override
            public EntityContentProducer get(int index) {
                return new Search1EntityContentProducer(documentProducers.get(index));
            }

            @Override
            public int size() {
                return documentProducers.size();
            }
        };
    }

    @Override
    public void destroy() {
    }

    @Override
    public int getPendingDocuments() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void rebuildIndex(String currentSiteId) {
        eventTrackingService.post(eventTrackingService.newEvent(IndexEventHandler.INDEX_SITE_EVENT, currentSiteId, false));
    }

    @Override
    public void refreshIndex(String currentSiteId) {
        rebuildIndex(currentSiteId);
    }

    @Override
    public List<SearchBuilderItem> getAllSearchItems() {
        return null;
    }

    @Override
    public EntityContentProducer newEntityContentProducer(Event event) {
        return newEntityContentProducer(event.getResource());
    }

    @Override
    public EntityContentProducer newEntityContentProducer(String ref) {
        return new Search1EntityContentProducer(documentProducerRegistry.getDocumentProducer(ref));
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
    public boolean isOnlyIndexSearchToolSites() {
        return onlyIndexSearchToolSites;
    }

    public void setOnlyIndexSearchToolSites(boolean onlyIndexSearchToolSites) {
        this.onlyIndexSearchToolSites = onlyIndexSearchToolSites;
    }

    @Override
    public boolean isExcludeUserSites() {
        return excludeUserSites;
    }

    public void setExcludeUserSites(boolean excludeUserSites) {
        this.excludeUserSites = excludeUserSites;
    }

    public void setSearch2EventHandler(Search2EventHandler search2EventHandler) {
        this.search2EventHandler = search2EventHandler;
    }

    public void setDocumentProducerRegistry(DocumentProducerRegistry documentProducerRegistry) {
        this.documentProducerRegistry = documentProducerRegistry;
    }

    public void setEventTrackingService(EventTrackingService eventTrackingService) {
        this.eventTrackingService = eventTrackingService;
    }
}
