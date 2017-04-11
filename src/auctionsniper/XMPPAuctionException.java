package auctionsniper;

public class XMPPAuctionException extends Exception {

    public XMPPAuctionException() {
        super();
    }

    public XMPPAuctionException(String message) {
        super(message);
    }

    public XMPPAuctionException(String message, Throwable cause) {
        super(message, cause);
    }

    public XMPPAuctionException(Throwable cause) {
        super(cause);
    }

    protected XMPPAuctionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
