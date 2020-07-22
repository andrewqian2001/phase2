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

import backend.models.users.Admin;
import frontend.panels.admin_subpanels.ControlPanel;
import frontend.panels.admin_subpanels.OverviewPanel;

public class AdminPanel extends JPanel implements ActionListener {

    private JLabel usernameTitle, userIdTitle;
    private JPanel overviewPanel, searchPanel, controlPanel, menuContainer,
            menuPanelContainer;
    private JButton overviewPanelButton, searchPanelButton, controlPanelButton, logoutButton;
    private CardLayout cardLayout;

    private Color bg = new Color(51, 51, 51);
    private Color current = new Color(32, 32, 32);
    private Color gray = new Color(75, 75, 75);
    private Color red = new Color(219, 58, 52);

    public AdminPanel(Admin admin, Font regular, Font bold, Font italic, Font boldItalic) {
        this.setSize(1600, 900);
        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        overviewPanel = new OverviewPanel(admin, regular, bold, italic, boldItalic);
        searchPanel = new SearchPanel(admin, regular, bold, italic, boldItalic);
        controlPanel = new ControlPanel(admin, regular, bold, italic, boldItalic);

        menuContainer = new JPanel(new GridBagLayout());
        menuContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;

        menuPanelContainer = new JPanel();
        cardLayout = new CardLayout();
        menuPanelContainer.setLayout(cardLayout);
        menuPanelContainer.setBackground(bg);

        JLabel iconText = new JLabel(admin.getUsername().substring(0, 1));
        iconText.setFont(regular.deriveFont(48f));
        iconText.setForeground(Color.WHITE);
        iconText.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.weighty = 0.16;
        gbc.gridy = 0;
        menuContainer.add(iconText,gbc);

        JPanel info = new JPanel();
        info.setLayout(new GridLayout(2,1));
        info.setOpaque(false);

        usernameTitle = new JLabel((admin.getUsername().length() > 12 ? admin.getUsername().substring(0, 12) + "..."
                : admin.getUsername()));
        usernameTitle.setFont(bold.deriveFont(24f));
        usernameTitle.setForeground(Color.WHITE);
        usernameTitle.setHorizontalAlignment(SwingConstants.CENTER);
        info.add(usernameTitle);

        userIdTitle = new JLabel("ID: #" + admin.getId().substring(admin.getId().length() - 12));
        userIdTitle.setFont(regular.deriveFont(20f));
        userIdTitle.setForeground(gray);
        info.add(userIdTitle);
        gbc.weighty = 0.03;
        gbc.gridy = 1;

        menuContainer.add(info,gbc);

        overviewPanelButton = new JButton("Overview");
        overviewPanelButton.setFont(bold.deriveFont(25f));
        overviewPanelButton.setForeground(Color.WHITE);
        overviewPanelButton.setBackground(current);
        overviewPanelButton.setOpaque(true);
        overviewPanelButton.setBorderPainted(false);
        overviewPanelButton.addActionListener(this);
        gbc.weighty = 0.12;
        gbc.gridy = 2;
        menuContainer.add(overviewPanelButton,gbc);

        controlPanelButton = new JButton("ControlPanel");
        controlPanelButton.setFont(bold.deriveFont(25f));
        controlPanelButton.setForeground(Color.WHITE);
        controlPanelButton.setBackground(current);
        controlPanelButton.setOpaque(false);
        controlPanelButton.setBorderPainted(false);
        controlPanelButton.addActionListener(this);
        gbc.gridy = 3;
        menuContainer.add(controlPanelButton,gbc);

        searchPanelButton = new JButton("Search");
        searchPanelButton.setFont(bold.deriveFont(25f));
        searchPanelButton.setForeground(Color.WHITE);
        searchPanelButton.setBackground(current);
        searchPanelButton.setOpaque(false);
        searchPanelButton.setBorderPainted(false);
        searchPanelButton.addActionListener(this);
        gbc.gridy = 4;
        menuContainer.add(searchPanelButton,gbc);

        gbc.weighty = 0.36;
        JPanel emptyPanel2 = new JPanel();
        emptyPanel2.setOpaque(false);
        gbc.gridy = 5;
        menuContainer.add(emptyPanel2,gbc);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(boldItalic.deriveFont(25f));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(red);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        gbc.weighty = 0.1;
        gbc.gridy = 6;
        menuContainer.add(logoutButton,gbc);

        menuPanelContainer.add(overviewPanel, "Overview");
        menuPanelContainer.add(searchPanel, "Search");
        menuPanelContainer.add(controlPanel, "ControlPanel");

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