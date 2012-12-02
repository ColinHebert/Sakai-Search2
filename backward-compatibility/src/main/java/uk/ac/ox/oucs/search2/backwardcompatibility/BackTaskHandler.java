package uk.ac.ox.oucs.search2.backwardcompatibility;

import org.sakaiproject.search.api.SearchIndexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.task.DefaultTask;
import uk.ac.ox.oucs.search2.task.Task;
import uk.ac.ox.oucs.search2.task.TaskHandler;
import uk.ac.ox.oucs.search2.task.exception.TaskException;

import static uk.ac.ox.oucs.search2.task.DefaultTask.Type.*;

/**
 * @author Colin Hebert
 */
public class BackTaskHandler implements TaskHandler {
    private static final Logger logger = LoggerFactory.getLogger(BackTaskHandler.class);
    private SearchIndexBuilder searchIndexBuilder;

    @Override
    public void executeTask(Task task) {
        String type = task.getType();
        if (INDEX_DOCUMENT.getTypeName().equals(type)) {
            throw new TaskException("The previous search service doesn't support manual indexation");
        } else if (UNINDEX_DOCUMENT.getTypeName().equals(type)) {
            throw new TaskException("The previous search service doesn't support manual indexation");
        } else if (INDEX_SITE.getTypeName().equals(type)) {
            searchIndexBuilder.rebuildIndex(task.getProperty(DefaultTask.SITE_ID));
        } else if (UNINDEX_SITE.getTypeName().equals(type)) {
            throw new TaskException("The previous search service doesn't support site deindexation");
        } else if (INDEX_ALL.getTypeName().equals(type)) {
            searchIndexBuilder.rebuildIndex();
        } else if (UNINDEX_ALL.getTypeName().equals(type)) {
            throw new TaskException("The previous search service doesn't support deindexation");
        } else if (IGNORE.getTypeName().equals(type)) {
            logger.debug("Task '"+task+"', was ignored as expected");
        } else {
            throw new TaskException("Unknown task '" + task + "'");
        }
    }

    public void setSearchIndexBuilder(SearchIndexBuilder searchIndexBuilder) {
        this.searchIndexBuilder = searchIndexBuilder;
    }
}
