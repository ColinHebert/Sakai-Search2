package uk.ac.ox.oucs.search2.solr.result;

import uk.ac.ox.oucs.search2.document.Document;
import uk.ac.ox.oucs.search2.result.SearchResult;

/**
 * @author Colin Hebert
 */
public class SolrSearchResult implements SearchResult {
    private final Document document;
    private final double score;
    private final long index;
    private final String displayedText;

    public SolrSearchResult(Document document, double score, long index, String displayedText) {
        this.document = document;
        this.score = score;
        this.index = index;
        this.displayedText = displayedText;
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public boolean isCensored() {
        return false;
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public long getIndex() {
        return index;
    }

    @Override
    public String getDisplayedText() {
        return displayedText;
    }
}
