package main.TradeSystem.Managers;

import Database.Database;
import Database.users.Admin;
import Database.users.AdminManager;
import Database.users.Trader;
import Database.users.TraderManager;
import Database.users.User;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import main.DatabaseFilePaths;

import java.io.IOException;
import java.util.LinkedList;

public class LoginManager {
    private Database<User> userDatabase;
    private int defaultTradeLimit = 10;

    public enum UserTypes {
        ADMIN,
        TRADER,
        DEFAULT
    }

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
            throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
        switch (type) {
            case ADMIN:
                return userDatabase.update(new Admin(username, password)).getId();
            case TRADER:
                return userDatabase.update(new Trader(username, password, defaultTradeLimit)).getId();
            case DEFAULT:
            default:
                return userDatabase.update(new User(username, password)).getId();
        }

    }

    /**
     * Ensuring user credentials is correct and returns the user id
     *
     * @param username username of user
     * @param password password of user
     * @return the user id of the logged in user
     * @throws EntryNotFoundException could not find the user
     */
    public String login(String username, String password) throws EntryNotFoundException {
        LinkedList<User> users = userDatabase.getItems();
        for (User user : users)
            if (user.getUsername().equals(username) && (user.getPassword().equals(password)))
                return user.getId();
        throw new EntryNotFoundException("Bad credentials.");
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
}
