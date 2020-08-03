package frontend.panels.general_panels;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.general_managers.MessageManager;
import backend.tradesystem.queries.UserQuery;
import backend.tradesystem.trader_managers.TradingInfoManager;

public class MessagePanel extends JPanel {

    private final TradingInfoManager infoManager = new TradingInfoManager();
    private final MessageManager messageManager = new MessageManager();
    private final UserQuery userQuery = new UserQuery();

    private final Color bg = new Color(51, 51, 51);
    private final Color gray = new Color(75, 75, 75);
    private final Color gray2 = new Color(196, 196, 196);
    private final Color red = new Color(219, 58, 52);
    private final Color gray3 = new Color(142, 142, 142);
    private final Color green = new Color(27, 158, 36);

    private final Dimension titleBarDimension = new Dimension(1200, 75);

    private JPanel messageTitleContainer, messagesListContainer;
    private JScrollPane messagesScrollPane;

    private Font regular, bold, italic, boldItalic;
    private Dimension preferredSize;


    private final String userId;

    public MessagePanel(String userId, Font regular, Font bold, Font italic, Font boldItalic, Dimension preferredSize) throws IOException, UserNotFoundException {

        this.userId = userId;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;
        this.preferredSize = preferredSize;

        this.setPreferredSize(preferredSize);
        this.setBackground(bg);

        messageTitleContainer = getMessageTitleContainer();
        messagesScrollPane = getMessagesScrollPane();

        this.add(messageTitleContainer);
        this.add(messagesScrollPane);

    }

    private JScrollPane getMessagesScrollPane() {
        JScrollPane msgScrollPane = new JScrollPane();
        JPanel messagesListContainer = new JPanel();
        
        msgScrollPane.setViewportView(messagesListContainer);
        return msgScrollPane;
    }

    private JPanel getMessageTitleContainer() throws UserNotFoundException {
        JPanel msgTitleContainer = new JPanel(new GridLayout(1, 3));
        msgTitleContainer.setPreferredSize(titleBarDimension);

        JLabel messagesTitle = new JLabel("Messages");
        messagesTitle.setBackground(bg);
        messagesTitle.setForeground(Color.WHITE);
        messagesTitle.setOpaque(true);
        messagesTitle.setFont(regular.deriveFont(30f));
        messagesTitle.setHorizontalAlignment(JButton.CENTER);

        msgTitleContainer.add(getNewMessageButton());
        msgTitleContainer.add(messagesTitle);
        msgTitleContainer.add(getClearAllmessagesButton());
        return msgTitleContainer;
    }

    private JButton getClearAllmessagesButton() {
        JButton clearAllmessagesButton = new JButton("Clear All Messages");
        clearAllmessagesButton.setBackground(bg);
        clearAllmessagesButton.setForeground(Color.CYAN);
        clearAllmessagesButton.setFont(boldItalic.deriveFont(25f));
        clearAllmessagesButton.setOpaque(true);
        clearAllmessagesButton.setBorderPainted(false);
        clearAllmessagesButton.setHorizontalAlignment(JButton.RIGHT);
        clearAllmessagesButton.addActionListener(e -> {
            if (userId.equals(""))
                return;
            try {
                messageManager.clearMessages(userId);
            } catch (UserNotFoundException e1) {
                e1.printStackTrace();
            }
            messagesListContainer.removeAll();
            messagesListContainer.setLayout(new BorderLayout());
            messagesListContainer.setBackground(gray3);
            JLabel noMessagesFound = new JLabel("<html><pre>No Messages Found</pre></html>");
            noMessagesFound.setFont(regular.deriveFont(30f));
            noMessagesFound.setPreferredSize(new Dimension(1000, (int) preferredSize.getHeight()));
            noMessagesFound.setHorizontalAlignment(JLabel.CENTER);
            noMessagesFound.setVerticalAlignment(JLabel.CENTER);
            noMessagesFound.setForeground(Color.WHITE);
            messagesListContainer.add(noMessagesFound);
            messagesScrollPane.revalidate();
            messagesScrollPane.repaint();
        });

        return clearAllmessagesButton;
    }

    private JButton getNewMessageButton() {
        JButton addNewMessageButton = new JButton("New Message");
        addNewMessageButton.setBackground(bg);
        addNewMessageButton.setForeground(Color.CYAN);
        addNewMessageButton.setFont(boldItalic.deriveFont(25f));
        addNewMessageButton.setOpaque(true);
        addNewMessageButton.setBorderPainted(false);
        addNewMessageButton.setHorizontalAlignment(JButton.LEFT);
        addNewMessageButton.addActionListener(e -> {
            if (userId.equals(""))
                return;
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

            JComboBox<TraderComboBoxItem> users = new JComboBox<>();
            users.setPreferredSize(new Dimension(500, 50));
            users.setFont(regular.deriveFont(20f));
            users.setBackground(gray2);
            users.setForeground(Color.BLACK);
            users.setOpaque(true);
            //TODO: FIX TO GET ALL USERS
            infoManager.getAllTraders().forEach(id -> {
                if (!id.equals(userId))
                    users.addItem(new TraderComboBoxItem(id));
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
                        messageManager.sendMessage(userId, ((TraderComboBoxItem) users.getSelectedItem()).getId(),fullMessageBody.getText().trim());
                        messageDetailsModal.dispose();
                    }
                } catch (UserNotFoundException | AuthorizationException e2) {
                    e2.printStackTrace();
                }
            });

            messageDetailsPanel.add(userNameTitle);
            messageDetailsPanel.add(users);
            messageDetailsPanel.add(messageBodyTitle);
            messageDetailsPanel.add(fullMessageBody);
            messageDetailsModal.add(messageDetailsPanel);
            messageDetailsModal.add(sendMessageButton, BorderLayout.SOUTH);
            messageDetailsModal.setModal(true);
            messageDetailsModal.setVisible(true);
        });
        return addNewMessageButton;
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
