package auctionsniper;

import java.util.EventListener;

/**
 * Is about feedback to the application
 * It reports changes to the current state of the Sniper
 */
public interface SniperListener extends EventListener {

    void sniperStateChanged(SniperSnapshot sniperSnapshot);

}
