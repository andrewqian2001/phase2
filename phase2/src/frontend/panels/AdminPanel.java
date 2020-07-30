package frontend.panels;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import frontend.WindowManager;

import backend.models.users.Admin;
import frontend.panels.admin_subpanels.ControlPanel;
import frontend.panels.admin_subpanels.OverviewPanel;

public class AdminPanel extends JPanel implements ActionListener {

    private JLabel iconText, usernameTitle, userIdTitle;
    private JPanel overviewPanel, searchPanel, controlPanel, menuContainer,
            menuPanelContainer;
    private JButton overviewPanelButton, searchPanelButton, controlPanelButton, logoutButton;
    private CardLayout cardLayout;
    private GridBagConstraints gbc;

    private Color bg = new Color(51, 51, 51);
    private Color current = new Color(32, 32, 32);
    private Color gray = new Color(184, 184, 184);
    private Color red = new Color(219, 58, 52);

    public AdminPanel(Admin admin, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        this.setSize(1600, 900);
        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        overviewPanel = new OverviewPanel(admin, regular, bold, italic, boldItalic);
        searchPanel = new SearchPanel(admin, regular, bold, italic, boldItalic);
        controlPanel = new ControlPanel(admin, regular, bold, italic, boldItalic);

        searchPanel.setBackground(Color.BLACK);

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

        iconText = new JLabel(admin.getUsername().toUpperCase().substring(0, 1));
        iconText.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        iconText.setFont(boldItalic.deriveFont(55f));
        iconText.setForeground(Color.WHITE);
        iconText.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.weighty = 0.16;
        menuContainer.add(iconText, gbc);

        usernameTitle = new JLabel((admin.getUsername().length() > 12 ? admin.getUsername().substring(0, 12) + "..."
                : admin.getUsername()));
        usernameTitle.setFont(regular.deriveFont(35f));
        usernameTitle.setForeground(Color.WHITE);
        usernameTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        usernameTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.weighty = 0.01;
        gbc.gridy = 1;
        menuContainer.add(usernameTitle, gbc);

        userIdTitle = new JLabel("<html><pre>ID: #" + admin.getId().substring(admin.getId().length() - 12) + "</pre></html>");
        userIdTitle.setFont(regular.deriveFont(20f));
        userIdTitle.setForeground(gray);
        userIdTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 2;
        gbc.weighty = 0.01;
        gbc.insets = new Insets(0, 0, 10, 0);
        menuContainer.add(userIdTitle, gbc);

        overviewPanelButton = new JButton("Overview");
        overviewPanelButton.setHorizontalAlignment(SwingConstants.LEFT);
        overviewPanelButton.setFont(regular.deriveFont(30f));
        overviewPanelButton.setForeground(Color.WHITE);
        overviewPanelButton.setBackground(current);
        overviewPanelButton.setOpaque(true);
        overviewPanelButton.setBorderPainted(false);
        overviewPanelButton.addActionListener(this);
        gbc.weighty = 0.14;
        gbc.gridy = 3;
        gbc.insets = new Insets(0,0,0,0);
        menuContainer.add(overviewPanelButton,gbc);

        controlPanelButton = new JButton("Control Panel");
        controlPanelButton.setHorizontalAlignment(SwingConstants.LEFT);
        controlPanelButton.setFont(regular.deriveFont(30f));
        controlPanelButton.setForeground(Color.WHITE);
        controlPanelButton.setBackground(current);
        controlPanelButton.setOpaque(false);
        controlPanelButton.setBorderPainted(false);
        controlPanelButton.addActionListener(this);
        gbc.gridy = 4;
        menuContainer.add(controlPanelButton,gbc);

        searchPanelButton = new JButton("Search");
        searchPanelButton.setHorizontalAlignment(SwingConstants.LEFT);
        searchPanelButton.setFont(regular.deriveFont(30f));
        searchPanelButton.setForeground(Color.WHITE);
        searchPanelButton.setBackground(current);
        searchPanelButton.setOpaque(false);
        searchPanelButton.setBorderPainted(false);
        searchPanelButton.addActionListener(this);
        gbc.gridy = 5;
        menuContainer.add(searchPanelButton,gbc);

        gbc.weighty = 0.28;
        JPanel emptyPanel2 = new JPanel();
        emptyPanel2.setOpaque(false);
        gbc.gridy = 6;
        menuContainer.add(emptyPanel2,gbc);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(boldItalic.deriveFont(25f));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(red);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> {
                ((WindowManager) SwingUtilities.getWindowAncestor(this)).logout();
        });
        gbc.weighty = 0.1;
        gbc.gridy = 7;
        menuContainer.add(logoutButton,gbc);

        menuPanelContainer.add(overviewPanel, "Overview");
        menuPanelContainer.add(searchPanel, "Search");
        menuPanelContainer.add(controlPanel, "Control Panel");

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
                return Color.WHITE;
            }
        });

        menuContainer.repaint();
    }
}