package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.Notification;
import org.sakaiproject.event.api.NotificationAction;

/**
 * Component in charge of obtaining events and dispatching them.
 * <p/>
 * The EventManager is expected to act as a register for {@link EventHandler} and forward relevant events
 * to each EventHandler.
 * </p>
 *
 * @author Colin Hebert
 */
public interface EventManager extends NotificationAction {
    /**
     * Adds an EventHandler for the notification of future Events.
     * <p>
     * It is expected that each EventHandler managed will be notified of new relevant events.
     * </p>
     *
     * @param eventHandler eventHandler to add to the system.
     */
    void addEventHandler(EventHandler eventHandler);

    /**
     * {@inheritDoc}
     * <p>
     * This method shouldn't let any exception propagate further.
     * </p>
     */
    @Override
    void notify(Notification notification, Event event);
}
