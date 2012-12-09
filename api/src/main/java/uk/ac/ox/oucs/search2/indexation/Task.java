package uk.ac.ox.oucs.search2.indexation;

import java.io.Serializable;
import java.util.Date;

/**
 * Indexation task executed by a {@link TaskHandler}.
 * <p>
 * Tasks are lightweight objects representing indexation orders that can be either executed right away or deferred to later.<br />
 * They can be sent across the network to be executed by other servers or to be stored before being executed.<br />
 * For this reason, a Task should always contain the minimum amount of information and be {@link Serializable}
 * (do not create a Task as an anonymous or inner class).
 * </p>
 * TODO: Update the task system to work with sequential unique identifiers across the sakai instance (snowflake?)
 *
 * @author Colin Hebert
 */
public interface Task extends Serializable {
    /**
     * Gets the type of the task, usually a simple unique String, allowing to the TaskHandler to determine what should be done.
     *
     * @return the type of the task which value depends on the search implementation used.
     */
    String getType();

    /**
     * Gets the creation date of the task.
     * <p>
     * It is recommended to use internally the joda-time library instead of the {@link Date} type.
     * </p>
     * <p>
     * The creation date of a Task may help to verify that the tasks are executed in the same order they were created.
     * </p>
     *
     * @return Creation date of the task.
     */
    Date getCreationDate();

    /**
     * Obtains the value of a property stored in the task.
     * <p>
     * Properties are key/values attached to one task, allowing to embed more details that can be used by the
     * TaskHandler during the execution.
     * </p>
     *
     * @param propertyName name of the property.
     * @return th value of the property or null if it wasn't set.
     */
    String getProperty(String propertyName);
}
