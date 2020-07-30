package frontend.panels;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.TradableItem;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.managers.TraderManager;
import backend.tradesystem.managers.TradingInfoManager;

public class SearchPanel extends JPanel {

    private JLabel userSearchTitle, tradableItemSearchTitle;
    private JPanel userListContainer, userSearchBarContainer, tradableItemSearchBarContainer, tradableItemListContainer;
    private JTextField userSearchTextField, tradableItemSearchTextField;
    private JButton userSearchButton, tradableItemSearchButton;
    private JScrollPane userListScrollPane, tradableItemListScrollPane;

    private TraderManager traderManager;
    private TradingInfoManager infoManager;
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
        infoManager = new TradingInfoManager();

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
        ArrayList<Trader> matches = infoManager.searchTrader(username);
        int numRows = matches.size();
        if (numRows < 3)
            numRows = 3;
        userListContainer = new JPanel(new GridLayout(numRows, 1));
        userListContainer.setBackground(gray2);
        userListContainer.setBorder(null);
        for (Trader t : matches) {
            // for(String userId : matches) {
            JPanel trader = new JPanel(new GridLayout(1, 3));
            trader.setPreferredSize(new Dimension(1000, 75));
            trader.setBackground(gray2);
            trader.setBorder(BorderFactory.createLineBorder(bg));

            JLabel traderName = new JLabel(t.getUsername());
            traderName.setFont(regular.deriveFont(20f));
            traderName.setForeground(Color.BLACK);
            traderName.setHorizontalAlignment(JLabel.LEFT);
            traderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

            JLabel traderId = new JLabel(
                    "<html><pre>#" + t.getId().substring(t.getId().length() - 12) + "</pre></html>");
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

                JLabel traderNameLabel = new JLabel("<html><pre>" + traderName.getText() + "</pre></html>");
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

                JLabel traderCityLabel = new JLabel("<html><pre>" + t.getCity() + "</pre></html>");
                traderCityLabel.setFont(regular.deriveFont(20f));
                traderCityLabel.setPreferredSize(new Dimension(290, 50));
                traderCityLabel.setOpaque(false);
                traderCityLabel.setForeground(Color.WHITE);

                JLabel traderNumTradesTitle = new JLabel("Trade Items Ratio:");
                traderNumTradesTitle.setFont(italic.deriveFont(20f));
                traderNumTradesTitle.setPreferredSize(new Dimension(290, 50));
                traderNumTradesTitle.setOpaque(false);
                traderNumTradesTitle.setForeground(Color.WHITE);

                JLabel traderNumTradesLabel = new JLabel("<html><pre>" + t.getTotalItemsBorrowed() + " borrowed / " + t.getTotalItemsLent() + " lent </pre></html>");
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

                int numberOfRows = t.getReviews().size();
                if(numberOfRows < 4) numberOfRows = 4;
                JPanel traderReviews = new JPanel(new GridLayout(numberOfRows, 1));
                traderReviews.setBackground(gray2);
                traderReviews.setPreferredSize(new Dimension(580, 250));
                t.getReviews().forEach(review -> {
                    JPanel traderReview = new JPanel(new GridLayout(1,3));
                    traderReview.setBackground(gray2);
                    traderReview.setPreferredSize(new Dimension(500, 50));
                    traderReview.setBorder(BorderFactory.createMatteBorder(0, 0 , 2, 0 , bg));

                    JLabel otherTraderName = new JLabel("DUMMY TEXT");
                    try {
                        otherTraderName.setText(traderManager.getUser(review.getFromUserId()).getUsername());
                    } catch(UserNotFoundException ex) {
                        System.out.println(ex.getMessage());
                    }
                    otherTraderName.setFont(regular.deriveFont(20f));
                    otherTraderName.setForeground(Color.WHITE);
                    otherTraderName.setHorizontalAlignment(JLabel.LEFT);
                    otherTraderName.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                    otherTraderName.setOpaque(false);

                    JLabel reviewRating = new JLabel(review.getRating() + "");
                    reviewRating.setFont(regular.deriveFont(20f));
                    reviewRating.setForeground(Color.WHITE);
                    reviewRating.setHorizontalAlignment(JLabel.CENTER);
                    reviewRating.setOpaque(false);

                    JLabel reviewMsg = new JLabel("<html><pre>" + review.getMessage()  + "</pre></html>");
                    reviewMsg.setFont(regular.deriveFont(20f));
                    reviewMsg.setForeground(Color.WHITE);
                    reviewMsg.setHorizontalAlignment(JLabel.LEFT);
                    reviewMsg.setOpaque(false);

                    traderReview.add(otherTraderName);
                    traderReview.add(reviewRating);
                    traderReview.add(reviewMsg);

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
        ArrayList<TradableItem> matches = infoManager.getTradableItemsWithName(itemNameSearchString);
        int numRows = matches.size();
        // int numRows = itemNameSearchString.length();
        if (numRows < 3)
            numRows = 3;
        tradableItemListContainer = new JPanel(new GridLayout(numRows, 1));
        // tradableItemListContainer = new JPanel(new GridLayout(matches.size(), 1));
        tradableItemListContainer.setBackground(gray2);
        tradableItemListContainer.setBorder(null);
        for (TradableItem t : matches) {
            try {
                Trader owner = infoManager.getTraderThatHasTradableItemId(t.getId());

                JPanel item = new JPanel(new GridLayout(1, 4));
                item.setPreferredSize(new Dimension(1000, 75));
                item.setBackground(gray2);
                item.setBorder(BorderFactory.createLineBorder(bg));

                JLabel itemName = new JLabel(t.getName());
                itemName.setFont(regular.deriveFont(20f));
                itemName.setForeground(Color.BLACK);
                itemName.setHorizontalAlignment(JLabel.LEFT);
                itemName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                JLabel itemDesc = new JLabel(t.getDesc());
                itemDesc.setFont(regular.deriveFont(20f));
                itemDesc.setForeground(Color.BLACK);
                itemDesc.setHorizontalAlignment(JLabel.CENTER);

                JLabel itemOwnerName = new JLabel(owner.getUsername());
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
                    if (user instanceof Trader) {
                        try {
                            traderManager.addToWishList(user.getId(), t.getId());
                        } catch (UserNotFoundException | TradableItemNotFoundException | AuthorizationException e1) {
                            System.out.println(e1.getMessage());
                        }
                    }
                });

                item.add(itemName);
                item.add(itemDesc);
                item.add(itemOwnerName);
                if(user instanceof Trader)
                    item.add(addToWishlistButton);
                tradableItemListContainer.add(item);
            } catch (TradableItemNotFoundException e1) {
                System.out.println(e1.getMessage());
            }
        }
        tradableItemListScrollPane.setViewportView(tradableItemListContainer);
    }
}