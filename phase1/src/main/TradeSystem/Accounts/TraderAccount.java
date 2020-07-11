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

    public String getUsername() throws UserNotFoundException, AuthorizationException {
        return traderManager.getTrader().getUsername();
    }

    public String getTradableItemDesc(String itemID) throws TradableItemNotFoundException, AuthorizationException, UserNotFoundException {
        return tradeManager.getTradableItem(itemID, traderId).getDesc();
    }

    public String getTradableItemName(String itemID) throws TradableItemNotFoundException, AuthorizationException, UserNotFoundException {
        return tradeManager.getTradableItem(itemID, traderId).getName();
    }

    public String getAcceptedTradeId(int i) throws UserNotFoundException, AuthorizationException {
        return traderManager.getTrader().getAcceptedTrades().get(i);
    }

    public String getRequestedTradeId(int i) throws UserNotFoundException, AuthorizationException {
        return traderManager.getTrader().getRequestedTrades().get(i);
    }

    public String getTraderIdFromTrade(String tradeID) throws EntryNotFoundException {
        return tradeManager.getOtherUser(tradeID, traderId);
    }

    /**
     * get the item offered in the trade by the user
     * @param userID id of the user
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
     * Checks if the trade is still in progress
     * @param tradeID id of the trade
     * @return true if the trade is in progress, false else
     * @throws EntryNotFoundException trade is not found
     */
    public boolean isTradeInProgress(String tradeID) throws EntryNotFoundException {
        return tradeManager.isTradeInProgress(tradeID);
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




}
