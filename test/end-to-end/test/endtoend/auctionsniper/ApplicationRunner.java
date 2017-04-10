package test.endtoend.auctionsniper;

import auctionsniper.Main;
import auctionsniper.ui.MainWindow;

import static test.endtoend.auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;

public class ApplicationRunner {

    public static final String SNIPER_ID        = "sniper";
    public static final String SNIPER_PASSWORD  = "sniper";

    public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + XMPP_HOSTNAME + "/Auction";

    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer... auctions) {
        startSniper(auctions);
        for (FakeAuctionServer auction: auctions) {
            String itemId = auction.getItemId();
            driver.startBiddingFor(itemId);
            driver.showsSniperStatus(itemId, 0, 0, MainWindow.STATUS_JOINING);
        }

    }

    private void startSniper(final FakeAuctionServer[] auctions) {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();

        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_NAME);
        driver.hasColumnTitles();
    }

    public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, MainWindow.STATUS_BIDDING);
    }

    public void showsSniperHasLostAuction(String expectedItemId, int expectedLastPrice, int expectedLastBid) {
        driver.showsSniperStatus(expectedItemId, expectedLastPrice, expectedLastBid, MainWindow.STATUS_LOST);
    }

    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, MainWindow.STATUS_WON);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

}
