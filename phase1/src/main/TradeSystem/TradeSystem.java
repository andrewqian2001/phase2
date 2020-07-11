package main.TradeSystem;

import exceptions.*;
import Database.tradableitems.TradableItem;
import Database.users.*;
import main.TradeSystem.Accounts.Account;
import main.TradeSystem.Accounts.AdminAccount;
import main.TradeSystem.Accounts.TraderAccount;
import main.TradeSystem.Accounts.UserTypes;
import main.TradeSystem.Managers.LoginManager;

import java.io.*;
import java.util.*;

public class TradeSystem implements Serializable {

    private final LoginManager loginManager;
    private Account account = null;
    private String lastLoggedInString = "";

    /**
     * For setting up the login manager
     * @throws IOException if database has issues
     */
    public TradeSystem() throws IOException {
        loginManager = new LoginManager();
    }

    /**
     * Logging in
     * @param username username of the user
     * @param password password of the user
     * @return the user id
     * @throws UserNotFoundException bad credentials
     */
    public String login(String username, String password) throws UserNotFoundException {
        lastLoggedInString = loginManager.login(username, password);
        return lastLoggedInString;
    }

    /**
     * Making a new account
     * @param username username of the user
     * @param password password of the user
     * @param type type of the account
     * @return the user id
     * @throws UserAlreadyExistsException if username isn't unique
     */
    public String register(String username, String password, UserTypes type) throws UserAlreadyExistsException {
        lastLoggedInString = loginManager.registerUser(username, password, type);
        return lastLoggedInString;
    }


    /**
     * Getting the account
     * @return the account
     * @throws AuthorizationException if the account doesn't match the user id
     * @throws UserNotFoundException if the user id doesn't exist
     * @throws IOException issues with database
     */
    public Account getAccount() throws AuthorizationException, UserNotFoundException, IOException {
        switch(loginManager.getLastLoggedInType()){
            case ADMIN:
                return new AdminAccount(lastLoggedInString);
            case TRADER:
                return new TraderAccount(lastLoggedInString);
        }
        return null;
    }


    /**
     * Freezes/Unfreezes a Trader given their username Requirement: Only an Admin
     * Account can preform this action
     *
     * @param username     the username of the Trader that needs to be (un-)frozen
     * @param freezeStatus if true, method will freeze the Trader, else it will
     *                     unFreeze
     * @throws EntryNotFoundException can't find username
     * @throws AuthorizationException not allowed to freeze user
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
     * Sets the new weekly trade limit (Admin method)
     * @param tradeLimit the new weekly trade limit
     * @throws EntryNotFoundException Can't find traders
     */
    public void setTradeLimit(int tradeLimit) throws EntryNotFoundException {
        ((AdminManager) userManager).setTradeLimit(tradeLimit);
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
