package frontend;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.*;

public class LoginPanel extends JPanel {

    private JLabel title, usernameTitle, passwordTitle, loginNotification;
    protected JTextField usernameInput;
    protected JPasswordField passwordInput;
    private JPanel buttonContainer;
    protected JButton loginButton, registerButton;


    // Color Palette
    private Color bg = new Color(15, 20, 23);
    private Color red = new Color(219, 58, 52);
    private Color blue = new Color(8, 76, 97);
    private Color purple = new Color(121,35,89);

    public LoginPanel(Font regular, Font bold, Font italic, Font boldItalic) {
        this.setSize(720, 840);
        this.setBorder(BorderFactory.createEmptyBorder(100, 50, 50, 50));
        this.setLayout(new GridLayout(8, 1));
        this.setBackground(bg);

        title = new JLabel("TradeSystem");
        title.setFont(boldItalic.deriveFont(50f));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(JLabel.CENTER);

        usernameTitle = new JLabel("Username:");
        usernameTitle.setFont(italic.deriveFont(25f));
        usernameTitle.setForeground(Color.WHITE);
        usernameTitle.setHorizontalAlignment(JLabel.LEFT);

        usernameInput = new JTextField("navn");
        // usernameInput = new JTextField();
        usernameInput.setFont(regular.deriveFont(20f));

        passwordTitle = new JLabel("Password:");
        passwordTitle.setFont(italic.deriveFont(25f));
        passwordTitle.setForeground(Color.WHITE);
        passwordTitle.setHorizontalAlignment(JLabel.LEFT);

        passwordInput = new JPasswordField("Password123");
        // passwordInput = new JPasswordField();

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
        registerButton.setBackground(purple);
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        buttonContainer.add(registerButton);

        loginNotification = new JLabel();
        loginNotification.setFont(boldItalic.deriveFont(20f));
        loginNotification.setForeground(red);
        loginNotification.setHorizontalAlignment(JLabel.CENTER);
        loginNotification.setVisible(false);

        this.add(title);
        this.add(usernameTitle);
        this.add(usernameInput);
        this.add(passwordTitle);
        this.add(passwordInput);
        this.add(buttonContainer);
        this.add(loginNotification);

    }

    public void notifyLogin(String msg) {
        loginNotification.setText(msg);
        loginNotification.setVisible(true);
    }
    
}