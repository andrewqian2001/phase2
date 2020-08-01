package frontend.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.managers.ItemQuery;
import backend.tradesystem.managers.TraderManager;
import backend.tradesystem.managers.TradingInfoManager;
import backend.tradesystem.managers.UserQuery;

public class SearchPanel extends JPanel {

    private JLabel userSearchTitle, tradableItemSearchTitle;
    private JPanel userListContainer, userSearchBarContainer, tradableItemSearchBarContainer, tradableItemListContainer;
    private JTextField userSearchTextField, tradableItemSearchTextField;
    private JButton userSearchButton, tradableItemSearchButton;
    private JScrollPane userListScrollPane, tradableItemListScrollPane;

    private TraderManager traderManager;
    private TradingInfoManager infoManager;
    private String user;

    private UserQuery userQuery;
    private ItemQuery itemQuery;

    private Font regular, bold, italic, boldItalic;

    private Color bg = new Color(51, 51, 51);
    private Color current = new Color(159, 159, 159);
    private Color gray = new Color(75, 75, 75);
    private Color gray2 = new Color(196, 196, 196);
    private Color red = new Color(219, 58, 52);
    private Color detailsButton = new Color(142, 142, 142);

    public SearchPanel(String user, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {

        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.user = user;
        traderManager = new TraderManager();
        infoManager = new TradingInfoManager();

        userQuery = new UserQuery();
        itemQuery = new ItemQuery();

        this.setSize(1000, 900);
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
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
        userSearchButton.setPreferredSize(new Dimension(200, 75));
        userSearchButton.addActionListener(e -> {
            if (userSearchTextField.getText().trim().length() > 0) {
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
        tradableItemSearchButton.setPreferredSize(new Dimension(200, 75));
        tradableItemSearchButton.addActionListener(e -> {
            if (tradableItemSearchTextField.getText().trim().length() > 0) {
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
        ArrayList<String> matches = infoManager.searchTrader(username);
        int numRows = matches.size();
        if (numRows < 3)
            numRows = 3;
        userListContainer = new JPanel(new GridLayout(numRows, 1));
        userListContainer.setBackground(gray2);
        userListContainer.setBorder(null);
        for (String t : matches) {
            // for(String userId : matches) {
            JPanel trader = new JPanel(new GridLayout(1, 3));
            trader.setPreferredSize(new Dimension(1000, 75));
            trader.setBackground(gray2);
            trader.setBorder(BorderFactory.createLineBorder(bg));

            JLabel traderName = null;
            try {
                traderName = new JLabel(userQuery.getUsername(t));
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
            traderName.setFont(regular.deriveFont(20f));
            traderName.setForeground(Color.BLACK);
            traderName.setHorizontalAlignment(JLabel.LEFT);
            traderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

            JLabel traderId = new JLabel(
                    "<html><pre>#" + t.substring(t.length() - 12) + "</pre></html>");
            traderId.setFont(regular.deriveFont(20f));
            traderId.setForeground(Color.BLACK);
            traderId.setHorizontalAlignment(JLabel.CENTER);

            JButton traderDetailsButton = new JButton("Details");
            traderDetailsButton.setFont(bold.deriveFont(20f));
            traderDetailsButton.setForeground(Color.WHITE);
            traderDetailsButton.setBackground(detailsButton);
            traderDetailsButton.setOpaque(true);
            traderDetailsButton.setBorder(BorderFactory.createLineBorder(gray2, 15));
            traderDetailsButton.addActionListener(e -> {
                JDialog traderDetailsModal = new JDialog();
                traderDetailsModal.setTitle("Trader Details");
                traderDetailsModal.setSize(600, 600);
                traderDetailsModal.setResizable(false);
                traderDetailsModal.setLocationRelativeTo(null);

                JPanel traderDetailsPanel = new JPanel();
                traderDetailsPanel.setPreferredSize(new Dimension(600, 600));
                traderDetailsPanel.setBackground(bg);

                JLabel traderNameTitle = new JLabel("Trader Username:");
                traderNameTitle.setFont(italic.deriveFont(20f));
                traderNameTitle.setPreferredSize(new Dimension(290, 50));
                traderNameTitle.setOpaque(false);
                traderNameTitle.setForeground(Color.WHITE);

                JLabel traderNameLabel = null;
                try {
                    traderNameLabel = new JLabel("<html><pre>" + userQuery.getUsername(t) + "</pre></html>");
                } catch (UserNotFoundException userNotFoundException) {
                    userNotFoundException.printStackTrace();
                }
                traderNameLabel.setFont(regular.deriveFont(20f));
                traderNameLabel.setPreferredSize(new Dimension(290, 50));
                traderNameLabel.setOpaque(false);
                traderNameLabel.setForeground(Color.WHITE);

                JLabel traderIdTitle = new JLabel("Trader ID:");
                traderIdTitle.setFont(italic.deriveFont(20f));
                traderIdTitle.setPreferredSize(new Dimension(290, 50));
                traderIdTitle.setOpaque(false);
                traderIdTitle.setForeground(Color.WHITE);

                JLabel traderIdLabel = new JLabel(traderId.getText());
                traderIdLabel.setFont(regular.deriveFont(20f));
                traderIdLabel.setPreferredSize(new Dimension(290, 50));
                traderIdLabel.setOpaque(false);
                traderIdLabel.setForeground(Color.WHITE);

                JLabel traderCityTitle = new JLabel("City:");
                traderCityTitle.setFont(italic.deriveFont(20f));
                traderCityTitle.setPreferredSize(new Dimension(290, 50));
                traderCityTitle.setOpaque(false);
                traderCityTitle.setForeground(Color.WHITE);

                JLabel traderCityLabel = null;
                try {
                    traderCityLabel = new JLabel("<html><pre>" + userQuery.getCity(t) + "</pre></html>");
                } catch (UserNotFoundException userNotFoundException) {
                    userNotFoundException.printStackTrace();
                } catch (AuthorizationException authorizationException) {
                    authorizationException.printStackTrace();
                }
                traderCityLabel.setFont(regular.deriveFont(20f));
                traderCityLabel.setPreferredSize(new Dimension(290, 50));
                traderCityLabel.setOpaque(false);
                traderCityLabel.setForeground(Color.WHITE);

                JLabel traderNumTradesTitle = new JLabel("Trade Items Ratio:");
                traderNumTradesTitle.setFont(italic.deriveFont(20f));
                traderNumTradesTitle.setPreferredSize(new Dimension(290, 50));
                traderNumTradesTitle.setOpaque(false);
                traderNumTradesTitle.setForeground(Color.WHITE);

                JLabel traderNumTradesLabel = null;
                try {
                    traderNumTradesLabel = new JLabel("<html><pre>" + userQuery.getTotalItemsBorrowed(t) + " borrowed / " + userQuery.getTotalItemsLent(t) + " lent </pre></html>");
                } catch (UserNotFoundException userNotFoundException) {
                    userNotFoundException.printStackTrace();
                } catch (AuthorizationException authorizationException) {
                    authorizationException.printStackTrace();
                }
                traderNumTradesLabel.setFont(regular.deriveFont(20f));
                traderNumTradesLabel.setPreferredSize(new Dimension(290, 50));
                traderNumTradesLabel.setOpaque(false);
                traderNumTradesLabel.setForeground(Color.WHITE);

                JLabel traderReviewsTitle = new JLabel("Reviews by other Traders:");
                traderReviewsTitle.setFont(italic.deriveFont(20f));
                traderReviewsTitle.setPreferredSize(new Dimension(580, 50));
                traderReviewsTitle.setOpaque(false);
                traderReviewsTitle.setForeground(Color.WHITE);

                JScrollPane traderReviewScrollPane = new JScrollPane();
                traderReviewScrollPane.setPreferredSize(new Dimension(580, 250));
                traderReviewScrollPane.setBorder(null);
                traderReviewScrollPane.setBackground(gray);

                ArrayList<String[]> reviews = null;
                try {
                    reviews = userQuery.getReviews(t);
                } catch (UserNotFoundException userNotFoundException) {
                    userNotFoundException.printStackTrace();
                } catch (AuthorizationException authorizationException) {
                    authorizationException.printStackTrace();
                }
                int numberOfRows = reviews.size();
                if(numberOfRows < 4) numberOfRows = 4;
                JPanel traderReviews = new JPanel(new GridLayout(numberOfRows, 1));
                traderReviews.setBackground(gray2);
                traderReviews.setPreferredSize(new Dimension(580, 250));
                reviews.forEach(review -> {
                    JPanel traderReview = new JPanel(new GridLayout(1,1));
                    traderReview.setBackground(gray2);
                    traderReview.setPreferredSize(new Dimension(500, 50));
                    traderReview.setBorder(BorderFactory.createMatteBorder(0, 0 , 2, 0 , bg));
                    //[fromUserId, toUserId, message, rating, reportId]
                    JLabel text = new JLabel("DUMMY TEXT");
                    try {
                        text.setText(userQuery.getUsername(review[0]) + ": " + (review[3] + "   ->  ") + review[2]);
                    } catch(UserNotFoundException ex) {
                        System.out.println(ex.getMessage());
                    }
                    text.setFont(regular.deriveFont(20f));
                    text.setForeground(Color.BLACK);
                    text.setHorizontalAlignment(JLabel.LEFT);
                    text.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                    text.setOpaque(false);

                    traderReview.add(text);

                    traderReviews.add(traderReview);
                });
                traderReviewScrollPane.setViewportView(traderReviews);

                traderDetailsPanel.add(traderNameTitle);
                traderDetailsPanel.add(traderNameLabel);
                traderDetailsPanel.add(traderIdTitle);
                traderDetailsPanel.add(traderIdLabel);
                traderDetailsPanel.add(traderCityTitle);
                traderDetailsPanel.add(traderCityLabel);
                traderDetailsPanel.add(traderNumTradesTitle);
                traderDetailsPanel.add(traderNumTradesLabel);
                traderDetailsPanel.add(traderReviewsTitle);
                traderDetailsPanel.add(traderReviewScrollPane);

                traderDetailsModal.add(traderDetailsPanel);
                traderDetailsModal.setModal(true);
                traderDetailsModal.setVisible(true);
            });

            trader.add(traderName);
            trader.add(traderId);
            trader.add(traderDetailsButton);
            userListContainer.add(trader);
        }
        userListScrollPane.setViewportView(userListContainer);
    }

    private void findItems(String itemNameSearchString) {
        ArrayList<String> matches = infoManager.getTradableItemsWithName(itemNameSearchString);
        int numRows = matches.size();
        // int numRows = itemNameSearchString.length();
        if (numRows < 3)
            numRows = 3;
        tradableItemListContainer = new JPanel(new GridLayout(numRows, 1));
        // tradableItemListContainer = new JPanel(new GridLayout(matches.size(), 1));
        tradableItemListContainer.setBackground(gray2);
        tradableItemListContainer.setBorder(null);
        for (String t : matches) {
            try {
                String owner = infoManager.getTraderThatHasTradableItemId(t);

                JPanel item = new JPanel(new GridLayout(1, 4));
                item.setPreferredSize(new Dimension(1000, 75));
                item.setBackground(gray2);
                item.setBorder(BorderFactory.createLineBorder(bg));

                JLabel itemName = new JLabel(itemQuery.getName(t));
                itemName.setFont(regular.deriveFont(20f));
                itemName.setForeground(Color.BLACK);
                itemName.setHorizontalAlignment(JLabel.LEFT);
                itemName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                JLabel itemDesc = new JLabel(itemQuery.getDesc(t));
                itemDesc.setFont(regular.deriveFont(20f));
                itemDesc.setForeground(Color.BLACK);
                itemDesc.setHorizontalAlignment(JLabel.CENTER);

                JLabel itemOwnerName = new JLabel(userQuery.getUsername(owner));
                itemOwnerName.setFont(regular.deriveFont(20f));
                itemOwnerName.setForeground(Color.BLACK);
                itemOwnerName.setHorizontalAlignment(JLabel.CENTER);

                JButton addToWishlistButton = new JButton("Add To Wishlist");
                addToWishlistButton.setFont(bold.deriveFont(20f));
                addToWishlistButton.setForeground(Color.WHITE);
                addToWishlistButton.setBackground(detailsButton);
                addToWishlistButton.setOpaque(true);
                addToWishlistButton.setBorder(BorderFactory.createLineBorder(gray2, 15));
                addToWishlistButton.addActionListener(e -> {
                    try {
                        traderManager.addToWishList(user, t);
                    } catch (UserNotFoundException | TradableItemNotFoundException | AuthorizationException e1) {
                        System.out.println(e1.getMessage());
                    }

                });

                item.add(itemName);
                item.add(itemDesc);
                item.add(itemOwnerName);
                if(userQuery.isTrader(user))
                    item.add(addToWishlistButton);
                tradableItemListContainer.add(item);
            } catch (TradableItemNotFoundException e1) {
                System.out.println(e1.getMessage());
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        tradableItemListScrollPane.setViewportView(tradableItemListContainer);
    }
}