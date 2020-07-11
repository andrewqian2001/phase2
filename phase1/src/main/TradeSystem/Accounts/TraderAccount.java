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


    @Override
    public UserTypes getAccountType() {
        return UserTypes.TRADER;
    }


}
