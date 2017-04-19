package test.integration.auctionsniper.ui;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;
import test.endtoend.auctionsniper.AuctionSniperDriver;

import static org.hamcrest.CoreMatchers.equalTo;

public class MainWindowTest {

    private final MainWindow mainWindow = new MainWindow(new SniperPortfolio());
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void
    makeUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<Item> buttonProbe = new ValueMatcherProbe<>(equalTo(new Item("an item-id", 789)), "join request");
        mainWindow.addUserRequestListener(
                new UserRequestListener() {
                    @Override
                    public void joinAuction(Item item) {
                        buttonProbe.setReceivedValue(item);
                    }
                }
        );
        driver.startBiddingFor("an item-id", 789);
        driver.check(buttonProbe);
    }

}
