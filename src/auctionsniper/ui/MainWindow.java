package auctionsniper.ui;

import auctionsniper.SniperState;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import java.awt.*;

public class MainWindow extends JFrame {

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    //  "Joining", "Bidding", "Winning", "Losing", "Lost", "Won", "Failed"
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_WON = "Won";
    public static final String SNIPERS_TABLE_NAME = "SnipersTable";

    private final SnipersTableModel snipers = new SnipersTableModel();

    public MainWindow() {
        super("Auction Sniper");
        setName(MainWindow.MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentPane(JTable snipersTable) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public void showStatus(String status) {
        snipers.setStatusText(status);
    }

    public void sniperStatusChanged(SniperState sniperState, String statusText) {
        snipers.sniperStatusChanged(sniperState, statusText);
    }

    public static class SnipersTableModel extends AbstractTableModel {

        private final static SniperState STARTING_UP = new SniperState("", 0, 0);

        private String statusText = MainWindow.STATUS_JOINING;
        private SniperState sniperState = STARTING_UP;

        public int getColumnCount() {
            return Column.values().length;
        }

        public int getRowCount() {
            return 1;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (Column.at(columnIndex)) {
                case ITEM_IDENTIFIER:
                    return sniperState.itemId;
                case LAST_PRICE:
                    return sniperState.lastPrice;
                case LAST_BID:
                    return sniperState.lastBid;
                case SNIPER_STATUS:
                    return statusText;
                default:
                    throw new IllegalArgumentException("No column at " + columnIndex);
            }
        }

        public void setStatusText(String newStatusText) {
            statusText = newStatusText;
            fireTableRowsUpdated(0, 0);
        }

        public void sniperStatusChanged(SniperState newSniperState, String newStatusText) {
            sniperState = newSniperState;
            statusText = newStatusText;
            fireTableRowsUpdated(0, 0);
        }
    }

} 
