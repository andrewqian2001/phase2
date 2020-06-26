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
            return adminUserManager.login(username, password);
        } catch(UserNotFoundException e) {
        }

        return userManager.login(username, password);
    }

    public void freezeUser(User user) {
        user.setFrozen(true);
    }

    public void unfreezeUser(User user) {
        user.setFrozen(false);
    }

    public void addItem(User user) {

    }

    public void printTrades(User user) {

    }

    public void printInventory(User user) {

    }

    public void printWishlist(User user) {

    }

    public void requestUnfreeze(User user) {

    }
}
