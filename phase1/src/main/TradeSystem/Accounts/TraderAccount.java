package main.TradeSystem.Accounts;

import Database.tradableitems.TradableItem;
import exceptions.*;

import java.io.IOException;
import java.util.*;

import main.TradeSystem.Managers.TraderManager;
import main.TradeSystem.Managers.TradeManager;

/**
 * Represents a trader account
 */
public class TraderAccount implements Account {

    private final TraderManager traderManager;
    private final TradeManager tradeManager;
    private final String traderId;

    /**
     * For accessing actions that a trader can do
     *
     * @param traderId the id of the trader
     * @throws IOException            if database file has issues
     * @throws UserNotFoundException  if this trader doesn't exist
     * @throws AuthorizationException if this user isn't a trader or if the account is frozen
     */
    public TraderAccount(String traderId) throws IOException, UserNotFoundException, AuthorizationException {
        traderManager = new TraderManager(traderId);
        tradeManager = new TradeManager(traderId);
        this.traderId = traderId;
    }

    /**
     * the trader id
     * @return trader id
     */
    public String getTraderId() {
        return traderId;
    }

    /**
     * whether this account is frozen
     * @return whether this account is frozen
     * @throws UserNotFoundException  if the user wasn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public boolean isFrozen() throws UserNotFoundException, AuthorizationException {
        return traderManager.getTrader().isFrozen();
    }

    /**
     * Requests that the item be added to the user's inventory
     *
     * @param itemName name of tradable item
     * @param description description of the tradable item
     * @throws UserNotFoundException  if the user isn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void requestItem(String itemName, String description) throws UserNotFoundException, AuthorizationException {
        traderManager.addRequestItem(itemName, description);
    }

    /**
     * Adds item to wishList
     *
     * @param itemName name of tradable item
     * @throws UserNotFoundException         can't find user id or can't find item name
     * @throws AuthorizationException        user isn't a trader
     * @throws TradableItemNotFoundException item name doesn't exist
     */
    public void addToWishList(String itemName) throws UserNotFoundException, AuthorizationException, TradableItemNotFoundException {
        traderManager.addToWishList(traderManager.getIdFromTradableItemName(itemName));
    }

    /**
     * whether or not the trader can borrow from another trader
     *
     * @return whether or not the trader can borrow from another trader
     * @throws UserNotFoundException  the user doesn't exist
     * @throws AuthorizationException the user isn't a trader
     */
    public boolean canBorrow() throws UserNotFoundException, AuthorizationException {
        return traderManager.getTrader().canBorrow();
    }

    /**
     * Gets the IDs of all Traders in the database
     *
     * @return An arraylist of Trader IDs
     */
    public ArrayList<String> getAllTraders() {
        return traderManager.getAllTraders();
    }


    /**
     * wish list of items of this trader
     *
     * @return wish list of items of this trader
     * @throws UserNotFoundException  if the user is not found
     * @throws AuthorizationException if the user is not a trader
     */
    public ArrayList<String> getWishlist() throws UserNotFoundException, AuthorizationException {
        return traderManager.getTrader().getWishlist();
    }

    /**
     * all the trades that have been requested with this trader
     * @return all the trades that have been requested with this trader
     * @throws UserNotFoundException  if the trader with the given userId is not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getRequestedTrades() throws UserNotFoundException, AuthorizationException {
        return traderManager.getTrader().getRequestedTrades();
    }

    /**
     * all the trades that have been accepted by this trader
     *
     * @return all the trades that have been accepted by this trader
     * @throws UserNotFoundException  if the trader with the given userId is not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getAcceptedTrades() throws UserNotFoundException, AuthorizationException {
        return traderManager.getTrader().getAcceptedTrades();
    }

    /**
     * this trader's available items
     *
     * @return this trader's available items
     * @throws UserNotFoundException  if the trader with the given userId is not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getAvailableItems() throws UserNotFoundException, AuthorizationException {
        return traderManager.getTrader().getAvailableItems();
    }

    /**
     * Gets the other trader in the trade
     *
     * @param tradeID the trade id
     * @return the other user id of the other user
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException trade doesn't belong to this user
     */
    public String getTraderIdFromTrade(String tradeID) throws TradeNotFoundException, AuthorizationException {
        return tradeManager.getOtherUser(tradeID, traderId);
    }

    /**
     * get the item offered in the trade by the user
     *
     * @param tradeId id of the trade
     * @return the item id
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException trade doesn't belong to this user
     */
    public String getUserOffer(String tradeId) throws TradeNotFoundException, AuthorizationException {
        String[] items = tradeManager.getItemsFromTrade(tradeId);
        if (tradeManager.isFirstUser(tradeId, traderId)) {
            return items[0];
        } else {
            return items[1];
        }
    }

