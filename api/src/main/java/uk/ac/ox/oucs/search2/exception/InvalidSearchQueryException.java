package uk.ac.ox.oucs.search2.exception;

/**
 * Exception thrown if the search query isn't valid.
 *
 * @author Colin Hebert
 */
public class InvalidSearchQueryException extends RuntimeException {

    public InvalidSearchQueryException() {
    }

    public InvalidSearchQueryException(String message) {
        super(message);
    }

    public InvalidSearchQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSearchQueryException(Throwable cause) {
        super(cause);
    }
}
