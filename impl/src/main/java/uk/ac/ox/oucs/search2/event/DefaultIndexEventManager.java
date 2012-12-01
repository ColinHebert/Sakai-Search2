package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.IndexingService;
import uk.ac.ox.oucs.search2.task.TaskQueueing;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Colin Hebert
 */
public class DefaultIndexEventManager extends AbstractIndexEventManager {
    private static final Logger logger = LoggerFactory.getLogger(DefaultIndexEventManager.class);
    private Map<String, Collection<IndexEventHandler>> indexEventHandlers = new HashMap<String, Collection<IndexEventHandler>>();
    private TaskQueueing taskQueueing;

    public DefaultIndexEventManager(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    public void addContentEventHandler(IndexEventHandler indexEventHandler) {
        super.addContentEventHandler(indexEventHandler);
        for (String eventName : indexEventHandler.getSupportedEventTypes()) {
            Collection<IndexEventHandler> eventHandlers = indexEventHandlers.get(eventName);
            if (eventHandlers == null) {
                eventHandlers = new LinkedList<IndexEventHandler>();
                indexEventHandlers.put(eventName, eventHandlers);
            }
            eventHandlers.add(indexEventHandler);
        }
    }

    @Override
    protected void notify(Event event) {
        for (IndexEventHandler eventHandler : indexEventHandlers.get(event.getEvent())) {
            handleEvent(event, eventHandler);
        }
    }

    protected void handleEvent(Event event, IndexEventHandler eventHandler) {
        if (!eventHandler.isHandled(event))
            return;

        taskQueueing.addTaskToQueue(eventHandler.getTask(event));
    }

    public void setTaskQueueing(TaskQueueing taskQueueing) {
        this.taskQueueing = taskQueueing;
    }
}
