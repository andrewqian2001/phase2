package frontend.panels;

import frontend.panels.trader_subpanels.*;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import backend.models.users.Trader;

public class TraderPanel extends JPanel implements ActionListener {

    private JLabel usernameTitle, userIdTitle, iconText;
    private JPanel tradePanel, inventoryPanel, wishlistPanel, notificationsPanel, searchPanel, menuContainer,
            menuPanelContainer;
    private JButton tradePanelButton, inventoryPanelButton, wishlistPanelButton, notificationsPanelButton,
            searchPanelButton, logoutButton;
    private CardLayout cardLayout;
    private GridBagConstraints gbc;

    // TODO: Set different colors
    private Color bg = new Color(214, 214, 214);
    private Color current = new Color(159, 159, 159);
    private Color gray = new Color(75, 75, 75);
    private Color red = new Color(219, 58, 52);

    public TraderPanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        this.setSize(1600, 900);
        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        tradePanel = new TradePanel(trader, regular, bold, italic, boldItalic);
        inventoryPanel = new InventoryPanel(trader, regular, bold, italic, boldItalic);
        wishlistPanel = new WishlistPanel(trader, regular, bold, italic, boldItalic);
        notificationsPanel = new NotificationsPanel(trader, regular, bold, italic, boldItalic);
        searchPanel = new SearchPanel(trader, regular, bold, italic, boldItalic);

        menuContainer = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        menuContainer.setOpaque(false);

        menuPanelContainer = new JPanel();
        cardLayout = new CardLayout();
        menuPanelContainer.setLayout(cardLayout);
        menuPanelContainer.setBackground(bg);

        iconText = new JLabel(trader.getUsername().substring(0, 1));
        iconText.setFont(regular.deriveFont(48f));
        iconText.setForeground(Color.BLACK);
        iconText.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.weighty = 0.16;
        menuContainer.add(iconText, gbc);

        usernameTitle = new JLabel((trader.getUsername().length() > 12 ? trader.getUsername().substring(0, 12) + "..."
                : trader.getUsername()));
        usernameTitle.setFont(regular.deriveFont(35f));
        usernameTitle.setForeground(Color.BLACK);
        usernameTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        usernameTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        gbc.weighty = 0.01;
        menuContainer.add(usernameTitle, gbc);

        userIdTitle = new JLabel("ID: #" + trader.getId().substring(trader.getId().length() - 12));
        userIdTitle.setFont(regular.deriveFont(20f));
        userIdTitle.setForeground(gray);
        userIdTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 2;
        gbc.weighty = 0.01;
        gbc.insets = new Insets(0,0,10,0);
        menuContainer.add(userIdTitle, gbc);

        tradePanelButton = new JButton("Trades");
        tradePanelButton.setFont(bold.deriveFont(25f));
        tradePanelButton.setForeground(Color.BLACK);
        tradePanelButton.setBackground(current);
        tradePanelButton.setOpaque(true);
        tradePanelButton.setBorderPainted(false);
        tradePanelButton.addActionListener(this);
        gbc.gridy = 3;
        gbc.weighty = 0.14;
        gbc.insets = new Insets(0,0,0,0);
        menuContainer.add(tradePanelButton, gbc);

        inventoryPanelButton = new JButton("Inventory");
        inventoryPanelButton.setFont(bold.deriveFont(25f));
        inventoryPanelButton.setForeground(Color.BLACK);
        inventoryPanelButton.setBackground(current);
        inventoryPanelButton.setOpaque(false);
        inventoryPanelButton.setBorderPainted(false);
        inventoryPanelButton.addActionListener(this);
        gbc.gridy = 4;
        menuContainer.add(inventoryPanelButton, gbc);

        wishlistPanelButton = new JButton("Wishlist");
        wishlistPanelButton.setFont(bold.deriveFont(25f));
        wishlistPanelButton.setForeground(Color.BLACK);
        wishlistPanelButton.setBackground(current);
        wishlistPanelButton.setOpaque(false);
        wishlistPanelButton.setBorderPainted(false);
        wishlistPanelButton.addActionListener(this);
        gbc.gridy = 5;
        menuContainer.add(wishlistPanelButton, gbc);

        notificationsPanelButton = new JButton("Notifications");
        notificationsPanelButton.setFont(bold.deriveFont(25f));
        notificationsPanelButton.setForeground(Color.BLACK);
        notificationsPanelButton.setBackground(current);
        notificationsPanelButton.setOpaque(false);
        notificationsPanelButton.setBorderPainted(false);
        notificationsPanelButton.addActionListener(this);
        gbc.gridy = 6;
        menuContainer.add(notificationsPanelButton, gbc);

        searchPanelButton = new JButton("Search");
        searchPanelButton.setFont(bold.deriveFont(25f));
        searchPanelButton.setForeground(Color.BLACK);
        searchPanelButton.setBackground(current);
        searchPanelButton.setOpaque(false);
        searchPanelButton.setBorderPainted(false);
        searchPanelButton.addActionListener(this);
        gbc.gridy = 7;
        menuContainer.add(searchPanelButton, gbc);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(boldItalic.deriveFont(25f));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(red);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        gbc.gridy = 8;
        menuContainer.add(logoutButton, gbc);

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
        for (Component button : menuContainer.getComponents()) {
            if (button instanceof JButton && !button.equals(logoutButton)) {
                button.setEnabled(true);
                ((JButton) button).setOpaque(false);
            }
        }
        ((JButton) e.getSource()).setEnabled(false);
        ((JButton) e.getSource()).setOpaque(true);
        ((JButton) e.getSource()).setUI(new MetalButtonUI() {
            protected Color getDisabledTextColor() {
                return Color.BLACK;
            }
        });

        menuContainer.repaint();
    }
}