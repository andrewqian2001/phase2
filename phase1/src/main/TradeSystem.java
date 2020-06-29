package main;

import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import tradableitems.TradableItem;
import tradableitems.TradableItemManager;
import trades.TradeManager;
import users.*;

import java.io.*;
import java.util.*;

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
     * 
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
     * 
     * @return the id of the loggedInUser
     */
    public String getLoggedInUserId() {
        return this.loggedInUserId;
    }

    /**
     * Registers a new trader into the system
     * 
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
     * 
     * @param username username of new admin
     * @param password password for new admin
     * @throws IOException
     * @throws UserAlreadyExistsException
     */
    public void registerAdmin(String username, String password) throws IOException, UserAlreadyExistsException {
        userManager = new AdminManager(USERS_FILE_PATH);
        ((AdminManager) userManager).registerUser(username, password);
    }

    /**
     * Logs-in a current user into the system
     * 
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
     * Check if a User, given their ID, is frozen NOTE: This method is not for
     * Admins since their accounts cannot be frozen
     * 
     * @param userID id of the user
     * @return true if the user is frozen, false else
     * @throws EntryNotFoundException
     */
    public boolean checkFrozen(String userID) throws EntryNotFoundException {
        return userManager.isFrozen(userID);
    }

    /**
     * Check if a User, given their ID, is an Admin
     * 
     * @param userID the id of the user
     * @return true if the User is of type Admin, false else
     * @throws EntryNotFoundException
     */
    public boolean checkAdmin(String userID) throws EntryNotFoundException {
        return userManager.isAdmin(userID);
    }

    /**
     * Freezes/Unfreezes a Trader given their username Requirement: Only an Admin
     * Account can preform this action
     * 
     * @param username     the username of the Trader that needs to be (un-)frozen
     * @param freezeStatus if true, method will freeze the Trader, else it will
     *                     unFreeze
     * @throws EntryNotFoundException
     * @throws AuthorizationException
     */
    public void freezeUser(String username, boolean freezeStatus)
            throws EntryNotFoundException, AuthorizationException {
        String userId = getIdFromUsername(username);
        userManager.freezeUser(loggedInUserId, userId, freezeStatus);
    }

    /**
     * Gets the name of the tradable item given its id
     * 
     * @param tradableItemId id of the tradable item
     * @return name of the tradable item
     * @throws EntryNotFoundException
     */
    public String getTradableItemName(String tradableItemId) throws EntryNotFoundException {
        return tradableItemManager.getName(tradableItemId);
    }

    /**
     * Gets the description of the tradable item given its id
     * 
     * @param tradableItemId id of the tradable item
     * @return description of the tradable item
     * @throws EntryNotFoundException
     */
    public String getTradableItemDesc(String tradableItemId) throws EntryNotFoundException {
        return tradableItemManager.getDesc(tradableItemId);
    }

    /**
     * Gets the username of a User given their ID NOTE: This will most likely be
     * deleted before rollout since theres no use for this
     * 
     * @param userId id of the User
     * @return username of the User
     * @throws EntryNotFoundException
     */
    public String getUsername(String userId) throws EntryNotFoundException {
        return userManager.getUsername(userId);
    }

    /**
     * Gets the id of a User given their username
     * 
     * @param username username of the User
     * @return id of the User
     * @throws EntryNotFoundException
     */
    public String getIdFromUsername(String username) throws EntryNotFoundException {
        return userManager.getUserId(username);
    }

    /**
     * Gets a list of a given user's WishList
     * 
     * @param userID the id of the user
     * @return a list of id's of wishlist items
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getWishlist(String userID) throws EntryNotFoundException {
        return ((TraderManager) userManager).getWishlist(userID);
    }

    /**
     * Gets a list of a given user's Available Items
     * 
     * @param userID the id of the user
     * @return a list of id's of available Items
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getAvailableItems(String userID) throws EntryNotFoundException {
        return ((TraderManager) userManager).getAvailableItems(userID);
    }

    /**
     * Gets a list of a given user's Accepted Trades
     * 
     * @param userID the id of the user
     * @return a list of id's of Accepted Trades
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getAcceptedTrades(String userID) throws EntryNotFoundException {
        return ((TraderManager) userManager).getAcceptedTrades(userID);
    }

    /**
     * Gets a list of a given user's Requested Trades
     * 
     * @param userID the id of the user
     * @return a list of id's of Requested Trades
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getRequestedTrades(String userID) throws EntryNotFoundException {
        return ((TraderManager) userManager).getRequestedTrades(userID);
    }

    /**
     * Gets a list of all traders in the database
     * 
     * @return A list of all traders in the database
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getAllTraders() {
        return ((TraderManager) userManager).getAllTraders();
    }

    /**
     * Requests that the item be added to the user's iventory
     * 
     * @param userID user ID
     * @param itemName  name of tradable item
     * @throws EntryNotFoundException
     */
    public void requestItem(String userID, String itemName) throws EntryNotFoundException {
        String itemID = tradableItemManager.getIdWithName(itemName).get(0);
        ((TraderManager) userManager).addRequestItem(userID, itemID);
    }

    /**
     * get all items in all user's inventories
     * 
     * @return hash map of the items
     * @throws EntryNotFoundException
     */
    public HashMap<String, ArrayList<String>> getAllAvailableItems() throws EntryNotFoundException {
        return ((TraderManager) userManager).getAllItemsInInventories();
    }

    /**
     * add item to wish list
     * 
     * @param userID user ID
     * @param item   item object
     * @throws EntryNotFoundException
     */
    public void addToWishList(String userID, TradableItem item) throws EntryNotFoundException {
        ((TraderManager) userManager).addToWishList(userID, item.getId());
    }

    /**
     * @param userId the user that wants to be unfrozen
     * @param status if the user requested to be unfrozen
     * @throws EntryNotFoundException
     */
    public void requestUnfreeze(String userId, boolean status) throws EntryNotFoundException {
        userManager.setRequestFrozenStatus(userId, status);
    }

    /**
     * return the 3 most traded with Traders
     * 
     * @param userID user Id
     * @return a String array of the usernames of the 3 most traded with Traders
     * @throws EntryNotFoundException
     */
    public String[] getFrequentTraders(String userID) throws EntryNotFoundException {
        String[] frequentTraders = new String[3];
        ArrayList<String> acceptedTrades = ((TraderManager) userManager).getAcceptedTrades(userID); // Trade IDs
        Set<String> distinct = new HashSet<>(acceptedTrades);
        int highest = 0;
        for (int i = 0; i < 3; i++) {
            for (String s : distinct) {
                if (Collections.frequency(acceptedTrades, s) > highest) {
                    frequentTraders[i] = s;
                }
            }
            distinct.remove(frequentTraders[i]);
        }
        for (int i = 0; i < 3; i++) {
            frequentTraders[i] = userManager.getUserId(frequentTraders[i]);
        }
        return frequentTraders;
    }

    /**
     * Gets a list of the items used in trades
     * 
     * @param userId id of the user
     * @return list of unique items that the user has traded/recieved from a trade
     * @throws EntryNotFoundException
     */
    public Set<String> getRecentTradeItems(String userId) throws EntryNotFoundException {
        ArrayList<String> acceptedTrades = ((TraderManager) userManager).getAcceptedTrades(userId);
        Set<String> recentTradeItemNames = new HashSet<>();
        for (String tradeID : acceptedTrades) {
            String[] tradeableItemIDs = tradeManager.getItemsFromTrade(tradeID);
            recentTradeItemNames.add(getTradableItemName(tradeableItemIDs[0]));
            if (!tradeableItemIDs[1].equals(""))
                recentTradeItemNames.add(getTradableItemName(tradeableItemIDs[1]));
        }
        return recentTradeItemNames;
    }

    /**
     * Gets a list of all Unfreeze Request
     * 
     * @return a list of all unfreeze requests
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getAllUnfreezeRequests() throws EntryNotFoundException {
        return ((AdminManager) userManager).getAllUnFreezeRequests();
    }

    /**
     * Gets a Map of key=id of user, value=list of their item requests
     * @return a list of item requests mapping to each user
     * @throws EntryNotFoundException
     */
    public HashMap<String, ArrayList<String>> getAllItemRequests() throws EntryNotFoundException {
        return ((AdminManager) userManager).getAllItemRequests();
    }
}
