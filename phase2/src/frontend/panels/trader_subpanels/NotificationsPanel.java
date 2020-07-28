package frontend.panels.trader_subpanels;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.TradableItem;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.managers.MessageManager;
import backend.tradesystem.managers.TradingInfoManager;

public class NotificationsPanel extends JPanel {

    private JPanel messagesListContainer, freqTradersPanel, freqTradableItemsPanel, bottomTitleHeaderContainer,
            bottomSplitContainer, topTitleHeaderContainer;
    private JLabel messagesTitle, freqTradersTitle, freqTradableItemsTitle;
    private JScrollPane messagesScrollPane;
    private JButton clearAllmessagesButton;

    private MessageManager messageManager;

    private Color bg = new Color(51, 51, 51);
    private Color current = new Color(159, 159, 159);
    private Color gray = new Color(75, 75, 75);
    private Color gray2 = new Color(196, 196, 196);
    private Color red = new Color(219, 58, 52);
    private Color gray3 = new Color(142, 142, 142);
    private Color green = new Color(27, 158, 36);

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
        messageManager = new MessageManager();

        topTitleHeaderContainer = new JPanel(new GridLayout(1, 2));
        topTitleHeaderContainer.setPreferredSize(new Dimension(1200, 75));

        messagesTitle = new JLabel("Messages");
        messagesTitle.setBackground(bg);
        messagesTitle.setForeground(Color.WHITE);
        messagesTitle.setOpaque(true);
        messagesTitle.setFont(regular.deriveFont(30f));

        clearAllmessagesButton = new JButton("Clear All Messages");
        clearAllmessagesButton.setBackground(bg);
        clearAllmessagesButton.setForeground(Color.CYAN);
        clearAllmessagesButton.setFont(boldItalic.deriveFont(25f));
        clearAllmessagesButton.setOpaque(true);
        clearAllmessagesButton.setBorderPainted(false);
        clearAllmessagesButton.setHorizontalAlignment(JButton.RIGHT);

        messagesScrollPane = new JScrollPane();
        messagesScrollPane.setPreferredSize(new Dimension(1200, 400));
        messagesScrollPane.setBorder(null);
        getMessages();
        messagesScrollPane.setViewportView(messagesListContainer);

        bottomTitleHeaderContainer = new JPanel(new GridLayout(1, 2, 25, 0));
        bottomTitleHeaderContainer.setPreferredSize(new Dimension(1200, 75));
        bottomTitleHeaderContainer.setBackground(bg);

        freqTradersTitle = new JLabel("Frequent Traders");
        freqTradersTitle.setFont(regular.deriveFont(30f));
        freqTradersTitle.setForeground(Color.WHITE);
        freqTradersTitle.setOpaque(false);

        freqTradableItemsTitle = new JLabel("Recently Traded Items");
        freqTradableItemsTitle.setFont(regular.deriveFont(30f));
        freqTradableItemsTitle.setForeground(Color.WHITE);
        freqTradableItemsTitle.setOpaque(false);

