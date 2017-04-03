package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import java.awt.*;

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

    private final SnipersTableModel snipers;

    public MainWindow(SnipersTableModel snipers) {
        super(APPLICATION_NAME);
        this.snipers = snipers;
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

    public void sniperStateChanged(SniperSnapshot snapshot) {
        snipers.sniperStateChanged(snapshot);
    }

    public static class SnipersTableModel extends AbstractTableModel {

        private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
        private final static String[] STATUS_TEXT = new String[] {
            "Joining", "Bidding", "Winning", "Lost", "Won"
        };

        private SniperSnapshot sniperSnapshot = STARTING_UP;

        public int getColumnCount() {
            return Column.values().length;
        }

        public int getRowCount() {
            return 1;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return Column.at(columnIndex).valueIn(sniperSnapshot);
        }

        public void sniperStateChanged(SniperSnapshot newSnapshot) {
            this.sniperSnapshot = newSnapshot;
            fireTableRowsUpdated(0, 0);
        }

        public static String textFor(SniperState state) {
            return STATUS_TEXT[state.ordinal()];
        }

        @Override
        public String getColumnName(int column) {
            return Column.at(column).name;
        }
    }

} 
