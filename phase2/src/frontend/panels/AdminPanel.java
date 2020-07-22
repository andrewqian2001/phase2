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

    public AdminPanel(Admin admin, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        this.setSize(1600, 900);
        this.setBackground(bg);
        this.setLayout(new BorderLayout());

        overviewPanel = new OverviewPanel();
        searchPanel = new SearchPanel(admin, regular, bold, italic, boldItalic);
        controlPanel = new ControlPanel();

        menuContainer = new JPanel(new GridLayout(8,1));
        menuContainer.setBackground(bg);

        menuPanelContainer = new JPanel();
        cardLayout = new CardLayout();
        menuPanelContainer.setLayout(cardLayout);
        menuPanelContainer.setBackground(bg);

        BufferedImage myImage = ImageIO.read(new File("phase2/src/frontend/images/Icon.png"));
        JLabel image = new JLabel(new ImageIcon(myImage));
        menuContainer.add(image);

        JPanel info = new JPanel();
        info.setLayout(new GridLayout(2,1));
        info.setOpaque(false);

        usernameTitle = new JLabel((admin.getUsername().length() > 12 ? admin.getUsername().substring(0, 12) + "..."
                : admin.getUsername()));
        usernameTitle.setFont(regular.deriveFont(35f));
        usernameTitle.setForeground(Color.WHITE);
        usernameTitle.setHorizontalAlignment(SwingConstants.CENTER);
        info.add(usernameTitle);

        userIdTitle = new JLabel("ID: #" + admin.getId().substring(admin.getId().length() - 12));
        userIdTitle.setFont(regular.deriveFont(20f));
        userIdTitle.setForeground(gray);
        info.add(userIdTitle);

        menuContainer.add(info);

        overviewPanelButton = new JButton("Overview");
        overviewPanelButton.setFont(bold.deriveFont(25f));
        overviewPanelButton.setForeground(Color.WHITE);
        overviewPanelButton.setBackground(current);
        overviewPanelButton.setOpaque(true);
        overviewPanelButton.setBorderPainted(false);
        overviewPanelButton.addActionListener(this);
         menuContainer.add(overviewPanelButton);

        controlPanelButton = new JButton("ControlPanel");
        controlPanelButton.setFont(bold.deriveFont(25f));
        controlPanelButton.setForeground(Color.WHITE);
        controlPanelButton.setBackground(current);
        controlPanelButton.setOpaque(false);
        controlPanelButton.setBorderPainted(false);
        controlPanelButton.addActionListener(this);
        menuContainer.add(controlPanelButton);

        searchPanelButton = new JButton("Search");
        searchPanelButton.setFont(bold.deriveFont(25f));
        searchPanelButton.setForeground(Color.WHITE);
        searchPanelButton.setBackground(current);
        searchPanelButton.setOpaque(false);
        searchPanelButton.setBorderPainted(false);
        searchPanelButton.addActionListener(this);
        menuContainer.add(searchPanelButton);

        JPanel emptyPanel2 = new JPanel();
        emptyPanel2.setOpaque(false);
        menuContainer.add(emptyPanel2);
        JPanel emptyPanel3 = new JPanel();
        emptyPanel3.setOpaque(false);
        menuContainer.add(emptyPanel3);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(boldItalic.deriveFont(25f));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(red);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        menuContainer.add(logoutButton);

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