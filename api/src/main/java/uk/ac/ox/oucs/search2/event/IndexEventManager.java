package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.NotificationAction;

/**
 * Class in charge of intercepting {@link org.sakaiproject.event.api.Event} and dispatching them to the right
 * {@link IndexEventHandler}
 *
 * @author Colin Hebert
 */
public interface IndexEventManager extends NotificationAction {
    void addContentEventHandler(IndexEventHandler indexEventHandler);
}
