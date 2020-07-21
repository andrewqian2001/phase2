package frontend.panels;

import frontend.panels.trader_subpanels.*;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

import backend.models.users.Trader;

public class TraderPanel extends JPanel implements ActionListener{

    private JLabel usernameTitle, userIdTitle;
    private JPanel tradePanel, inventoryPanel, wishlistPanel, notificationsPanel, searchPanel, menuContainer, menuPanelContainer;
    private JButton tradePanelButton, inventoryPanelButton, wishlistPanelButton, notificationsPanelButton,
            searchPanelButton, logoutButton;
    private CardLayout cardLayout;


    //TODO: Set different colors
    private Color bg = new Color(214, 214, 214);
    private Color current = new Color (159,159,159);
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

        menuPanelContainer = new JPanel();
        cardLayout = new CardLayout();
        menuPanelContainer.setLayout(cardLayout);
        menuPanelContainer.setBackground(bg);

        usernameTitle = new JLabel((trader.getUsername().length() > 12 ? trader.getUsername().substring(0,12) + "..." : trader.getUsername()));
        usernameTitle.setFont(regular.deriveFont(35f));
        usernameTitle.setForeground(Color.BLACK);
        usernameTitle.setBorder(BorderFactory.createEmptyBorder(0,10,10,0));
        usernameTitle.setHorizontalAlignment(JLabel.CENTER);

        userIdTitle = new JLabel("ID: #" + trader.getId().substring(trader.getId().length() - 12));
        userIdTitle.setFont(regular.deriveFont(20f));
        userIdTitle.setForeground(gray);
        userIdTitle.setHorizontalAlignment(JLabel.CENTER);

        tradePanelButton = new JButton("Trades");
        tradePanelButton.setFont(bold.deriveFont(25f));
        tradePanelButton.setForeground(Color.BLACK);
        tradePanelButton.setBackground(current);
        tradePanelButton.setOpaque(true);
        tradePanelButton.setBorderPainted(false);
        tradePanelButton.addActionListener(this);

        inventoryPanelButton = new JButton("Inventory");
        inventoryPanelButton.setFont(bold.deriveFont(25f));
        inventoryPanelButton.setForeground(Color.BLACK);
        inventoryPanelButton.setBackground(current);
        inventoryPanelButton.setOpaque(false);
        inventoryPanelButton.setBorderPainted(false);
        inventoryPanelButton.addActionListener(this);

        wishlistPanelButton = new JButton("Wishlist");
        wishlistPanelButton.setFont(bold.deriveFont(25f));
        wishlistPanelButton.setForeground(Color.BLACK);
        wishlistPanelButton.setBackground(current);
        wishlistPanelButton.setOpaque(false);
        wishlistPanelButton.setBorderPainted(false);
        wishlistPanelButton.addActionListener(this);

        notificationsPanelButton = new JButton("Notifications");
        notificationsPanelButton.setFont(bold.deriveFont(25f));
        notificationsPanelButton.setForeground(Color.BLACK);
        notificationsPanelButton.setBackground(current);
        notificationsPanelButton.setOpaque(false);
        notificationsPanelButton.setBorderPainted(false);
        notificationsPanelButton.addActionListener(this);

        searchPanelButton = new JButton("Search");
        searchPanelButton.setFont(bold.deriveFont(25f));
        searchPanelButton.setForeground(Color.BLACK);
        searchPanelButton.setBackground(current);
        searchPanelButton.setOpaque(false);
        searchPanelButton.setBorderPainted(false);
        searchPanelButton.addActionListener(this);
        

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

        menuPanelContainer.add(tradePanel, "Trades");
        menuPanelContainer.add(inventoryPanel, "Inventory");
        menuPanelContainer.add(wishlistPanel, "Wishlist");
        menuPanelContainer.add(notificationsPanel, "Notifications");
        menuPanelContainer.add(searchPanel, "Search");


        this.add(menuContainer, BorderLayout.WEST);
        this.add(menuPanelContainer, BorderLayout.CENTER);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        cardLayout.show(menuPanelContainer, e.getActionCommand());

        tradePanelButton.setOpaque(false);
        inventoryPanelButton.setOpaque(false);
        wishlistPanelButton.setOpaque(false);
        notificationsPanelButton.setOpaque(false);
        searchPanelButton.setOpaque(false);

        if (tradePanelButton.equals(e.getSource())) {
            tradePanelButton.setOpaque(true);
        } else if (inventoryPanelButton.equals(e.getSource())) {
            inventoryPanelButton.setOpaque(true);
        } else if (wishlistPanelButton.equals(e.getSource())) {
            wishlistPanelButton.setOpaque(true);
        } else if (notificationsPanelButton.equals(e.getSource())) {
            notificationsPanelButton.setOpaque(true);
        } else {
            searchPanelButton.setOpaque(true);
        }

        menuContainer.repaint();

    }
}