    /**
     * return if trade is temporary
     *
     * @param tradeID id of the trade
     * @return true if trade is temporary
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException trade doesn't belong to this user
     */
    public boolean isTradeTemporary(String tradeID) throws TradeNotFoundException, AuthorizationException {
        return tradeManager.hasSecondMeeting(tradeID);
    }

    /**
     * trader account type
     *
     * @return trader account type
     */
    @Override
    public UserTypes getAccountType() {
        return UserTypes.TRADER;
    }

    /**
     * Gets a list of the items used in trades
     *
     * @return list of unique items that the user has traded/received from a trade
     * @throws TradeNotFoundException        trade wasn't found
     * @throws AuthorizationException        trade doesn't belong to this user
     * @throws UserNotFoundException         user is not a trader
     * @throws TradableItemNotFoundException tradable item isn't found
     */
    public Set<String> getRecentTradeItems() throws AuthorizationException, UserNotFoundException,
            TradableItemNotFoundException, TradeNotFoundException {
        return tradeManager.getRecentTradeItems();
    }

    /**
     * Gets the id of a User given their username (usually used to get id of other trader)
     *
     * @param username username of the User
     * @return id of the User
     * @throws UserNotFoundException cant find user id
     */
    public String getIdFromUsername(String username) throws UserNotFoundException {
        return traderManager.getIdFromUsername(username);
    }

    /**
     * 1-way Trade Method: logged-in user giving an item to another user
     *
     * @param secondUserName  username of the want-to-trade with user
     * @param firstMeeting    Date of the first meeting
     * @param secondMeeting   Date of the second meeting
     * @param meetingLocation Location of both meetings
     * @param lendItemIndex   index of the item that will be lent
     * @throws UserNotFoundException         userId not found
     * @throws TradableItemNotFoundException tradable item not found
     * @throws AuthorizationException        user isn't a trader
     * @throws CannotTradeException          trading restrictions
     */
    public void lendItem(String secondUserName, Date firstMeeting, Date secondMeeting,
                         String meetingLocation, int lendItemIndex) throws UserNotFoundException,
            TradableItemNotFoundException, AuthorizationException, CannotTradeException {

        tradeManager.requestLend(traderManager.getIdFromUsername(secondUserName),
                firstMeeting, secondMeeting, meetingLocation, traderManager.getItemByIndex(traderId, lendItemIndex), 3);
    }

    /**
     * 1-way Trade Method: logged-in user wants an item from another user
     *
     * @param secondUserName  username of the want-to-trade with user
     * @param firstMeeting    Date of the first meeting
     * @param secondMeeting   Date of the second meeting
     * @param meetingLocation Location of both meetings
     * @param borrowItemIndex index of the item that will be borrowed
     * @throws UserNotFoundException         userId not found
     * @throws TradableItemNotFoundException tradable item not found
     * @throws AuthorizationException        user isn't a trader
     * @throws CannotTradeException          trading restrictions
     */
    public void borrowItem(String secondUserName, Date firstMeeting, Date secondMeeting,
                           String meetingLocation, int borrowItemIndex) throws UserNotFoundException,
            TradableItemNotFoundException, AuthorizationException, CannotTradeException {
        String secondUser = traderManager.getIdFromUsername(secondUserName);
        tradeManager.requestBorrow(secondUser,
                firstMeeting, secondMeeting, meetingLocation, traderManager.getItemByIndex(secondUser, borrowItemIndex), 3);
    }

    /**
     * For making a two way trade
     *
     * @param traderName               the user wanting to be traded with
     * @param firstMeeting             first meeting time
     * @param secondMeeting            second meeting time
     * @param meetingLocation          where to meet
     * @param inventoryItemIndex       for getting the item in the inventory
     * @param traderInventoryItemIndex for getting the item in the other person's inventory
     * @throws UserNotFoundException         userId not found
     * @throws TradableItemNotFoundException tradable item not found
     * @throws AuthorizationException        user isn't a trader
     * @throws CannotTradeException          trading restrictions
     */
    public void trade(String traderName, Date firstMeeting, Date secondMeeting, String meetingLocation, int inventoryItemIndex, int traderInventoryItemIndex) throws UserNotFoundException, AuthorizationException, TradableItemNotFoundException, CannotTradeException {
        String secondUser = traderManager.getIdFromUsername(traderName);
        tradeManager.requestTrade(secondUser,
                firstMeeting, secondMeeting, meetingLocation, traderManager.getItemByIndex(traderId, inventoryItemIndex),
                traderManager.getItemByIndex(secondUser, traderInventoryItemIndex), 3);
    }

