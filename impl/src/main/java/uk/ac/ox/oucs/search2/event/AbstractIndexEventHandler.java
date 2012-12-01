package uk.ac.ox.oucs.search2.event;

import org.joda.time.DateTime;
import org.sakaiproject.event.api.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.task.DefaultTask;
import uk.ac.ox.oucs.search2.task.Task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Basic implementation of {@link IndexEventHandler}
 *
 * @author Colin Hebert
 */
public abstract class AbstractIndexEventHandler implements IndexEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIndexEventHandler.class);

    @Override
    public Task getTask(Event event) {
        IndexAction indexAction = getIndexAction(event);
        Task task;

        switch (indexAction){
            case INDEX_FILE:
                task = new DefaultTask(DefaultTask.Type.INDEX_FILE, new DateTime(event.getEventTime())).setProperty(DefaultTask.DOCUMENT_REFERENCE, event.getResource());
                break;
            case UNINDEX_FILE:
                task = new DefaultTask(DefaultTask.Type.UNINDEX_FILE, new DateTime(event.getEventTime())).setProperty(DefaultTask.DOCUMENT_REFERENCE, event.getResource());
                break;
            case INDEX_SITE:
                task = new DefaultTask(DefaultTask.Type.INDEX_SITE, new DateTime(event.getEventTime())).setProperty(DefaultTask.SITE_ID, event.getContext());
                break;
            case REINDEX_SITE:
                task = new DefaultTask(DefaultTask.Type.REINDEX_SITE, new DateTime(event.getEventTime())).setProperty(DefaultTask.SITE_ID, event.getContext());
                break;
            case UNINDEX_SITE:
                task = new DefaultTask(DefaultTask.Type.UNINDEX_SITE, new DateTime(event.getEventTime())).setProperty(DefaultTask.SITE_ID, event.getContext());
                break;
            case INDEX_ALL:
                task = new DefaultTask(DefaultTask.Type.INDEX_ALL, new DateTime(event.getEventTime()));
                break;
            case REINDEX_ALL:
                task = new DefaultTask(DefaultTask.Type.REINDEX_ALL, new DateTime(event.getEventTime()));
                break;
            case UNINDEX_ALL:
                task = new DefaultTask(DefaultTask.Type.UNINDEX_ALL, new DateTime(event.getEventTime()));
                break;
            default:
                logger.error("Action '" + indexAction + "' isn't supported");
                task = new DefaultTask(DefaultTask.Type.IGNORE, new DateTime(event.getEventTime()));
        }
        return task;
    }

    /**
     * {@inheritDoc}
     * Get the current class canonical name as the unique name
     */
    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    /**
     * {@inheritDoc}
     * An event can he handled if it is one of the event returned by {@link #getSupportedEventTypes()}
     */
    @Override
    public boolean isHandled(Event event) {
        return getSupportedEventTypes().contains(event.getEvent());
    }

    /**
     * Get content from a reference
     *
     * @param reference Reference of the wanted content
     * @return a {@link Content}
     */
    protected abstract Content getContent(String reference);

    /**
     * Get every content possible associated with one site
     *
     * @param siteId Unique identifier of the site
     * @return a {@link Queue} containing every element related to the site
     */
    protected abstract Queue<Content> getSiteContent(String siteId);

    /**
     * Get every possible content to be indexed/unindexed
     *
     * @return a {@link Queue} containing every element handled
     */
    protected abstract Queue<Content> getAllContent();
}
