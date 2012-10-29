package uk.ac.ox.oucs.search2.result;

import uk.ac.ox.oucs.search2.content.Content;

import java.io.Serializable;

/**
 * One entry in the results returned for a search
 *
 * @author Colin Hebert
 */
public interface SearchResult extends Serializable {
    Content getContent();

    /**
     * Score of the entry
     *
     * @return score
     */
    double getScore();

    /**
     * Index of the entry in the entire list of results
     *
     * @return Current position in the list of results
     */
    long getIndex();

    /**
     * Should the entry be displayed/used
     * A censored entry is usually empty (see {@link CensoredSearchResult}) and shouldn't be used
     *
     * @return true if the result is censored, false otherwise
     */
    boolean isCensored();

    /**
     * Short text accompanying the result
     * Usually a relevant part of the content, containing the searched keywords
     *
     * @return A short text specific to the result
     */
    String getDisplayedText();

    /**
     * Implementation of {@link SearchResult} used to censor a result.
     */
    public static final class CensoredSearchResult implements SearchResult {
        @Override
        public Content getContent() {
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
