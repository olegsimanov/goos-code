package auctionsniper;

/**
 * Is about feedback to the application
 * It reports changes to the current state of the Sniper
 */
public interface SniperListener {

    void sniperLost();

    void sniperBidding();
}
