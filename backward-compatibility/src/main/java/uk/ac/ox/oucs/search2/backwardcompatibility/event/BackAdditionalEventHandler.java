package uk.ac.ox.oucs.search2.backwardcompatibility.event;

import org.joda.time.DateTime;
import org.sakaiproject.event.api.Event;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.event.IndexEventHandler;
import uk.ac.ox.oucs.search2.task.DefaultTask;
import uk.ac.ox.oucs.search2.task.Task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;

/**
 * This implementation MUST NOT be used outside the backward compatible project.
 *
 * @author Colin Hebert
 */
public class BackAdditionalEventHandler implements IndexEventHandler {
    private final static String INDEX_SITE = "search2.index.site";
    private final static String REINDEX_SITE = "search2.reindex.site";
    private final static String INDEX_ALL = "search2.index.all";
    private final static String REINDEX_ALL = "search2.reindex.all";
    public static final Collection<String> SUPPORTED_EVENTS = Arrays.asList(INDEX_SITE, REINDEX_SITE, INDEX_ALL, REINDEX_ALL);

    @Override
    public Collection<String> getSupportedEventTypes() {
        return SUPPORTED_EVENTS;
    }

    @Override
    public Task getTask(Event event) {
        String eventName = event.getEvent();
        DateTime creationDate = new DateTime(event.getEventTime());
        if (INDEX_SITE.equals(eventName)) {
            return new DefaultTask(DefaultTask.Type.INDEX_SITE, creationDate).setProperty(DefaultTask.SITE_ID, getSite(event));
        } else if (REINDEX_SITE.equals(eventName)) {
            return new DefaultTask(DefaultTask.Type.REINDEX_SITE, creationDate).setProperty(DefaultTask.SITE_ID, getSite(event));
        } else if (INDEX_ALL.equals(eventName)) {
            return new DefaultTask(DefaultTask.Type.INDEX_ALL, creationDate);
        } else if (REINDEX_ALL.equals(eventName)) {
            return new DefaultTask(DefaultTask.Type.REINDEX_ALL, creationDate);
        } else {
            return new DefaultTask(DefaultTask.Type.IGNORE, creationDate);
        }
    }

    @Override
    public IndexAction getIndexAction(Event event) {
        String eventName = event.getEvent();
        if (INDEX_SITE.equals(eventName)) {
            return IndexAction.INDEX_SITE;
        } else if (REINDEX_SITE.equals(eventName)) {
            return IndexAction.REINDEX_SITE;
        } else if (INDEX_ALL.equals(eventName)) {
            return IndexAction.INDEX_ALL;
        } else if (REINDEX_ALL.equals(eventName)) {
            return IndexAction.REINDEX_ALL;
        } else {
            return null;
        }
    }

    @Override
    public Queue<Content> getContent(Event event) {
        //Doesn't matter
        return null;
    }

    @Override
    public String getSite(Event event) {
        return event.getResource();
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isHandled(Event event) {
        return SUPPORTED_EVENTS.contains(event.getEvent());
    }
}
