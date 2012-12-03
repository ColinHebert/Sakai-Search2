package uk.ac.ox.oucs.search2.result;

import uk.ac.ox.oucs.search2.document.Document;

import java.io.Serializable;

/**
 * One entry in the results returned for a search
 *
 * @author Colin Hebert
 */
public interface SearchResult extends Serializable {
    /**
     * Gets the indexed document.
     * <p>
     * Gets the {@link Document} indexed and found as a result for a query.<br />
     * The document can either be generated from its reference with a {@link uk.ac.ox.oucs.search2.document.DocumentProducer}
     * or if every useful information is already available, from the result returned by the search engine.
     * </p>
     *
     * @return The Document found as a result for a query.
     */
    Document getDocument();

    /**
     * Gets the score of the result.
     *
     * @return The score of the result.
     */
    double getScore();

    /**
     * Get the position of this result in the list of all results.
     * <p>
     * When paging is used, this position is the position of the result over all possible results.
     * </p>
     *
     * @return Position of this result in the list of all results.
     */
    long getIndex();

    /**
     * Checks if the result should be censored.
     * <p>
     * A censored result should never be displayed to the user.<br />
     * For security reasons, it's recommended to use {@link CensoredSearchResult} to represent a censoredResult.
     * </p>
     *
     * @return true if the result is censored, false otherwise.
     */
    boolean isCensored();

    /**
     * Gets the text that should be displayed with the result.
     * <p>
     * The displayed text usually contains a relevant part of the document, showing the reason making this document
     * eligible as a result.
     * </p>
     * <p>
     * The expected format for this text is Markdown, but isn't guaranteed.<br />
     * An implementation returning a format other than Markdown should document it.
     * </p>
     *
     * @return A text describing the result.
     */
    String getDisplayedText();

    /**
     * Censored implementation of {@link SearchResult}.
     */
    public static final class CensoredSearchResult implements SearchResult {
        @Override
        public Document getDocument() {
            return null;
        }

        @Override
        public double getScore() {
            return 0;
        }

        @Override
        public long getIndex() {
            return 0;
        }

        @Override
        public boolean isCensored() {
            return true;
        }

        @Override
        public String getDisplayedText() {
            return null;
        }
    }
}
