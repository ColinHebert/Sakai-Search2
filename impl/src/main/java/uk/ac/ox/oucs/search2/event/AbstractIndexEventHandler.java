package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.content.Content;

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
    public Queue<Content> getContent(Event event) {
        IndexAction indexAction = getIndexAction(event);

        switch (indexAction) {
            case INDEX_FILE:
            case UNINDEX_FILE:
                return new LinkedList<Content>(Collections.singleton(getContent(event.getResource())));
            case INDEX_SITE:
            case REINDEX_SITE:
            case UNINDEX_SITE:
                return getSiteContent(getSite(event));
            case INDEX_ALL:
            case REINDEX_ALL:
            case UNINDEX_ALL:
                return getAllContent();
            default:
                logger.warn("Action '" + indexAction + "' isn't supported");
                return new LinkedList<Content>();
        }
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

    @Override
    public String getSite(Event event) {
        return event.getContext();
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
