package main.TradeSystem.Managers;

import Database.Database;
import Database.users.Admin;
import Database.users.Trader;
import Database.users.User;
import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;
import main.DatabaseFilePaths;
import main.TradeSystem.Accounts.UserTypes;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Used for logging in and registering
 */
public class LoginManager {
    private final Database<User> userDatabase;
    private int defaultTradeLimit = 10;
    private int defaultIncompleteTradeLim = 3;
    private int defaultMinimumAmountNeededToBorrow = 1;
    private UserTypes lastLoggedInType = null;

    /**
     * For logging in and registering accounts
     *
     * @throws IOException if the database file is bad
     */
    public LoginManager() throws IOException {
        userDatabase = new Database<User>(DatabaseFilePaths.USER.getFilePath());
    }

    /**
     * Registers a user
     *
     * @param username username of new user
     * @param password password of new user
     * @return The ID of the newly created user
     * @throws UserAlreadyExistsException username is not unique
     */
    public String registerUser(String username, String password, UserTypes type) throws UserAlreadyExistsException {
        if (!isUsernameUnique(username))
            throw new UserAlreadyExistsException();
        switch (type) {
            case ADMIN:
                lastLoggedInType = UserTypes.ADMIN;
                return userDatabase.update(new Admin(username, password)).getId();
            case TRADER:
            case DEFAULT:
            default:
                lastLoggedInType = UserTypes.TRADER;
                return userDatabase.update(new Trader(username, password, defaultTradeLimit, defaultIncompleteTradeLim,
                        defaultMinimumAmountNeededToBorrow)).getId();
        }

    }

    /**
     * Ensuring user credentials is correct and returns the user id
     *
     * @param username username of user
     * @param password password of user
     * @return the user id of the logged in user
     * @throws UserNotFoundException could not find the user
     */
    public String login(String username, String password) throws UserNotFoundException {
        LinkedList<User> users = userDatabase.getItems();
        for (User user : users)
            if (user.getUsername().equals(username) && (user.getPassword().equals(password)))
                return user.getId();
        throw new UserNotFoundException();
    }

    public UserTypes getLastLoggedInType(){
        return lastLoggedInType;
    }

    /**
     * Checks if the username exists in the database file
     *
     * @param username username to check for
     * @return if username is unique
     */
    public boolean isUsernameUnique(String username) {
        for (User user : userDatabase.getItems())
            if (user.getUsername().equals(username))
                return false;
        return true;
    }

    /**
     * For the trader user type, this sets the initial trade limit when making a new account
     *
     * @param tradeLimit initial trade limit of a trader user
     */
    public void setDefaultTradeLimit(int tradeLimit) {
        defaultTradeLimit = tradeLimit;
    }

    /**
     *
     * @return the current defautl trade limit.
     */
    public int getDefaultTradeLimit() {
        return defaultTradeLimit;
    }

    /**
     * For the trader user type, this sets the initial incomplete trade limit when making a new account.
     *
     * @param defaultIncompleteTradeLim initial incomplete trade limit of a trader user
     */
    public void setDefaultIncompleteTradeLim(int defaultIncompleteTradeLim) {
        this.defaultIncompleteTradeLim = defaultIncompleteTradeLim;
    }

    /**
     * For the trader user type, this sets the initial minimum amount that (items lent - items borrowed) should be
     * before the trader is allowed to borrow.
     *
     * @param defaultMinimumAmountNeededToBorrow the number of items that need to be lent out before borrowing is allowed
     */
    public void setDefaultMinimumAmountNeededToBorrow(int defaultMinimumAmountNeededToBorrow) {
        this.defaultMinimumAmountNeededToBorrow = defaultMinimumAmountNeededToBorrow;
    }
}
