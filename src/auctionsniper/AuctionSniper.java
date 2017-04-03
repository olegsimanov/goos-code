package auctionsniper;


/**
 * Application itself
 */
public class AuctionSniper implements AuctionEventListener {

    private boolean isWinning = false;

    private final String itemId;
    private final Auction auction;
    private final SniperListener sniperListener;

    public AuctionSniper(String itemId, Auction auction, SniperListener sniperListener) {
        this.itemId = itemId;
        this.auction = auction;
        this.sniperListener = sniperListener;
    }

    public void auctionClosed() {
        if (isWinning) {
            sniperListener.sniperWon();
        } else {
            sniperListener.sniperLost();
        }
    }

    public void currentPrice(int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FromSniper;
        switch (priceSource) {
            case FromSniper:
                sniperListener.sniperWinning();
                break;
            case FromOtherBidder:
                int bid = price + increment;
                auction.bid(bid);
                sniperListener.sniperBidding(new SniperState(itemId, price, bid));
                break;
        }
    }

}
