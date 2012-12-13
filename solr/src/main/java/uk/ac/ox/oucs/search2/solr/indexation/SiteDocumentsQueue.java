package uk.ac.ox.oucs.search2.solr.indexation;

import uk.ac.ox.oucs.search2.document.Document;
import uk.ac.ox.oucs.search2.document.DocumentProducer;
import uk.ac.ox.oucs.search2.document.DocumentProducerRegistry;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;

/**
 * @author Colin Hebert
 */
class SiteDocumentsQueue extends AbstractQueue<Document> {
    private final Iterator<DocumentProducer> contentProducerIterator;
    private final String siteId;
    private Queue<Document> contentQueue;
    private Document nextContent;

    public SiteDocumentsQueue(String siteId, DocumentProducerRegistry documentProducerRegistry) {
        this.siteId = siteId;
        this.contentProducerIterator = documentProducerRegistry.getDocumentProducers().iterator();
        this.nextContent = popContent();
    }

    @Override
    public Iterator<Document> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(Document content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Document poll() {
        Document content = nextContent;
        nextContent = popContent();
        return content;
    }

    @Override
    public Document peek() {
        return nextContent;
    }

    private Document popContent() {
        if (contentQueue == null) {
            if (contentProducerIterator.hasNext()) {
                contentQueue = contentProducerIterator.next().getSiteDocuments(siteId);
            } else return null;
        }

        if (contentQueue.peek() == null) {
            contentQueue = null;
            return popContent();
        } else {
            return contentQueue.poll();
        }
    }
}
