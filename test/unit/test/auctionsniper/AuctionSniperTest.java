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

    private final Mockery context = new Mockery();
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);

    private final AuctionSniper sniper = new AuctionSniper(ITEM_ID, auction);

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


}
