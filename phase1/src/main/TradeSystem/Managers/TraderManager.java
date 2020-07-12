package main.TradeSystem.Managers;

import Database.Database;
import Database.tradableitems.TradableItem;
import Database.trades.Trade;
import Database.users.Trader;
import Database.users.User;
import exceptions.*;
import main.DatabaseFilePaths;

import java.io.IOException;
import java.util.*;

/**
 * Used for the actions of a Trader
 */
public class TraderManager {
    private final Database<User> userDatabase;
    private final String traderId;
    private final Database<Trade> tradeDatabase;
    private final Database<TradableItem> tradableItemDatabase;

    /**
     * This is used for the actions that a trader user can do
     *
     * @param traderId this is the user id of the trader account
     * @throws IOException            if something goes wrong with getting database
     * @throws UserNotFoundException  if the user id passed in doesn't exist
     * @throws AuthorizationException if the user is not a trader or if the user is frozen
     */
    public TraderManager(String traderId) throws IOException, UserNotFoundException, AuthorizationException {
        userDatabase = new Database<User>(DatabaseFilePaths.USER.getFilePath());
        this.traderId = getTrader(traderId).getId();
        tradeDatabase = new Database<Trade>(DatabaseFilePaths.TRADE.getFilePath());
        tradableItemDatabase = new Database<TradableItem>(DatabaseFilePaths.TRADABLE_ITEM.getFilePath());
    }

