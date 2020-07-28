package frontend.panels;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;

import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.managers.TraderManager;

public class SearchPanel extends JPanel {

    private JLabel userSearchTitle, tradableItemSearchTitle;
    private JPanel userListContainer, userSearchBarContainer, tradableItemSearchBarContainer, tradableItemListContainer;
    private JTextField userSearchTextField, tradableItemSearchTextField;
    private JButton userSearchButton, tradableItemSearchButton;
    private JScrollPane userListScrollPane, tradableItemListScrollPane;

    private TraderManager traderManager;
    private User user;

    private Font regular, bold, italic, boldItalic;

    private Color bg = new Color(51, 51, 51);
    private Color current = new Color(159, 159, 159);
    private Color gray = new Color(75, 75, 75);
    private Color gray2 = new Color(196, 196, 196);
    private Color red = new Color(219, 58, 52);
    private Color detailsButton = new Color(142, 142, 142);

    public SearchPanel(User user, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {

        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.user = user;
        traderManager = new TraderManager();
        
        this.setSize(1000, 900);
        this.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        this.setBackground(bg);

        userSearchTitle = new JLabel("Trader Search");
        userSearchTitle.setPreferredSize(new Dimension(1200, 75));
        userSearchTitle.setBackground(bg);
        userSearchTitle.setForeground(Color.WHITE);
        userSearchTitle.setFont(regular.deriveFont(30f));

        userSearchBarContainer = new JPanel();
        userSearchBarContainer.setLayout(new BoxLayout(this.userSearchBarContainer, BoxLayout.X_AXIS));
        userSearchBarContainer.setPreferredSize(new Dimension(1200, 75));
        userSearchBarContainer.setBackground(bg);

        userSearchTextField = new JTextField();
        userSearchTextField.setBackground(gray);
        userSearchTextField.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        userSearchTextField.setFont(regular.deriveFont(25f));
        userSearchTextField.setCaretColor(Color.WHITE);
        userSearchTextField.setForeground(Color.WHITE);
        
        userSearchButton = new JButton("Search");
        userSearchButton.setBackground(current);
        userSearchButton.setForeground(Color.WHITE);
        userSearchButton.setFont(boldItalic.deriveFont(30f));
        userSearchButton.setOpaque(true);
        userSearchButton.setBorder(BorderFactory.createLineBorder(current, 20));
        userSearchButton.setPreferredSize(new Dimension(200,75));
        userSearchButton.addActionListener(e -> {
            if(userSearchTextField.getText().trim().length() > 0) {
                findUsers(userSearchTextField.getText().trim());
            }
        });
        
        userSearchBarContainer.add(userSearchTextField);
        userSearchBarContainer.add(userSearchButton);
        
        userListContainer = new JPanel();
        userListContainer.setBackground(gray2);
        userListContainer.setBorder(null);

        userListScrollPane = new JScrollPane();
        userListScrollPane.setPreferredSize(new Dimension(1200, 230));
        userListScrollPane.setViewportView(userListContainer);

        tradableItemSearchTitle = new JLabel("Tradable Item Search");
        tradableItemSearchTitle.setPreferredSize(new Dimension(1200, 75));
        tradableItemSearchTitle.setBackground(bg);
        tradableItemSearchTitle.setForeground(Color.WHITE);
        tradableItemSearchTitle.setFont(regular.deriveFont(30f));

        tradableItemSearchBarContainer = new JPanel();
        tradableItemSearchBarContainer.setLayout(new BoxLayout(this.tradableItemSearchBarContainer, BoxLayout.X_AXIS));
        tradableItemSearchBarContainer.setPreferredSize(new Dimension(1200, 75));
        tradableItemSearchBarContainer.setBackground(bg);

        tradableItemSearchTextField = new JTextField();
        tradableItemSearchTextField.setBackground(gray);
        tradableItemSearchTextField.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        tradableItemSearchTextField.setFont(regular.deriveFont(25f));
        tradableItemSearchTextField.setCaretColor(Color.WHITE);
        tradableItemSearchTextField.setForeground(Color.WHITE);
        
        tradableItemSearchButton = new JButton("Search");
        tradableItemSearchButton.setBackground(current);
        tradableItemSearchButton.setForeground(Color.WHITE);
        tradableItemSearchButton.setFont(boldItalic.deriveFont(30f));
        tradableItemSearchButton.setOpaque(true);
        tradableItemSearchButton.setBorder(BorderFactory.createLineBorder(current, 20));
        tradableItemSearchButton.setPreferredSize(new Dimension(200,75));
        tradableItemSearchButton.addActionListener(e -> {
            if(tradableItemSearchTextField.getText().trim().length() > 0) {
                findItems(tradableItemSearchTextField.getText().trim());
            }
        });
        
        tradableItemSearchBarContainer.add(tradableItemSearchTextField);
        tradableItemSearchBarContainer.add(tradableItemSearchButton);
        
        tradableItemListContainer = new JPanel();
        tradableItemListContainer.setBackground(gray2);
        tradableItemListContainer.setBorder(null);

        tradableItemListScrollPane = new JScrollPane();
        tradableItemListScrollPane.setPreferredSize(new Dimension(1200, 230));
        tradableItemListScrollPane.setViewportView(userListContainer);
        
        this.add(userSearchTitle);
        this.add(userSearchBarContainer);
        this.add(userListScrollPane);
        this.add(tradableItemSearchTitle);
        this.add(tradableItemSearchBarContainer);
        this.add(tradableItemListScrollPane);
    }

    private void findUsers(String username) {
        // ArrayList<String> matches = manager.findAllUsers(username, user);
        // int numRows = matches.size();
        int numRows = username.length();
        if(numRows < 3) numRows = 3;
        userListContainer = new JPanel(new GridLayout(numRows, 1));
        userListContainer.setBackground(gray2);
        userListContainer.setBorder(null);
        for(int i = 0; i < username.length(); i++) {
        // for(String userId : matches) {
            JPanel trader = new JPanel(new GridLayout(1,3)); 
            trader.setPreferredSize(new Dimension(1000, 75));
            trader.setBackground(gray2);
            trader.setBorder(BorderFactory.createLineBorder(bg));

            JLabel traderName = new JLabel(username.toLowerCase().substring(0, i + 1));
            // JLabel traderName = new JLabel(tradeManager.getTrader(userId).getUsername());
            traderName.setFont(regular.deriveFont(20f));
            traderName.setForeground(Color.BLACK);
            traderName.setHorizontalAlignment(JLabel.LEFT);
            traderName.setBorder(BorderFactory.createEmptyBorder(0,25,0,0));

            JLabel traderId = new JLabel("<html><pre>#aib-94nmd-823</pre></html>");
            // JLabel traderId = new JLabel("<html><pre>"+ userId.substring(userId.length() - 12) +"</pre></html>");
            traderId.setFont(regular.deriveFont(20f));
            traderId.setForeground(Color.BLACK);
            traderId.setHorizontalAlignment(JLabel.CENTER);

            JButton traderDetailsButton = new JButton("Details");
            traderDetailsButton.setFont(bold.deriveFont(20f));
            traderDetailsButton.setForeground(Color.WHITE);
            traderDetailsButton.setBackground(detailsButton);
            traderDetailsButton.setOpaque(true);
            traderDetailsButton.setBorder(BorderFactory.createLineBorder(gray2, 15));

            trader.add(traderName);
            trader.add(traderId);
            trader.add(traderDetailsButton);
            userListContainer.add(trader);
        }
        userListScrollPane.setViewportView(userListContainer);
    }


    private void findItems(String itemNameSearchString) {
        // ArrayList<String> matches = manager.findAllTradableItems(itemName);
        // int numRows = matches.size();
        int numRows = itemNameSearchString.length();
        if (numRows < 3) numRows = 3;
        tradableItemListContainer = new JPanel(new GridLayout(numRows, 1));
        // tradableItemListContainer = new JPanel(new GridLayout(matches.size(), 1));
        tradableItemListContainer.setBackground(gray2);
        tradableItemListContainer.setBorder(null);
        for (int i = 0; i < itemNameSearchString.length(); i++) {
            // for(String itemId : matches) {
            JPanel item = new JPanel(new GridLayout(1, 4));
            item.setPreferredSize(new Dimension(1000, 75));
            item.setBackground(gray2);
            item.setBorder(BorderFactory.createLineBorder(bg));

            JLabel itemName = new JLabel(itemNameSearchString.toLowerCase().substring(0, i + 1));
            // JLabel itemName = new JLabel(manager.getItem(itemId).getName());
            itemName.setFont(regular.deriveFont(20f));
            itemName.setForeground(Color.BLACK);
            itemName.setHorizontalAlignment(JLabel.LEFT);
            itemName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

            JLabel itemDesc = new JLabel("Desc for " + itemName.getText());
            // JLabel itemDesc = new JLabel(manager.getItem(itemId).getDesc());
            itemDesc.setFont(regular.deriveFont(20f));
            itemDesc.setForeground(Color.BLACK);
            itemDesc.setHorizontalAlignment(JLabel.CENTER);

            JLabel itemIdLabel = new JLabel("<html><pre>OWNER</pre></html>");
            // JLabel traderId = new JLabel("<html><pre>"+ itemId.substring(userId.length() - 12) +"</pre></html>");
            itemIdLabel.setFont(regular.deriveFont(20f));
            itemIdLabel.setForeground(Color.BLACK);
            itemIdLabel.setHorizontalAlignment(JLabel.CENTER);

            JButton itemDetailsButton = new JButton("Add To Wishlist");
            itemDetailsButton.setFont(bold.deriveFont(20f));
            itemDetailsButton.setForeground(Color.WHITE);
            itemDetailsButton.setBackground(detailsButton);
            itemDetailsButton.setOpaque(true);
            itemDetailsButton.setBorder(BorderFactory.createLineBorder(gray2, 15));
            itemDetailsButton.addActionListener(e -> {
                if(user instanceof Trader) {
                    // traderManager.addToWishList(user.getId(), itemId);
                }
            });

            item.add(itemName);
            item.add(itemDesc);
            item.add(itemIdLabel);
            item.add(itemDetailsButton);
            tradableItemListContainer.add(item);
        }
        tradableItemListScrollPane.setViewportView(tradableItemListContainer);
    }
}