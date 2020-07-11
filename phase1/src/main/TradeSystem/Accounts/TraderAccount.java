package main.TradeSystem.Accounts;

import Database.users.AdminManager;
import exceptions.*;
import java.io.IOException;
import java.util.*;

import main.TradeSystem.Managers.TraderManager;
import main.TradeSystem.Managers.TradeManager;
/**
 * For interacting with traders
 */
public class TraderAccount implements Account{

    private final TraderManager traderManager;
    private final TradeManager tradeManager;
    private final String traderId;
    /**
     * For accessing actions that a trader can do
     * @param traderId the id of the trader
     * @throws IOException if database file has issues
     * @throws UserNotFoundException if this trader doesn't exist
     * @throws AuthorizationException if this user isn't a trader or if the account is frozen
     */
    public TraderAccount(String traderId) throws IOException, UserNotFoundException, AuthorizationException {
        traderManager = new TraderManager(traderId);
        tradeManager = new TradeManager(traderId);
        this.traderId = traderId;
    }

    /**
     * @return whether this account is frozen
     * @throws UserNotFoundException if the user wasn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public boolean isFrozen() throws UserNotFoundException, AuthorizationException {
        return traderManager.getTrader().isFrozen();
    }
    /**
     * Requests that the item be added to the user's inventory
     *
     * @param itemName  name of tradable item
     * @throws EntryNotFoundException can't find user id
     */
    public void requestItem(String itemName) throws EntryNotFoundException, AuthorizationException {

        traderManager.addRequestItem(itemName);
    }

    /**
     * Adds item to wishList
     *
     * @param itemName name of tradable item
     * @throws EntryNotFoundException can't find user id or cna't find item name
     */
    public void addToWishList(String itemName) throws EntryNotFoundException, AuthorizationException {
        String itemId = traderManager.getItemId(itemName);
        traderManager.addToWishList(itemId);
    }

    /**
     * @return whether or not the trader can borrow from another trader
     * @throws UserNotFoundException the user doesn't exist
     * @throws AuthorizationException the user isn't a trader
     */
    public boolean canBorrow() throws UserNotFoundException, AuthorizationException {
        return traderManager.getTrader().canBorrow();
    }

    /**
     * Gets the IDs of all Traders in the database
     * @return An arraylist of Trader IDs
     */
    public ArrayList<String> getAllTraders() {
       return traderManager.getAllTraders();
    }


