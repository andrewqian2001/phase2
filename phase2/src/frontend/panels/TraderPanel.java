package frontend.panels;

import frontend.panels.trader_subpanels.*;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;

import backend.models.users.Trader;

public class TraderPanel extends JPanel {

    private JLabel usernameTitle, userIdTitle;
    private JPanel tradePanel, inventoryPanel, wishlistPanel, notificationsPanel, searchPanel, menuContainer;
    private JButton tradePanelButton, inventoryPanelButton, wishlistPanelButton, notificationsPanelButton,
            searchPanelButton, logoutButton;


    //TODO: Set different colors
    private Color bg = new Color(15, 20, 23);
    private Color gray = new Color(75,75,75);
    private Color red = new Color(219, 58, 52);


    public TraderPanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) {
        this.setSize(1600, 900);
        this.setBackground(bg);
        this.setLayout(new BorderLayout());

        tradePanel = new  TradePanel();
        inventoryPanel = new InventoryPanel();
        wishlistPanel = new WishlistPanel();
        notificationsPanel = new NotificationsPanel();
        searchPanel = new SearchPanel();

        menuContainer = new JPanel(new GridLayout(8,1));
        menuContainer.setBackground(bg);

        usernameTitle = new JLabel(trader.getUsername());
        usernameTitle.setFont(regular.deriveFont(35f));
        usernameTitle.setForeground(Color.WHITE);
        usernameTitle.setHorizontalAlignment(JLabel.CENTER);

        userIdTitle = new JLabel("ID: #" + trader.getId());
        userIdTitle.setFont(regular.deriveFont(20f));
        userIdTitle.setForeground(gray);
        userIdTitle.setHorizontalAlignment(JLabel.CENTER);

        tradePanelButton = new JButton("Trades");
        tradePanelButton.setFont(bold.deriveFont(25f));
        tradePanelButton.setHorizontalTextPosition(JButton.LEFT);
        tradePanelButton.setForeground(Color.WHITE);
        tradePanelButton.setBackground(gray);
        tradePanelButton.setOpaque(true);
        tradePanelButton.setBorderPainted(false);

        inventoryPanelButton = new JButton("Inventory");
        inventoryPanelButton.setFont(bold.deriveFont(25f));
        inventoryPanelButton.setForeground(Color.WHITE);
        inventoryPanelButton.setBackground(gray);
        inventoryPanelButton.setOpaque(true);
        inventoryPanelButton.setBorderPainted(false);

        wishlistPanelButton = new JButton("Wishlist");
        wishlistPanelButton.setFont(bold.deriveFont(25f));
        wishlistPanelButton.setForeground(Color.WHITE);
        wishlistPanelButton.setBackground(gray);
        wishlistPanelButton.setOpaque(true);
        wishlistPanelButton.setBorderPainted(false);

        notificationsPanelButton = new JButton("Search");
        notificationsPanelButton.setFont(bold.deriveFont(25f));
        notificationsPanelButton.setForeground(Color.WHITE);
        notificationsPanelButton.setBackground(gray);
        notificationsPanelButton.setOpaque(true);
        notificationsPanelButton.setBorderPainted(false);

        searchPanelButton = new JButton("Notifications");
        searchPanelButton.setFont(bold.deriveFont(25f));
        searchPanelButton.setForeground(Color.WHITE);
        searchPanelButton.setBackground(gray);
        searchPanelButton.setOpaque(true);
        searchPanelButton.setBorderPainted(false);


        logoutButton = new JButton("Logout");
        logoutButton.setFont(boldItalic.deriveFont(25f));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(red);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);

        menuContainer.add(usernameTitle);
        menuContainer.add(userIdTitle);
        menuContainer.add(tradePanelButton);
        menuContainer.add(inventoryPanelButton);
        menuContainer.add(wishlistPanelButton);
        menuContainer.add(notificationsPanelButton);
        menuContainer.add(searchPanelButton);
        menuContainer.add(logoutButton);

        this.add(menuContainer, BorderLayout.WEST);
        
    }
}