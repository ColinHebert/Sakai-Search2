package uk.ac.ox.oucs.search2.indexation;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of Task, handle basic index operations.
 * <p>
 * This implementation should be the parent class of every other implementations of {@link Task}.<br />
 * It provides a way to set properties during the Task creation, and uses internally the Joda-Time library for creationDate management.
 * </p>
 * <p>
 * An enum {@code Type} is recommended in subclasses of DefaultTask, providing a limited set of possibilities for new Tasks.<br />
 * Even if {@link #getType()} returns a {@code String}, an enumeration provides a type safety when new Tasks are created.
 * </p>
 * <p>
 * It's recommended to create constants for property names, allowing to retrieve the values without any risk of typo.
 * </p>
 *
 * @author Colin Hebert
 */
public class DefaultTask implements Task {
    /**
     * Property name for the reference of a document
     */
    public final static String DOCUMENT_REFERENCE = DefaultTask.class.getCanonicalName() + ".documentReference";
    /**
     * Property name for the unique identifier of a site
     */
    public final static String SITE_ID = DefaultTask.class.getCanonicalName() + ".siteId";
    private final String type;
    private final DateTime creationDate;
    private final Map<String, String> properties = new HashMap<String, String>();

    /**
     * Creates a basic Task with a creation date set automatically.
     *
     * @param type type of the task.
     * @throws NullPointerException thrown if {@code #type} is null.
     */
    public DefaultTask(Type type) {
        this(type, DateTime.now());
    }

    /**
     * Creates a basic Task with a creation date.
     *
     * @param type         type of the task.
     * @param creationDate DateTime of the creation of the task
     * @throws NullPointerException     thrown if {@code #type} is null.
     * @throws IllegalArgumentException thrown if {@code creationDate} is null.
     */
    public DefaultTask(Type type, DateTime creationDate) {
        this(type.getTypeName(), creationDate);
    }

    /**
     * Creates a Task based on the type provided by the subclass.
     *
     * @param type         type of the task.
     * @param creationDate DateTime of the creation of the task.
     * @throws IllegalArgumentException thrown if {@code type} or {@code creationDate} are null.
     */
    protected DefaultTask(String type, DateTime creationDate) {
        if (type == null)
            throw new IllegalArgumentException("A task type can't be null");
        if (creationDate == null)
            throw new IllegalArgumentException("The creation date of the task can't be null");
        this.type = type;
        this.creationDate = creationDate;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Date getCreationDate() {
        return creationDate.toDate();
    }

    @Override
    public String getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    /**
     * Sets a property.
     *
     * @param propertyName  name of the property (a constant is recommended).
     * @param propertyValue value of the propertyl
     * @return current {@code Task}, allowing to chain calls to {@link #setProperty(String, String)}.
     */
    public DefaultTask setProperty(String propertyName, String propertyValue) {
        properties.put(propertyName, propertyValue);
        return this;
    }

    /**
     * Basic types of tasks generated by the event system.
     */
    public enum Type {
        /**
         * Index a new specific content, if the content was already indexed, it will be reindexed and the new values
         * will overwrite the previous values.
         */
        INDEX_DOCUMENT,
        /**
         * Remove a content from the index
         */
        UNINDEX_DOCUMENT,

        /**
         * Index an entire site, if the content was indexed, the new version will overwrite the previous one.
         * If the content is not available anymore, it won't be modified (or deleted) from the index.
         */
        INDEX_SITE,
        /**
         * Remove every entry in the index related to one site.
         */
        UNINDEX_SITE,

        /**
         * Index every content available and handled.
         * If the content is not available anymore, it won't be modified (or deleted) from the index.
         */
        INDEX_ALL,
        /**
         * Remove every entry in the index.
         */
        UNINDEX_ALL,

        /**
         * TODO: Remove it?
         * Do nothing.
         */
        IGNORE;

        /**
         * Obtains a unique name for the type based on the class name.
         *
         * @return a uniquely identifiable type name.
         */
        public String getTypeName() {
            return Type.class.getCanonicalName() + "." + name();
        }
    }
}