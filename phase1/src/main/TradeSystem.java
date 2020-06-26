package main;

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

import java.io.*;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TradeSystem implements Serializable {

    private static final String filePath = "src/users/users.ser";
    private static final String adminFilePath = "src/users/admin.ser";

    private UserManager userManager = new UserManager(filePath);
    private UserManager adminUserManager = new UserManager(adminFilePath);
    private TradeManager tradeManager;
    private TradableItemManager tradableItemManager;
    private static final Logger LOGGER = Logger.getLogger(Manager.class.getName());
    private static final Handler CONSOLE_HANDLER = new ConsoleHandler();

    /**
     * Stores the file paths to the .ser files of all users and initial admin
     *
     * @throws IOException
     */
    public TradeSystem() throws IOException {
        LOGGER.setLevel(Level.ALL);
        CONSOLE_HANDLER.setLevel(Level.WARNING);
        LOGGER.addHandler(CONSOLE_HANDLER);
    }

    /**
     * Registers a new trader account, and gets "logged in"
     *
     * @param username
     * @param password
     * @return ID of the newly registered user
     * @throws UserAlreadyExistsException
     * @throws UserNotFoundException
     */
    public String register(String username, String password) throws FileNotFoundException, ClassNotFoundException, UserAlreadyExistsException {
        return userManager.registerUser(username, password, "Trader").getId();
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
     * Finds the User given its usernam|e and password, and returns the User's ID
     *
     * @param username
     * @param password
     * @return ID of the user
     * @throws UserNotFoundException
     */
    public String login(String username, String password) throws UserNotFoundException, FileNotFoundException, ClassNotFoundException {
        //Check if the account is the initial admin
        try {
            return adminUserManager.login(username, password).getId();
        } catch(UserNotFoundException e) {}

        return userManager.login(username, password).getId();
    }

    public User find(String username) throws UserNotFoundException, FileNotFoundException, ClassNotFoundException {
        return userManager.find(username);
    }

    public void freezeUser(String username) throws UserNotFoundException, FileNotFoundException, ClassNotFoundException {
        User wantToFreezeUser = find(username);
        wantToFreezeUser.setFrozen(true);
    }

    public void unfreezeUser(String username) throws UserNotFoundException, FileNotFoundException, ClassNotFoundException {
        User wantToUnFreezeUser = find(username);
        wantToUnFreezeUser.setFrozen(false);
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

            ArrayList<String> List = user.getRequestedItems();
            List.add(item.getId());
            user.setRequestedItems(List);
            userManager.update(user);

    }

    public void addItem(String ID, TradableItem item) {
        Trader user = (Trader) getLoggedInUser(ID);
        if(user.hasPermission(Permission.CONFIRM_ADDED_ITEM)){
            ArrayList<String> inventory = user.getAvailableItems();
            ArrayList<String> reqList = user.getRequestedItems();

            if(reqList.contains(item.getId())){
                inventory.add(item.getId());
                user.setAvailableItems(inventory);
                reqList.remove(item.getId());
                user.setRequestedItems(reqList);
                userManager.update(user);
            }
        }
    }

    public void printTrades(String ID) {
        Trader user = (Trader) getLoggedInUser(ID);
        ArrayList<String> AccTrades = user.getAcceptedTrades();
        ArrayList<String> ReqTrades = user.getRequestedTrades();
        System.out.println("User " + user.getUsername() + "'s accepted trades");
        for(int i = 0; i < AccTrades.size(); i++){
            String item = AccTrades.get(i); //this is just the ID, how do you get the actual item name?
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
        ArrayList<String> Inventory = user.getAvailableItems();
        System.out.println("User " + user.getUsername() + "'s available items");
        for(int i = 0; i < Inventory.size(); i++){
            String item =Inventory.get(i);  //this is just the ID, how do you get the actual item name?
            System.out.println(item);
        }
    }

    public void printWishlist(String ID) {
        Trader user = (Trader) getLoggedInUser(ID);
        ArrayList<String> list = user.getWishlist();
        System.out.println("User " + user.getUsername() + "'s wishlist");
        for(int i = 0; i < list.size(); i++){
            String item = list.get(i);  //this is just the ID, how do you get the actual item name?
            System.out.println(item);
        }
    }

    public void requestUnfreeze(String ID) {
        Trader user = (Trader) getLoggedInUser(ID);
    }
}
