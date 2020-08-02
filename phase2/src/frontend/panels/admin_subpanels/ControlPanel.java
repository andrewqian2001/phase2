package frontend.panels.admin_subpanels;

import backend.exceptions.BadPasswordException;
import backend.exceptions.UserAlreadyExistsException;
import backend.tradesystem.TraderProperties;
import backend.tradesystem.UserTypes;
import backend.tradesystem.general_managers.LoginManager;
import frontend.WindowManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * This is used to show the different settings that an admin can configure
 */
public class ControlPanel extends JPanel implements ActionListener {

    private final LoginManager loginManager = new LoginManager();
    private final JLabel errorMessage = new JLabel();
    private final JComboBox<Integer> minLendChoice, tradeLimitChoice, incompleteLimitChoice;
    private final JButton submitSettings = new JButton("Submit"), submitAdmin = new JButton("Submit");
    private final JTextField usernameInput = new JTextField();
    private final JPasswordField passwordInput = new JPasswordField();

    /**
     * Makes a new control panel
     *
     * @param userId     the admin id
     * @param regular    regular font
     * @param bold       bold font
     * @param italic     italics font
     * @param boldItalic bold italics font
     * @throws IOException issues with accessing database
     */
    public ControlPanel(String userId, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        Color bg = new Color(51, 51, 51);
        this.setSize(1200, 900);
        this.setBackground(Color.BLACK);
        this.setBorder(new EmptyBorder(50, 50, 100, 50));

        JPanel titles = new JPanel(new GridLayout(1, 2, 100, 0));
        titles.setOpaque(false);
        titles.setPreferredSize(new Dimension(1200, 50));

        createLabel(boldItalic, titles, "Trade Settings", 30f, JLabel.LEFT);
        createLabel(boldItalic, titles, "Create New Admin", 30f, JLabel.LEFT);

        JPanel splitContainer = new JPanel(new GridLayout(1, 2, 100, 0));
        splitContainer.setOpaque(false);
        splitContainer.setPreferredSize(new Dimension(1200, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        JPanel tradeSettings = createTradeSettings(bg, splitContainer);

        JPanel info = createInfoPanel(bg, gbc, tradeSettings);

        createLabel(boldItalic, info, "Minimum to Borrow:", 22f, JLabel.RIGHT);

        Integer[] minLendChoices = new Integer[100];
        for (int i = 1; i < 101; i++) {
            minLendChoices[i - 1] = i;
        }
        minLendChoice = new JComboBox<>(minLendChoices);

        handleInfoSubpanel(bg, info, minLendChoice, TraderProperties.MINIMUM_AMOUNT_NEEDED_TO_BORROW);

        createLabel(boldItalic, info, "Default Trade Limit:", 22f, JLabel.RIGHT);

        Integer[] tradeLimitChoices = new Integer[100];
        for (int i = 1; i < 101; i++) {
            tradeLimitChoices[i - 1] = i;
        }
        tradeLimitChoice = new JComboBox<>(tradeLimitChoices);
        handleInfoSubpanel(bg, info, tradeLimitChoice, TraderProperties.TRADE_LIMIT);

        createLabel(boldItalic, info, "Incomplete Trade Limit:", 22f, JLabel.RIGHT);

        Integer[] incompleteLimitChoices = new Integer[100];
        for (int i = 1; i < 101; i++) {
            incompleteLimitChoices[i - 1] = i;
        }
        incompleteLimitChoice = new JComboBox<>(incompleteLimitChoices);
        handleInfoSubpanel(bg, info, incompleteLimitChoice, TraderProperties.INCOMPLETE_TRADE_LIM);
        createSubmitSettings(bold, bg, gbc, tradeSettings);
        JPanel newAdmin = createNewAdmin(bg, splitContainer);
        gbc = new GridBagConstraints();
        JPanel input = createNewInputForAdmin(bg, gbc, newAdmin);
        createLabel(boldItalic, input, "Username:", 25f, JLabel.CENTER);
        createAccountInputs(regular, boldItalic, bg, input);
        createMessageWrapper(regular, bg, gbc, newAdmin);
        handleSubmitAdmin(bold, bg, gbc, newAdmin);

        JLabel ah = new JLabel("<html><b><i>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. <br>Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. <br>Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. <br>Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</i></b></html>");
        ah.setBackground(Color.black);
        ah.setForeground(Color.white);
        ah.setFont(ah.getFont().deriveFont(20f));
        ah.setBorder(BorderFactory.createMatteBorder(80, 0, 0, 0, Color.black));

        this.add(titles);
        this.add(splitContainer);
        this.add(ah);
    }

    private void handleSubmitAdmin(Font bold, Color bg, GridBagConstraints gbc, JPanel newAdmin) {
        submitAdmin.setBorder(BorderFactory.createMatteBorder(0, 180, 10, 180, bg));
        submitAdmin.setBackground(Color.green);
        submitAdmin.setForeground(Color.WHITE);
        submitAdmin.setFont(bold.deriveFont(25f));
        submitAdmin.addActionListener(this);
        gbc.gridy = 2;
        gbc.weighty = 0.1;
        newAdmin.add(submitAdmin, gbc);
    }

    private void createMessageWrapper(Font regular, Color bg, GridBagConstraints gbc, JPanel newAdmin) {
        JPanel messageWrapper = new JPanel();
        messageWrapper.setOpaque(false);
        messageWrapper.setPreferredSize(new Dimension(450, 50));
        errorMessage.setForeground(Color.red);
        errorMessage.setBackground(bg);
        errorMessage.setFont(regular.deriveFont(15f));
        errorMessage.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        gbc.weighty = 0.3;
        messageWrapper.add(errorMessage);
        newAdmin.add(messageWrapper, gbc);
    }

    private void createAccountInputs(Font regular, Font boldItalic, Color bg, JPanel input) {
        usernameInput.setBorder(BorderFactory.createMatteBorder(25, 0, 25, 30, bg));
        usernameInput.setFont(regular.deriveFont(25f));
        input.add(usernameInput);

        createLabel(boldItalic, input, "Password:", 25f, JLabel.CENTER);

        passwordInput.setBorder(BorderFactory.createMatteBorder(25, 0, 25, 30, bg));
        passwordInput.setFont(regular.deriveFont(25f));
        input.add(passwordInput);
    }

    private JPanel createNewInputForAdmin(Color bg, GridBagConstraints gbc, JPanel newAdmin) {
        JPanel input = new JPanel(new GridLayout(2, 2, 70, 0));
        input.setBorder(BorderFactory.createMatteBorder(0, 0, 20, 0, bg));
        input.setBackground(bg);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 0.6;
        newAdmin.add(input, gbc);
        return input;
    }

    private JPanel createNewAdmin(Color bg, JPanel splitContainer) {
        JPanel newAdmin = new JPanel(new GridBagLayout());
        newAdmin.setPreferredSize(new Dimension(450, 250));
        newAdmin.setBorder(BorderFactory.createMatteBorder(0, 0, 20, 0, bg));
        newAdmin.setBackground(bg);
        splitContainer.add(newAdmin);
        return newAdmin;
    }

    private void createSubmitSettings(Font bold, Color bg, GridBagConstraints gbc, JPanel tradeSettings) {
        submitSettings.setBorder(BorderFactory.createMatteBorder(10, 160, 10, 160, bg));
        submitSettings.setBackground(Color.green);
        submitSettings.setForeground(Color.WHITE);
        submitSettings.setFont(bold.deriveFont(25f));
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        gbc.insets = new Insets(30, 0, 0, 0);
        tradeSettings.add(submitSettings, gbc);
    }

    private void handleInfoSubpanel(Color bg, JPanel info, JComboBox<Integer> minLendChoice, TraderProperties minimumAmountNeededToBorrow) throws IOException {
        minLendChoice.setSelectedIndex(loginManager.getProperty(minimumAmountNeededToBorrow) - 1);
        minLendChoice.setBorder(BorderFactory.createMatteBorder(0, 75, 0, 75, bg));
        info.add(minLendChoice);
    }

    private JPanel createInfoPanel(Color bg, GridBagConstraints gbc, JPanel tradeSettings) {
        JPanel info = new JPanel(new GridLayout(3, 2, 20, 35));
        info.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, bg));
        info.setBackground(bg);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 0.9;
        tradeSettings.add(info, gbc);
        return info;
    }

