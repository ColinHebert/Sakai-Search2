package uk.ac.ox.oucs.search2.compatibility.event;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.search.api.SearchIndexBuilder;
import uk.ac.ox.oucs.search2.event.AbstractEventManager;

/**
 * @author Colin Hebert
 */
public class Search2EventManager extends AbstractEventManager {
    private SearchIndexBuilder searchIndexBuilder;

    @Override
    protected void notify(Event event) {
        searchIndexBuilder.addResource(null, event);
    }

    public void setSearchIndexBuilder(SearchIndexBuilder searchIndexBuilder) {
        this.searchIndexBuilder = searchIndexBuilder;
    }
}
