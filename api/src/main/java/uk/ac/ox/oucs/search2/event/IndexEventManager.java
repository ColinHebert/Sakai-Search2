package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.NotificationAction;

/**
 * Class in charge of intercepting {@link org.sakaiproject.event.api.Event} and dispatching them to the right
 * {@link IndexEventHandler}
 *
 * @author Colin Hebert
 */
public interface IndexEventManager extends NotificationAction {
    /**
     * Add a new {@link IndexEventHandler} to the system, making the IndexEventManager aware of the relevant events
     * with {@link IndexEventHandler#getSupportedEventTypes()}
     *
     * @param indexEventHandler eventHandler to add to the system
     */
    void addContentEventHandler(IndexEventHandler indexEventHandler);
}
