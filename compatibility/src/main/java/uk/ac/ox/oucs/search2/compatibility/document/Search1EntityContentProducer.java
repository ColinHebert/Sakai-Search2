package uk.ac.ox.oucs.search2.compatibility.document;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.search.api.EntityContentProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.document.*;
import uk.ac.ox.oucs.search2.tika.document.TikaDocument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

/**
 * @author Colin Hebert
 */
public class Search1EntityContentProducer implements EntityContentProducer {
    private final static Logger logger = LoggerFactory.getLogger(Search1EntityContentProducer.class);
    private final DocumentProducer documentProducer;

    public Search1EntityContentProducer(DocumentProducer documentProducer) {
        this.documentProducer = documentProducer;
    }

    @Override
    public boolean isContentFromReader(String reference) {
        return documentProducer.getDocument(reference) instanceof ReaderDocument;
    }

    @Override
    public Reader getContentReader(String reference) {
        Document document = documentProducer.getDocument(reference);
        if (document instanceof ReaderDocument)
            return ((ReaderDocument) document).getContent();
        else
            return null;
    }

    @Override
    public String getContent(String reference) {
        Document document = documentProducer.getDocument(reference);
        if (document instanceof StringDocument) {
            return ((StringDocument) document).getContent();
        } else if (document instanceof ReaderDocument) {
            BufferedReader bufferedReader = new BufferedReader(((ReaderDocument) document).getContent());
            StringBuilder sb = new StringBuilder();
            try {
                String currentString = "";
                do {
                    sb.append(currentString);
                    currentString = bufferedReader.readLine();
                } while (currentString != null);
            } catch (IOException e) {
                logger.warn("Couldn't parse the content of '" + reference + "'", e);
            }

            return sb.toString();
        } else if (document instanceof StreamDocument) {
            return new TikaDocument((StreamDocument) document).getContent();
        } else {
            return null;
        }
    }

    @Override
    public String getTitle(String reference) {
        return documentProducer.getDocument(reference).getTitle();
    }

    @Override
    public String getUrl(String reference) {
        return documentProducer.getDocument(reference).getUrl();
    }

    @Override
    public boolean matches(String reference) {
        return documentProducer.isHandled(reference);
    }

    @Override
    public Integer getAction(Event event) {
        /**
         * Do not generate actions, the {@link Event2Handler} is in charge of that.
         */
        return null;
    }

    @Override
    public boolean matches(Event event) {
        /**
         * Do not match any event, the {@link Event2Handler} is in charge of that.
         */
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * As getTool doesn't provide a reference to work with, instead of giving the tool of the reference,
     * the documentProducer name is returned.
     * </p>
     */
    @Override
    public String getTool() {
        if (documentProducer instanceof Search2DocumentProducer)
            return ((Search2DocumentProducer) documentProducer).getEntityContentProducer().getTool();
        else
            return documentProducer.getClass().getSimpleName();
    }

    @Override
    public String getSiteId(String reference) {
        return documentProducer.getDocument(reference).getSiteId();
    }

    @Override
    public Iterator<String> getSiteContentIterator(final String context) {
        return new Iterator<String>() {
            Queue<Document> documents = documentProducer.getSiteDocuments(context);

            @Override
            public boolean hasNext() {
                return documents.peek() != null;
            }

            @Override
            public String next() {
                return documents.poll().getReference();
            }

            @Override
            public void remove() {
                documents.poll();
            }
        };
    }

    @Override
    public boolean isForIndex(String reference) {
        return documentProducer.isHandled(reference);
    }

    @Override
    public boolean canRead(String reference) {
        return documentProducer.isReadable(reference);
    }

    @Override
    public Map<String, ?> getCustomProperties(String ref) {
        return documentProducer.getDocument(ref).getProperties();
    }

    @Override
    public String getCustomRDF(String ref) {
        return null;
    }

    @Override
    public String getId(String ref) {
        return documentProducer.getDocument(ref).getId();
    }

    @Override
    public String getType(String ref) {
        return documentProducer.getDocument(ref).getType();
    }

    @Override
    public String getSubType(String ref) {
        return documentProducer.getDocument(ref).getSubtype();
    }

    @Override
    public String getContainer(String ref) {
        return documentProducer.getDocument(ref).getContainer();
    }
}
