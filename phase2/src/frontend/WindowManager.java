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
import frontend.panels.ImagePanel;

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
    private BufferedImage LoginBg, AdminBg, TraderBg;

    public WindowManager() throws IOException, FontFormatException {
        regular = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Regular.ttf"));
        bold = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Bold.ttf"));
        italic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Italic.ttf"));
        boldItalic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-BoldItalic.ttf"));
        LoginBg = ImageIO.read(new File("phase2/src/frontend/images/LoginPanelBg.jpg"));
        AdminBg = ImageIO.read(new File("phase2/src/frontend/images/IconAdmin.jpg"));
        TraderBg = ImageIO.read(new File("phase2/src/frontend/images/IconTrader.jpg"));
        loginPanel = new LoginPanel(regular, bold, italic, boldItalic);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(new ImagePanel(LoginBg));
        this.add(loginPanel, BorderLayout.CENTER);
        this.setSize(loginPanel.getSize());
        this.setResizable(false);
    }

    public void login() throws IOException {
        if(loggedInUser instanceof Trader) {
            userPanel = new TraderPanel((Trader) loggedInUser, regular, bold, italic, boldItalic);
            this.setContentPane(new ImagePanel(TraderBg));
        } else {
            userPanel = new AdminPanel((Admin) loggedInUser, regular, bold, italic, boldItalic);
            this.setContentPane(new ImagePanel(AdminBg));
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
                loginPanel.notifyLogin("<html><b><i>Username or Password is incorrect.</i></b></html>");
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
            
        });
        loginPanel.registerButton.addActionListener(e -> {
            try {
                WindowManager.this.loggedInUser = loginManager.registerUser(loginPanel.usernameInput.getText(), String.valueOf(loginPanel.passwordInput.getPassword()), UserTypes.TRADER);
                WindowManager.this.login();
            } catch(BadPasswordException exception) { 
                loginPanel.notifyLogin("<html><b><i>Invalid Password: " + exception.getMessage() + "</i></b></html>");
            } catch(UserAlreadyExistsException exception) {
                loginPanel.notifyLogin("<html><b><i>The username '" + loginPanel.usernameInput.getText() + "' is taken.</i></b></html>");
            } catch(IOException exception) {
                System.out.println(exception.getMessage());
            }
            
        });
    }
}