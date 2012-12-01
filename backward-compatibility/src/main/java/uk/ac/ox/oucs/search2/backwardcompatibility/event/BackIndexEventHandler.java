package uk.ac.ox.oucs.search2.backwardcompatibility.event;

import org.joda.time.DateTime;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.search.api.EntityContentProducer;
import uk.ac.ox.oucs.search2.backwardcompatibility.content.BackContent;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.event.IndexEventHandler;
import uk.ac.ox.oucs.search2.task.DefaultTask;
import uk.ac.ox.oucs.search2.task.Task;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Colin Hebert
 */
public class BackIndexEventHandler implements IndexEventHandler {
    private final EntityContentProducer ecp;

    public BackIndexEventHandler(EntityContentProducer ecp) {
        this.ecp = ecp;
    }

    @Override
    public Collection<String> getSupportedEventTypes() {
        return Collections.emptyList();
    }

    @Override
    public Task getTask(Event event) {
        int action = ecp.getAction(event);
        DateTime creationDate = new DateTime(event.getEventTime());
        switch (action) {
            case 1 /*SearchBuilderItem.ACTION_ADD*/:
                return new DefaultTask(DefaultTask.Type.INDEX_FILE, creationDate).setProperty(DefaultTask.DOCUMENT_REFERENCE, event.getResource());
            case 2 /*SearchBuilderItem.ACTION_DELETE*/:
                return new DefaultTask(DefaultTask.Type.UNINDEX_FILE, creationDate).setProperty(DefaultTask.DOCUMENT_REFERENCE, event.getResource());
            case 10 /*SearchBuilderItem.ACTION_REFRESH*/:
                return new DefaultTask(DefaultTask.Type.INDEX_ALL, creationDate);
            case 11 /*SearchBuilderItem.ACTION_REBUILD*/ :
                return new DefaultTask(DefaultTask.Type.REINDEX_ALL, creationDate);
            default:
                return new DefaultTask(DefaultTask.Type.IGNORE, creationDate);
        }
    }

    @Override
    public IndexAction getIndexAction(Event event) {
        int action = ecp.getAction(event);
        switch (action) {
            case 1 /*SearchBuilderItem.ACTION_ADD*/:
                return IndexAction.INDEX_FILE;
            case 2 /*SearchBuilderItem.ACTION_DELETE*/:
                return IndexAction.UNINDEX_FILE;
            case 10 /*SearchBuilderItem.ACTION_REFRESH*/:
                return IndexAction.INDEX_ALL;
            case 11 /*SearchBuilderItem.ACTION_REBUILD*/ :
                return IndexAction.REINDEX_ALL;
            default:
                //TODO: Log that
                return null;
        }
    }

    @Override
    public String getSite(Event event) {
        return ecp.getSiteId(event.getResource());
    }

    @Override
    public boolean isHandled(Event event) {
        if (!ecp.matches(event))
            return false;

        String reference = event.getResource();
        return reference == null || ecp.isForIndex(reference);
    }

    @Override
    public String getName() {
        return ecp.getClass().getCanonicalName();
    }

    public EntityContentProducer getEntityContentProducer() {
        return ecp;
    }
}
