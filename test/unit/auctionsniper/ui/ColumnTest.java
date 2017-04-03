package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import org.junit.Test;

import static org.junit.Assert.*;

public class ColumnTest {
    @Test
    public void at() throws Exception {
        assertEquals("ITEM_IDENTIFIER", Column.at(0).name());
        assertEquals("LAST_PRICE", Column.at(1).name());
        assertEquals("LAST_BID", Column.at(2).name());
        assertEquals("SNIPER_STATUS", Column.at(3).name());
    }

    @Test
    public void valueIn() throws Exception {
        SniperSnapshot snapshot = new SniperSnapshot("itemId", 10, 20, SniperState.BIDDING);
        assertEquals("itemId", Column.at(0).valueIn(snapshot));
        assertEquals(10, Column.at(1).valueIn(snapshot));
        assertEquals(20, Column.at(2).valueIn(snapshot));
        assertEquals("Bidding", Column.at(3).valueIn(snapshot));
    }

}