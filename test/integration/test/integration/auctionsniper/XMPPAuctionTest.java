package test.integration.auctionsniper;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.xmpp.XMPPAuction;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.endtoend.auctionsniper.ApplicationRunner;
import test.endtoend.auctionsniper.FakeAuctionServer;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;

public class XMPPAuctionTest {

    private XMPPConnection connection;
    private final FakeAuctionServer server = new FakeAuctionServer("item-54321");

    @Before public void ceateConnection() throws XMPPException {
        connection = new XMPPConnection(FakeAuctionServer.XMPP_HOSTNAME);
        connection.connect();
        connection.login(ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD, FakeAuctionServer.AUCTION_RESOURCE);
    }

    @After public void disconnect() {
        connection.disconnect();
    }


    @Test public void
    receivesEventsFromAuctionServerAfterJoining() throws Exception {
        server.startSellingItem();
        CountDownLatch auctionWasClosed = new CountDownLatch(1);
        Auction auction = new XMPPAuction(connection, server.getItemId());
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
        auction.join();

        server.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        server.announceClosed();

        assertTrue("should have been closed", auctionWasClosed.await(2, SECONDS));

    }

    private AuctionEventListener
    auctionClosedListener(final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            @Override
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource from) {
                // not implemented
            }
        };
    }

}