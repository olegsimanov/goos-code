package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.Main;
import auctionsniper.XMPPAuctionException;
import auctionsniper.util.Announcer;
import org.apache.commons.io.FilenameUtils;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static auctionsniper.XMPPAuctionHouse.LOG_FILE_NAME;

public class XMPPAuction implements Auction {

    private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
    private final Chat chat;

    public XMPPAuction(XMPPConnection connection, String auctionJID, XMPPFailureReporter failureReporter) {
        AuctionMessageTranslator messageTranslator = translatorFor(connection, failureReporter);
        chat = connection.getChatManager().createChat(auctionJID, messageTranslator);
        addAuctionEventListener(chatDisconnectorFor(messageTranslator));
    }

    private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator messageTranslator) {
        return new AuctionEventListener() {
            @Override
            public void auctionFailed() {
                chat.removeMessageListener(messageTranslator);
            }

            @Override
            public void auctionClosed() {
                // nothing to do
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource from) {
                // nothing to do
            }
        };
    }

    private AuctionMessageTranslator translatorFor(XMPPConnection connection, XMPPFailureReporter failureReporter) {
        return new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce(), failureReporter);
    }

    @Override
    public void bid(int amount) {
        sendMessage(String.format(Main.BID_COMMAND_FORMAT, amount));
    }

    @Override
    public void join() {
        sendMessage(Main.JOIN_COMMAND_FORMAT);
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener auctionEventListener) {
        auctionEventListeners.addListener(auctionEventListener);
    }

    private void sendMessage(final String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
