package uk.ac.ox.oucs.search2.event;

import org.joda.time.DateTime;
import org.sakaiproject.event.api.Event;
import uk.ac.ox.oucs.search2.indexation.DefaultTask;
import uk.ac.ox.oucs.search2.indexation.Task;

import java.util.Arrays;
import java.util.Collection;

/**
 * Captures and handles events linked the index.
 *
 * @author Colin Hebert
 */
public class IndexEventHandler implements EventHandler {
    public static final String INDEX_SITE_EVENT =
            truncateEventName(IndexEventHandler.class.getCanonicalName() + ".indexSiteEvent");
    public static final String UNINDEX_SITE_EVENT =
            truncateEventName(IndexEventHandler.class.getCanonicalName() + ".unindexSiteEvent");
    public static final String INDEX_ALL_EVENT =
            truncateEventName(IndexEventHandler.class.getCanonicalName() + ".indexAllEvent");
    public static final String UNINDEX_ALL_EVENT =
            truncateEventName(IndexEventHandler.class.getCanonicalName() + ".unindexAllEvent");
    private static final Collection<String> HANDLED_EVENTS =
            Arrays.asList(INDEX_SITE_EVENT, UNINDEX_SITE_EVENT, INDEX_ALL_EVENT, UNINDEX_ALL_EVENT);

    /**
     * Truncates an event name to be 32 chars max
     *
     * @param eventName eventName to truncate
     * @return
     */
    private static String truncateEventName(String eventName) {
        return (eventName.length() > 32) ? eventName.substring(eventName.length() - 32) : eventName;
    }

    @Override
    public Collection<String> getSupportedEventTypes() {
        return HANDLED_EVENTS;
    }

    @Override
    public Task getTask(Event event) {
        DateTime creationDate = new DateTime(event.getEventTime());
        if (INDEX_SITE_EVENT.equals(event.getEvent()))
            return new DefaultTask(DefaultTask.Type.INDEX_SITE, creationDate)
                    .setProperty(DefaultTask.SITE_ID, event.getResource());
        else if (UNINDEX_SITE_EVENT.equals(event.getEvent()))
            return new DefaultTask(DefaultTask.Type.UNINDEX_SITE, creationDate)
                    .setProperty(DefaultTask.SITE_ID, event.getResource());
        else if (INDEX_ALL_EVENT.equals(event.getEvent()))
            return new DefaultTask(DefaultTask.Type.INDEX_ALL, creationDate);
        else if (UNINDEX_ALL_EVENT.equals(event.getEvent()))
            return new DefaultTask(DefaultTask.Type.UNINDEX_ALL, creationDate);
        else
            return new DefaultTask(DefaultTask.Type.IGNORE);
    }

    @Override
    public boolean isHandled(Event event) {
        return getSupportedEventTypes().contains(event.getEvent());
    }
}
