package main.TradeSystem;

import exceptions.*;
import main.TradeSystem.Accounts.Account;
import main.TradeSystem.Accounts.AdminAccount;
import main.TradeSystem.Accounts.TraderAccount;
import main.TradeSystem.Accounts.UserTypes;
import main.TradeSystem.Managers.LoginManager;

import java.io.*;

/**
 * This is used to set up the type of account required after logging in
 */
public class TradeSystem implements Serializable {

    private final LoginManager loginManager;
    private Account account = null;
    private String lastLoggedInString = "";

    /**
     * For setting up the login manager
     *
     * @throws IOException if database has issues
     */
    public TradeSystem() throws IOException {
        loginManager = new LoginManager();
    }

    /**
     * Logging in
     *
     * @param username username of the user
     * @param password password of the user
     * @return the user id
     * @throws UserNotFoundException bad credentials
     */
    public String login(String username, String password) throws UserNotFoundException {
        lastLoggedInString = loginManager.login(username, password);
        return lastLoggedInString;
    }

    /**
     * Making a new account
     *
     * @param username username of the user
     * @param password password of the user
     * @param type     type of the account
     * @return the user id
     * @throws UserAlreadyExistsException if username isn't unique
     */
    public String register(String username, String password, UserTypes type) throws UserAlreadyExistsException {
        lastLoggedInString = loginManager.registerUser(username, password, type);
        return lastLoggedInString;
    }


    /**
     * Getting the account
     *
     * @return the account
     * @throws AuthorizationException if the account doesn't match the user id
     * @throws UserNotFoundException  if the user id doesn't exist
     * @throws IOException            issues with database
     */
    public Account getAccount() throws AuthorizationException, UserNotFoundException, IOException {
        System.out.println(loginManager.getLastLoggedInType());
        switch (loginManager.getLastLoggedInType()) {
            case ADMIN:
                return new AdminAccount(lastLoggedInString);
            case TRADER:
                return new TraderAccount(lastLoggedInString);
        }
        return null;
    }

}
