package uk.ac.ox.oucs.search2.backwardcompatibility.content;

import org.sakaiproject.search.api.EntityContentProducer;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.content.ContentProducer;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;

/**
 * @author Colin Hebert
 */
public class BackContentProducer implements ContentProducer {
    private final EntityContentProducer ecp;

    public BackContentProducer(EntityContentProducer ecp) {
        this.ecp = ecp;
    }

    @Override
    public Content getContent(String reference) {
        return BackContent.extractContent(reference, ecp);
    }

    @Override
    public Queue<Content> getSiteContents(String siteId) {
        return new ContentIteratorQueue(ecp.getSiteContentIterator(siteId));
    }

    @Override
    public boolean isHandled(String reference) {
        return ecp.matches(reference);
    }

    @Override
    public boolean isReadable(String reference) {
        return ecp.canRead(reference);
    }

    class ContentIteratorQueue extends AbstractQueue<Content> {
        private final Iterator<String> iterator;
        private Content currentEntry;

        private ContentIteratorQueue(Iterator<String> iterator) {
            this.iterator = iterator;
            popAndConvert();
        }

        @Override
        public Iterator<Content> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean offer(Content t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Content poll() {
            Content previousEntry = currentEntry;
            popAndConvert();
            return previousEntry;
        }

        @Override
        public Content peek() {
            return currentEntry;
        }

        private void popAndConvert() {
            if (iterator.hasNext()) {
                BackContent.extractContent(iterator.next(), ecp);
            } else {
                currentEntry = null;
            }
        }
    }
}