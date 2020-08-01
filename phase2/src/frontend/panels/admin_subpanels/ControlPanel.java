package frontend.panels.admin_subpanels;

import backend.exceptions.AuthorizationException;
import backend.exceptions.BadPasswordException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.Trader;
import backend.tradesystem.UserTypes;
import backend.tradesystem.general_managers.LoginManager;
import backend.tradesystem.trader_managers.TraderManager;
import frontend.WindowManager;
import frontend.controllers.AdminController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ControlPanel extends JPanel implements ActionListener {

    private Color bg = new Color(51, 51, 51);
    private String userId;
    private final LoginManager loginManager = new LoginManager();
    private final TraderManager traderManager = new TraderManager();
    private JPanel titles, splitContainer, tradeSettings, info, newAdmin, input;
    private JLabel tradeSettingsTitle, newAdminTitle, minLend, tradeLimit, incompleteLimit, username, password, errorMessage, ah;
    private JComboBox<Integer> minLendChoice, tradeLimitChoice, incompleteLimitChoice;
    private GridBagConstraints gbc;
    private JButton submitSettings, submitAdmin;
    private JTextField usernameInput;
    private JPasswordField passwordInput;

    public ControlPanel(String userId, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        this.userId = userId;
        this.setSize(1200, 900);
        this.setBackground(Color.BLACK);
        this.setBorder(new EmptyBorder(50,50,100,50));

        titles = new JPanel( new GridLayout(1,2, 100, 0));
        titles.setOpaque(false);
        titles.setPreferredSize(new Dimension(1200,50));

            tradeSettingsTitle = new JLabel("Trade Settings");
            tradeSettingsTitle.setForeground(Color.white);
            tradeSettingsTitle.setFont(boldItalic.deriveFont(30f));
            tradeSettingsTitle.setHorizontalAlignment(JLabel.LEFT);
            titles.add(tradeSettingsTitle);

            newAdminTitle = new JLabel("Create New Admin");
            newAdminTitle.setForeground(Color.white);
            newAdminTitle.setFont(boldItalic.deriveFont(30f));
            newAdminTitle.setHorizontalAlignment(JLabel.LEFT);
            titles.add(newAdminTitle);

        splitContainer = new JPanel(new GridLayout(1,2, 100, 0));
        splitContainer.setOpaque(false);
        splitContainer.setPreferredSize(new Dimension(1200,400));

            tradeSettings = new JPanel(new GridBagLayout());
            gbc = new GridBagConstraints();
            tradeSettings.setBorder(BorderFactory.createMatteBorder(30, 20, 20, 20, bg));
            tradeSettings.setPreferredSize(new Dimension(450, 300));
            tradeSettings.setBackground(bg);
            splitContainer.add(tradeSettings);

                info = new JPanel(new GridLayout(3,2, 20, 70));
                info.setBorder(BorderFactory.createMatteBorder(0, 0, 20, 0, bg));
                info.setBackground(bg);
                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridy = 0;
                gbc.weighty = 0.9;
                tradeSettings.add(info, gbc);

                    minLend = new JLabel("Minimum to Borrow:");
                    minLend.setForeground(Color.white);
                    minLend.setFont(boldItalic.deriveFont(22f));
                    minLend.setHorizontalAlignment(JLabel.RIGHT);
                    info.add(minLend);

                    Integer[] minLendChoices = {1, 2, 3, 4, 5};
                    minLendChoice = new JComboBox<>(minLendChoices);
                    minLendChoice.setBorder(BorderFactory.createMatteBorder(5,75,5,75,bg));
                    info.add(minLendChoice);

                    tradeLimit = new JLabel("Default Trade Limit:");
                    tradeLimit.setForeground(Color.white);
                    tradeLimit.setFont(boldItalic.deriveFont(22f));
                    tradeLimit.setHorizontalAlignment(JLabel.RIGHT);
                    info.add(tradeLimit);

                    Integer[] tradeLimitChoices = {1, 2, 3, 4, 5};
                    tradeLimitChoice = new JComboBox<>(tradeLimitChoices);
                    tradeLimitChoice.setBorder(BorderFactory.createMatteBorder(5,75,5,75,bg));
                    info.add(tradeLimitChoice);

                    incompleteLimit = new JLabel("Incomplete Trade Limit:");
                    incompleteLimit.setForeground(Color.white);
                    incompleteLimit.setFont(boldItalic.deriveFont(22f));
                    incompleteLimit.setHorizontalAlignment(JLabel.RIGHT);
                    info.add(incompleteLimit);

                    Integer[] incompleteLimitChoices = {1, 2, 3, 4, 5};
                    incompleteLimitChoice = new JComboBox<>(incompleteLimitChoices);
                    incompleteLimitChoice.setBorder(BorderFactory.createMatteBorder(5,75,5,75,bg));
                    info.add(incompleteLimitChoice);

                submitSettings = new JButton("Submit");
                submitSettings.setBorder(BorderFactory.createMatteBorder(0,160,0,160, bg));
                submitSettings.setBackground(Color.green);
                submitSettings.setForeground(Color.WHITE);
                submitSettings.setFont(bold.deriveFont(25f));
                gbc.gridy = 1;
                gbc.weighty = 0.1;
                gbc.insets = new Insets(30,0,0,0);
                tradeSettings.add(submitSettings, gbc);

            newAdmin = new JPanel(new GridBagLayout());
            newAdmin.setPreferredSize(new Dimension(450, 300));
            gbc = new GridBagConstraints();
            newAdmin.setBorder(BorderFactory.createMatteBorder(10, 0, 20, 0, bg));
            newAdmin.setBackground(bg);
            splitContainer.add(newAdmin);

                input = new JPanel(new GridLayout(2,2, 60, 20));
                input.setPreferredSize(new Dimension(450, 180));
                input.setBorder(BorderFactory.createMatteBorder(0, 0, 20, 0, bg));
                input.setBackground(bg);
                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridy = 0;
                gbc.weighty = 0.4;
                newAdmin.add(input, gbc);

                    username = new JLabel("Username:");
                    username.setForeground(Color.white);
                    username.setFont(boldItalic.deriveFont(25f));
                    username.setHorizontalAlignment(JLabel.CENTER);
                    input.add(username);

                    usernameInput = new JTextField();
                    usernameInput.setBorder(BorderFactory.createMatteBorder(25,0,25,0,bg));
                    usernameInput.setFont(regular.deriveFont(25f));
                    input.add(usernameInput);

                    password = new JLabel("Password:");
                    password.setForeground(Color.white);
                    password.setFont(boldItalic.deriveFont(25f));
                    password.setHorizontalAlignment(JLabel.CENTER);
                    input.add(password);

                    passwordInput = new JPasswordField();
                    passwordInput.setBorder(BorderFactory.createMatteBorder(25,0,25,0,bg));
                    passwordInput.setFont(regular.deriveFont(25f));
                    input.add(passwordInput);

                errorMessage = new JLabel();
                errorMessage.setPreferredSize(new Dimension(450, 60));
                errorMessage.setForeground(Color.red);
                errorMessage.setFont(regular.deriveFont(15f));
                errorMessage.setHorizontalAlignment(JLabel.CENTER);
                gbc.gridy = 1;
                gbc.weighty = 0.5;
                newAdmin.add(errorMessage, gbc);

                submitAdmin = new JButton("Submit");
                submitAdmin.setBorder(BorderFactory.createMatteBorder(0,160,0,160, bg));
                submitAdmin.setBackground(Color.green);
                submitAdmin.setForeground(Color.WHITE);
                submitAdmin.setFont(bold.deriveFont(25f));
                submitAdmin.addActionListener(this);
                gbc.gridy = 2;
                gbc.weighty = 0.1;
                gbc.insets = new Insets(30,0,0,0);
                newAdmin.add(submitAdmin, gbc);

        ah = new JLabel("<html><b><i>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. <br>Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. <br>Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. <br>Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</i></b></html>");
        ah.setBackground(Color.black);
        ah.setForeground(Color.white);
        ah.setFont(ah.getFont().deriveFont(20f));
        ah.setBorder(BorderFactory.createMatteBorder(80,0,0,0,Color.black));

        this.add(titles);
        this.add(splitContainer);
        this.add(ah);
    }

    /**
     * Used for displaying some message in the login screen
     * @param msg the message being displayed
     */
    public void notifyLogin(String msg) {
        errorMessage.setText(msg);
        errorMessage.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == submitAdmin) {
            try {
                String loggedInUser = loginManager.registerUser(usernameInput.getText(), String.valueOf(passwordInput.getPassword()), UserTypes.ADMIN);
                if(loginManager.getType(loggedInUser).equals(UserTypes.ADMIN)) {
                    traderManager.setCity(loggedInUser, "Toronto");
                }
                ((WindowManager) SwingUtilities.getWindowAncestor(this)).login(loggedInUser);
            } catch(BadPasswordException ex) {
                notifyLogin("<html><b><i>Invalid Password: " + ex.getMessage() + "</i></b></html>");
            } catch(UserAlreadyExistsException ignored) {
                notifyLogin("<html><b><i>The username '" + usernameInput.getText() + "' is taken.</i></b></html>");
            } catch(IOException | UserNotFoundException | AuthorizationException ex) {
                ex.printStackTrace();
            }
        } else if(e.getActionCommand().equals("Register")) {

        }
    }
}

