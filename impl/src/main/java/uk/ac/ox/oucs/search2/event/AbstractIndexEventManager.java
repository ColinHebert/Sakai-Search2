package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * @author Colin Hebert
 */
public abstract class AbstractIndexEventManager implements IndexEventManager {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIndexEventManager.class);
    private NotificationEdit notification;

    public AbstractIndexEventManager(NotificationService notificationService) {
        notification = notificationService.addTransientNotification();
        // set the filter to any site related resource
        notification.setResourceFilter("/");
        notification.setAction(this);
        logger.info("A notification has been created");
    }

    @Override
    public void addContentEventHandler(IndexEventHandler indexEventHandler) {
        for (String eventName : indexEventHandler.getSupportedEventTypes()) {
            notification.addFunction(eventName);
            logger.info("The event '" + eventName + "' is now monitored by the index event manager.");
        }
    }

    @Override
    public final void notify(Notification notification, Event event) {
        logger.debug("The event '" + event.getResource() + "' has been caught.");
        notify(event);
    }

    protected abstract void notify(Event event);

    //-------------------------------------------------------------------------------
    //  The following methods aren't relevant for the IndexEventManager
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
}
