package uk.ac.ox.oucs.search2.solr.document;

import uk.ac.ox.oucs.search2.document.Document;
import uk.ac.ox.oucs.search2.solr.SolrSchemaConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Document extracted from the Solr search engine.
 * <p>
 * While it is possible to only retrieve the reference and reconstruct an document from that, Solr already contains
 * everything needed.<br />
 * It is possible that some property weren't indexed at all, and all the fields retrieved from Solr are also available
 * as properties.
 * </p>
 * TODO: Remove constant fields from the properties?
 *
 * @author Colin Hebert
 */
public class SolrDocument implements Document {
    private final org.apache.solr.common.SolrDocument document;

    /**
     * Builds a {@link Document} out of a Solr result.
     *
     * @param document document generated by Solr.
     */
    public SolrDocument(org.apache.solr.common.SolrDocument document) {
        this.document = document;
    }

    @Override
    public String getId() {
        return (String) document.getFieldValue(SolrSchemaConstants.ID_FIELD);
    }

    @Override
    public String getTitle() {
        return (String) document.getFieldValue(SolrSchemaConstants.TITLE_FIELD);
    }

    @Override
    public String getUrl() {
        return (String) document.getFieldValue(SolrSchemaConstants.URL_FIELD);
    }

    @Override
    public boolean isPortalUrl() {
        return (Boolean) document.getFieldValue(SolrSchemaConstants.PORTAL_URL_FIELD);
    }

    @Override
    public String getType() {
        return (String) document.getFieldValue(SolrSchemaConstants.TYPE_FIELD);

    }

    @Override
    public String getSubtype() {
        return (String) document.getFieldValue(SolrSchemaConstants.SUBTYPE_FIELD);
    }

    @Override
    public String getTool() {
        return (String) document.getFieldValue(SolrSchemaConstants.TOOL_FIELD);
    }

    @Override
    public String getSiteId() {
        return (String) document.getFieldValue(SolrSchemaConstants.SITE_ID_FIELD);
    }

    @Override
    public String getReference() {
        return (String) document.getFieldValue(SolrSchemaConstants.REFERENCE_FIELD);

    }

    @Override
    public String getContainer() {
        return (String) document.getFieldValue(SolrSchemaConstants.CONTAINER_FIELD);
    }

    @Override
    public Map<String, Collection<String>> getProperties() {
        Map<String, Collection<Object>> fieldValuesMap = document.getFieldValuesMap();
        Map<String, Collection<String>> properties = new HashMap<String, Collection<String>>(fieldValuesMap.size());
        for (Map.Entry<String, Collection<Object>> property : fieldValuesMap.entrySet()) {
            Collection<String> values = new ArrayList<String>(property.getValue().size());
            for (Object value : property.getValue()) {
                values.add(value.toString());
            }

            properties.put(property.getKey(), values);
        }
        return properties;
    }
}