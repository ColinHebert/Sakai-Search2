package uk.ac.ox.oucs.search2.service;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import uk.ac.ox.oucs.search2.document.Document;
import uk.ac.ox.oucs.search2.document.DocumentProducerRegistry;
import uk.ac.ox.oucs.search2.indexation.DefaultTask;
import uk.ac.ox.oucs.search2.indexation.Task;
import uk.ac.ox.oucs.search2.indexation.TaskHandler;
import uk.ac.ox.oucs.search2.indexation.TaskQueuing;

import java.util.Collection;
import java.util.LinkedList;

import static uk.ac.ox.oucs.search2.indexation.DefaultTask.DOCUMENT_REFERENCE;
import static uk.ac.ox.oucs.search2.indexation.DefaultTask.SITE_ID;
import static uk.ac.ox.oucs.search2.indexation.DefaultTask.Type.*;

/**
 * Default implementation of the indexService.
 * <p>
 * Relies on a {@link TaskHandler} to process a {@link Task} directly or sends it to a {@link TaskQueuing} if it can
 * be processed later.
 * </p>
 *
 * @author Colin Hebert
 */
public class DefaultIndexService implements IndexService {
    public static final String SEARCH_TOOL_ID = "sakai.search";
    private TaskHandler taskHandler;
    private TaskQueuing taskQueuing;
    private SiteService siteService;
    private DocumentProducerRegistry documentProducerRegistry;
    private boolean indexSiteWithSearchToolOnly;
    private boolean excludeUserSites;

    @Override
    public void indexDocument(String documentReference, boolean now) {
        Task task = new DefaultTask(INDEX_DOCUMENT).setProperty(DOCUMENT_REFERENCE, documentReference);
        addTask(task, now);
    }

    @Override
    public void unindexDocument(String documentReference, boolean now) {
        Task task = new DefaultTask(UNINDEX_DOCUMENT).setProperty(DOCUMENT_REFERENCE, documentReference);
        addTask(task, now);
    }

    @Override
    public void indexSiteDocuments(String siteId, boolean now) {
        Task task = new DefaultTask(INDEX_SITE).setProperty(SITE_ID, siteId);
        addTask(task, now);
    }

    @Override
    public void unindexSiteDocuments(String siteId, boolean now) {
        Task task = new DefaultTask(UNINDEX_SITE).setProperty(SITE_ID, siteId);
        addTask(task, now);
    }

    @Override
    public void indexEveryDocuments(boolean now) {
        Task task = new DefaultTask(INDEX_ALL);
        addTask(task, now);
    }

    @Override
    public void unindexEveryDocuments(boolean now) {
        Task task = new DefaultTask(UNINDEX_ALL);
        addTask(task, now);
    }

    @Override
    public boolean isSiteIndexable(Site site) {
        return !(siteService.isSpecialSite(site.getId())
                || (indexSiteWithSearchToolOnly && site.getToolForCommonId(SEARCH_TOOL_ID) == null)
                || (excludeUserSites && siteService.isUserSite(site.getId())));
    }

    @Override
    public Collection<String> getIndexableSiteIds() {
        Collection<String> indexableSiteIds = new LinkedList<String>();
        for (Site s : siteService.getSites(SiteService.SelectionType.ANY, null, null, null, SiteService.SortType.NONE, null)) {
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

    /**
     * Add a new {@link Task} by either sending it to a {@link TaskHandler} or a {@link TaskQueuing}
     *
     * @param task Task to add to the system.
     * @param now  true if the task should be handler right away, false if it can wait in the queue.
     */
    private void addTask(Task task, boolean now) {
        if (now)
            taskHandler.executeTask(task);
        else
            taskQueuing.addTaskToQueue(task);
    }

    public void setTaskHandler(TaskHandler taskHandler) {
        this.taskHandler = taskHandler;
    }

    public void setTaskQueuing(TaskQueuing taskQueuing) {
        this.taskQueuing = taskQueuing;
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
