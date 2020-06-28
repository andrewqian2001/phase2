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

public class TradeSystem implements Serializable {

    private static final String USERS_FILE_PATH = "src/users/users.ser";
    private static final String TRADE_FILE_PATH = "src/users/trades.ser";
    private static final String TRADABLE_ITEM_FILE_PATH = "src/users/tradableitems.ser";

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
     * @return id of the newly registered admin
     * @throws IOException
     * @throws UserAlreadyExistsException
     */
    public String registerAdmin(String username, String password) throws IOException, UserAlreadyExistsException {
        userManager = new AdminManager(USERS_FILE_PATH);
        /**
         * REMOVE THIS COMMENT AFTER YOU READ THIS
         * 
         * Motivation behind removing this line: this.loggedInUserId = userManager.registerUser(username, password);
         * If I am an admin and I decide to add a new admin, I still need to be able to do other admin stuff after
         */
        return this.loggedInUserId;
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
     * Freezes/Unfreezes a Trader given their username
     * Requirement: Only an Admin Account can preform this action
     * @param userId id of the Trader than needs to be (un-)frozen
     * @param freezeStatus if true, method will freeze the Trader, else it will unFreeze
     * @throws EntryNotFoundException
     * @throws AuthorizationException
     */
    public void freezeUser(String userId, boolean freezeStatus) throws EntryNotFoundException, AuthorizationException {
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
    public String getUsername(String userId) throws EntryNotFoundException {
        return userManager.getUsername(userId);
    }
    public String getIdFromUsername(String username) throws EntryNotFoundException{
        return userManager.getUserId(username);
    }

    public void requestItem(String ID, TradableItem item) {
        Trader user = null;
        try {
            user = (Trader) userManager.populate(ID);
        } catch (EntryNotFoundException e) {
            // Not sure what im supposed to do when I get an exception
        } finally {
            ArrayList<String> List = user.getRequestedItems();
            List.add(item.getId());
            user.setRequestedItems(List);
            userManager.update(user);
        }

    }

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

    }

    public void printTrades(String ID) {
       printList(ID, "Accepted", "Trade");
       System.out.println();
       printList(ID, "Requested", "Trade");
    }

    public void printInventory(String ID) {
        printList(ID, "Inventory", "Item");
    }

    public void printWishlist(String ID) {
        printList(ID, "Wishlist", "Item");
    }

    private void printList(String ID, String listType, String itemType) {
        try {
            Trader user = (Trader) userManager.populate(ID);
            ArrayList<String> list = null;
            String itemID = "";
            if(listType.equals("Wishlist")) list = user.getWishlist();
            else if(listType.equals("Inventory")) list = user.getAvailableItems();
            else if(listType.equals("Accepted")) list = user.getAcceptedTrades();
            else if(listType.equals("Requested")) list = user.getRequestedTrades();
            System.out.printf("%s's %s %ss\n***************\n", user.getUsername(), listType, itemType);
            for(int i = 0; i < list.size(); i++) {
                itemID = list.get(i);
                System.out.printf("%s %s #%d: %s\n\t%s\n", listType, itemType, i, getTradableItemName(itemID), getTradableItemDesc(itemID));
            }
        } catch (EntryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void requestUnfreeze(String ID) {
        Trader user = (Trader) getLoggedInUser(ID);
    }
}
