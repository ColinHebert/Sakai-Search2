package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.Event;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.task.Task;

import java.util.Collection;
import java.util.Queue;

/**
 * Component able to intercept an {@link Event} and provide a {@link Content} and the appropriate course of action
 *
 * @author Colin Hebert
 */
public interface IndexEventHandler {
    /**
     * Get a collection of events handled.
     *
     * @return Every event that can be handled
     */
    Collection<String> getSupportedEventTypes();

    /**
     * Generate a task for the given event
     *
     * @param event event handled
     * @return an executable task
     */
    Task getTask(Event event);

    /**
     * Returns true if and only if the event is handled
     *
     * @param event Event to check
     * @return true if and only if the event is handled
     */
    boolean isHandled(Event event);
}
