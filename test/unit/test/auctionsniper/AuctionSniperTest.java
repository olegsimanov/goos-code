package test.auctionsniper;

import auctionsniper.*;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static auctionsniper.SniperState.*;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(JMock.class)
public class AuctionSniperTest {

    private static final String ITEM_ID = "itemId";
    public static final Item ITEM = new Item(ITEM_ID, 1234);

    private final Mockery context = new Mockery();
    private final States sniperState = context.states("sniper");
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);

    private final AuctionSniper sniper = new AuctionSniper(ITEM, auction);

     @Before public void
     attachListener() {
        sniper.addSniperListener(sniperListener);
     }

    @Test public void
    reportsLostWhenAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
        }});

        sniper.auctionClosed();
    }

    @Test public void
    bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 100;
        final int increment = 25;
        final int bid = price + increment;
        context.checking(new Expectations(){{
            one(auction).bid(price + increment);
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
        }});
        sniper.currentPrice(price, increment, AuctionEventListener.PriceSource.FromOtherBidder);
    }

    @Test public void
    reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
        }});
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test public void
    reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations(){{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
        }});
        sniper.currentPrice(135, 12, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.currentPrice(147, 45, AuctionEventListener.PriceSource.FromSniper);
    }

    @Test public void
    reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations(){{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WON)));
        }});
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromSniper);
        sniper.auctionClosed();
    }

    @Test public void
    doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        allowingSniperBidding();
        context.checking(new Expectations() {{
            int bid = 123 + 45;
            allowing(auction).bid(bid);

            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, bid, SniperState.LOSING)); when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.currentPrice(2345, 25, AuctionEventListener.PriceSource.FromOtherBidder);
    }

    @Test public void
    reportsFailedIfAuctionFailsWhenBidding() {
         ignoringAuction();
         allowingSniperBidding();

         expectSniperToFailWhenItIs("bidding");

         sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromOtherBidder);
         sniper.auctionFailed();
    }

    private void allowingSniperBidding() {
         context.checking(new Expectations() {{
             allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING))); then(sniperState.is("bidding"));
         }});
    }

    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(
                equalTo(state), "sniper that is ", "was"
        ) {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }

    private void expectSniperToFailWhenItIs(final String state) {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 00, 0, SniperState.FAILED));
            when(sniperState.is(state));
        }});
    }

    private void ignoringAuction() {
        context.checking(new Expectations() {{
            ignoring(auction);
        }});
    }


}
