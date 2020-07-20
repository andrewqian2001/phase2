package frontend;

import backend.exceptions.BadPasswordException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.UserTypes;
import backend.tradesystem.managers.LoginManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;

public class WindowManager extends JFrame {
    protected Font regular, bold, italic, boldItalic;
    private LoginPanel loginPanel;
    private JPanel userPanel; // either TraderPanel or AdminPanel
    private User loggedInUser;
    private LoginManager loginManager;

    public WindowManager() throws IOException, FontFormatException {
        regular = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Regular.ttf"));
        bold = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Bold.ttf"));
        italic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Italic.ttf"));
        boldItalic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-BoldItalic.ttf"));

        loginPanel = new LoginPanel(regular, bold, italic, boldItalic);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(loginPanel, BorderLayout.CENTER);
        this.setSize(loginPanel.getSize());
    }

    public void login() {
        if(loggedInUser instanceof Trader) {
            userPanel = new TraderPanel();
        } else {
            userPanel = new AdminPanel();
        }
    }

    public void start() throws IOException {
        this.setVisible(true);

        loginManager = new LoginManager();

        loginPanel.loginButton.addActionListener(e -> {
            System.out.printf("USERNAME=%s\tPASSWORD=%s\n", loginPanel.usernameInput.getText(),
                    String.valueOf(loginPanel.passwordInput.getPassword()));
            try {
                WindowManager.this.loggedInUser = loginManager.login(loginPanel.usernameInput.getText(),
                        String.valueOf(loginPanel.passwordInput.getPassword()));
                System.out.println(loggedInUser.getId());
            } catch (UserNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
        });

        loginPanel.registerButton.addActionListener(e -> {
            System.out.printf("USERNAME=%s\tPASSWORD=%s\n", loginPanel.usernameInput.getText(), String.valueOf(loginPanel.passwordInput.getPassword()));
            try {
                WindowManager.this.loggedInUser = loginManager.registerUser(loginPanel.usernameInput.getText(), String.valueOf(loginPanel.passwordInput.getPassword()), UserTypes.TRADER);
                System.out.println(loggedInUser.getId());
            } catch(BadPasswordException | UserAlreadyExistsException exception) {
                System.out.println(exception.getMessage());
            }
        });
    }
}