    /**
     * @return wish list of items of this trader
     * @throws UserNotFoundException if the user is not found
     * @throws AuthorizationException if the user is not a trader
     */
    public ArrayList<String> getWishlist() throws UserNotFoundException, AuthorizationException {
        return traderManager.getTrader().getWishlist();
    }
    /**
     * @return all the trades that have been requested with this trader
     * @throws UserNotFoundException if the trader with the given userId is not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getRequestedTrades() throws UserNotFoundException, AuthorizationException{
        return traderManager.getTrader().getRequestedTrades();
    }

    /**
     * @return all the trades that have been accepted by this trader
     * @throws UserNotFoundException if the trader with the given userId is not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getAcceptedTrades() throws UserNotFoundException, AuthorizationException{
        return traderManager.getTrader().getAcceptedTrades();
    }

    /**
     * @return this trader's available items
     * @throws UserNotFoundException if the trader with the given userId is not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getAvailableItems() throws UserNotFoundException, AuthorizationException{
        return traderManager.getTrader().getAvailableItems();
    }

    public String getTraderIdFromTrade(String tradeID) throws EntryNotFoundException {
        return tradeManager.getOtherUser(tradeID, traderId);
    }

    /**
     * get the item offered in the trade by the user
     * @param tradeId id of the trade
     * @return the item id
     * @throws EntryNotFoundException if the user or the trade can not be found
     */
    public String getUserOffer(String tradeId) throws EntryNotFoundException {
        String[] items = tradeManager.getItemsFromTrade(tradeId);
        if(tradeManager.isFirstUser(tradeId, traderId)) {
            return items[0];
        }
        else {
            return items[1];
        }
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
     * Gets a Map of key=id of user, value=list of their item requests
     * @return a list of item requests mapping to each user
     */
    public HashMap<String, ArrayList<String>> getAllItemRequests()  {
        return traderManager.getAllItemRequests();
    }






    @Override
    public UserTypes getAccountType() {
        return UserTypes.TRADER;
    }

    /**
     * Gets a list of the items used in Database.trades
     *
     * @param userId id of the user
     * @return list of unique items that the user has traded/received from a trade
     * @throws EntryNotFoundException cant find user id
     */
    public Set<String> getRecentTradeItems(String userId) throws EntryNotFoundException {
        ArrayList<String> completedTrades = ((Database.users.TraderManager) userManager).getCompletedTrades(userId);
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
     * Gets the id of a User given their username (usually used to get id of other trader)
     *
     * @param username username of the User
     * @return id of the User
     * @throws EntryNotFoundException cant find user id
     */
    public String getIdFromUsername(String username) throws EntryNotFoundException {
        return userManager.getUserId(username);
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
     * @throws EntryNotFoundException userId not found
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
     * @throws EntryNotFoundException user id not found
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
     * @throws EntryNotFoundException user id / trade id not found
     */
    public boolean confirmTrade(String userID, String tradeID) throws EntryNotFoundException, AuthorizationException {
        return traderManager.confirmTrade(tradeID);
    }

    /**
     * Rejects a requested trade
     * @param userID id of the user
     * @param tradeID id of the trade
     * @return true if the trade request was successfully rejected
     * @throws EntryNotFoundException could not find trade id / user id
     */
    public boolean rejectTrade(String userID, String tradeID) throws EntryNotFoundException  {
        return ((Database.users.TraderManager) userManager).denyTrade(loggedInUserId, userID, tradeID);
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
     * @return true
     */
    public boolean editTrade(String userID, String traderId,  String tradeID, Date firstMeeting, Date secondMeeting, String meetingLocation, int inventoryItemIndex, int traderInventoryItemIndex) throws CannotTradeException, EntryNotFoundException {

        traderManager.editTrade(userID, traderId, tradeID, firstMeeting, secondMeeting, meetingLocation, inventoryItemIndex, traderInventoryItemIndex);

        return true;
    }

    /**
     * Gets the tradeID given the index of the Database.users requested trade
     * @param userId id of the user
     * @param requestedTradeIndex index of the requested trade
     * @return the trade ID
     * @throws EntryNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getRequestedTradeId(int requestedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return traderManager.getRequestedTradeId(requestedTradeIndex);
    }

    /**
     * Checks if user can trade
     * @param userID
     * @return
     */
    public boolean canTrade(String userID) throws EntryNotFoundException {
        return ((Database.users.TraderManager) userManager).canTrade(userID);
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
     * Sends a trade Method
     * @param userId  id of the logged-in user
     * @param secondUserName username of the want-to-trade with user
     * @param firstMeeting Date of the first meeting
     * @param secondMeeting Date of the second meeting
     * @param meetingLocation Location of both meetings
     * @param lendItemIndex index of the item that will be lent to
     * @param borrowItemIndex index of the item that will be borrowed
     * @return true if the trade has been processed successfully
     * @throws EntryNotFoundException userId not found
     */

    public boolean sendTrade(String traderId, String secondUserName, Date firstMeeting, Date secondMeeting, String meetingLocation, int lendItemIndex, int borrowItemIndex) throws EntryNotFoundException, IndexOutOfBoundsException {



        return true;
    }

    /**
     * Accepts a requested trade
     * @param tradeID id of the trade
     * @return true if the trade request was sucessfully confirmed
     * @throws EntryNotFoundException could not find trade id
     */
    public boolean acceptTrade(String tradeID) throws EntryNotFoundException {
        return traderManager.acceptTradeRequest(tradeID);
    }

    /**
     * Confirms that other trader did not show up to the trade
     * This method should increment the other traders incomplete trade count but not this traders
     * @param userID is the ID of the user
     * @param tradeID the trade id
     * @return true
     * @throws EntryNotFoundException could not find user id / trade id
     */
    public boolean confirmIncompleteTrade(String userID, String tradeID) throws EntryNotFoundException,AuthorizationException {

        if(tradeManager.isFirstMeetingConfirmed(tradeID)){
            tradeManager.confirmSecondMeeting(tradeID,false); //maybe ill need to change this to input the other Database.users ID?
        }else{
            tradeManager.confirmFirstMeeting(tradeID,false);
        }

        String trader2Id = tradeManager.getOtherUser(tradeID, userID);
        ((Database.users.TraderManager)userManager).addToIncompleteTradeCount(trader2Id);
        return true;
    }

    /**
     * @param userId the user that wants to be unfrozen
     * @param status if the user requested to be unfrozen
     * @throws EntryNotFoundException can't find user id
     */
    public void requestUnfreeze(String userId, boolean status) throws EntryNotFoundException {
        userManager.setRequestFrozenStatus(userId, status);
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
        ArrayList<String> inventory = ((Database.users.TraderManager) userManager).getInventory(userID);
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

    /**
     * return the 3 most traded with Traders
     *
     * @param userID user Id
     * @return a String array of the usernames of the 3 most traded with Traders
     * @throws EntryNotFoundException cant find user id
     */
    public String[] getFrequentTraders(String userID) throws EntryNotFoundException {
        String[] frequentTraders = new String[3];
        ArrayList<String> users = new ArrayList<>();

        // converts trade-id to other Database.users' id
        for(String trade_id : ((Database.users.TraderManager) userManager).getCompletedTrades(userID)){
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
     * get meeting location
     * @param tradeId of the trade
     * @return the meeting location
     * @throws EntryNotFoundException if the user or the trade can not be found
     */
    public String getMeetingLocation(String tradeId) throws EntryNotFoundException {
        return tradeManager.getMeetingLocation(tradeId);
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
     * Gets your own username
     *
     * @param userId id of the User
     * @return username of the User
     * @throws EntryNotFoundException cant find user id
     */
    public String getUsername() throws EntryNotFoundException {
        return userManager.getUsername();
    }

    /**
     * Gets the tradeID given the index of the Database.users accepted trade
     * @param userId id of the user
     * @param acceptedTradeIndex index of the accepted trade
     * @return the trade ID
     * @throws EntryNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getAcceptedTradeId(int acceptedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return traderManager.getAcceptedTradeId(acceptedTradeIndex);
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
     * Gets a Map of key=id of user, value=list of their item requests
     * @return a list of item requests mapping to each user
     */
    public HashMap<String, ArrayList<String>> getAllItemRequests()  {
        return ((AdminManager) userManager).getAllItemRequests();
    }



}
