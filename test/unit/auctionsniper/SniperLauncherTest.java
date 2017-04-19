package auctionsniper;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(JMock.class)
public class SniperLauncherTest {

    private final Mockery context = new Mockery();
    private final States auctionState = context.states("auction state").startsAs("not joined");

    private final Auction auction = context.mock(Auction.class);
    private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
    private final SniperCollector sniperCollector = context.mock(SniperCollector.class);
    private SniperLauncher launcher = new SniperLauncher(auctionHouse, sniperCollector);

    @Test
    public void addsNewSniperToCollectorAndThenJoinsAuction() throws Exception {
        final Item item = new Item("item 123", 456);
        context.checking(new Expectations() {{
            allowing(auctionHouse).auctionFor(item.identifier); will(returnValue(auction));
            oneOf(auction).addAuctionEventListener(with(sniperForItem(item))); when(auctionState.is("not joined"));
            oneOf(sniperCollector).addSniper(with(sniperForItem(item))); when(auctionState.is("not joined"));
            one(auction).join(); then(auctionState.is("joined"));
        }});
        launcher.joinAuction(item);
    }

    protected Matcher<AuctionSniper> sniperForItem(Item item) {
        return new FeatureMatcher<AuctionSniper, String>(equalTo(item.identifier), "sniper with item id", "item") {
            @Override protected String featureValueOf(AuctionSniper actual) {
                return actual.getSnapshot().itemId;
            }
        };
    }

}