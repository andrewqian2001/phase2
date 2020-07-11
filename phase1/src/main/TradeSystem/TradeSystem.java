package main.TradeSystem;

import exceptions.AuthorizationException;
import exceptions.CannotTradeException;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import Database.tradableitems.TradableItem;
import Database.tradableitems.TradableItemManager;
import Database.trades.TradeManager;
import Database.users.*;
import main.TradeSystem.Accounts.AdminAccount;
import main.TradeSystem.Accounts.TraderAccount;

import java.io.*;
import java.util.*;

public class TradeSystem implements Serializable {

    private static final String USERS_FILE_PATH = "./phase1/src/Database.users/Database.users.ser";
    private static final String TRADE_FILE_PATH = "./phase1/src/Database.trades/Database.trades.ser";
    private static final String TRADABLE_ITEM_FILE_PATH = "./phase1/src/Database.tradableitems/Database.tradableitems.ser";
    protected UserManager userManager;
    protected TradeManager tradeManager;
    protected TradableItemManager tradableItemManager;
    protected String loggedInUserId;

    /**
     * Constructor for TradeSystem, initializes managers
     *
     * @throws IOException file path is bad
     */
    public TradeSystem() throws IOException {
        userManager = new UserManager(USERS_FILE_PATH);
        tradeManager = new TradeManager(TRADE_FILE_PATH);
        tradableItemManager = new TradableItemManager((TRADABLE_ITEM_FILE_PATH));

    }
    /*
    DO I DELETE TRADER AND ADMIN SPECIFIC METHODS?
     */
    /**
     * Registers a new trader into the system
     *
     * @param username username of new trader
     * @param password password for new trader
     * @return id of the newly registered trader
     * @throws IOException file path is bad
     * @throws UserAlreadyExistsException can't register a user that already exists
     */
    public String registerTrader(String username, String password) throws IOException, UserAlreadyExistsException {
        userManager = new TraderManager(USERS_FILE_PATH);
        this.loggedInUserId = ((TraderManager) userManager).registerUser(username, password, 3);
        return this.loggedInUserId;
    }

    /**
     * Registers a new Admin into the system
     *
     * @param username username of new admin
     * @param password password for new admin
     * @throws IOException file path is bad
     * @throws UserAlreadyExistsException username already exists
     */
    public void registerAdmin(String username, String password) throws IOException, UserAlreadyExistsException {
        userManager = new AdminManager(USERS_FILE_PATH);
        userManager.registerUser(username, password);
    }

