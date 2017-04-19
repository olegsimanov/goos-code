package auctionsniper;

import auctionsniper.ui.UserRequestListener;

public class SniperLauncher implements UserRequestListener {

    private final AuctionHouse auctionHouse;
    private final SniperCollector collector;

    SniperLauncher(AuctionHouse auctionHouse, SniperCollector sniperCollector) {
        this.auctionHouse = auctionHouse;
        this.collector = sniperCollector;
    }


    @Override
    public void joinAuction(String itemId) {
        Auction auction = auctionHouse.auctionFor(itemId);
        AuctionSniper sniper = new AuctionSniper(itemId, auction);
        auction.addAuctionEventListener(sniper);
        collector.addSniper(sniper);
        auction.join();

    }


}
