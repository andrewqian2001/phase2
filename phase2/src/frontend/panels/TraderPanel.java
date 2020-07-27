package frontend.panels;

import frontend.WindowManager;
import frontend.panels.trader_subpanels.*;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import backend.models.users.Trader;

public class TraderPanel extends JPanel implements ActionListener {

    private JLabel usernameTitle, userIdTitle, iconText, gap;
    private JPanel tradePanel, itemsPanel, notificationsPanel, searchPanel, menuContainer,
            menuPanelContainer;
    private JButton tradePanelButton, itemsPanelButton, notificationsPanelButton,
            searchPanelButton, logoutButton;
    private CardLayout cardLayout;
    private GridBagConstraints gbc;

    private Color bg = new Color(214, 214, 214);
    private Color current = new Color(159, 159, 159);
    private Color gray = new Color(75, 75, 75);
    private Color red = new Color(219, 58, 52);

    public TraderPanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        this.setSize(1600, 900);
        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        tradePanel = new TradePanel(trader, regular, bold, italic, boldItalic);
        itemsPanel = new ItemsPanel(); //TODO: FIX
        notificationsPanel = new NotificationsPanel(trader, regular, bold, italic, boldItalic);
        searchPanel = new SearchPanel(trader, regular, bold, italic, boldItalic);

        menuContainer = new JPanel(new GridBagLayout());
        menuContainer.setPreferredSize(new Dimension(250, this.getHeight()));
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        menuContainer.setOpaque(false);

        menuPanelContainer = new JPanel();
        cardLayout = new CardLayout();
        menuPanelContainer.setLayout(cardLayout);
        menuPanelContainer.setBackground(bg);

        iconText = new JLabel(trader.getUsername().toUpperCase().substring(0, 1));
        iconText.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        iconText.setFont(boldItalic.deriveFont(55f));
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

        userIdTitle = new JLabel("<html><pre>ID: #" + trader.getId().substring(trader.getId().length() - 12) + "</pre></html>");
        userIdTitle.setFont(regular.deriveFont(20f));
        userIdTitle.setForeground(gray);
        userIdTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 2;
        gbc.weighty = 0.01;
        gbc.insets = new Insets(0,0,10,0);
        menuContainer.add(userIdTitle, gbc);

        tradePanelButton = new JButton("Trades");
        tradePanelButton.setHorizontalAlignment(SwingConstants.LEFT);
        tradePanelButton.setFont(regular.deriveFont(30f));
        tradePanelButton.setForeground(Color.BLACK);
        tradePanelButton.setBackground(current);
        tradePanelButton.setOpaque(true);
        tradePanelButton.setBorderPainted(false);
        tradePanelButton.addActionListener(this);
        gbc.gridy = 3;
        gbc.weighty = 0.14;
        gbc.insets = new Insets(0,0,0,0);
        menuContainer.add(tradePanelButton, gbc);

        itemsPanelButton = new JButton("Items");
        itemsPanelButton.setHorizontalAlignment(SwingConstants.LEFT);
        itemsPanelButton.setFont(regular.deriveFont(30f));
        itemsPanelButton.setForeground(Color.BLACK);
        itemsPanelButton.setBackground(current);
        itemsPanelButton.setOpaque(false);
        itemsPanelButton.setBorderPainted(false);
        itemsPanelButton.addActionListener(this);
        gbc.gridy = 4;
        menuContainer.add(itemsPanelButton, gbc);

        notificationsPanelButton = new JButton("Notifications");
        notificationsPanelButton.setHorizontalAlignment(SwingConstants.LEFT);
        notificationsPanelButton.setFont(regular.deriveFont(30f));
        notificationsPanelButton.setForeground(Color.BLACK);
        notificationsPanelButton.setBackground(current);
        notificationsPanelButton.setOpaque(false);
        notificationsPanelButton.setBorderPainted(false);
        notificationsPanelButton.addActionListener(this);
        gbc.gridy = 5;
        menuContainer.add(notificationsPanelButton, gbc);

        searchPanelButton = new JButton("Search");
        searchPanelButton.setHorizontalAlignment(SwingConstants.LEFT);
        searchPanelButton.setFont(regular.deriveFont(30f));
        searchPanelButton.setForeground(Color.BLACK);
        searchPanelButton.setBackground(current);
        searchPanelButton.setOpaque(false);
        searchPanelButton.setBorderPainted(false);
        searchPanelButton.addActionListener(this);
        gbc.gridy = 6;
        menuContainer.add(searchPanelButton, gbc);

        gap = new JLabel("    ");
        gap.setFont(regular.deriveFont(30f));
        gbc.gridy = 7;
        menuContainer.add(gap, gbc);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(boldItalic.deriveFont(25f));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(red);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> {
            try {
                ((WindowManager) SwingUtilities.getWindowAncestor(this)).logout();
            } catch(IOException ex) {
                System.out.println(ex.getMessage());
            }
        }); 
        gbc.gridy = 8;
        menuContainer.add(logoutButton, gbc);

        menuPanelContainer.add(tradePanel, "Trades");
        menuPanelContainer.add(itemsPanel, "Items");
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