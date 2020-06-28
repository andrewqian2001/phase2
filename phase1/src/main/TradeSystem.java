package main;

import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import tradableitems.TradableItem;
import tradableitems.TradableItemManager;
import trades.TradeManager;
import users.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TradeSystem implements Serializable {

    private static final String USERS_FILE_PATH = "./phase1/src/users/users.ser";
    private static final String TRADE_FILE_PATH = "./phase1/src/trades/trades.ser";
    private static final String TRADABLE_ITEM_FILE_PATH = "./phase1/src/tradableitems/tradableitems.ser";
    private UserManager userManager;
    private TradeManager tradeManager;
    private TradableItemManager tradableItemManager;
    private String loggedInUserId;

    /**
     * Constructor for TradeSystem, initializes managers
     * @throws IOException
     */
    public TradeSystem() throws IOException {
        userManager = new UserManager(USERS_FILE_PATH);
        tradeManager = new TradeManager(TRADE_FILE_PATH);
        tradableItemManager = new TradableItemManager((TRADABLE_ITEM_FILE_PATH));
        loggedInUserId = "";
    }

    /**
     * Getter method for userID
     * @return the id of the loggedInUser
     */
    public String getLoggedInUserId() {
        return this.loggedInUserId;
    }

    /**
     * Registers a new trader into the system
     * @param username username of new trader
     * @param password password for new trader
     * @return id of the newly registered trader
     * @throws IOException
     * @throws UserAlreadyExistsException
     */
    public String registerTrader(String username, String password) throws IOException, UserAlreadyExistsException {
        userManager = new TraderManager(USERS_FILE_PATH);
        this.loggedInUserId = userManager.registerUser(username, password);
        return this.loggedInUserId;
    }

    /**
     * Registers a new Admin into the system
     * @param username username of new admin
     * @param password password for new admin
     * @throws IOException
     * @throws UserAlreadyExistsException
     */
    public void registerAdmin(String username, String password) throws IOException, UserAlreadyExistsException {
        userManager = new AdminManager(USERS_FILE_PATH);
    }

    /**
     * Logs-in a current user into the system
     * @param username username of existing user
     * @param password password of existing user
     * @return id of newly logged in user
     * @throws EntryNotFoundException
     * @throws IOException
     */
    public String login(String username, String password) throws EntryNotFoundException, IOException {
        this.loggedInUserId = userManager.login(username, password);

        User loggedInUser = userManager.populate(loggedInUserId);
        if (loggedInUser instanceof Admin)
            userManager = new AdminManager(USERS_FILE_PATH);
        else
            userManager = new TraderManager(USERS_FILE_PATH);

        return this.loggedInUserId;
    }

    /**
     * Check if a User, given their ID, is frozen 
     * NOTE: This method is not for Admins since their accounts cannot be frozen
     * 
     * @param ID id of the user
     * @return true if the user is frozen, false else
     * @throws EntryNotFoundException
     */
    public boolean checkFrozen(String ID) throws EntryNotFoundException {
        return userManager.populate(ID).isFrozen();
    }

    /**
     * Check if a User, given their ID, is an Admin
     * 
     * @param ID
     * @return true if the User is of type Admin, false else
     * @throws EntryNotFoundException
     */
    public boolean checkAdmin(String ID) throws EntryNotFoundException {
       return userManager.populate(ID).hasPermission(Permission.REGISTER_ADMIN);
    }

    /**
     * Freezes/Unfreezes a Trader given their username
     * Requirement: Only an Admin Account can preform this action
     * @param username the username of the Trader that needs to be (un-)frozen
     * @param freezeStatus if true, method will freeze the Trader, else it will unFreeze
     * @throws EntryNotFoundException
     * @throws AuthorizationException
     */
    public void freezeUser(String username, boolean freezeStatus) throws EntryNotFoundException, AuthorizationException {
        String userId = getIdFromUsername(username);
        userManager.freezeUser(loggedInUserId, userId, freezeStatus);
    }

    /**
     * Gets the name of the tradable item given its id
     * @param tradableItemId id of the tradable item
     * @return name of the tradable item
     * @throws EntryNotFoundException
     */
    public String getTradableItemName(String tradableItemId) throws EntryNotFoundException {
        return tradableItemManager.getName(tradableItemId);
    }

    /**
     * Gets the description of the tradable item given its id
     * @param tradableItemId id of the tradable item
     * @return description of the tradable item
     * @throws EntryNotFoundException
     */
    public String getTradableItemDesc(String tradableItemId) throws EntryNotFoundException {
        return tradableItemManager.getDesc(tradableItemId);
    }

    /**
     * Gets the username of a User given their ID
     * NOTE: This will most likely be deleted before rollout since theres no use for this
     * @param userId id of the User
     * @return username of the User
     * @throws EntryNotFoundException
     */
    public String getUsername(String userId) throws EntryNotFoundException {
        return userManager.getUsername(userId);
    }


    /**
     * Gets the id of a User given their username
     * @param username username of the User
     * @return id of the User
     * @throws EntryNotFoundException
     */
    public String getIdFromUsername(String username) throws EntryNotFoundException{
        return userManager.getUserId(username);
    }

    public ArrayList<String> getWishlist(String ID) throws EntryNotFoundException {
        return ((Trader) userManager.populate(ID)).getWishlist();
    }

    public ArrayList<String> getAvailableItems(String ID) throws EntryNotFoundException {
        return ((Trader) userManager.populate(ID)).getAvailableItems();
    }
    
    public ArrayList<String> getAcceptedTrades(String ID) throws EntryNotFoundException {
        return ((Trader) userManager.populate(ID)).getAcceptedTrades();
    }
    
    public ArrayList<String> getRequestedTrades(String ID) throws EntryNotFoundException {
        return ((Trader) userManager.populate(ID)).getRequestedTrades();
    }

    public ArrayList<String> getAllTraders() {
        return ((TraderManager) userManager).getAllTraders();
    }

    /**
     * Requests that the item be added to the user's iventory
     * @param ID user ID
     * @param item item object
     * @throws EntryNotFoundException
     */
    public void requestItem(String ID, TradableItem item) throws EntryNotFoundException {
        ((TraderManager)userManager).addRequestItem(ID, item.getId());
    }

    /*
    public void addItem(String ID, TradableItem item) {
        Trader user = null;
        try {
            user = (Trader) userManager.populate(ID);
        } catch (EntryNotFoundException e) {
            // Not sure what im supposed to do when I get an exception
        } finally {
            if (user != null && user.hasPermission(Permission.CONFIRM_ADDED_ITEM)) {
                ArrayList<String> inventory = user.getAvailableItems();
                ArrayList<String> reqList = user.getRequestedItems();

                if (reqList.contains(item.getId())) {
                    inventory.add(item.getId());
                    user.setAvailableItems(inventory);
                    reqList.remove(item.getId());
                    user.setRequestedItems(reqList);
                    userManager.update(user);
                }
            }
        }

    }*/

    /**
     * @param userId the user that wants to be unfrozen
     * @param status if the user requested to be unfrozen
     * @throws EntryNotFoundException
     */
    public void requestUnfreeze(String userId, boolean status) throws EntryNotFoundException {
        userManager.setRequestFrozenStatus(userId, status);
    }
}
