package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.indexation.Task;
import uk.ac.ox.oucs.search2.indexation.TaskQueuing;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Default implementation for {@link EventManager}, it will generate and queue {@link Task} for each {@link Event}.
 * <p>
 * When an {@link EventHandler} is added, it is stored and waits for an Event to be intercepted.<br />
 * Once an Event is captured, a Task is generated from each relevant EventHandler and added to the {@link TaskQueuing}.
 * </p>
 *
 * @author Colin Hebert
 */
public class DefaultEventManager extends AbstractEventManager {
    private static final Logger logger = LoggerFactory.getLogger(DefaultEventManager.class);
    /**
     * {@link EventHandler} mapped by event types.
     */
    private Map<String, Collection<EventHandler>> eventHandlers = new HashMap<String, Collection<EventHandler>>();
    /**
     * Queueing system.
     */
    private TaskQueuing taskQueuing;

    @Override
    public void addEventHandler(EventHandler eventHandler) {
        logger.info("Registering '" + eventHandler + "' for future events.");

        super.addEventHandler(eventHandler);
        for (String eventName : eventHandler.getSupportedEventTypes()) {
            Collection<EventHandler> eventHandlers = this.eventHandlers.get(eventName);
            if (eventHandlers == null) {
                eventHandlers = new LinkedList<EventHandler>();
                this.eventHandlers.put(eventName, eventHandlers);
            }
            eventHandlers.add(eventHandler);
        }
    }

    @Override
    protected void notify(Event event) {
        Collection<EventHandler> eventHandlers = this.eventHandlers.get(event.getEvent());

        if (logger.isDebugEnabled())
            logger.debug("The EventHandlers '" + eventHandlers + "' match the event '" + event + "'");

        for (EventHandler eventHandler : eventHandlers) {
            if (eventHandler.isHandled(event)) {
                queueTask(eventHandler.getTask(event));
            } else if (logger.isDebugEnabled()) {
                logger.debug("The EventHandler '" + eventHandler + "' couldn't handle '" + event + "'");
            }
        }
    }

    /**
     * Adds a {@link Task} to the queue.
     *
     * @param task Task to queue.
     */
    protected void queueTask(Task task) {
        taskQueuing.addTaskToQueue(task);
    }

    public void setTaskQueuing(TaskQueuing taskQueuing) {
        this.taskQueuing = taskQueuing;
    }
}
