package frontend.panels.trader_subpanels;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import backend.exceptions.AuthorizationException;
import backend.exceptions.BadPasswordException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.Trader;
import backend.tradesystem.managers.LoginManager;
import backend.tradesystem.managers.TraderManager;
import backend.tradesystem.managers.UserQuery;

public class SettingsPanel extends JPanel {
    private Font regular, bold, italic, boldItalic;
    private String userId;

    private JLabel settingsTitleLabel;
    private JPanel changePasswordPanel, changeCityPanel, changeUsernamePanel, goIdlePanel;

    private TraderManager traderManager;
    private LoginManager loginManager;
    private final UserQuery userQuery = new UserQuery();

    private Color bg = new Color(51, 51, 51);
    private Color gray = new Color(196, 196, 196);
    private Color gray2 = new Color(142, 142, 142);
    private Color green = new Color(27, 158, 36);
    private Color red = new Color(219, 58, 52);

    public SettingsPanel(String userId, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {

        this.setBackground(bg);
        this.setBorder(BorderFactory.createEmptyBorder(25, 0, 100, 25));

        this.userId = userId;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        traderManager = new TraderManager();
        loginManager = new LoginManager();

        settingsTitleLabel = new JLabel("Account Settings");
        settingsTitleLabel.setFont(regular.deriveFont(35f));
        settingsTitleLabel.setPreferredSize(new Dimension(1200, 75));
        settingsTitleLabel.setForeground(Color.WHITE);
        settingsTitleLabel.setOpaque(false);

        try {
            changeUsernamePanel = getChangeUsernamePanel();
            changePasswordPanel = getChangePasswordPanel();
            changeCityPanel = getChangeCityPanel();
            goIdlePanel = getGoIdlePanel();

            this.add(settingsTitleLabel);
            this.add(changeUsernamePanel);
            this.add(changePasswordPanel);
            this.add(changeCityPanel);
            this.add(goIdlePanel);
        } catch (UserNotFoundException | AuthorizationException e) {
            e.printStackTrace();
        }
    }

    private JPanel getChangeUsernamePanel() throws UserNotFoundException {
        JPanel changeNamePanel = new JPanel(new GridLayout(1, 4));
        changeNamePanel.setPreferredSize(new Dimension(1200, 100));
        changeNamePanel.setBackground(gray2);

        JLabel changeUsernameLabel = new JLabel("Change Username:");
        changeUsernameLabel.setFont(italic.deriveFont(25f));
        changeUsernameLabel.setForeground(Color.BLACK);
        changeUsernameLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        changeUsernameLabel.setOpaque(false);

        JTextField changeUsername = new JTextField(userQuery.getUsername(userId));
        changeUsername.setFont(regular.deriveFont(25f));
        changeUsername.setForeground(Color.BLACK);
        changeUsername.setBackground(gray);
        changeUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(15, 25, 15, 50, gray2), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton changeUsernameButton = new JButton("Submit");
        changeUsernameButton.setFont(bold.deriveFont(20f));
        changeUsernameButton.setBackground(green);
        changeUsernameButton.setForeground(Color.WHITE);
        changeUsernameButton.setBorder(BorderFactory.createMatteBorder(15, 50, 15, 25, gray2));
        changeUsernameButton.addActionListener(e -> {
            if (changeUsername.getText().trim().length() != 0) {
                try {
                    loginManager.changeUsername(userId, changeUsername.getText());
                    changeUsernameLabel.setFont(regular.deriveFont(25f));
                    changeUsernameLabel.setText("Username Changed!");
                    changeUsername.setText("");
                } catch (UserNotFoundException | UserAlreadyExistsException e1) {
                    changeUsernameLabel.setFont(boldItalic.deriveFont(22.5f));
                    changeUsernameLabel.setText("'" + changeUsername.getText().trim() + "' is taken");
                }
            }
        });

        changeNamePanel.add(changeUsernameLabel);
        changeNamePanel.add(changeUsername);
        changeNamePanel.add(changeUsernameButton);

        return changeNamePanel;

    }

    private JPanel getChangePasswordPanel() throws UserNotFoundException {
        JPanel changePassPanel = new JPanel(new GridLayout(1, 4));
        changePassPanel.setPreferredSize(new Dimension(1200, 100));
        changePassPanel.setBackground(gray2);

        JLabel changePasswordLabel = new JLabel("Change Password:");
        changePasswordLabel.setFont(italic.deriveFont(25f));
        changePasswordLabel.setForeground(Color.BLACK);
        changePasswordLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        changePasswordLabel.setOpaque(false);

        JPasswordField changePassword = new JPasswordField(userQuery.getPassword(userId));
        changePassword.setFont(regular.deriveFont(25f));
        changePassword.setForeground(Color.BLACK);
        changePassword.setBackground(gray);
        changePassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(15, 25, 15, 50, gray2), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton changePasswordButton = new JButton("Submit");
        changePasswordButton.setFont(bold.deriveFont(20f));
        changePasswordButton.setBackground(green);
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setBorder(BorderFactory.createMatteBorder(15, 50, 15, 25, gray2));
        changePasswordButton.addActionListener(e -> {
            try {
                loginManager.changePassword(userId, String.valueOf(changePassword.getPassword()));
                changePasswordLabel.setFont(regular.deriveFont(25f));
                changePasswordLabel.setText("Password Changed!");
                changePassword.setText("");
            } catch (UserNotFoundException | BadPasswordException e1) {
                changePasswordLabel.setFont(boldItalic.deriveFont(20f));
                changePasswordLabel.setText(e1.getMessage());
            }
        });

        changePassPanel.add(changePasswordLabel);
        changePassPanel.add(changePassword);
        changePassPanel.add(changePasswordButton);

        return changePassPanel;

    }

    private JPanel getChangeCityPanel() throws UserNotFoundException, AuthorizationException {
        JPanel cityPanel = new JPanel(new GridLayout(1, 3));
        cityPanel.setPreferredSize(new Dimension(1200, 100));
        cityPanel.setBackground(gray2);

        JLabel changeCityLabel = new JLabel("Change City:");
        changeCityLabel.setFont(italic.deriveFont(25f));
        changeCityLabel.setForeground(Color.BLACK);
        changeCityLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        changeCityLabel.setOpaque(false);

        JTextField changeCity = new JTextField(userQuery.getCity(userId));
        changeCity.setFont(regular.deriveFont(25f));
        changeCity.setForeground(Color.BLACK);
        changeCity.setBackground(gray);
        changeCity.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(15, 25, 15, 50, gray2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton changeCityButton = new JButton("Submit");
        changeCityButton.setFont(bold.deriveFont(20f));
        changeCityButton.setBackground(green);
        changeCityButton.setForeground(Color.WHITE);
        changeCityButton.setBorder(BorderFactory.createMatteBorder(15, 50, 15, 25, gray2));
        changeCityButton.addActionListener(e -> {
            if (changeCity.getText().trim().length() != 0) {
                try {
                    traderManager.setCity(userId, changeCity.getText());
                    changeCityLabel.setText("City Changed!");
                    changeCity.setText("");
                } catch (UserNotFoundException | AuthorizationException e1) {
                    System.out.println(e1.getMessage());
                }
            }
        });

        cityPanel.add(changeCityLabel);
        cityPanel.add(changeCity);
        cityPanel.add(changeCityButton);

        return cityPanel;
    }

    private JPanel getGoIdlePanel() {
        JPanel idlePanel = new JPanel(new GridLayout(1, 3));
        idlePanel.setPreferredSize(new Dimension(1200, 150));
        idlePanel.setBackground(gray2);
        idlePanel.setBorder(BorderFactory.createMatteBorder(50, 0, 0, 0, bg));

        JLabel idleLabel = new JLabel("Idle Mode");
        idleLabel.setFont(bold.deriveFont(25f));
        idleLabel.setForeground(Color.BLACK);
        idleLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        idleLabel.setOpaque(false);

        JLabel errMsg = new JLabel("You cannot trade in this mode.");
        errMsg.setFont(italic.deriveFont(25f));
        errMsg.setForeground(Color.BLACK);
        errMsg.setOpaque(false);

        JButton goIdleButton = new JButton("Toggle Idle");
        goIdleButton.setFont(bold.deriveFont(20f));
        goIdleButton.setBackground(red);
        goIdleButton.setForeground(Color.WHITE);
        goIdleButton.setBorder(BorderFactory.createMatteBorder(15, 50, 15, 25, gray2));
        goIdleButton.addActionListener(e -> {
            try {
                traderManager.setIdle(userId, !userQuery.isIdle(userId));
                goIdleButton.setBackground(bg);
                goIdleButton.setFont(boldItalic.deriveFont(20f));
                goIdleButton.setText("Activated");
                goIdleButton.setSelected(true);
            } catch (UserNotFoundException | AuthorizationException e1) {
                errMsg.setFont(boldItalic.deriveFont(20f));
                errMsg.setText(e1.getMessage());
            };
        });

        idlePanel.add(idleLabel);
        idlePanel.add(errMsg);
        idlePanel.add(goIdleButton);
        // idlePanel.add();
        return idlePanel;
    }
}