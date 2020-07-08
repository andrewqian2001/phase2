package main;

import exceptions.AuthorizationException;
import exceptions.CannotTradeException;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import tradableitems.TradableItem;
import tradableitems.TradableItemManager;
import trades.Trade;
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
        this.loggedInUserId = ((TraderManager) userManager).registerUser(username, password, 3);
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
        userManager.registerUser(username, password);
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
     * Checks if the given user is able to borrow items
     * @param userID id of the user
     * @return true if the user is able to borrow, else false
     */
    public boolean canBorrow(String userID) throws EntryNotFoundException {
        return ((TraderManager) userManager).canBorrow(userID);
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
     * Requests that the item be added to the user's inventory
     *
     * @param userID user ID
     * @param itemName  name of tradable item
     * @param itemDesc description of tradableItem
     * @throws EntryNotFoundException
     */
    public void requestItem(String userID, String itemName, String itemDesc) throws EntryNotFoundException{
        TradableItem newItem = tradableItemManager.addItem(itemName, itemDesc);
        ((TraderManager) userManager).addRequestItem(userID, newItem.getId());
    }
    /**
     * Adds item to wishList
     *
     * @param userID   user ID
     * @param itemName name of tradable item
     * @throws EntryNotFoundException
     */
    public void addToWishList(String userID, String itemName) throws EntryNotFoundException {
        ArrayList<String> itemIDs = tradableItemManager.getIdsWithName(itemName);
        if (itemIDs.size() == 0)
            throw new EntryNotFoundException("No items found with name " + itemName);
        ((TraderManager) userManager).addToWishList(userID, itemIDs.get(0));
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
        ArrayList<String> users = new ArrayList<>();

        // converts trade-id to other users' id
        for(String trade_id : ((TraderManager) userManager).getAcceptedTrades(userID)){
            users.add(tradeManager.getOtherUser(trade_id, userID));
        }

        Set<String> distinct = new HashSet<>(users);
        int highest = 0;
        for (int i = 0; i < 3; i++) {
            for (String user_id : distinct) {
                int possible_high = Collections.frequency(users, user_id);
                if (possible_high > highest) {
                    frequentTraders[i] = user_id;
                    highest = possible_high;
                }
            }
            distinct.remove(frequentTraders[i]);
        }

        //converts frequentTraders from ID array to username array
        for (int i = 0; i < 3 && frequentTraders[i] != null; i++){
            frequentTraders[i] = userManager.getUsername(frequentTraders[i]);
        }

        return frequentTraders;
    }

    /**
     * Gets a list of the items used in trades
     *
     * @param userId id of the user
     * @return list of unique items that the user has traded/received from a trade
     * @throws EntryNotFoundException
     */
    public Set<String> getRecentTradeItems(String userId) throws EntryNotFoundException {
        ArrayList<String> completedTrades = ((TraderManager) userManager).getCompletedTrades(userId);
        Set<String> recentTradeItemNames = new HashSet<>();
        for (String tradeID : completedTrades) {
            String[] tradableItemIDs = tradeManager.getItemsFromTrade(tradeID);
            recentTradeItemNames.add(getTradableItemName(tradableItemIDs[0]));
            if (!tradableItemIDs[1].equals(""))
                recentTradeItemNames.add(getTradableItemName(tradableItemIDs[1]));
        }
        return recentTradeItemNames;
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
     * Process the item request of a user
     * @param traderName username of the trader
     * @param itemName name of the item
     * @param isAccepted true if item is accepted, false if rejected
     * @throws EntryNotFoundException
     */
    public void processItemRequest(String traderName, String itemName, boolean isAccepted) throws EntryNotFoundException {
        String traderID = userManager.getUserId(traderName);
        ArrayList<String> reqItems = ((AdminManager)userManager).getRequestedItems(traderID);
        String reqItemID = "";
        for(String itemID : reqItems) {
            if(itemName.equals(tradableItemManager.getName(itemID))) {
                reqItemID = itemID;
                break;
            }
        } if(!reqItemID.trim().equals("")) {
            if(isAccepted) {
                ((AdminManager) userManager).acceptRequestItem(traderID, reqItemID);
            } else {
                ((AdminManager) userManager).rejectRequestItem(traderID, reqItemID);
                tradableItemManager.deleteItem(reqItemID);
            }
        } else {
            throw new EntryNotFoundException(itemName + " was not found in the user's requested items list");
        }
    }

    /**
     * Gets the current weekly trade limit
     * @return the current trade limit
     */
    public int getCurrentTradeLimit() throws EntryNotFoundException {
        return ((AdminManager) userManager).getTradeLimit();
    }

    /**
     * Sets the new weekly trade limit (Admin method)
     * @param tradeLimit the new weekly trade limit
     */
    public void setTradeLimit(int tradeLimit) throws EntryNotFoundException {
        ((AdminManager) userManager).setTradeLimit(tradeLimit);
    }

    /**
     * 2-way trade method
     * @param userId  id of the logged-in user
     * @param secondUserName username of the want-to-trade with user
     * @param firstMeeting Date of the first meeting
     * @param secondMeeting Date of the second meeting
     * @param meetingLocation Location of both meetings
     * @param lendItemIndex index of the item that will be lent to
     * @param borrowItemIndex index of the item that will be borrowed
     * @return true if the trade has been processed successfully
     * @throws EntryNotFoundException
     */
    public boolean trade(String userId, String secondUserName, Date firstMeeting, Date secondMeeting, String meetingLocation, int lendItemIndex, int borrowItemIndex) throws EntryNotFoundException, IndexOutOfBoundsException {
        String secondUserId = getIdFromUsername(secondUserName);
        String lendItemId = lendItemIndex == -1 ? "" : getAvailableItems(userId).get(lendItemIndex);
        String borrowItemId = borrowItemIndex == -1 ? "" : getAvailableItems(secondUserId).get(borrowItemIndex);

        String tradeId = tradeManager.addTrade(userId, secondUserId, firstMeeting, secondMeeting, meetingLocation, lendItemId, borrowItemId, 3);
        ((TraderManager) userManager).addToIncompleteTradeCount(userId);
        ((TraderManager) userManager).addRequestTrade(secondUserId, tradeId);
        ((TraderManager) userManager).addRequestTrade(userId, tradeId);
        ((TraderManager) userManager).addToIncompleteTradeCount(secondUserId);

        return true;
    }

    /**
     * 1-way Trade Method: logged-in user giving an item to another user
     * @param userId id of the logged-in user
     * @param secondUserName username of the want-to-trade with user
     * @param firstMeeting Date of the first meeting
     * @param secondMeeting Date of the second meeting
     * @param meetingLocation Location of both meetings
     * @param lendItemIndex index of the item that will be lent
     * @return true if the trade was processed successfully
     * @throws EntryNotFoundException
     */
    public boolean lendItem(String userId, String secondUserName, Date firstMeeting, Date secondMeeting,
                            String meetingLocation, int lendItemIndex) throws EntryNotFoundException{
        return trade(userId, secondUserName, firstMeeting, secondMeeting, meetingLocation, lendItemIndex, -1);
    }

    /**
     * 1-way Trade Method: logged-in user wants an item from another user
     * @param userId id of the logged-in user
     * @param secondUserName username of the want-to-trade with user
     * @param firstMeeting Date of the first meeting
     * @param secondMeeting Date of the second meeting
     * @param meetingLocation Location of both meetings
     * @param borrowItemIndex index of the item that will be borrowed
     * @return true if the trade has been processed successfully
     * @throws EntryNotFoundException
     */
    public boolean borrowItem(String userId, String secondUserName, Date firstMeeting, Date secondMeeting,
                              String meetingLocation, int borrowItemIndex) throws EntryNotFoundException {
        return trade(userId, secondUserName, firstMeeting, secondMeeting, meetingLocation, -1, borrowItemIndex);
    }

    /**
     * Confirms an accepted trade took place outside of the program
     * @param userID id of the user
     * @param tradeID id of the trade
     * @return true if the trade was successfully confirmed
     */
    public boolean confirmTrade(String userID, String tradeID) throws EntryNotFoundException {

        if (tradeManager.getFirstMeetingConfirmed(tradeID, userID) && tradeManager.hasSecondMeeting(tradeID))
            tradeManager.confirmSecondMeeting(tradeID, userID, true);
        else
            tradeManager.confirmFirstMeeting(tradeID, userID, true);
        if(tradeManager.isFirstMeetingConfirmed(tradeID)){ //once both users have confirmed the trade has taken place, the inventories(avalible items list) should update
            String itemsFromTrade[] = tradeManager.getItemsFromTrade(tradeID);
            String TradeIds[] = tradeManager.getTraderIDsFromTrade(tradeID);
            ((TraderManager)userManager).trade(TradeIds[0], TradeIds[1], itemsFromTrade[0], itemsFromTrade[1]);
        }
        return true;
    }

    /**
     * Accepts a requested trade
     * @param tradeID id of the trade
     * @return true if the trade request was sucessfully confirmed
     * @throws EntryNotFoundException
     */
    public boolean acceptTrade(String tradeID) throws EntryNotFoundException {
        return ((TraderManager) userManager).acceptTradeRequest(loggedInUserId, tradeID);
    }

    /**
     * Rejects a requested trade
     * @param userID id of the user
     * @param tradeID id of the trade
     * @return true if the trade request was successfully rejected
     */
    public boolean rejectTrade(String userID, String tradeID) throws EntryNotFoundException  {
        return ((TraderManager) userManager).denyTrade(loggedInUserId, userID, tradeID);
    }

    /**
     * edits the trade object
     * @param userID id of the user
     * @param traderId id of the other user of the trade
     * @param tradeID id of the trade
     * @param firstMeeting first meeting date object
     * @param secondMeeting second meeting date object
     * @param meetingLocation String of the meeting location
     * @param inventoryItemIndex index of the user's trade item
     * @param traderInventoryItemIndex index of the trader's trade item
     * @throws EntryNotFoundException if user or trade can  not be found
     * @throws CannotTradeException if the trade is not allowed
     */
    public boolean editTrade(String userID, String traderId,  String tradeID, Date firstMeeting,
                             Date secondMeeting, String meetingLocation, int inventoryItemIndex, int traderInventoryItemIndex) throws EntryNotFoundException, CannotTradeException {
        ArrayList<String> userInventory = ((TraderManager) userManager).getInventory(userID);
        ArrayList<String> traderInventory = ((TraderManager) userManager).getInventory(traderId);
        tradeManager.editTrade(tradeID, firstMeeting, secondMeeting, meetingLocation,
                userInventory.get(inventoryItemIndex), traderInventory.get(traderInventoryItemIndex));
        return true;
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
            for(int i = 0; i < inventory.size(); i++) {
                if(items[0].equals(inventory.get(i))) return i;
            }
        }
        else {
            for(int i = 0; i < inventory.size(); i++) {
                if(items[1].equals(inventory.get(i))) return i;
            }
        }
        throw new EntryNotFoundException("The trade item could not be found in the inventory.");
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
     * Gets the tradeID given the index of the users requested trade
     * @param userId id of the user
     * @param requestedTradeIndex index of the requested trade
     * @return the trade ID
     * @throws EntryNotFoundException
     * @throws IndexOutOfBoundsException
     */
    public String getRequestedTradeId(String userId, int requestedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException {
        return getRequestedTrades(userId).get(requestedTradeIndex);
    }

    /**
     * Gets the tradeID given the index of the users accepted trade
     * @param userId id of the user
     * @param acceptedTradeIndex index of the accepted trade
     * @return the trade ID
     * @throws EntryNotFoundException
     * @throws IndexOutOfBoundsException
     */
    public String getAcceptedTradeId(String userId, int acceptedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException {
        return getAcceptedTrades(userId).get(acceptedTradeIndex);
    }

    /**
     * checks if the given user has confirmed all meetings took place
     * @param userID  id of the user
     * @param tradeID id of the trade
     * @return true if the user has confirmed all meetings took place, false else
     * @throws EntryNotFoundException
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
     * @throws EntryNotFoundException
     */
    public boolean isTradeInProgress(String tradeID) throws EntryNotFoundException {
        return tradeManager.isTradeInProgress(tradeID);
    }
}