    /**
     * Confirms an accepted trade took place outside of the program
     *
     * @param tradeID id of the trade
     * @return true if the trade was successfully confirmed
     * @throws UserNotFoundException  userId not found
     * @throws AuthorizationException user isn't a trader
     * @throws CannotTradeException   trading restrictions
     * @throws TradeNotFoundException if the trade isn't found
     */
    public boolean confirmTrade(String tradeID) throws AuthorizationException, TradeNotFoundException,
            CannotTradeException, UserNotFoundException {
        if(tradeManager.isFirstMeetingConfirmed(tradeID))
            tradeManager.confirmSecondMeeting(tradeID, true);
        else
            tradeManager.confirmFirstMeeting(tradeID, true);
        return true;
    }



    /**
     * Rejects a requested trade
     *
     * @param tradeID id of the trade
     * @throws AuthorizationException user isn't a trader
     * @throws TradeNotFoundException trade wasn't found
     */
    public void rejectTrade(String tradeID) throws AuthorizationException, TradeNotFoundException {
        tradeManager.denyTrade(tradeID);
    }

    /**
     * edits the trade object
     *
     * @param tradeID                  id of the trade
     * @param firstMeeting             first meeting date object
     * @param secondMeeting            second meeting date object
     * @param meetingLocation          String of the meeting location
     * @param inventoryItemIndex       index of the user's trade item
     * @param traderInventoryItemIndex index of the trader's trade item
     * @throws CannotTradeException          if the trade is not allowed
     * @throws AuthorizationException        if the user cannot access this trade
     * @throws TradableItemNotFoundException couldn't find the item
     * @throws UserNotFoundException         if the user isn't found
     * @throws TradeNotFoundException        if the trade isn't found
     */
    public void editTrade(String tradeID, Date firstMeeting, Date secondMeeting, String meetingLocation,
                          int inventoryItemIndex, int traderInventoryItemIndex)
            throws CannotTradeException, TradeNotFoundException, UserNotFoundException, AuthorizationException, TradableItemNotFoundException {

        tradeManager.editTrade(tradeID, firstMeeting, secondMeeting, meetingLocation, inventoryItemIndex, traderInventoryItemIndex);
    }

    /**
     * Gets the tradeID given the index of the users requested trade
     *
     * @param requestedTradeIndex index of the requested trade
     * @return the trade ID
     * @throws UserNotFoundException     if the user isn't found
     * @throws AuthorizationException    if the user isn't a trader
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getRequestedTradeId(int requestedTradeIndex) throws UserNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return traderManager.getRequestedTradeId(requestedTradeIndex);
    }

    /**
     * Checks if user can trade
     *
     * @return if user cna trade
     * @throws UserNotFoundException  user id wasn't found
     * @throws AuthorizationException user isn't a trader
     */
    public boolean canTrade() throws UserNotFoundException, AuthorizationException {
        return traderManager.canTrade();
    }

    /**
     * Accepts a requested trade
     *
     * @param tradeID the trade id
     * @return if trade went through
     * @throws UserNotFoundException  if user wasn't found
     * @throws AuthorizationException if user isn't a trader
     * @throws CannotTradeException   if user has trading restrictions
     * @throws TradeNotFoundException if the trade wasn't found
     */
    public void acceptTrade(String tradeID) throws UserNotFoundException, AuthorizationException, CannotTradeException, TradeNotFoundException {
        tradeManager.confirmRequest(tradeID);
    }

    /**
     * Confirms that other trader did not show up to the trade
     * This method should increment the other traders incomplete trade count but not this traders
     *
     * @param tradeID the trade id
     * @throws AuthorizationException this trade doesn't belong to the user
     * @throws TradeNotFoundException trade wasn't found
     * @throws UserNotFoundException  user wasn't fond
     */
    public void confirmIncompleteTrade(String tradeID) throws AuthorizationException, TradeNotFoundException, UserNotFoundException {

        if (tradeManager.isFirstMeetingConfirmed(tradeID))
            tradeManager.confirmSecondMeeting(tradeID, true);
        tradeManager.confirmFirstMeeting(tradeID, true);
    }

