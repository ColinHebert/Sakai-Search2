package uk.ac.ox.oucs.search2.compatibility.backward.document;

import org.sakaiproject.search.api.EntityContentProducer;
import org.sakaiproject.search.api.PortalUrlEnabledProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.document.Document;
import uk.ac.ox.oucs.search2.document.ReaderDocument;
import uk.ac.ox.oucs.search2.document.StringDocument;

import java.io.Reader;
import java.util.*;

/**
 * @author Colin Hebert
 */
public class Search2Document implements Document {
    private final static Logger logger = LoggerFactory.getLogger(Search2Document.class);
    private final EntityContentProducer entityContentProducer;
    private final String reference;

    private Search2Document(EntityContentProducer entityContentProducer, String reference) {
        this.entityContentProducer = entityContentProducer;
        this.reference = reference;
    }

    public static Document generateDocument(EntityContentProducer entityContentProducer, String reference) {
        if (entityContentProducer.isContentFromReader(reference))
            return new ReaderSearch2Document(entityContentProducer, reference);
        else
            return new StringSearch2Document(entityContentProducer, reference);
    }

    @Override
    public String getReference() {
        return reference;
    }

    @Override
    public String getId() {
        return entityContentProducer.getId(reference);
    }

    @Override
    public String getTitle() {
        return entityContentProducer.getTitle(reference);
    }

    @Override
    public String getUrl() {
        return entityContentProducer.getUrl(reference);
    }

    @Override
    public boolean isPortalUrl() {
        return entityContentProducer instanceof PortalUrlEnabledProducer;
    }

    @Override
    public String getTool() {
        return entityContentProducer.getTool();
    }

    @Override
    public String getType() {
        return entityContentProducer.getType(reference);
    }

    @Override
    public String getSubtype() {
        return entityContentProducer.getSubType(reference);
    }

    @Override
    public String getSiteId() {
        return entityContentProducer.getSiteId(reference);
    }

    @Override
    public String getContainer() {
        return entityContentProducer.getContainer(reference);
    }

    @Override
    public Map<String, Collection<String>> getProperties() {
        return convertProperties(entityContentProducer.getCustomProperties(reference));
    }

    private Map<String, Collection<String>> convertProperties(Map<String, ?> originalProperties) {
        Map<String, Collection<String>> properties = new HashMap<String, Collection<String>>();

        for (Map.Entry<String, ?> originalProperty : originalProperties.entrySet()) {
            Object originalPropertyValue = originalProperty.getValue();
            Collection<String> propertyValue;

            if (originalProperty instanceof Collection) {
                propertyValue = (Collection<String>) originalProperty;
            } else if (originalPropertyValue instanceof String[]) {
                propertyValue = Arrays.asList((String[]) originalPropertyValue);
            } else if (originalPropertyValue instanceof String) {
                propertyValue = Collections.singleton((String) originalPropertyValue);
            } else {
                logger.warn("Couldn't find what the value for '" + originalProperty.getKey() + "' was. It has been ignored: " + originalPropertyValue);
                propertyValue = Collections.emptyList();
            }

            properties.put(originalProperty.getKey(), propertyValue);
        }

        return properties;
    }

    public static class ReaderSearch2Document extends Search2Document implements ReaderDocument {

        private ReaderSearch2Document(EntityContentProducer entityContentProducer, String reference) {
            super(entityContentProducer, reference);
        }

        @Override
        public Reader getContent() {
            return super.entityContentProducer.getContentReader(super.reference);
        }
    }

    public static class StringSearch2Document extends Search2Document implements StringDocument {

        private StringSearch2Document(EntityContentProducer entityContentProducer, String reference) {
            super(entityContentProducer, reference);
        }

        @Override
        public String getContent() {
            return super.entityContentProducer.getContent(super.reference);
        }
    }
}
