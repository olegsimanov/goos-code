package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.util.Announcer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String APPLICATION_NAME = "Auction Sniper";
    //  "Joining", "Bidding", "Winning", "Losing", "Lost", "Won", "Failed"
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_WON = "Won";
    public static final String SNIPERS_TABLE_NAME = "SnipersTable";
    public static final String NEW_ITEM_ID_NAME = "item id";
    public static final String JOIN_BUTTON_NAME = "join button";

    private final SnipersTableModel snipers;

    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

    public MainWindow(SnipersTableModel snipers) {
        super(APPLICATION_NAME);
        this.snipers = snipers;
        setName(MainWindow.MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(snipers), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentPane(JTable snipersTable, JPanel joinAuctionButton) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
        contentPane.add(new JScrollPane(joinAuctionButton), BorderLayout.NORTH);
    }

    private JTable makeSnipersTable(SnipersTableModel snipers) {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());
        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userRequests.announce().joinAuction(itemIdField.getText());
            }
        });
        controls.add(joinAuctionButton);

        return controls;
    }

    public void sniperStateChanged(SniperSnapshot snapshot) {
        snipers.sniperStateChanged(snapshot);
    }

    public void addUserRequestListener(UserRequestListener userRequestListener) {
        userRequests.addListener(userRequestListener);
    }
}
