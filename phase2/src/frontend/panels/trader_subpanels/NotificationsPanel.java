package frontend.panels.trader_subpanels;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.Trader;
import backend.tradesystem.managers.TradingInfoManager;

public class NotificationsPanel extends JPanel {

    private JPanel notificationsListContainer, freqTradersPanel, freqTradableItemsPanel, bottomTitleHeaderContainer,
            bottomSplitContainer, topTitleHeaderContainer;
    private JLabel notificationsTitle, freqTradersTitle, freqTradableItemsTitle;
    private JScrollPane notificationsScrollPane;
    private JButton clearAllNotificationsButton;

    private Color bg = new Color(51, 51, 51);
    private Color current = new Color(159, 159, 159);
    private Color gray = new Color(75, 75, 75);
    private Color gray2 = new Color(196, 196, 196);
    private Color red = new Color(219, 58, 52);
    private Color detailsButton = new Color(142, 142, 142);

    private Font regular, bold, italic, boldItalic;

    private Trader trader;

    private TradingInfoManager infoManager;

    public NotificationsPanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {

        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setSize(1000, 900);
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        this.setBackground(bg);

        infoManager = new TradingInfoManager();

        topTitleHeaderContainer = new JPanel(new GridLayout(1, 2));
        topTitleHeaderContainer.setPreferredSize(new Dimension(1200, 75));

        notificationsTitle = new JLabel("Notifications");
        notificationsTitle.setBackground(bg);
        notificationsTitle.setForeground(Color.WHITE);
        notificationsTitle.setOpaque(true);
        notificationsTitle.setFont(regular.deriveFont(30f));

        clearAllNotificationsButton = new JButton("Clear All Notifications");
        clearAllNotificationsButton.setBackground(bg);
        clearAllNotificationsButton.setForeground(Color.CYAN);
        clearAllNotificationsButton.setFont(boldItalic.deriveFont(25f));
        clearAllNotificationsButton.setOpaque(true);
        clearAllNotificationsButton.setBorderPainted(false);
        clearAllNotificationsButton.setHorizontalAlignment(JButton.RIGHT);

        notificationsScrollPane = new JScrollPane();
        notificationsScrollPane.setPreferredSize(new Dimension(1200, 400));
        notificationsScrollPane.setBorder(null);
        getNotifications();
        notificationsScrollPane.setViewportView(notificationsListContainer);

        bottomTitleHeaderContainer = new JPanel(new GridLayout(1, 2, 25, 0));
        bottomTitleHeaderContainer.setPreferredSize(new Dimension(1200, 75));
        bottomTitleHeaderContainer.setBackground(bg);

        freqTradersTitle = new JLabel("Frequent Traders");
        freqTradersTitle.setFont(regular.deriveFont(30f));
        freqTradersTitle.setForeground(Color.WHITE);
        freqTradersTitle.setOpaque(false);

        freqTradableItemsTitle = new JLabel("Frequent Tradable Items");
        freqTradableItemsTitle.setFont(regular.deriveFont(30f));
        freqTradableItemsTitle.setForeground(Color.WHITE);
        freqTradableItemsTitle.setOpaque(false);

        topTitleHeaderContainer.add(notificationsTitle);
        topTitleHeaderContainer.add(clearAllNotificationsButton);

        bottomTitleHeaderContainer.add(freqTradersTitle);
        bottomTitleHeaderContainer.add(freqTradableItemsTitle);

        bottomSplitContainer = new JPanel(new GridLayout(1, 2, 25, 0));
        bottomSplitContainer.setBackground(bg);
        bottomSplitContainer.setPreferredSize(new Dimension(1200, 250));

        getFreqTraders();
        getFreqTradableItems();

        bottomSplitContainer.add(freqTradersPanel);
        bottomSplitContainer.add(freqTradableItemsPanel);

        this.add(topTitleHeaderContainer);
        this.add(notificationsScrollPane);
        this.add(bottomTitleHeaderContainer);
        this.add(bottomSplitContainer);
    }

    private void getNotifications() {
        notificationsListContainer = new JPanel(new GridLayout(10, 1));
        notificationsListContainer.setBackground(gray);
    }

    private void getFreqTraders() {
        try {
            //Trader[] freqTraders = infoManager.getFrequentTraders(trader.getId());
            // freqTradersPanel = new JPanel(new GridLayout(freqTraders.length, 1));
            freqTradersPanel = new JPanel(new GridLayout(3, 1));
            freqTradersPanel.setBackground(gray2);
            // for(int i = 0; i < freqTraders.length; i++) {
            for(int i = 0; i < 3; i++) {
                // JLabel traderName = new JLabel("<html><pre>#" + freqTraders[i].getId().substring(freqTraders[i].getId().length() - 12) + "</pre></html>\t" + freqTraders[i].getUsername());
                JLabel traderName = new JLabel("#" + (i+1) + "      " + trader.getUsername());
                traderName.setFont(regular.deriveFont(20f));
                traderName.setForeground(Color.BLACK);
                traderName.setBackground(gray2);
                traderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
                
                freqTradersPanel.add(traderName);
            }
        // } catch (UserNotFoundException | TradeNotFoundException | AuthorizationException e) {
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void getFreqTradableItems() {
        try {
            freqTradableItemsPanel = new JPanel(new GridLayout(3, 1));
            freqTradableItemsPanel.setBackground(gray2);

            // for(int i = 0; i < freqTraders.length; i++) {
            for (int i = 0; i < 3; i++) {
                JLabel itemName = new JLabel("#" + (i + 1) + "      " + "Most Traded with Item");
                itemName.setFont(regular.deriveFont(20f));
                itemName.setForeground(Color.BLACK);
                itemName.setBackground(gray2);
                itemName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                freqTradableItemsPanel.add(itemName);
            }
            // } catch (UserNotFoundException | TradeNotFoundException |
            // AuthorizationException e) {
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}