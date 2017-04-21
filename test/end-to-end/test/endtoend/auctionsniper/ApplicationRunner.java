package test.endtoend.auctionsniper;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;

import java.io.IOException;

import static auctionsniper.ui.SnipersTableModel.textFor;
import static org.hamcrest.core.StringContains.containsString;
import static test.endtoend.auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;

public class ApplicationRunner {

    public static final String SNIPER_ID        = "sniper";
    public static final String SNIPER_PASSWORD  = "sniper";

    public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + "192.168.254.116" + "/Auction";

    private AuctionSniperDriver driver;
    private AuctionLogDriver logDriver = new AuctionLogDriver();

    public void startBiddingIn(final FakeAuctionServer... auctions) {
        startSniper();
        for (FakeAuctionServer auction: auctions) {
            String itemId = auction.getItemId();
            driver.startBiddingFor(itemId, Integer.MAX_VALUE);
            driver.showsSniperStatus(itemId, 0, 0, MainWindow.STATUS_JOINING);
        }

    }

    private void startSniper() {
        logDriver.clearLog();
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

    public void startBiddingWithStopPrice(final FakeAuctionServer auction, int stopPrice) {
        startSniper();
        driver.startBiddingWithStopPrice(auction.getItemId(), stopPrice);
    }

    public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOSING));
    }

    public void hasShownSniperHasFailed(FakeAuctionServer auction) {
        driver.showsSniperStatus(auction.getItemId(), 0, 0, textFor(SniperState.FAILED));
    }

    public void reportsInvalidMessage(FakeAuctionServer auction, String brokenMessage) throws IOException {
        logDriver.hasEntry(containsString(brokenMessage));
    }
}
