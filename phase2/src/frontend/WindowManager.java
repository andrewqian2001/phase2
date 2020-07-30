package frontend;

import backend.models.users.Trader;
import backend.models.users.Admin;
import backend.models.users.User;
import frontend.panels.AdminPanel;
import frontend.panels.TraderPanel;
import frontend.panels.ImagePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This is used to manage settings in the overall window itself
 */
public class WindowManager extends JFrame {
    protected Font regular, bold, italic, boldItalic;
    private final LoginPanel loginPanel;
    private JPanel userPanel;
    private final BufferedImage loginBg = ImageIO.read(new File("./src/frontend/images/LoginPanelBg.jpg")),
            adminBg = ImageIO.read(new File("./src/frontend/images/IconAdmin.jpg")),
            traderBg = ImageIO.read(new File("./src/frontend/images/IconTrader.jpg"));

    /**
     * This is where initial settings that affects the entire window is at
     * @throws IOException if logging causes issues
     * @throws FontFormatException if the font is bad
     */
    public WindowManager() throws IOException, FontFormatException {
        regular = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Regular.ttf"));
        bold = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Bold.ttf"));
        italic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Italic.ttf"));
        boldItalic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-BoldItalic.ttf"));
        loginPanel = new LoginPanel(regular, bold, italic, boldItalic);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(new ImagePanel(loginBg));
        this.add(loginPanel, BorderLayout.CENTER);
        this.setSize(loginPanel.getSize());
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }

    /**
     * Changes from login screen to the actual dashboard
     * @param loggedInUser the user that is logged in
     * @throws IOException if login causes issues
     */
    public void login(User loggedInUser) throws IOException {
        if (loggedInUser instanceof Trader) {
            userPanel = new TraderPanel((Trader) loggedInUser, regular, bold, italic, boldItalic);
            this.setContentPane(new ImagePanel(traderBg));
        } else {
            userPanel = new AdminPanel((Admin) loggedInUser, regular, bold, italic, boldItalic);
            this.setContentPane(new ImagePanel(adminBg));
        }
        this.remove(loginPanel);
        this.add(userPanel, BorderLayout.CENTER);
        this.setSize(userPanel.getSize());
        this.setResizable(false);
    }

    /**
     * Puts the window back on the login screen
     */
    public void logout() {
        this.remove(userPanel);
        this.setContentPane(new ImagePanel(loginBg));
        this.add(loginPanel, BorderLayout.CENTER);
        this.setSize(loginPanel.getSize());
        this.setResizable(false);
    }

    /**
     * Sets the window to visible
     */
    public void run() {
        this.setVisible(true);
    }
}