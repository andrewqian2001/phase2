package frontend.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalButtonUI;

import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.UserQuery;
import frontend.WindowManager;
import frontend.panels.trader_subpanels.ItemsPanel;
import frontend.panels.trader_subpanels.NotificationsPanel;
import frontend.panels.trader_subpanels.SettingsPanel;
import frontend.panels.trader_subpanels.TradePanel;

public class TraderPanel extends JPanel implements ActionListener {

    private JLabel usernameTitle, userIdTitle, iconText, gap;
    private JPanel tradePanel, itemsPanel, notificationsPanel, searchPanel, settingsPanel, menuContainer,
            menuPanelContainer;
    private JButton tradePanelButton, itemsPanelButton, notificationsPanelButton,
            searchPanelButton, logoutButton, settingsPanelButton;
    private CardLayout cardLayout;
    private GridBagConstraints gbc;
    private final UserQuery userQuery = new UserQuery();

    private Color bg = new Color(214, 214, 214);
    private Color current = new Color(159, 159, 159);
    private Color gray = new Color(75, 75, 75);
    private Color red = new Color(219, 58, 52);

    public TraderPanel(String traderId, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {

        this.setSize(1600, 900);
        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        tradePanel = new TradePanel(traderId, regular, bold, italic, boldItalic);
        itemsPanel = new ItemsPanel(traderId, regular, bold, italic, boldItalic);
        notificationsPanel = new NotificationsPanel(traderId, regular, bold, italic, boldItalic);
        searchPanel = new SearchPanel(traderId, regular, bold, italic, boldItalic);
        settingsPanel = new SettingsPanel(traderId, regular, bold, italic, boldItalic);

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

        try {
            iconText = new JLabel(userQuery.getUsername(traderId).toUpperCase().substring(0, 1));

            iconText.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
            iconText.setFont(boldItalic.deriveFont(55f));
            iconText.setForeground(Color.BLACK);
            iconText.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridy = 0;
            gbc.weighty = 0.16;
            menuContainer.add(iconText, gbc);

            usernameTitle = new JLabel((userQuery.getUsername(traderId).length() > 12 ? userQuery.getUsername(traderId).substring(0, 12) + "..."
                    : userQuery.getUsername(traderId)));
            usernameTitle.setFont(regular.deriveFont(35f));
            usernameTitle.setForeground(Color.BLACK);
            usernameTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
            usernameTitle.setHorizontalAlignment(JLabel.CENTER);
            gbc.gridy = 1;
            gbc.weighty = 0.01;
            menuContainer.add(usernameTitle, gbc);

            userIdTitle = new JLabel("<html><pre>ID: #" + traderId.substring(traderId.length() - 12) + "</pre></html>");
            userIdTitle.setFont(regular.deriveFont(20f));
            userIdTitle.setForeground(gray);
            userIdTitle.setHorizontalAlignment(JLabel.CENTER);
            gbc.gridy = 2;
            gbc.weighty = 0.01;
            gbc.insets = new Insets(0, 0, 10, 0);
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
            gbc.insets = new Insets(0, 0, 0, 0);
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

            settingsPanelButton = new JButton("Settings");
            settingsPanelButton.setHorizontalAlignment(SwingConstants.LEFT);
            settingsPanelButton.setFont(regular.deriveFont(30f));
            settingsPanelButton.setForeground(Color.BLACK);
            settingsPanelButton.setBackground(current);
            settingsPanelButton.setOpaque(false);
            settingsPanelButton.setBorderPainted(false);
            settingsPanelButton.addActionListener(this);
            gbc.gridy = 7;
            menuContainer.add(settingsPanelButton, gbc);

            logoutButton = new JButton("Logout");
            logoutButton.setFont(boldItalic.deriveFont(25f));
            logoutButton.setForeground(Color.WHITE);
            logoutButton.setBackground(red);
            logoutButton.setOpaque(true);
            logoutButton.setBorderPainted(false);
            logoutButton.addActionListener(e -> {
                ((WindowManager) SwingUtilities.getWindowAncestor(this)).logout();
            });
            gbc.gridy = 8;
            menuContainer.add(logoutButton, gbc);

            menuPanelContainer.add(tradePanel, "Trades");
            menuPanelContainer.add(itemsPanel, "Items");
            menuPanelContainer.add(notificationsPanel, "Notifications");
            menuPanelContainer.add(searchPanel, "Search");
            menuPanelContainer.add(settingsPanel, "Settings");

            this.add(menuContainer, BorderLayout.WEST);
            this.add(menuPanelContainer, BorderLayout.CENTER);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
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