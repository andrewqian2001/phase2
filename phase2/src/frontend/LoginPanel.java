package frontend;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.*;

public class LoginPanel extends JPanel {

    private JLabel title, usernameTitle, passwordTitle;
    private JTextField usernameInput;
    private JPasswordField passwordInput;
    private JPanel buttonContainer;
    private JButton loginButton, registerButton;

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

        /* loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.printf("USERNAME=%s\tPASSWORD=%s\n", usernameInput.getText(),
                        String.valueOf(passwordInput.getPassword()));
                try {
                    User loggedInUser = loginManager.login(usernameInput.getText(),
                            String.valueOf(passwordInput.getPassword()));
                    System.out.println(loggedInUser.getId());
                } catch (UserNotFoundException exception) {
                    System.out.println(exception.getMessage());
                }
            }
        }); */

        registerButton = new JButton("Register");
        registerButton.setFont(bold.deriveFont(25f));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(red);
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        buttonContainer.add(registerButton);

        /* registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.printf("USERNAME=%s\tPASSWORD=%s\n", usernameInput.getText(), String.valueOf(passwordInput.getPassword()));
                try {
                    User loggedInUser = loginManager.registerUser(usernameInput.getText(), String.valueOf(passwordInput.getPassword()), UserTypes.TRADER);
                    System.out.println(loggedInUser.getId());
                } catch(BadPasswordException | UserAlreadyExistsException exception) {
                    System.out.println(exception.getMessage());
                }
            }
        }); */

        this.add(title);
        this.add(usernameTitle);
        this.add(usernameInput);
        this.add(passwordTitle);
        this.add(passwordInput);
        this.add(buttonContainer);
    }
    
}