package frontend;

import javax.swing.*;

import java.awt.*;

public class LoginPanel extends JPanel {

    private JLabel title, usernameTitle, passwordTitle, loginNotification, copyright;
    protected JTextField usernameInput;
    protected JPasswordField passwordInput;
    private JPanel buttonContainer, info, inputs;
    private GridBagConstraints gbc;
    protected JButton loginButton, registerButton;


    // Color Palette
    private Color bg = new Color(15, 20, 23);
    private Color red = new Color(219, 58, 52);
    private Color blue = new Color(8, 76, 97);
    private Color purple = new Color(121,35,89);

    public LoginPanel(Font regular, Font bold, Font italic, Font boldItalic) {
        this.setSize(480, 720);
        this.setBorder(BorderFactory.createEmptyBorder(60, 50, 20, 50));
        // this.setBackground(bg);
        this.setLayout(new GridLayout(4, 1));
        this.setOpaque(false);

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
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputs.add(passwordInput, gbc);

        //TODO: Remove/Uncomment before submitting/pushing
        // usernameInput = new JTextField("navn");
        // passwordInput = new JPasswordField("Password123");

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
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonContainer.add(loginButton, gbc);

        registerButton = new JButton("Register");
        registerButton.setFont(bold.deriveFont(20f));
        registerButton.setForeground(Color.RED);
        registerButton.setOpaque(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
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
    
}