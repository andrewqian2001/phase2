package frontend.panels.trader_subpanels;

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
import backend.models.TradableItem;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.UserTypes;
import backend.tradesystem.managers.*;

public class NotificationsPanel extends JPanel {

    private JPanel messagesListContainer, freqTradersPanel, freqTradableItemsPanel, bottomTitleHeaderContainer,
            bottomSplitContainer, topTitleHeaderContainer;
    private JLabel messagesTitle, freqTradersTitle, freqTradableItemsTitle;
    private JScrollPane messagesScrollPane;
    private JButton clearAllmessagesButton, addNewMessageButton;

    private MessageManager messageManager;
    private final LoginManager loginManager = new LoginManager();
    private final ItemQuery itemQuery = new ItemQuery();
    private final UserQuery userQuery = new UserQuery();

    private Color bg = new Color(51, 51, 51);
    private Color current = new Color(159, 159, 159);
    private Color gray = new Color(75, 75, 75);
    private Color gray2 = new Color(196, 196, 196);
    private Color red = new Color(219, 58, 52);
    private Color gray3 = new Color(142, 142, 142);
    private Color green = new Color(27, 158, 36);

    private Font regular, bold, italic, boldItalic;

    private String traderId;

    private TradingInfoManager infoManager;

    public NotificationsPanel(String traderId, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {

        this.traderId = traderId;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setSize(1000, 900);
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        this.setBackground(bg);

        infoManager = new TradingInfoManager();
        messageManager = new MessageManager();

        topTitleHeaderContainer = new JPanel(new GridLayout(1, 3));
        topTitleHeaderContainer.setPreferredSize(new Dimension(1200, 75));

        addNewMessageButton = new JButton("New Message");
        addNewMessageButton.setBackground(bg);
        addNewMessageButton.setForeground(Color.CYAN);
        addNewMessageButton.setFont(boldItalic.deriveFont(25f));
        addNewMessageButton.setOpaque(true);
        addNewMessageButton.setBorderPainted(false);
        addNewMessageButton.setHorizontalAlignment(JButton.LEFT);
        addNewMessageButton.addActionListener(e -> {
            JDialog messageDetailsModal = new JDialog();
            messageDetailsModal.setTitle("Compose New Message");
            messageDetailsModal.setSize(600, 500);
            messageDetailsModal.setResizable(false);
            messageDetailsModal.setLocationRelativeTo(null);

            JPanel messageDetailsPanel = new JPanel();
            messageDetailsPanel.setPreferredSize(new Dimension(600, 500));
            messageDetailsPanel.setBackground(bg);

            JLabel userNameTitle = new JLabel("Sender Username:");
            userNameTitle.setFont(italic.deriveFont(20f));
            userNameTitle.setPreferredSize(new Dimension(500, 50));
            userNameTitle.setOpaque(false);
            userNameTitle.setForeground(Color.WHITE);


            JComboBox<TraderComboBoxItem> traders = new JComboBox<>();
            traders.setPreferredSize(new Dimension(500, 50));
            traders.setFont(regular.deriveFont(20f));
            traders.setBackground(gray2);
            traders.setForeground(Color.BLACK);
            traders.setOpaque(true);
            infoManager.getAllTraders().forEach(id -> {
                if (!id.equals(traderId))
                    traders.addItem(new TraderComboBoxItem(id));
            });

            JLabel messageBodyTitle = new JLabel("Full Message:");
            messageBodyTitle.setFont(italic.deriveFont(20f));
            messageBodyTitle.setPreferredSize(new Dimension(500, 50));
            messageBodyTitle.setOpaque(false);
            messageBodyTitle.setForeground(Color.WHITE);

            JTextArea fullMessageBody = new JTextArea();
            fullMessageBody.setFont(regular.deriveFont(20f));
            fullMessageBody.setBackground(gray2);
            fullMessageBody.setForeground(Color.BLACK);
            fullMessageBody.setPreferredSize(new Dimension(500, 200));
            fullMessageBody.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            fullMessageBody.setLineWrap(true);

            JButton sendMessageButton = new JButton("Send");
            sendMessageButton.setFont(bold.deriveFont(25f));
            sendMessageButton.setBackground(green);
            sendMessageButton.setOpaque(true);
            sendMessageButton.setForeground(Color.WHITE);
            sendMessageButton.setPreferredSize(new Dimension(225, 75));
            sendMessageButton.setBorder(BorderFactory.createMatteBorder(10, 50, 10, 50, bg));
            sendMessageButton.addActionListener(e1 -> {
                try {
                    if (fullMessageBody.getText().trim().length() != 0) {
                        messageManager.sendMessage(traderId, ((TraderComboBoxItem) traders.getSelectedItem()).getId(), fullMessageBody.getText().trim());
                        messageDetailsModal.dispose();
                    }
                } catch (UserNotFoundException | AuthorizationException e2) {
                    System.out.println(e2.getMessage());
                }
            });

            messageDetailsPanel.add(userNameTitle);
            messageDetailsPanel.add(traders);
            messageDetailsPanel.add(messageBodyTitle);
            messageDetailsPanel.add(fullMessageBody);
            messageDetailsModal.add(messageDetailsPanel);
            messageDetailsModal.add(sendMessageButton, BorderLayout.SOUTH);
            messageDetailsModal.setModal(true);
            messageDetailsModal.setVisible(true);
        });

        messagesTitle = new JLabel("Messages");
        messagesTitle.setBackground(bg);
        messagesTitle.setForeground(Color.WHITE);
        messagesTitle.setOpaque(true);
        messagesTitle.setFont(regular.deriveFont(30f));
        messagesTitle.setHorizontalAlignment(JButton.CENTER);

        clearAllmessagesButton = new JButton("Clear All Messages");
        clearAllmessagesButton.setBackground(bg);
        clearAllmessagesButton.setForeground(Color.CYAN);
        clearAllmessagesButton.setFont(boldItalic.deriveFont(25f));
        clearAllmessagesButton.setOpaque(true);
        clearAllmessagesButton.setBorderPainted(false);
        clearAllmessagesButton.setHorizontalAlignment(JButton.RIGHT);
        clearAllmessagesButton.addActionListener(e -> {
            try {
                messageManager.clearMessages(traderId);
                messagesListContainer.removeAll();
                messagesListContainer.setLayout(new BorderLayout());
                messagesListContainer.setBackground(gray3);
                JLabel noMessagesFound = new JLabel("<html><pre>No Messages Found</pre></html>");
                noMessagesFound.setFont(regular.deriveFont(30f));
                noMessagesFound.setPreferredSize(new Dimension(1000, 375));
                noMessagesFound.setHorizontalAlignment(JLabel.CENTER);
                noMessagesFound.setVerticalAlignment(JLabel.CENTER);
                noMessagesFound.setForeground(Color.WHITE);
                messagesListContainer.add(noMessagesFound);
                messagesScrollPane.revalidate();
                messagesScrollPane.repaint();
            } catch (UserNotFoundException e1) {
                System.out.println(e1.getMessage());
            }
        });

        messagesScrollPane = new JScrollPane();
        messagesScrollPane.setPreferredSize(new Dimension(1200, 400));
        messagesScrollPane.setBorder(null);
        messagesScrollPane.setBackground(gray3);
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


        topTitleHeaderContainer.add(addNewMessageButton);
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
            HashMap<String, ArrayList<String>> messages = messageManager.getMessages(traderId);
            if (messages.size() == 0) {
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
            }
            int numRows = messages.keySet().size();
            if (numRows < 4)
                numRows = 4;
            messagesListContainer = new JPanel(new GridLayout(numRows, 1));
            messagesListContainer.setPreferredSize(new Dimension(1200, 400));
            messagesListContainer.setBackground(gray3);
            messages.keySet().forEach(fromUserId -> {
                JPanel messagePanel = new JPanel(new GridLayout(1, 5));
                messagePanel.setPreferredSize(new Dimension(1000, 75));
                messagePanel.setBackground(gray2);
                messagePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, bg));

                try {
                    JLabel userName = new JLabel(userQuery.getUsername(fromUserId));
                    if (loginManager.getType(fromUserId).equals(UserTypes.TRADER))
                        userName.setFont(regular.deriveFont(20f));
                    else
                        userName.setFont(bold.deriveFont(20f));
                    userName.setForeground(Color.BLACK);
                    userName.setHorizontalAlignment(JLabel.LEFT);
                    userName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));


                    JButton detailsButton = new JButton("View Conversation");
                    detailsButton.setFont(bold.deriveFont(20f));
                    detailsButton.setForeground(Color.WHITE);
                    detailsButton.setBackground(gray3);
                    detailsButton.setOpaque(true);
                    detailsButton.setBorder(BorderFactory.createMatteBorder(15, 20, 15, 20, gray2));
                    detailsButton.addActionListener(e -> {
                        JDialog messageDetailsModal = new JDialog();
                        messageDetailsModal.setTitle("Message Details");
                        messageDetailsModal.setSize(600, 400);
                        messageDetailsModal.setResizable(false);
                        messageDetailsModal.setLocationRelativeTo(null);

                        JPanel messageDetailsPanel = new JPanel();
                        messageDetailsPanel.setPreferredSize(new Dimension(600, 400));
                        messageDetailsPanel.setBackground(bg);

                        JLabel userNameTitle = new JLabel("Sender Username:");
                        userNameTitle.setFont(italic.deriveFont(20f));
                        userNameTitle.setPreferredSize(new Dimension(550, 50));
                        userNameTitle.setOpaque(false);
                        userNameTitle.setForeground(Color.WHITE);

                        userName.setForeground(Color.WHITE);

                        JLabel messageBodyTitle = new JLabel("Full Message:");
                        messageBodyTitle.setFont(italic.deriveFont(20f));
                        messageBodyTitle.setPreferredSize(new Dimension(550, 50));
                        messageBodyTitle.setOpaque(false);
                        messageBodyTitle.setForeground(Color.WHITE);

                        StringBuilder fullMessageString = new StringBuilder("");
                        messages.get(fromUserId).forEach(msg -> {
                            fullMessageString.append("-> " + msg + "\n");
                        });
                        JTextArea fullMessageBody = new JTextArea(fullMessageString.toString());
                        fullMessageBody.setFont(regular.deriveFont(20f));
                        fullMessageBody.setBackground(gray);
                        fullMessageBody.setForeground(Color.WHITE);
                        fullMessageBody.setPreferredSize(new Dimension(550, 200));
                        fullMessageBody.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        fullMessageBody.setLineWrap(true);
                        fullMessageBody.setEditable(false);

                        messageDetailsPanel.add(userNameTitle);
                        messageDetailsPanel.add(userName);
                        messageDetailsPanel.add(messageBodyTitle);
                        messageDetailsPanel.add(fullMessageBody);

                        messageDetailsModal.add(messageDetailsPanel);
                        messageDetailsModal.setModal(true);
                        messageDetailsModal.setVisible(true);

                    });


                    JButton clearButton = new JButton("Clear");
                    clearButton.setFont(bold.deriveFont(20f));
                    clearButton.setForeground(Color.WHITE);
                    clearButton.setBackground(red);
                    clearButton.setOpaque(true);
                    clearButton.setBorder(BorderFactory.createMatteBorder(15, 20, 15, 20, gray2));
                    clearButton.addActionListener(e -> {
                        try {
                            messageManager.clearMessagesFromUser(traderId, fromUserId);
                            messagesListContainer.remove(messagePanel);
                            messagesListContainer.revalidate();
                            messagesListContainer.repaint();
                        } catch (UserNotFoundException e1) {
                            System.out.println(e1.getMessage());
                        }
                    });

                    JButton replyButton = new JButton("Reply");
                    replyButton.setFont(bold.deriveFont(20f));
                    replyButton.setForeground(Color.WHITE);
                    replyButton.setBackground(green);
                    replyButton.setOpaque(true);
                    replyButton.setBorder(BorderFactory.createMatteBorder(15, 20, 15, 20, gray2));
                    replyButton.addActionListener(e -> {
                        JDialog messageReplyModal = new JDialog();
                        messageReplyModal.setTitle("Message Details");
                        messageReplyModal.setSize(600, 400);
                        messageReplyModal.setResizable(false);
                        messageReplyModal.setLocationRelativeTo(null);

                        JPanel messageReplyPanel = new JPanel();
                        messageReplyPanel.setPreferredSize(new Dimension(600, 400));
                        messageReplyPanel.setBackground(bg);

                        JLabel userNameTitle = new JLabel("Sender Username:");
                        userNameTitle.setFont(italic.deriveFont(20f));
                        userNameTitle.setPreferredSize(new Dimension(550, 50));
                        userNameTitle.setOpaque(false);
                        userNameTitle.setForeground(Color.WHITE);

                        userName.setForeground(Color.WHITE);

                        JLabel messageBodyTitle = new JLabel("Enter Message:");
                        messageBodyTitle.setFont(italic.deriveFont(20f));
                        messageBodyTitle.setPreferredSize(new Dimension(550, 50));
                        messageBodyTitle.setOpaque(false);
                        messageBodyTitle.setForeground(Color.WHITE);

                        JTextArea fullMessageBody = new JTextArea();
                        fullMessageBody.setFont(regular.deriveFont(20f));
                        fullMessageBody.setPreferredSize(new Dimension(550, 150));
                        fullMessageBody.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        fullMessageBody.setLineWrap(true);


                        JButton submitButton = new JButton("Submit");
                        submitButton.setFont(bold.deriveFont(20f));
                        submitButton.setBackground(green);
                        submitButton.setForeground(Color.WHITE);
                        submitButton.setPreferredSize(new Dimension(325, 50));
                        submitButton.addActionListener(e1 -> {
                            if (fullMessageBody.getText().trim().length() > 0) {
                                try {
                                    messageManager.sendMessage(traderId, fromUserId, fullMessageBody.getText());
                                    messageManager.clearMessagesFromUser(traderId, fromUserId);
                                    messageReplyModal.dispose();
                                    messagesListContainer.remove(messagePanel);
                                    messagesListContainer.revalidate();
                                    messagesListContainer.repaint();
                                } catch (UserNotFoundException | AuthorizationException e2) {
                                    System.out.println(e2.getMessage());
                                }
                            }
                        });

                        messageReplyPanel.add(userNameTitle);
                        messageReplyPanel.add(userName);
                        messageReplyPanel.add(messageBodyTitle);
                        messageReplyPanel.add(fullMessageBody);

                        messageReplyModal.add(messageReplyPanel);
                        messageReplyModal.add(submitButton, BorderLayout.SOUTH);
                        messageReplyModal.setModal(true);
                        messageReplyModal.setVisible(true);

                    });

                    messagePanel.add(userName);
                    messagePanel.add(detailsButton);
                    messagePanel.add(clearButton);
                    messagePanel.add(replyButton);
                    messagesListContainer.add(messagePanel);

                } catch (UserNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getFreqTraders() {
        try {
            ArrayList<String> freqTraders = infoManager.getFrequentTraders(traderId);
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
        } catch (UserNotFoundException | TradeNotFoundException | AuthorizationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getFreqTradableItems() {
        try {
            ArrayList<String> items = infoManager.getRecentTradeItems(traderId);
            int numRows = items.size();
            if (numRows < 3)
                numRows = 3;
            freqTradableItemsPanel = new JPanel(new GridLayout(numRows, 1));
            freqTradableItemsPanel.setBackground(gray2);
            for (String itemId : items) {
                JLabel itemName = new JLabel(itemQuery.getName(itemId));
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