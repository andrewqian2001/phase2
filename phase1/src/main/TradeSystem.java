package main;

import com.sun.deploy.security.SelectableSecurityManager;
import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;
import tradableitems.TradableItem;
import tradableitems.TradableItemManager;
import trades.TradeManager;
import users.User;
import users.UserManager;

import javax.jws.soap.SOAPBinding;
import java.io.*;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TradeSystem implements Serializable {

    private UserManager userManager;
    private UserManager adminUserManager;
    private TradeManager tradeManager;
    private TradableItemManager tradableItemManager;
    private static final Logger LOGGER = Logger.getLogger(Manager.class.getName());
    private static final Handler CONSOLE_HANDLER = new ConsoleHandler();
    private User user;
    private String username;

    /**
     * Stores the file paths to the .ser files of all users and initial admin
     *
     * @param filepath .ser file of all users
     * @param adminFilepath .ser file of initial admin
     * @throws IOException
     */
    public TradeSystem(String filepath, String adminFilepath) throws IOException {
        LOGGER.setLevel(Level.ALL);
        CONSOLE_HANDLER.setLevel(Level.WARNING);
        LOGGER.addHandler(CONSOLE_HANDLER);

        this.adminUserManager = new UserManager(adminFilepath);
        this.userManager = new UserManager(filepath);
    }

    /**
     * Registers a new trader account
     *
     * @param username
     * @param password
     * @return the User object
     * @throws UserAlreadyExistsException
     * @throws UserNotFoundException
     */
    public User register(String username, String password) throws FileNotFoundException, ClassNotFoundException, UserAlreadyExistsException {
        this.username = username;
        return userManager.registerUser(username, password, "Trader");
    }

    /**
     * Registers a new admin account
     *
     * @param username
     * @param password
     * @throws UserAlreadyExistsException
     */
    public void registerAdmin(String username, String password) throws FileNotFoundException, ClassNotFoundException, UserAlreadyExistsException {
        userManager.registerUser(username, password, "Admin");
    }

    /**
     * Find the user object and returns it
     *
     * @param username
     * @param password
     * @return User object
     * @throws UserNotFoundException
     */
    public User login(String username, String password) throws UserNotFoundException, FileNotFoundException, ClassNotFoundException {
        //Check if the account is the initial admin
        try {
            this.user = adminUserManager.login(username, password);
        } catch(UserNotFoundException e) {
        }

        this.user = userManager.login(username, password);
        return user;
    }

    public User find() {
        try {
            return userManager.find(username);
        } catch(UserNotFoundException | FileNotFoundException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    public void freezeUser() {
        user = find();
        user.setFrozen(true);
    }

    public void unfreezeUser() {
        user = find();
        user.setFrozen(false);
    }

    public void addItem() {

    }

    public void printTrades() {

    }

    public void printInventory() {

    }

    public void printWishlist() {

    }

    public void requestUnfreeze() {

    }
}
