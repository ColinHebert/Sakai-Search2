package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Abstract implementation of {@link EventManager}, handles the registration to events and their interception.
 * <p>
 * This class removes the complexity of {@link NotificationAction} and automatically registers itself for relevant events.
 * </p>
 *
 * @author Colin Hebert
 */
public abstract class AbstractEventManager implements EventManager {
    private static final Logger logger = LoggerFactory.getLogger(AbstractEventManager.class);
    private NotificationEdit notification;
    private NotificationService notificationService;

    public void init() {
        notification = notificationService.addTransientNotification();
        // set the filter to any site related resource
        notification.setAction(this);
        logger.info("The EventManager has now a notification for relevant events");
    }

    @Override
    public void addEventHandler(EventHandler eventHandler) {
        logger.info("Adding '" + eventHandler + "' event types to the list of monitored ones.");
        for (String eventName : eventHandler.getSupportedEventTypes()) {
            notification.addFunction(eventName);
            logger.info("The event '" + eventName + "' is now monitored by the event manager.");
        }
    }

    @Override
    public final void notify(Notification notification, Event event) {
        if (logger.isDebugEnabled())
            logger.debug("The event '" + event + "' has been caught.");
        try {
            notify(event);
        } catch (Exception e) {
            logger.error("An uncaught exception tried to propagate in the event system", e);
        }
    }

    /**
     * Notifies the system of the interception of an {@link Event}.
     *
     * @param event intercepted event
     */
    protected abstract void notify(Event event);

    //-------------------------------------------------------------------------------
    //  The following methods aren't relevant for the EventManager
    //-------------------------------------------------------------------------------

    @Override
    public void set(Element element) {
    }

    @Override
    public void set(NotificationAction notificationAction) {
    }

    @Override
    public NotificationAction getClone() {
        return null;
    }

    @Override
    public void toXml(Element element) {
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
