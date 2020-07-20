package frontend;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import backend.exceptions.BadPasswordException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.User;
import backend.tradesystem.UserTypes;
import backend.tradesystem.managers.LoginManager;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class LoginPanel extends JPanel {

    private JLabel title, usernameTitle, passwordTitle;
    protected JTextField usernameInput;
    protected JPasswordField passwordInput;
    private JPanel buttonContainer;
    protected JButton loginButton, registerButton;


    // Color Palette
    private Color backgroundColor = new Color(15, 20, 23);
    private Color red = new Color(155, 29, 32);
    private Color blue = new Color(34, 116, 165);

    public LoginPanel(Font regular, Font bold, Font italic, Font boldItalic) {
        this.setSize(720, 840);
        this.setBorder(BorderFactory.createEmptyBorder(100, 50, 50, 50));
        this.setLayout(new GridLayout(8, 1));
        this.setBackground(backgroundColor);

        title = new JLabel("TradeSystem");
        title.setFont(boldItalic.deriveFont(50f));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(JLabel.CENTER);

        usernameTitle = new JLabel("Username:");
        usernameTitle.setFont(italic.deriveFont(25f));
        usernameTitle.setForeground(Color.WHITE);
        usernameTitle.setHorizontalAlignment(JLabel.LEFT);

        usernameInput = new JTextField();
        usernameInput.setFont(regular.deriveFont(20f));

        passwordTitle = new JLabel("Password:");
        passwordTitle.setFont(italic.deriveFont(25f));
        passwordTitle.setForeground(Color.WHITE);
        passwordTitle.setHorizontalAlignment(JLabel.LEFT);

        passwordInput = new JPasswordField();

        buttonContainer = new JPanel();
        buttonContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonContainer.setBackground(new Color(15, 20, 23));
        buttonContainer.setLayout(new GridLayout(1, 2, 20, 0));

        loginButton = new JButton("Login");
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(bold.deriveFont(25f));
        loginButton.setBackground(blue);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        buttonContainer.add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setFont(bold.deriveFont(25f));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(red);
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        buttonContainer.add(registerButton);

        this.add(title);
        this.add(usernameTitle);
        this.add(usernameInput);
        this.add(passwordTitle);
        this.add(passwordInput);
        this.add(buttonContainer);
    }
    
}