package uk.ac.ox.oucs.search2.solr.indexation;

import org.joda.time.DateTime;
import uk.ac.ox.oucs.search2.indexation.DefaultTask;

/**
 * Task specific to a Solr index.
 * <p>
 * Additional Tasks supported by Solr servers such as commit and optimise.
 * </p>
 *
 * @author Colin Hebert
 */
public class SolrTask extends DefaultTask {

    /**
     * Creates a {@code Task} specific for a Solr server.
     * <p>
     * The creation date of the task will be set automatically to the current DateTime.
     * </p>
     *
     * @param type type of task
     */
    public SolrTask(Type type) {
        super(type.getTypeName(), DateTime.now());
    }

    /**
     * Creates a {@code Task} specific for a Solr server.
     *
     * @param type         type of task
     * @param creationDate the creation date of the task.
     */
    public SolrTask(Type type, DateTime creationDate) {
        super(type.getTypeName(), creationDate);
    }

    /**
     * Task types specific to Solr.
     */
    public static enum Type {
        /**
         * Optimisation of the index.
         */
        OPTIMISE,
        /**
         * Commit.
         */
        COMMIT;

        public String getTypeName() {
            return SolrTask.class.getCanonicalName() + "." + name();
        }
    }
}
