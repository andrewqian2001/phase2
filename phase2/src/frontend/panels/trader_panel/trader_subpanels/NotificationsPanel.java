package frontend.panels.trader_panel.trader_subpanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.UserTypes;
import backend.tradesystem.general_managers.LoginManager;
import backend.tradesystem.general_managers.MessageManager;
import backend.tradesystem.queries.ItemQuery;
import backend.tradesystem.queries.UserQuery;
import backend.tradesystem.trader_managers.TradingInfoManager;
import frontend.panels.general_panels.MessagePanel;

/**
 * Used for messages and any notifications
 */
public class NotificationsPanel extends JPanel {

    private JPanel freqTradersPanel, freqTradableItemsPanel;

    private final MessageManager messageManager = new MessageManager();
    private final LoginManager loginManager = new LoginManager();
    private final ItemQuery itemQuery = new ItemQuery();
    private final UserQuery userQuery = new UserQuery();

    private final Color bg = new Color(51, 51, 51);
    private final Color gray = new Color(75, 75, 75);
    private final Color gray2 = new Color(196, 196, 196);
    private final Color red = new Color(219, 58, 52);
    private final Color gray3 = new Color(142, 142, 142);
    private final Color green = new Color(27, 158, 36);

    private final Font regular, bold, italic, boldItalic;

    private final String traderId;

    private final TradingInfoManager infoManager = new TradingInfoManager();

    /**
     * Used to create a new panel for messaging and notifications
     *
     * @param traderId   the trader id
     * @param regular    the regular font
     * @param bold       bold font
     * @param italic     italics font
     * @param boldItalic bold italics font
     * @throws IOException issues with getting database files
     */
    public NotificationsPanel(String traderId, Font regular, Font bold, Font italic, Font boldItalic) throws IOException, UserNotFoundException, AuthorizationException {

        this.traderId = traderId;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setSize(1000, 900);
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        this.setBackground(bg);


        JPanel bottomTitleHeaderContainer = new JPanel(new GridLayout(1, 2, 25, 0));
        bottomTitleHeaderContainer.setPreferredSize(new Dimension(1200, 75));
        bottomTitleHeaderContainer.setBackground(bg);

        JLabel freqTradersTitle = new JLabel("Frequent Traders");
        freqTradersTitle.setFont(regular.deriveFont(30f));
        freqTradersTitle.setForeground(Color.WHITE);
        freqTradersTitle.setOpaque(false);

        JLabel freqTradableItemsTitle = new JLabel("Recently Traded Items");
        freqTradableItemsTitle.setFont(regular.deriveFont(30f));
        freqTradableItemsTitle.setForeground(Color.WHITE);
        freqTradableItemsTitle.setOpaque(false);



        bottomTitleHeaderContainer.add(freqTradersTitle);
        bottomTitleHeaderContainer.add(freqTradableItemsTitle);



        JPanel bottomSplitContainer = new JPanel(new GridLayout(1, 2, 25, 0));
        bottomSplitContainer.setBackground(bg);
        bottomSplitContainer.setPreferredSize(new Dimension(1200, 250));

        getFreqTraders();
        getFreqTradableItems();

        bottomSplitContainer.add(freqTradersPanel);
        bottomSplitContainer.add(freqTradableItemsPanel);

        this.add(new MessagePanel(traderId, regular, bold, italic, boldItalic)); // add new messagePnael
        this.add(bottomTitleHeaderContainer);
        this.add(bottomSplitContainer);
    }



    private void getFreqTraders() throws AuthorizationException, UserNotFoundException {
        ArrayList<String> freqTraders = new ArrayList<>();
        if (!traderId.equals("")) {
            try {
                freqTraders = infoManager.getFrequentTraders(traderId);
            } catch (TradeNotFoundException e) {
                e.printStackTrace();
            }
        }
        int numRows = freqTraders.size();
        if (numRows < 3)
            numRows = 3;
        freqTradersPanel = new JPanel(new GridLayout(numRows, 1));
        freqTradersPanel.setBackground(gray2);
        for (String freqTraderId : freqTraders) {
            JLabel traderName = new JLabel(userQuery.getUsername(freqTraderId));
            traderName.setFont(regular.deriveFont(20f));
            traderName.setForeground(Color.BLACK);
            traderName.setBackground(gray2);
            traderName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, bg),
                    BorderFactory.createEmptyBorder(0, 25, 0, 0)));
            freqTradersPanel.add(traderName);
        }
    }

    private void getFreqTradableItems() throws UserNotFoundException {
        freqTradableItemsPanel = new JPanel(new GridLayout(0, 1));
        freqTradableItemsPanel.setBackground(gray2);
        ArrayList<String> items = new ArrayList<>();
        if (!traderId.equals("")) {
            try {
                items = infoManager.getRecentTradeItems(traderId);
            } catch (TradeNotFoundException e) {
                e.printStackTrace();
            } catch (AuthorizationException ignored){

            }

        }
        int numRows = items.size();
        if (numRows < 3)
            numRows = 3;
        freqTradableItemsPanel = new JPanel(new GridLayout(numRows, 1));
        freqTradableItemsPanel.setBackground(gray2);

        for (String itemId : items) {
            try {
                JLabel itemName = new JLabel(itemQuery.getName(itemId));
                itemName.setFont(regular.deriveFont(20f));
                itemName.setForeground(Color.BLACK);
                itemName.setBackground(gray2);
                itemName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, bg),
                        BorderFactory.createEmptyBorder(0, 25, 0, 0)));
                freqTradableItemsPanel.add(itemName);
            } catch (TradableItemNotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    private class TraderComboBoxItem {
        final String id;

        public TraderComboBoxItem(String id) {
            this.id = id;
        }

        public String toString() {
            try {
                return userQuery.getUsername(id);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
            return "";
        }

        public String getId() {
            return id;
        }
    }
}