    private JPanel createTradeSettings(Color bg, JPanel splitContainer) {
        JPanel tradeSettings = new JPanel(new GridBagLayout());
        tradeSettings.setBorder(BorderFactory.createMatteBorder(30, 20, 20, 20, bg));
        tradeSettings.setPreferredSize(new Dimension(450, 250));
        tradeSettings.setBackground(bg);
        splitContainer.add(tradeSettings);
        return tradeSettings;
    }

    private void createLabel(Font boldItalic, JPanel titles, String s, float v, int left) {
        JLabel tradeSettingsTitle = new JLabel(s);
        tradeSettingsTitle.setForeground(Color.white);
        tradeSettingsTitle.setFont(boldItalic.deriveFont(v));
        tradeSettingsTitle.setHorizontalAlignment(left);
        titles.add(tradeSettingsTitle);
    }

    /**
     * Used for displaying some message in the login screen
     *
     * @param msg the message being displayed
     */
    public void notifyLogin(String msg) {
        errorMessage.setText(msg);
        errorMessage.setVisible(true);
    }

    /**
     * Used to handle events
     *
     * @param e the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitAdmin) {
            try {
                String loggedInUser = loginManager.registerUser(usernameInput.getText(), String.valueOf(passwordInput.getPassword()), UserTypes.ADMIN);
                ((WindowManager) SwingUtilities.getWindowAncestor(this)).login(loggedInUser);
            } catch (BadPasswordException ex) {
                notifyLogin("<html><b><i>Invalid Password: " + ex.getMessage() + "</i></b></html>");
            } catch (UserAlreadyExistsException ignored) {
                notifyLogin("<html><b><i>The username '" + usernameInput.getText() + "' is taken.</i></b></html>");
            }
            catch (IOException ignored){
                notifyLogin("<html><b><i>Could not create the account at this time.</i></b></html>");
            }
        } else if (e.getSource() == submitSettings) {
            try {
                loginManager.setProperty(TraderProperties.INCOMPLETE_TRADE_LIM, incompleteLimitChoice.getItemAt(incompleteLimitChoice.getSelectedIndex()));
                loginManager.setProperty(TraderProperties.TRADE_LIMIT, tradeLimitChoice.getItemAt(tradeLimitChoice.getSelectedIndex()));
                loginManager.setProperty(TraderProperties.MINIMUM_AMOUNT_NEEDED_TO_BORROW, minLendChoice.getItemAt(minLendChoice.getSelectedIndex()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}

