package main;

import com.sun.deploy.security.SelectableSecurityManager;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;
import tradableitems.TradableItem;
import tradableitems.TradableItemManager;
import trades.TradeManager;
import users.Permission;
import users.Trader;
import users.User;
import users.UserManager;

import javax.jws.soap.SOAPBinding;
import java.io.*;
import java.util.ArrayList;
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

    public User getLoggedInUser(String ID){ //Not sure if I handled the exception correctly
        User loggedInUser = null;
        try {
            loggedInUser = userManager.populate(ID);
            return loggedInUser;
        } catch (EntryNotFoundException e) {
            LOGGER.log(Level.INFO, "No user found with ID " + ID, e);
        }
        return loggedInUser;
    }

    public void requestItem(String ID, TradableItem item) {
        Trader user = (Trader) getLoggedInUser(ID);

        ArrayList<String> List = user.getUnconfirmedInv();
        List.add(item.getId());
        user.setUnconfirmedInv(List);
        userManager.update(user);

    }

    public void addItem(String ID, TradableItem item) {
        Trader user = (Trader) getLoggedInUser(ID);
        if(user.hasPermission(Permission.ADD_ITEM)){
            ArrayList<String> inventory = user.getInventory();
            inventory.add(item.getId());
            user.setInventory(inventory);
            userManager.update(user);
        }
    }

    public void printTrades(String ID) {
        Trader user = (Trader) getLoggedInUser(ID);
        ArrayList<String> AccTrades = user.getAcceptedTrades();
        ArrayList<String> ReqTrades = user.getRequestedTrades();
        System.out.println("User " + user.getUsername() + "'s accepted trades");
        for(int i = 0; i < AccTrades.size(); i++){
            String item = AccTrades.get(i);  //this is just the ID, how do you get the actual item name?
            System.out.println(item);
        }
        System.out.println("User " + user.getUsername() + "'s requested trades");
        for(int i = 0; i < ReqTrades.size(); i++){
            String item = ReqTrades.get(i); //this is just the ID, how do you get the actual item name?
            System.out.println(item);
        }

    }

    public void printInventory(String ID) {
        Trader user = (Trader) getLoggedInUser(ID);
        ArrayList<String> Inventory = user.getInventory();
        System.out.println("User " + user.getUsername() + "'s inventory");
        for(int i = 0; i < Inventory.size(); i++){
            String item = Inventory.get(i);  //this is just the ID, how do you get the actual item name?
            System.out.println(item);
        }

    }

    public void printWishlist(String ID) {
        Trader user = (Trader) getLoggedInUser(ID);
        ArrayList<String> Inventory = user.getInventory();
        System.out.println("User " + user.getUsername() + "'s inventory");
        for(int i = 0; i < Inventory.size(); i++){
            String item = Inventory.get(i);  //this is just the ID, how do you get the actual item name?
            System.out.println(item);
        }
    }

    public void requestUnfreeze() {

    }
}
