package backend.tradesystem.managers;


import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.models.users.User;

import java.io.IOException;
import java.util.*;

/**
 * Used for the actions of a Trader
 */
public class TraderManager extends Manager {

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public TraderManager() throws IOException {
        super();
    }


    /**
     * Makes this user request an item
     *
     * @param name name of the item
     * @param desc description of the item
     * @throws UserNotFoundException  if the user was not found
     * @throws AuthorizationException if the user isn't a trader
     * @params id trader id
     */
    public void addRequestItem(String id, String name, String desc) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(id);
        TradableItem item = new TradableItem(name, desc);
        updateTradableItemDatabase(item);
        trader.getRequestedItems().add(item.getId());
        updateUserDatabase(trader);
    }


    /**
     * Gets the IDs of all Traders in the database
     *
     * @return An arraylist traders
     */
    public ArrayList<Trader> getAllTraders() {
        ArrayList<Trader> allTraders = new ArrayList<>();
        for (User user : getUserDatabase().getItems())
            if (user instanceof Trader)
                allTraders.add((Trader) user);
        return allTraders;
    }

    /**
     * Adds an item to this trader's wishlist
     *
     * @param traderId the trader id
     * @param itemId   the tradable item id to be added
     * @throws UserNotFoundException         if the trader with the given userId is not found
     * @throws AuthorizationException        if the user isn't a trader
     * @throws TradableItemNotFoundException the item wasn't found
     */
    public void addToWishList(String traderId, String itemId) throws UserNotFoundException, AuthorizationException,
            TradableItemNotFoundException {
        Trader trader = getTrader(traderId);
        if (!getTradableItemDatabase().contains(itemId)) throw new TradableItemNotFoundException(itemId);
        trader.getWishlist().add(itemId);
        updateUserDatabase(trader);
    }


    /**
     * Gets a user by username
     *
     * @param username username of the User
     * @return the user
     * @throws UserNotFoundException cant find username
     */
    public User getUserByUsername(String username) throws UserNotFoundException {
        ArrayList<User> users = getUserDatabase().getItems();
        for (User user : users)
            if (user.getUsername().equals(username))
                return user;
        throw new UserNotFoundException();
    }

    /**
     * Gets tradable items that has a name that starts with the input name
     * For example, if the item name is "Apple Pie", and the name to check for is "apple",
     * then that TradableItem is included as a list of items to return
     *
     * @param name the name to check the starts with
     * @return list of tradable items that match the name
     */
    public TradableItem[] getTradableItemsStartsWithName(String name) {
        ArrayList<TradableItem> items = new ArrayList<>();
        for (TradableItem item : getTradableItemDatabase().getItems())
            if (item.getName().toLowerCase().startsWith(name.toLowerCase()))
                items.add(item);
        return (TradableItem[]) items.toArray();
    }

    /**
     * @param traderId the trader id
     * @param status   if the user requested to be unfrozen
     * @throws AuthorizationException if the user is not a trader
     * @throws UserNotFoundException  if the user wasn't found
     */
    public void requestUnfreeze(String traderId, boolean status) throws AuthorizationException, UserNotFoundException {
        Trader trader = getTrader(traderId);
        trader.setUnfrozenRequested(status);
        updateUserDatabase(trader);
    }
    /**
     * return the 3 most traded with Traders
     *
     * @param traderId the trader being checked for
     * @return a String array of the usernames of the 3 most traded with Traders
     * @throws AuthorizationException user isn't a trader
     * @throws UserNotFoundException  user not found
     * @throws TradeNotFoundException trade not found
     */
    public Trader[] getFrequentTraders(String traderId) throws AuthorizationException, UserNotFoundException, TradeNotFoundException {
        ArrayList<Trader> traders = new ArrayList<>();
        ArrayList<String> completedTradesIds = getTrader(traderId).getCompletedTrades();
        for (int i = completedTradesIds.size() - 1; i >= Math.max(completedTradesIds.size() - 3, 0); i--){
            Trade trade = getTrade(completedTradesIds.get(i));
            if (trade.getFirstUserId().equals(traderId)) traders.add((getTrader(trade.getSecondUserId())));
            else traders.add(getTrader(trade.getFirstUserId()));
        }
        return (Trader[]) traders.toArray();
    }

}