    /**
     * Makes this user request unfreeze (changes the status of whether an unfreeze was requested)
     *
     * @param status if the user requested to be unfrozen
     * @throws UserNotFoundException  if the user isn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void requestUnfreeze(boolean status) throws AuthorizationException, UserNotFoundException {
        traderManager.requestUnfreeze(status);
    }

    /**
     * get the item index of the trade item
     *
     * @param tradeId id of the trade
     * @return item index of the trade item
     * @throws TradableItemNotFoundException tradable item not found
     * @throws AuthorizationException        user isn't a trader
     * @throws TradeNotFoundException        trade not found
     * @throws UserNotFoundException         trader not found
     */
    public int getUserTradeItemIndex(String tradeId) throws TradableItemNotFoundException, AuthorizationException,
            TradeNotFoundException, UserNotFoundException {
        String[] items = tradeManager.getItemsFromTrade(tradeId);
        ArrayList<String> inventory = traderManager.getAvailableItems(traderId);
        if (tradeManager.isFirstUser(tradeId, traderId)) {
            if (items[0].equals("")) {
                return -1;
            }

            for (int i = 0; i < inventory.size(); i++) {
                if (items[0].equals(inventory.get(i))) return i;
            }
        } else {
            if (items[1].equals("")) {
                return -1;
            }
            for (int i = 0; i < inventory.size(); i++) {
                if (items[1].equals(inventory.get(i))) return i;
            }
        }
        throw new TradableItemNotFoundException();
    }

    /**
     * return the 3 most traded with Traders
     *
     * @return a String array of the usernames of the 3 most traded with Traders
     * @throws AuthorizationException user isn't a trader
     * @throws TradeNotFoundException trade not found
     * @throws UserNotFoundException  trader not found
     */
    public String[] getFrequentTraders() throws AuthorizationException, UserNotFoundException, TradeNotFoundException {

        return tradeManager.getFrequentTraders();
    }


    /**
     * get first meeting time of the trade
     *
     * @param tradeID id of the trade
     * @return Date object of the first meeting time
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException trade doesn't belong to this user
     */
    public Date getFirstMeeting(String tradeID) throws TradeNotFoundException, AuthorizationException {
        return tradeManager.getFirstMeetingTime(tradeID);
    }

    /**
     * get second meeting time of the trade
     *
     * @param tradeID id of the trade
     * @return Date object of the second meeting time
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException trade doesn't belong to this user
     */
    public Date getSecondMeeting(String tradeID) throws TradeNotFoundException, AuthorizationException {
        return tradeManager.getSecondMeetingTime(tradeID);
    }

    /**
     * get meeting location
     *
     * @param tradeId of the trade
     * @return the meeting location
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException trade doesn't belong to this user
     */
    public String getMeetingLocation(String tradeId) throws TradeNotFoundException, AuthorizationException {
        return tradeManager.getMeetingLocation(tradeId);
    }


    /**
     * Gets your own username
     *
     * @return username of the User
     * @throws UserNotFoundException  if the user isn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public String getUsername() throws UserNotFoundException, AuthorizationException {
        return traderManager.getUsername();
    }

    /**
     * Gets the tradeID given the index of the users accepted trade
     *
     * @param acceptedTradeIndex index of the accepted trade
     * @return the trade ID
     * @throws IndexOutOfBoundsException index out of bounds
     * @throws AuthorizationException    trade doesn't belong to the user
     * @throws UserNotFoundException     user wasn't found
     */
    public String getAcceptedTradeId(int acceptedTradeIndex) throws IndexOutOfBoundsException, AuthorizationException, UserNotFoundException {
        return traderManager.getAcceptedTradeId(acceptedTradeIndex);
    }

    /**
     * Checks if the trade is still in progress
     *
     * @param tradeID id of the trade
     * @return true if the trade is in progress, false else
     * @throws AuthorizationException trade doesn't belong to the user
     * @throws TradeNotFoundException trade wasn't found
     */
    public boolean isTradeInProgress(String tradeID) throws TradeNotFoundException, AuthorizationException {
        return tradeManager.isTradeInProgress(tradeID);
    }

    /**
     * checks if the given user has confirmed all meetings took place
     *
     * @param tradeID id of the trade
     * @return true if the user has confirmed all meetings took place, false else
     * @throws AuthorizationException trade doesn't belong to the user
     * @throws UserNotFoundException  user wasn't found
     * @throws TradeNotFoundException trade wasn't found
     */
    public boolean hasUserConfirmedAllMeetings(String tradeID) throws AuthorizationException, UserNotFoundException, TradeNotFoundException {
        return tradeManager.hasUserConfirmedAllMeetings(traderId, tradeID);

    }

    /**
     * Gets the name of the tradable item given its id
     *
     * @param tradableItemId id of the tradable item
     * @return name of the tradable item
     * @throws TradableItemNotFoundException cant find tradable item id
     */
    public String getTradableItemName(String tradableItemId) throws TradableItemNotFoundException {
        return traderManager.getTradableItem(tradableItemId).getName();
    }

    /**
     * Gets the description  of the tradable item given its id
     *
     * @param tradableItemId id of the tradable item
     * @return description of the tradable item
     * @throws TradableItemNotFoundException cant find tradable item id
     */
    public String getTradableItemDesc(String tradableItemId) throws TradableItemNotFoundException {
        return traderManager.getTradableItem(tradableItemId).getDesc();
    }


}
