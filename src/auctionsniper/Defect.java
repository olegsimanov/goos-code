package auctionsniper;

/**
 * Exceptions for programming logic
 */
public class Defect extends RuntimeException {

    public Defect() {
    }

    public Defect(String message) {
        super(message);
    }

    public Defect(String message, Throwable cause) {
        super(message, cause);
    }

    public Defect(Throwable cause) {
        super(cause);
    }

    public Defect(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
