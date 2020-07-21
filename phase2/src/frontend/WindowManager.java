package frontend;

import backend.exceptions.BadPasswordException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.Trader;
import backend.models.users.Admin;
import backend.models.users.User;
import backend.tradesystem.UserTypes;
import backend.tradesystem.managers.LoginManager;
import frontend.panels.AdminPanel;
import frontend.panels.TraderPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WindowManager extends JFrame {
    protected Font regular, bold, italic, boldItalic;
    private LoginPanel loginPanel;
    private JPanel userPanel;
    private User loggedInUser;
    private LoginManager loginManager;

    public WindowManager() throws IOException, FontFormatException {
        regular = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Regular.ttf"));
        bold = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Bold.ttf"));
        italic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Italic.ttf"));
        boldItalic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-BoldItalic.ttf"));
        // BufferedImage myImage = ImageIO.read(new File("./images/LoginPanelBg.jpg"));

        loginPanel = new LoginPanel(regular, bold, italic, boldItalic);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // this.setContentPane(new ImagePanel(myImage));
        this.add(loginPanel, BorderLayout.CENTER);
        this.setSize(loginPanel.getSize());
        this.setResizable(false);
    }

    public void login() {
        if(loggedInUser instanceof Trader) {
            userPanel = new TraderPanel((Trader) loggedInUser, regular, bold, italic, boldItalic);
        } else {
            userPanel = new AdminPanel((Admin) loggedInUser, regular, bold, italic, boldItalic);
        }
        //TODO: Move these into the run() method later
        this.remove(loginPanel);
        this.add(userPanel, BorderLayout.CENTER);
        this.setSize(userPanel.getSize());
        this.setResizable(false);
    }

    public void run() throws IOException {
        this.setVisible(true);

        //TODO: Move to a better place
        loginManager = new LoginManager();
        loginPanel.loginButton.addActionListener(e -> {
            try {
                WindowManager.this.loggedInUser = loginManager.login(loginPanel.usernameInput.getText(), String.valueOf(loginPanel.passwordInput.getPassword()));
                WindowManager.this.login();
            } catch (UserNotFoundException exception) {
                loginPanel.notifyLogin("Username or Password is incorrect.");
            }
            
        });
        loginPanel.registerButton.addActionListener(e -> {
            try {
                WindowManager.this.loggedInUser = loginManager.registerUser(loginPanel.usernameInput.getText(), String.valueOf(loginPanel.passwordInput.getPassword()), UserTypes.TRADER);
                WindowManager.this.login();
            } catch(BadPasswordException exception) { 
                loginPanel.notifyLogin("<html>Invalid Password: " + exception.getMessage() + "</html>");
            } catch(UserAlreadyExistsException exception) {
                loginPanel.notifyLogin("<html>The username '" + loginPanel.usernameInput.getText() + loginPanel.usernameInput
                        .getText() + loginPanel.usernameInput.getText() + "' is taken.</html>");
            }
            
        });
    }
}

class ImagePanel extends JComponent {
    private Image image;
    public ImagePanel(Image image) {
        this.image = image;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}