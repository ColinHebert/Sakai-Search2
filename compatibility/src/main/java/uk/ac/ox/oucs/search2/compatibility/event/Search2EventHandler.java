package uk.ac.ox.oucs.search2.compatibility.event;

import org.joda.time.DateTime;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.search.api.EntityContentProducer;
import uk.ac.ox.oucs.search2.event.EventHandler;
import uk.ac.ox.oucs.search2.indexation.DefaultTask;
import uk.ac.ox.oucs.search2.indexation.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Captures event for Search1 registered EntityContentProducers and tries to associate it with
 * the right content producer.
 *
 * @author Colin Hebert
 */
public class Search2EventHandler implements EventHandler {
    private Map<String, Collection<EntityContentProducer>> eventTypes =
            new HashMap<String, Collection<EntityContentProducer>>();
    private Collection<EntityContentProducer> entityContentProducers = new LinkedList<EntityContentProducer>();
    private Search2EventManager eventManager;

    public void init() {
        eventManager.addEventHandler(this);
    }

    public void addEventType(String eventType) {
        if (!eventTypes.containsKey(eventType)) {
            eventTypes.put(eventType, new LinkedList<EntityContentProducer>());
            // Update the event handler with the new events to watch
            eventManager.addEventHandlerForEvent(this, eventType);
        }
    }

    public void addEntityContentProducer(EntityContentProducer entityContentProducer) {
        entityContentProducers.add(entityContentProducer);
    }

    @Override
    public Collection<String> getSupportedEventTypes() {
        return eventTypes.keySet();
    }

    @Override
    public Task getTask(Event event) {
        EntityContentProducer entityProducer = getEntityContentProducerForEvent(event);

        if (entityProducer == null) {
            return new DefaultTask(DefaultTask.Type.IGNORE);
        }

        DefaultTask.Type taskType;
        switch (entityProducer.getAction(event)) {
            case 1: // SearchBuilderItem.ACTION_ADD
                taskType = DefaultTask.Type.INDEX_DOCUMENT;
                break;
            case 2: // SearchBuilderItem.ACTION_DELETE
                taskType = DefaultTask.Type.INDEX_DOCUMENT;
                break;
            default:
                taskType = DefaultTask.Type.IGNORE;
        }

        return new DefaultTask(taskType, new DateTime(event.getEventTime()))
                .setProperty(DefaultTask.DOCUMENT_REFERENCE, event.getResource());
    }

    @Override
    public boolean isHandled(Event event) {
        return getSupportedEventTypes().contains(event.getEvent()) && getEntityContentProducerForEvent(event) != null;
    }

    /**
     * Method that searches through contentProducers to find one matching the event.
     * <p>
     * This methods gets faster over time as it caches successful results in {@link #eventTypes}.
     * </p>
     *
     * @param event event for which an {@link EntityContentProducer} is required.
     * @return a matching {@code EntityContentProducer} or null if nothing was found.
     */
    private EntityContentProducer getEntityContentProducerForEvent(Event event) {
        Collection<EntityContentProducer> contentProducers = eventTypes.get(event.getEvent());
        // Look through already matched content producers
        for (EntityContentProducer contentProducer : contentProducers) {
            if (contentProducer.matches(event))
                return contentProducer;
        }

        // Look through not yet matched content producers (slower but we shouldn't hit this part often).
        for (EntityContentProducer contentProducer : entityContentProducers) {
            if (contentProducer.matches(event)) {
                contentProducers.add(contentProducer);
                return contentProducer;
            }
        }

        return null;
    }

    public void setEventManager(Search2EventManager eventManager) {
        this.eventManager = eventManager;
    }
}