    /**
     * Logs-in a current user into the system
     *
     * @param username username of existing user
     * @param password password of existing user
     * @return id of newly logged in user
     * @throws EntryNotFoundException bad credentials
     * @throws IOException file path is bad
     */
    public String login(String username, String password) throws EntryNotFoundException, IOException {
        this.loggedInUserId = userManager.login(username, password);
        User loggedInUser = userManager.getUser(loggedInUserId);
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
     * @throws EntryNotFoundException cant find user id
     */
    public boolean checkFrozen(String userID) throws EntryNotFoundException {
        return userManager.isFrozen(userID);
    }

    /**
     * Check if a User, given their ID, is an Admin
     *
     * @param userID the id of the user
     * @return true if the User is of type Admin, false else
     * @throws EntryNotFoundException cant find user id
     */
    public boolean checkAdmin(String userID) throws EntryNotFoundException {
        return userManager.isAdmin(userID);
    }

    /**
     * Checks if the given user is able to borrow items
     * @param userID id of the user
     * @return true if the user is able to borrow, else false
     * @throws EntryNotFoundException cant find user id
     */
    public boolean canBorrow(String userID) throws EntryNotFoundException {
        return ((TraderManager) userManager).canBorrow(userID);
    }

    /**
     * Gets the name of the tradable item given its id
     *
     * @param tradableItemId id of the tradable item
     * @return name of the tradable item
     * @throws EntryNotFoundException cant find tradable item id
     */
    public String getTradableItemName(String tradableItemId) throws EntryNotFoundException {
        return tradableItemManager.getName(tradableItemId);
    }

    /**
     * Gets the description of the tradable item given its id
     *
     * @param tradableItemId id of the tradable item
     * @return description of the tradable item
     * @throws EntryNotFoundException cant find tradable item id
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
     * @throws EntryNotFoundException cant find user id
     */
    public String getUsername(String userId) throws EntryNotFoundException {
        return userManager.getUsername(userId);
    }

    /**
     * Gets the id of a User given their username
     *
     * @param username username of the User
     * @return id of the User
     * @throws EntryNotFoundException cant find user id
     */
    public String getIdFromUsername(String username) throws EntryNotFoundException {
        return userManager.getUserId(username);
    }

    /**
     * Gets a list of a given user's WishList
     *
     * @param userID the id of the user
     * @return a list of id's of wishlist items
     * @throws EntryNotFoundException cant find user id
     */
    public ArrayList<String> getWishlist(String userID) throws EntryNotFoundException {
        return ((TraderManager) userManager).getWishlist(userID);
    }

    /**
     * Gets a list of a given user's Available Items
     *
     * @param userID the id of the user
     * @return a list of id's of available Items
     * @throws EntryNotFoundException cant find user id
     */
    public ArrayList<String> getAvailableItems(String userID) throws EntryNotFoundException {
        return ((TraderManager) userManager).getAvailableItems(userID);
    }

    /**
     * Gets a list of a given user's Accepted Trades
     *
     * @param userID the id of the user
     * @return a list of id's of Accepted Trades
     * @throws EntryNotFoundException cant find user id
     */
    public ArrayList<String> getAcceptedTrades(String userID) throws EntryNotFoundException {
        return ((TraderManager) userManager).getAcceptedTrades(userID);
    }

    /**
     * Gets a list of a given user's Requested Trades
     *
     * @param userID the id of the user
     * @return a list of id's of Requested Trades
     * @throws EntryNotFoundException cant find user id
     */
    public ArrayList<String> getRequestedTrades(String userID) throws EntryNotFoundException {
        return ((TraderManager) userManager).getRequestedTrades(userID);
    }

    /**
     * Gets a list of all traders in the database
     *
     * @return A list of all traders in the database
     */
    public ArrayList<String> getAllTraders() {
        return ((TraderManager) userManager).getAllTraders();
    }

    /**
     * Gets a list of all Unfreeze Request
     *
     * @return a list of all unfreeze requests
     */
    public ArrayList<String> getAllUnfreezeRequests()  {
        return  userManager.getAllUnFreezeRequests();
    }

    /**
     * Gets a Map of key=id of user, value=list of their item requests
     * @return a list of item requests mapping to each user
     */
    public HashMap<String, ArrayList<String>> getAllItemRequests()  {
        return ((AdminManager) userManager).getAllItemRequests();
    }

    /**
     * Gets the current weekly trade limit
     * @return the current trade limit
     * @throws EntryNotFoundException Can't find traders
     */
    public int getCurrentTradeLimit() throws EntryNotFoundException {
        return ((AdminManager) userManager).getTradeLimit();
    }

    /**
     * Checks if user can trade
     * @param userID
     * @return
     */
    public boolean canTrade(String userID) throws EntryNotFoundException {
        return ((TraderManager) userManager).canTrade(userID);
    }

    /**
     * Get userId of the other user in the trade
     * @param userID id of the user
     * @param tradeID id of the trade
     * @return userId of the other user
     * @throws EntryNotFoundException if the user or trade can not be found
     */
    public String getTraderIdFromTrade(String userID, String tradeID) throws EntryNotFoundException {
        return tradeManager.getOtherUser(tradeID, userID);
    }

    /**
     * get the item index of the trade item
     * @param userID id of the user
     * @param tradeId id of the trade
     * @return item index of the trade item
     * @throws EntryNotFoundException if the user or trade can not be found
     */
    public int getUserTradeItemIndex(String userID, String tradeId) throws EntryNotFoundException {
        String[] items = tradeManager.getItemsFromTrade(tradeId);
        ArrayList<String> inventory = ((TraderManager) userManager).getInventory(userID);
        if(tradeManager.isFirstUser(tradeId, userID)) {
            if (items[0].equals("")){
                return -1;
            }

            for(int i = 0; i < inventory.size(); i++) {
                if(items[0].equals(inventory.get(i))) return i;
            }
        }
        else {
            if (items[1].equals("")){
                return -1;
            }
            for(int i = 0; i < inventory.size(); i++) {
                if(items[1].equals(inventory.get(i))) return i;
            }
        }
        throw new EntryNotFoundException("The trade item could not be found in the inventory.");
    }

    public boolean isFirstUser(String tradeId, String userId) throws EntryNotFoundException {
        return tradeManager.isFirstUser(tradeId, userId);
    }

    /**
     * return if trade is temporary
     * @param tradeID id of the trade
     * @return true if trade is temporary
     * @throws EntryNotFoundException if the user or trade can not be found
     */
    public boolean isTradeTemporary(String tradeID) throws EntryNotFoundException {
        return tradeManager.hasSecondMeeting(tradeID);
    }

    /**
     * get first meeting time of the trade
     *
     * @param tradeID id of the trade
     * @return Date object of the firstmeeting time
     * @throws EntryNotFoundException if the user or trade can not be found
     */
    public Date getFirstMeeting(String tradeID) throws EntryNotFoundException {
        return tradeManager.getFirstMeetingTime(tradeID);
    }

    /**
     * get second meeting time of the trade
     * @param tradeID id of the trade
     * @return Date object of the second meeting time
     * @throws EntryNotFoundException if the user or trade can not be found
     */
    public Date getSecondMeeting(String tradeID) throws EntryNotFoundException {
        return tradeManager.getSecondMeetingTime(tradeID);
    }

    /**
     * get the item offered in the trade by the user
     * @param userID id of the user
     * @param tradeId id of the trade
     * @return the item id
     * @throws EntryNotFoundException if the user or the trade can not be found
     */
    public String getUserOffer(String userID, String tradeId) throws EntryNotFoundException {
        String[] items = tradeManager.getItemsFromTrade(tradeId);
        if(tradeManager.isFirstUser(tradeId, userID)) {
            return items[0];
        }
        else {
            return items[1];
        }
    }

    /**
     * get meeting location
     * @param tradeId of the trade
     * @return the meeting location
     * @throws EntryNotFoundException if the user or the trade can not be found
     */
    public String getMeetingLocation(String tradeId) throws EntryNotFoundException {
        return tradeManager.getMeetingLocation(tradeId);
    }

    /**
     * Gets the tradeID given the index of the Database.users requested trade
     * @param userId id of the user
     * @param requestedTradeIndex index of the requested trade
     * @return the trade ID
     * @throws EntryNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getRequestedTradeId(String userId, int requestedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException {
        return getRequestedTrades(userId).get(requestedTradeIndex);
    }

    /**
     * Gets the tradeID given the index of the Database.users accepted trade
     * @param userId id of the user
     * @param acceptedTradeIndex index of the accepted trade
     * @return the trade ID
     * @throws EntryNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getAcceptedTradeId(String userId, int acceptedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException {
        return getAcceptedTrades(userId).get(acceptedTradeIndex);
    }

    /**
     * checks if the given user has confirmed all meetings took place
     * @param userID  id of the user
     * @param tradeID id of the trade
     * @return true if the user has confirmed all meetings took place, false else
     * @throws EntryNotFoundException user id / trade id not found
     */
    public boolean hasUserConfirmedAllMeetings(String userID, String tradeID) throws EntryNotFoundException {
        if(tradeManager.hasSecondMeeting(tradeID))
            return tradeManager.getFirstMeetingConfirmed(tradeID, userID) && tradeManager.getSecondMeetingConfirmed(tradeID, userID);
        return tradeManager.getFirstMeetingConfirmed(tradeID, userID);
    }

    /**
     * Checks if the trade is still in progress
     * @param tradeID id of the trade
     * @return true if the trade is in progress, false else
     * @throws EntryNotFoundException trade is not found
     */
    public boolean isTradeInProgress(String tradeID) throws EntryNotFoundException {
        return tradeManager.isTradeInProgress(tradeID);
    }
}
