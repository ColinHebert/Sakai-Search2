package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.task.Task;
import uk.ac.ox.oucs.search2.task.TaskQueuing;

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
    private TaskQueuing taskQueuing;

    public DefaultIndexEventManager(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    public void addIndexEventHandler(IndexEventHandler indexEventHandler) {
        super.addIndexEventHandler(indexEventHandler);
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
            addTask(eventHandler.getTask(event));
        }
    }

    protected void addTask(Task task){
        taskQueuing.addTaskToQueue(task);
    }

    public void setTaskQueuing(TaskQueuing taskQueuing) {
        this.taskQueuing = taskQueuing;
    }
}
