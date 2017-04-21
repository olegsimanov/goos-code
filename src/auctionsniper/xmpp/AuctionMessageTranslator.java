package auctionsniper.xmpp;

import auctionsniper.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;
import java.util.Map;

public class AuctionMessageTranslator implements MessageListener {

    private final String sniperId;                  // helps translator to know to whom belongs the price
    private AuctionEventListener listener;
    private XMPPFailureReporter failureReporter;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener, XMPPFailureReporter failureReporter) {
        this.sniperId = sniperId;
        this.listener = listener;
        this.failureReporter = failureReporter;
    }


    //    "SOLVersion: 1.1; Event: CLOSE;"
    //    "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"

    @Override
    public void processMessage(Chat chat, Message message) {
        String messageBody = message.getBody();
        try {
            translate(message);
        } catch (Exception parseException) {
            listener.auctionFailed();
            failureReporter.cannotTranslateMessage(sniperId, messageBody, parseException);
        }
    }

    private void translate(Message message) throws MissingValueException {
        AuctionEvent event = AuctionEvent.from(message.getBody());
        String type = event.type();
        if ("CLOSE".equals(type)) {
            listener.auctionClosed();
        } else if ("PRICE".equals(type)) {
            listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId));
        }
    }

    private static class AuctionEvent {

        private final Map<String, String> fields = new HashMap<>();
        public String type() throws MissingValueException { return get("Event");}
        public int currentPrice() throws MissingValueException { return getInt("CurrentPrice");}
        public int increment() throws MissingValueException{ return getInt("Increment");}

        private int getInt(String fieldName) throws MissingValueException {
            return Integer.parseInt(get(fieldName));
        }

        private String get(String fieldName) throws MissingValueException {
            String field = fields.get(fieldName);
            if (field == null) {
                throw new MissingValueException();
            }
            return field;
        }

        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }

        private AuctionEventListener.PriceSource isFrom(String sniperId) throws MissingValueException {
            return sniperId.equals(bidder()) ? AuctionEventListener.PriceSource.FromSniper : AuctionEventListener.PriceSource.FromOtherBidder;
        }

        private String bidder() throws MissingValueException {
            return get("Bidder");
        }

        static AuctionEvent from(String messageBody) {
            AuctionEvent event = new AuctionEvent();
            for (String field: fieldsIn(messageBody)) {
                event.addField(field);
            }
            return event;
        }
        static String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }

    }
}