    /**
     * Gets the tradeID given the index of the users requested trade
     *
     * @param requestedTradeIndex index of the requested trade
     * @return the trade ID
     * @throws UserNotFoundException     userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     * @throws AuthorizationException    if the user isn't a trader
     */
    public String getRequestedTradeId(int requestedTradeIndex) throws UserNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return getTrader().getRequestedTrades().get(requestedTradeIndex);
    }

    /**
     * Gets the tradeID given the index of the Database.users accepted trade
     *
     * @param acceptedTradeIndex index of the accepted trade
     * @return the trade ID
     * @throws UserNotFoundException     userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getAcceptedTradeId(int acceptedTradeIndex) throws UserNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return getTrader().getAcceptedTrades().get(acceptedTradeIndex);
    }

    /**
     * Makes this user request an item
     *
     * @param itemId id of the item to add
     * @return the user's id
     * @throws UserNotFoundException  if the user was not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void addRequestItem(String itemId) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader();
        trader.getRequestedItems().add(itemId);
        userDatabase.update(trader);
    }

    /**
     * Gets a tradable item id by index in available items
     *
     * @param traderId the trader id
     * @param index    the index
     * @return the tradable item id
     * @throws UserNotFoundException         if user isn't found
     * @throws AuthorizationException        if user a trader
     * @throws TradableItemNotFoundException if the item wasn't found
     */
    public String getItemByIndex(String traderId, int index) throws UserNotFoundException, AuthorizationException, TradableItemNotFoundException {
        try {
            return getTrader(traderId).getAvailableItems().get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new TradableItemNotFoundException();
        }
    }


    /**
     * Gets a hashmap of trader ids to an arraylist of their available items
     *
     * @return a hashmap of trader ids to an arraylist of their available items
     */
    public HashMap<String, ArrayList<String>> getAllItemsInInventories() {
        HashMap<String, ArrayList<String>> allItems = new HashMap<>();

        for (User user : userDatabase.getItems()) {
            if (user instanceof Trader)
                allItems.put(user.getId(), ((Trader) user).getAvailableItems());
        }
        return allItems;
    }

    /**
     * Gets the IDs of all Traders in the database
     *
     * @return An arraylist of Trader IDs
     */
    public ArrayList<String> getAllTraders() {
        ArrayList<String> allTraders = new ArrayList<>();
        for (User user : userDatabase.getItems())
            if (user instanceof Trader)
                allTraders.add(user.getId());
        return allTraders;
    }


    /**
     * get all wish list items
     *
     * @return arraylist of all wish list item
     * @throws UserNotFoundException  user id not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getWishlist() throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getWishlist();
    }

    /**
     * Adds an item to this trader's wishlist
     *
     * @param tradableItemId the item to be added to this user's wishlist
     * @throws UserNotFoundException if the trader with the given userId is not found
     */
    public void addToWishList(String tradableItemId) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(traderId);
        trader.getWishlist().add(tradableItemId);
        userDatabase.update(trader);
    }


    /**
     * For getting the trader object
     *
     * @param id the id of the trader
     * @return the trader object
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    private Trader getTrader(String id) throws UserNotFoundException, AuthorizationException {
        try {

            User tmp = userDatabase.populate(id);
            if (tmp instanceof Trader) return (Trader) tmp;
            throw new AuthorizationException("The user requested is not a trader");
        } catch (EntryNotFoundException e) {
            throw new UserNotFoundException(id);
        }
    }

    /**
     * get all available items
     *
     * @param userID user Id
     * @return arraylist of all items in all inventories
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getAvailableItems(String userID) throws UserNotFoundException, AuthorizationException {
        return getTrader(userID).getAvailableItems();
    }

    /**
     * For getting the trader object
     *
     * @return the trader object
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public Trader getTrader() throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId);
    }



    /**
     * Gets the id of a User given their username
     *
     * @param username username of the User
     * @return id of the User
     * @throws UserNotFoundException cant find user id
     */
    public String getIdFromUsername(String username) throws UserNotFoundException {
        LinkedList<User> users = userDatabase.getItems();
        for (User user : users)
            if (user.getUsername().equals(username))
                return user.getId();
        throw new UserNotFoundException("User with the username " + username + " not found.");
    }
    /**
     * Gets the id of a tradable item by name
     *
     * @param name item name
     * @return id of the item
     * @throws TradableItemNotFoundException can't find item with the name
     */
    public String getIdFromTradableItemName(String name) throws TradableItemNotFoundException {
        LinkedList<TradableItem> items = tradableItemDatabase.getItems();
        for (TradableItem item: items)
            if (item.getName().equals(name))
                return item.getId();
        throw new TradableItemNotFoundException();
    }

    /**
     * @param status if the user requested to be unfrozen
     */
    public void requestUnfreeze(boolean status) throws AuthorizationException, UserNotFoundException {
        Trader t = getTrader();
        getTrader().setUnfrozenRequested(status);
        userDatabase.update(t);
    }

    /**
     * Gets the username of a User given their ID
     *
     * @return username of the User
     * @throws UserNotFoundException cant find user id
     * @throws AuthorizationException user is not a trader
     */
    public String getUsername() throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getUsername();
    }

    /**
     * Gets a hashmap of trader ids to an arraylist of their requested items
     *
     * @return a hashmap of trader ids to an arraylist of their requested items
     */
    public HashMap<String, ArrayList<String>> getAllItemRequests() {
        HashMap<String, ArrayList<String>> allItems = new HashMap<>();

        for (User user : userDatabase.getItems()) {
            if (user instanceof Trader) {
                ArrayList<String> requestedItems = ((Trader) user).getRequestedItems();
                if (requestedItems.size() > 0)
                    allItems.put(user.getId(), requestedItems);
            }
        }
        return allItems;
    }

    /**
     * @return whether or not the current user can trade
     * @throws UserNotFoundException  if the current user could not be found
     * @throws AuthorizationException
     */
    public boolean canTrade() throws UserNotFoundException, AuthorizationException {
        return getTrader().canTrade();
    }

    /**
     * Gets the tradable item object
     *
     * @param itemID the ID of this item
     * @return the item object
     * @throws TradableItemNotFoundException if the item could not be found in the database
     */
    public TradableItem getTradableItem(String itemID) throws TradableItemNotFoundException {
        try {
            return tradableItemDatabase.populate(itemID);
        } catch (EntryNotFoundException e) {
            throw new TradableItemNotFoundException(itemID);
        }
    }
}
