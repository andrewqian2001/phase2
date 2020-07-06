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
     * get all items in all user's inventories 
     * 
     * @return hash map of the items
     */
    public HashMap<String, ArrayList<String>> getAllAvailableItems() {
        return ((TraderManager) userManager).getAllItemsInInventories();
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
        ArrayList<String> acceptedTrades = ((TraderManager) userManager).getAcceptedTrades(userID);
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
     * @return list of unique items that the user has traded/received from a trade
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
     * @param acceptedTradeIndex the index of the want-to-confirm trade
     * @return true if the trade was successfully confirmed
     */
	public boolean confirmTrade(String userID, int acceptedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException {
        String tradeID = getRequestedTrades(userID).get(acceptedTradeIndex);
        if(tradeManager.getFirstMeetingConfirmed(tradeID, userID) && tradeManager.hasSecondMeeting(tradeID))
            tradeManager.confirmSecondMeeting(tradeID, userID, true);
        else
            tradeManager.confirmFirstMeeting(tradeID, userID, true);
        return true;
	}

    /**
     * Accepts a requested trade 
     * @param userID id of the user
     * @param requestedTradeIndex the index of the want-to-accept trade
     * @return true if the trade request was sucesssfully confirmed
     * @throws EntryNotFoundException
     */
	public boolean acceptTrade(String userID, int requestedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException {
        String tradeID = getRequestedTrades(userID).get(requestedTradeIndex);
		return ((TraderManager) userManager).acceptTradeRequest(userID, tradeID);
	}

    /**
     * Rejects a requested trade
     * @param userID id of the user
     * @param requestedTradeIndex the index of the want-to-reject trade
     * @return true if the trade request was successfully rejected
     */
	public boolean rejectTrade(String userID, int requestedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException {
		String tradeID = getRequestedTrades(userID).get(requestedTradeIndex);
        return ((TraderManager) userManager).denyTrade(userID, tradeID);
	}

	public String getTraderIdFromTrade(String userID, int requestedTradeIndex) {
		return null;
	}

	public int getUserTradeItemIndex(String userID, int requestedTradeIndex) {
		return 0;
	}

	public int getOtherUserTradeItemIndex(String traderID, int requestedTradeIndex) {
		return 0;
	}

	public boolean isTradeTemporary(String userID, int requestedTradeIndex) {
		return false;
	}

	public Date getFirstMeeting(String userID, int requestedTradeIndex) {
		return null;
	}

	public Date getSecondMeeting(String userID, int requestedTradeIndex) {
		return null;
	}

	public Object getUserOffer(String userID, int requestedTradeIndex) {
		return null;
	}
}