        topTitleHeaderContainer.add(messagesTitle);
        topTitleHeaderContainer.add(clearAllmessagesButton);

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
        this.add(messagesScrollPane);
        this.add(bottomTitleHeaderContainer);
        this.add(bottomSplitContainer);
    }

    private void getMessages() {
        try {
            HashMap<User, ArrayList<String>> messages = messageManager.getMessages(trader.getId());
            if(messages.size() == 0) {
                messagesListContainer = new JPanel();
                messagesListContainer.setBackground(gray3);
                JLabel noMessagesFound = new JLabel("<html><pre>No Messages Found</pre></html>");
                noMessagesFound.setFont(regular.deriveFont(30f));
                noMessagesFound.setPreferredSize(new Dimension(1000, 375));
                noMessagesFound.setHorizontalAlignment(JLabel.CENTER);
                noMessagesFound.setVerticalAlignment(JLabel.CENTER);
                noMessagesFound.setForeground(Color.WHITE);
                messagesListContainer.add(noMessagesFound);
                return;
            } int numRows = 0;
            for(User user : messages.keySet()) {
                numRows += messages.get(user).size();
            }
            if (numRows < 4)
                numRows = 4;
            messagesListContainer = new JPanel(new GridLayout(numRows, 1));
            messagesListContainer.setPreferredSize(new Dimension(1200, 400));
            messagesListContainer.setBackground(gray3);
            messages.forEach((u, msgs) -> {
                msgs.forEach(message -> {
                    JPanel messagePanel = new JPanel(new GridLayout(1, 5));
                    messagePanel.setPreferredSize(new Dimension(1000, 75));
                    messagePanel.setBackground(gray2);
                    messagePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, bg));

                    JLabel userName = new JLabel(u.getUsername());
                    if(u instanceof Trader)
                        userName.setFont(regular.deriveFont(20f));
                    else
                        userName.setFont(bold.deriveFont(20f));
                    userName.setForeground(Color.BLACK);
                    userName.setHorizontalAlignment(JLabel.LEFT);
                    userName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                    JLabel messageBody = new JLabel("<html><pre>" + (message.length() > 15 ? message.substring(0, 15) + "..." : message) + "</pre></html>");
                    messageBody.setFont(regular.deriveFont(20f));
                    messageBody.setForeground(Color.BLACK);
                    messageBody.setHorizontalAlignment(JLabel.LEFT);

                    JButton detailsButton = new JButton("Full Message");
                    detailsButton.setFont(bold.deriveFont(20f));
                    detailsButton.setForeground(Color.WHITE);
                    detailsButton.setBackground(gray3);
                    detailsButton.setOpaque(true);
                    detailsButton.setBorder(BorderFactory.createMatteBorder(15, 20, 15, 20, gray2));

                    JButton replyButton = new JButton("Reply");
                    replyButton.setFont(bold.deriveFont(20f));
                    replyButton.setForeground(Color.WHITE);
                    replyButton.setBackground(green);
                    replyButton.setOpaque(true);
                    replyButton.setBorder(BorderFactory.createMatteBorder(15, 20, 15, 20, gray2));
                    
                    JButton clearButton = new JButton("Clear");
                    clearButton.setFont(bold.deriveFont(20f));
                    clearButton.setForeground(Color.WHITE);
                    clearButton.setBackground(red);
                    clearButton.setOpaque(true);
                    clearButton.setBorder(BorderFactory.createMatteBorder(15, 20, 15, 20, gray2));

                    messagePanel.add(userName);
                    messagePanel.add(messageBody);
                    messagePanel.add(detailsButton);
                    messagePanel.add(replyButton);
                    messagePanel.add(clearButton);
                    messagesListContainer.add(messagePanel);
                });
            });
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }  

    private void getFreqTraders() {
        try {
            ArrayList<Trader> freqTraders = infoManager.getFrequentTraders(trader.getId());
            int numRows = freqTraders.size();
            if (numRows < 3)
                numRows = 3;
            freqTradersPanel = new JPanel(new GridLayout(numRows, 1));
            freqTradersPanel.setBackground(gray2);
            for (Trader t : freqTraders) {
                JLabel traderName = new JLabel(t.getUsername());
                traderName.setFont(regular.deriveFont(20f));
                traderName.setForeground(Color.BLACK);
                traderName.setBackground(gray2);
                traderName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, bg),
                        BorderFactory.createEmptyBorder(0, 25, 0, 0)));
                freqTradersPanel.add(traderName);
            }
        } catch (UserNotFoundException | TradeNotFoundException | AuthorizationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getFreqTradableItems() {
        try {
            ArrayList<TradableItem> items = infoManager.getRecentTradeItems(trader.getId());
            int numRows = items.size();
            if (numRows < 3)
                numRows = 3;
            freqTradableItemsPanel = new JPanel(new GridLayout(numRows, 1));
            freqTradableItemsPanel.setBackground(gray2);
            for (TradableItem item : items) {
                JLabel itemName = new JLabel(item.getName());
                itemName.setFont(regular.deriveFont(20f));
                itemName.setForeground(Color.BLACK);
                itemName.setBackground(gray2);
                itemName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, bg),
                        BorderFactory.createEmptyBorder(0, 25, 0, 0)));
                freqTradableItemsPanel.add(itemName);
            }
        } catch (UserNotFoundException | TradeNotFoundException | AuthorizationException
                | TradableItemNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }
}