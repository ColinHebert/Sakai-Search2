package uk.ac.ox.oucs.search2.compatibility.document;

import org.sakaiproject.search.api.EntityContentProducer;
import uk.ac.ox.oucs.search2.document.Document;
import uk.ac.ox.oucs.search2.document.DocumentProducer;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;

/**
 * Wraps an {@link EntityContentProducer} from the Search1 API into a {@link DocumentProducer}.
 *
 * @author Colin Hebert
 */
public class Search2DocumentProducer implements DocumentProducer {
    private final EntityContentProducer entityContentProducer;

    public Search2DocumentProducer(EntityContentProducer entityContentProducer) {
        this.entityContentProducer = entityContentProducer;
    }

    public EntityContentProducer getEntityContentProducer() {
        return entityContentProducer;
    }

    @Override
    public Document getDocument(String reference) {
        return Search2Document.generateDocument(entityContentProducer, reference);
    }

    @Override
    public Queue<Document> getSiteDocuments(final String siteId) {
        return new AbstractQueue<Document>() {
            Iterator<String> referencesIterator = entityContentProducer.getSiteContentIterator(siteId);
            Document currentDocument = popDocument();

            @Override
            public Iterator<Document> iterator() {
                return new Iterator<Document>() {
                    @Override
                    public boolean hasNext() {
                        return peek() != null;
                    }

                    @Override
                    public Document next() {
                        return poll();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override
            public int size() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean offer(Document document) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Document poll() {
                Document document = currentDocument;
                currentDocument = popDocument();
                return document;
            }

            @Override
            public Document peek() {
                return currentDocument;
            }

            public Document popDocument() {
                if (referencesIterator.hasNext())
                    return getDocument(referencesIterator.next());
                else
                    return null;
            }
        };
    }

    @Override
    public boolean isHandled(String reference) {
        return entityContentProducer.matches(reference);
    }

    @Override
    public boolean isReadable(String reference) {
        return entityContentProducer.canRead(reference);
    }
}
