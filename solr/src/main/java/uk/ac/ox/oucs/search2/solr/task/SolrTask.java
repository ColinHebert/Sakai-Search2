package uk.ac.ox.oucs.search2.solr.task;

import org.joda.time.DateTime;
import uk.ac.ox.oucs.search2.task.DefaultTask;

/**
 * @author Colin Hebert
 */
public class SolrTask extends DefaultTask {

    public SolrTask(Type type) {
        super(type.getTypeName(), DateTime.now());
    }

    public SolrTask(Type type, DateTime creationDate) {
        super(type.getTypeName(), creationDate);
    }

    public static enum Type {
        OPTIMISE,
        COMMIT;

        public String getTypeName() {
            return SolrTask.class.getCanonicalName() + "." + name();
        }
    }
}
