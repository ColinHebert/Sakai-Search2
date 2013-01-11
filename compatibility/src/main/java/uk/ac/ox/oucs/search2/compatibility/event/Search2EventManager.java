package uk.ac.ox.oucs.search2.compatibility.event;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.search.api.SearchIndexBuilder;
import org.sakaiproject.search.model.SearchBuilderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.document.DocumentProducer;
import uk.ac.ox.oucs.search2.event.AbstractEventManager;
import uk.ac.ox.oucs.search2.event.EventHandler;
import uk.ac.ox.oucs.search2.indexation.DefaultTask;
import uk.ac.ox.oucs.search2.indexation.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Colin Hebert
 */
public class Search2EventManager extends AbstractEventManager {
    private static final Logger logger = LoggerFactory.getLogger(AbstractEventManager.class);
    private SearchIndexBuilder searchIndexBuilder;
    /**
     * {@link EventHandler} mapped by event types.
     */
    private Map<String, Collection<EventHandler>> eventHandlers = new HashMap<String, Collection<EventHandler>>();

    @Override
    public void addEventHandler(EventHandler eventHandler) {
        logger.info("Adding '" + eventHandler + "' event types to the list of monitored ones.");
        for (String eventName : eventHandler.getSupportedEventTypes()) {
            addEventHandlerForEvent(eventHandler, eventName);
        }
        logger.info("Registering '" + eventHandler + "' for future events.");
    }

    protected void addEventHandlerForEvent(EventHandler eventHandler, String eventName) {
        addEventType(eventName);
        logger.info("The event '" + eventName + "' is now monitored by the event manager.");

        Collection<EventHandler> eventHandlers = this.eventHandlers.get(eventName);
        if (eventHandlers == null) {
            eventHandlers = new LinkedList<EventHandler>();
            this.eventHandlers.put(eventName, eventHandlers);
        }
        eventHandlers.add(eventHandler);
    }

    @Override
    public void notify(Event event) {
        Collection<EventHandler> eventHandlers = this.eventHandlers.get(event.getEvent());

        if (logger.isDebugEnabled())
            logger.debug("The EventHandlers '" + eventHandlers + "' match the event '" + event + "'");

        for (EventHandler eventHandler : eventHandlers) {
            if (eventHandler.isHandled(event)) {
                transferEvent(event, eventHandler.getTask(event));
            } else if (logger.isDebugEnabled()) {
                logger.debug("The EventHandler '" + eventHandler + "' couldn't handle '" + event + "'");
            }
        }
    }

    private void transferEvent(Event event, Task task) {
        if (DefaultTask.Type.INDEX_SITE.getTypeName().equals(task.getType()))
            searchIndexBuilder.rebuildIndex(task.getProperty(DefaultTask.SITE_ID));
        else if (DefaultTask.Type.INDEX_ALL.getTypeName().equals(task.getType()))
            searchIndexBuilder.rebuildIndex();
        else if (DefaultTask.Type.INDEX_DOCUMENT.getTypeName().equals(task.getType()) || DefaultTask.Type.UNINDEX_DOCUMENT.getTypeName().equals(task.getType()))
            searchIndexBuilder.addResource(null, event);
    }

    public boolean isProducerForEvent(Event event, DocumentProducer documentProducer) {
        return getTaskForEventAndDocumentProducer(event, documentProducer) != null;
    }

    public int getActionForEvent(Event event, DocumentProducer documentProducer) {
        Task task = getTaskForEventAndDocumentProducer(event, documentProducer);

        if(task == null)
            return SearchBuilderItem.ACTION_UNKNOWN;

        if (DefaultTask.Type.INDEX_DOCUMENT.getTypeName().equals(task.getType())) {
            return SearchBuilderItem.ACTION_ADD;
        } else if (DefaultTask.Type.UNINDEX_DOCUMENT.getTypeName().equals(task.getType())) {
            return SearchBuilderItem.ACTION_DELETE;
        } else
            return SearchBuilderItem.ACTION_UNKNOWN;
    }

    private Task getTaskForEventAndDocumentProducer(Event event, DocumentProducer documentProducer) {
        Collection<EventHandler> eventHandlers = this.eventHandlers.get(event.getEvent());
        for (EventHandler eventHandler : eventHandlers) {
            if (eventHandler.isHandled(event)) {
                Task task = eventHandler.getTask(event);
                String reference = task.getProperty(DefaultTask.DOCUMENT_REFERENCE);
                if(documentProducer.isHandled(reference))
                    return task;
            }
        }
        return null;
    }

    public void setSearchIndexBuilder(SearchIndexBuilder searchIndexBuilder) {
        this.searchIndexBuilder = searchIndexBuilder;
    }
}
