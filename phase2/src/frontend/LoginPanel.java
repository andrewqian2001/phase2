package frontend;

import javax.swing.*;

import backend.exceptions.BadPasswordException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.User;
import backend.tradesystem.UserTypes;
import backend.tradesystem.managers.LoginManager;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class LoginPanel extends JPanel implements ActionListener {

    private JLabel title, usernameTitle, passwordTitle, loginNotification, copyright;
    protected JTextField usernameInput;
    protected JPasswordField passwordInput;
    private JPanel buttonContainer, info, inputs;
    private GridBagConstraints gbc;
    protected JButton loginButton, registerButton;
    private LoginManager loginManager;

    // Color Palette
    private Color bg = new Color(15, 20, 23);
    private Color red = new Color(219, 58, 52);
    private Color blue = new Color(8, 76, 97);
    private Color purple = new Color(121,35,89);
    private Color input = new Color(156,156,156);

    public LoginPanel(Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        this.setSize(480, 720);
        this.setBorder(BorderFactory.createEmptyBorder(60, 50, 20, 50));
        // this.setBackground(bg);
        this.setLayout(new GridLayout(4, 1));
        this.setOpaque(false);

        loginManager = new LoginManager();

        title = new JLabel("TradeSystem");
        title.setFont(boldItalic.deriveFont(60f));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(JLabel.CENTER);

        inputs = new JPanel();
        inputs.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        inputs.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,0,40,10);
        inputs.setOpaque(false);

        usernameTitle = new JLabel("Username:");
        usernameTitle.setFont(italic.deriveFont(20f));
        usernameTitle.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputs.add(usernameTitle, gbc);

        usernameInput = new JTextField();
        usernameInput.setFont(regular.deriveFont(20f));
        usernameInput.setBackground(input);
        usernameInput.setForeground(Color.BLACK);
        usernameInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputs.add(usernameInput, gbc);

        gbc.insets = new Insets(0,0,0,10);

        passwordTitle = new JLabel("Password:");
        passwordTitle.setFont(italic.deriveFont(20f));
        passwordTitle.setForeground(Color.WHITE);
        passwordTitle.setHorizontalAlignment(JLabel.LEFT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        inputs.add(passwordTitle, gbc);

        passwordInput = new JPasswordField();
        passwordInput.setFont(regular.deriveFont(20f));
        passwordInput.setBackground(input);
        passwordInput.setForeground(Color.BLACK);
        passwordInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputs.add(passwordInput, gbc);

        //TODO: REMOVE before final deployment (only for testing)
        usernameInput = new JTextField("trader2");
        passwordInput = new JPasswordField("userPassword1");

        buttonContainer = new JPanel();
        buttonContainer.setLayout(new GridBagLayout());
        buttonContainer.setOpaque(false);

        gbc.insets = new Insets(0,80,0,80);
        loginButton = new JButton("Login");
        loginButton.setForeground(new Color(98,123,255));
        loginButton.setFont(bold.deriveFont(20f));
        loginButton.setOpaque(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonContainer.add(loginButton, gbc);

        registerButton = new JButton("Register");
        registerButton.setFont(bold.deriveFont(20f));
        registerButton.setForeground(Color.RED);
        registerButton.setOpaque(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
        registerButton.addActionListener(this);
        gbc.gridy = 1;
        buttonContainer.add(registerButton, gbc);

        info = new JPanel();
        info.setLayout(new GridLayout(2,0));
        info.setOpaque(false);

        loginNotification = new JLabel();
        loginNotification.setFont(boldItalic.deriveFont(20f));
        loginNotification.setForeground(red);
        loginNotification.setHorizontalAlignment(JLabel.CENTER);
        loginNotification.setVisible(false);
        info.add(loginNotification);

        copyright = new JLabel("Copyright © 2020 group_56. All rights reserved.");
        copyright.setFont(regular.deriveFont(10f));
        copyright.setForeground(new Color(169,169,169));
        copyright.setHorizontalAlignment(JLabel.CENTER);
        info.add(copyright);

        this.add(title);
        this.add(inputs);
        this.add(buttonContainer);
        this.add(info);

    }

    public void notifyLogin(String msg) {
        loginNotification.setText(msg);
        loginNotification.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Login")) {
            try {
                User loggedInUser = loginManager.login(usernameInput.getText(), String.valueOf(passwordInput.getPassword()));
                ((WindowManager)SwingUtilities.getWindowAncestor(this)).login(loggedInUser);
            } catch (UserNotFoundException exception) {
                notifyLogin("<html><b><i>Username or Password is incorrect.</i></b></html>");
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        } else if(e.getActionCommand().equals("Register")) {
            try {
                User loggedInUser = loginManager.registerUser(usernameInput.getText(), String.valueOf(passwordInput.getPassword()), UserTypes.TRADER);
                ((WindowManager) SwingUtilities.getWindowAncestor(this)).login(loggedInUser);
            } catch(BadPasswordException exception) { 
                notifyLogin("<html><b><i>Invalid Password: " + exception.getMessage() + "</i></b></html>");
            } catch(UserAlreadyExistsException exception) {
                notifyLogin("<html><b><i>The username '" + usernameInput.getText() + "' is taken.</i></b></html>");
            } catch(IOException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
    
}