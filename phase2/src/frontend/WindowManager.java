package frontend;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.UserTypes;
import backend.tradesystem.general_managers.LoginManager;
import frontend.panels.admin_panel.AdminPanel;
import frontend.panels.trader_panel.TraderPanel;
import frontend.panels.login_panel.LoginPanel;

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
    private final LoginManager loginManager = new LoginManager();
    private boolean infiltraded;

    /**
     * This is where initial settings that affects the entire window is at
     *
     * @throws IOException         if logging causes issues
     * @throws FontFormatException if the font is bad
     */
    public WindowManager() throws IOException, FontFormatException {
        regular = Font.createFont(Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("./fonts/IBMPlexSans-Regular.ttf"));
        bold = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Bold.ttf"));
        italic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Italic.ttf"));
        boldItalic = Font.createFont(Font.TRUETYPE_FONT,
                getClass().getResourceAsStream("./fonts/IBMPlexSans-BoldItalic.ttf"));
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
     *
     * @param loggedInUserId the user id that is logged in
     * @throws IOException            if login causes issues
     * @throws TradeNotFoundException
     */
    public void login(String loggedInUserId) throws IOException, TradeNotFoundException {
        try {
            if (loggedInUserId.equals("") || loginManager.getType(loggedInUserId).equals(UserTypes.TRADER)) {
                userPanel = new TraderPanel(loggedInUserId, regular, bold, italic, boldItalic, infiltraded);
                this.setContentPane(new ImagePanel(traderBg));
            } else {
                userPanel = new AdminPanel(loggedInUserId, regular, bold, italic, boldItalic);
                this.setContentPane(new ImagePanel(adminBg));
            }
            this.add(userPanel, BorderLayout.CENTER);
            this.setSize(userPanel.getSize());
            this.setResizable(false);
        } catch (UserNotFoundException | AuthorizationException e) {
            e.printStackTrace();
        }
        this.remove(loginPanel);
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


    private static class ImagePanel extends JComponent {
        private final Image image;

        public ImagePanel(Image image) {
            this.image = image;
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this);
        }
    }

    /**
     * Sets infiltraded to true (this WindowManager was created by an admin to infiltrade)
     */
    public void setInfiltraded() {
        infiltraded = true;
    }
}
