package frontend;

import javax.swing.*;

import backend.exceptions.BadPasswordException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.UserTypes;
import backend.tradesystem.managers.LoginManager;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Login {
    public void initialize() throws IOException, FontFormatException {

        LoginManager loginManager = new LoginManager();

        //TODO: Move these fonts outside of this class (maybe to windowmanager)
        Font regular = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Regular.ttf"));
        Font bold = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Bold.ttf"));
        Font italic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Italic.ttf"));
        Font boldItalic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-BoldItalic.ttf"));
        

        //Color Palette
        Color backgroundColor = new Color(15, 20, 23);
        Color red = new Color(155, 29, 32);
        Color blue = new Color(34,116,165);

        JFrame frame = new JFrame();
        
        frame.setTitle("Login/Register");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 840);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(100, 50, 50, 50));
        panel.setLayout(new GridLayout(8, 1));
        panel.setBackground(backgroundColor);


        JLabel title = new JLabel("TradeSystem");
        title.setFont(boldItalic.deriveFont(50f));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(JLabel.CENTER);
        panel.add(title);

        
        JLabel usernameTitle = new JLabel("Username:");
        usernameTitle.setFont(italic.deriveFont(25f));
        usernameTitle.setForeground(Color.WHITE);
        usernameTitle.setHorizontalAlignment(JLabel.LEFT);
        panel.add(usernameTitle);

        JTextField usernameInput = new JTextField();
        usernameInput.setFont(regular.deriveFont(20f));
        panel.add(usernameInput);

        JLabel passwordTitle = new JLabel("Password:");
        passwordTitle.setFont(italic.deriveFont(25f));
        passwordTitle.setForeground(Color.WHITE);
        passwordTitle.setHorizontalAlignment(JLabel.LEFT);
        panel.add(passwordTitle);

        JPasswordField passwordInput = new JPasswordField();
        panel.add(passwordInput);

        //TODO: Increase margin between this container and the password input
        JPanel buttonContainer = new JPanel();
        buttonContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonContainer.setBackground(new Color(15, 20, 23));
        buttonContainer.setLayout(new GridLayout(1,2, 20, 0));

        JButton loginButton = new JButton("Login");
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(bold.deriveFont(25f));
        loginButton.setBackground(blue);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        buttonContainer.add(loginButton);

        //TODO: Finish
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.printf("USERNAME=%s\tPASSWORD=%s\n", usernameInput.getText(), String.valueOf(passwordInput.getPassword()));
                try {
                    loginManager.login(usernameInput.getText(), String.valueOf(passwordInput.getPassword()));
                } catch(UserNotFoundException exception) {
                    System.out.println(exception.getMessage());
                }
            }
        });
        
        JButton registerButton = new JButton("Register");
        registerButton.setFont(bold.deriveFont(25f));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(red);
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        buttonContainer.add(registerButton);

        //TODO: Finish    
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.printf("USERNAME=%s\tPASSWORD=%s\n", usernameInput.getText(), String.valueOf(passwordInput.getPassword()));
                try {
                    loginManager.registerUser(usernameInput.getText(), String.valueOf(passwordInput.getPassword()), UserTypes.TRADER);
                } catch(BadPasswordException | UserAlreadyExistsException exception) {
                    System.out.println(exception.getMessage());
                }
            }
        });

        panel.add(buttonContainer);


        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
