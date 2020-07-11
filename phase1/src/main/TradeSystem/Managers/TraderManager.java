package main.TradeSystem.Managers;

import Database.Database;
import Database.users.Trader;
import Database.users.User;
import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import main.DatabaseFilePaths;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TraderManager {
    private Database<User> userDatabase;
    private String traderId;
    private Trader trader;

    /**
     * This is used for the actions that a trader user can do
     *
     * @param traderId this is the user id of the trader account
     * @throws IOException            if something goes wrong with getting database
     * @throws EntryNotFoundException if the user id passed in doesn't exist
     * @throws AuthorizationException if the user id is of the wrong type
     */
    public TraderManager(String traderId) throws IOException, EntryNotFoundException, AuthorizationException {
        userDatabase = new Database<User>(DatabaseFilePaths.USER.getFilePath());
        User tmp = userDatabase.populate(traderId);
        if (!(tmp instanceof Trader))
            throw new AuthorizationException("This account is not a trader type.");
        this.traderId = tmp.getId();

    }

    /**
     * Gets the tradeID given the index of the users requested trade
     * @param requestedTradeIndex index of the requested trade
     * @return the trade ID
     * @throws EntryNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getRequestedTradeId(int requestedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return trader.getRequestedTrades().get(requestedTradeIndex);
    }



    /**
     * Gets the tradeID given the index of the Database.users accepted trade
     * @param userId id of the user
     * @param acceptedTradeIndex index of the accepted trade
     * @return the trade ID
     * @throws EntryNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getAcceptedTradeId(String userId, int acceptedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return trader.getAcceptedTrades().get(acceptedTradeIndex);
    }

    /**
     * Makes this user request an item
     *
     * @param itemId id of the item to add
     * @return the user's id
     * @throws EntryNotFoundException if the user was not found
     */
    public void addRequestItem(String itemId) throws EntryNotFoundException {
        Trader trader = findTraderbyId(traderId);
        trader.getRequestedItems().add(itemId);
        userDatabase.update(trader);
    }





    //This version solves 1 bug but creates more
    /*
    public boolean acceptTradeRequest2(String user1, String user2, String tradeId) throws EntryNotFoundException {
        Trader trader1 = findTraderbyId(user1);
        Trader trader2 = findTraderbyId(user2);
        if (trader1.isFrozen() || trader1.getTradeLimit() <= trader1.getTradeCount()) {
            System.out.println("You have exceeded your trade limit!");
            return false;
        }

        trader1.getRequestedTrades().remove(tradeId);
        trader1.getAcceptedTrades().add(tradeId);
        trader1.setTradeCount(trader1.getTradeCount() + 1);
        trader2.getAcceptedTrades().add(tradeId);
        trader2.setTradeCount(trader1.getTradeCount() + 1);
        update(trader1);
        update(trader2);
        return true;
    }
    */







//    /**
//     * Return the list of requested items for this user
//     *
//     * @param userId the id of the user
//     * @return the list of requested items for this user
//     * @throws EntryNotFoundException if the user was not found
//     */
//    public ArrayList<String> getRequestedItems(String userId) throws EntryNotFoundException {
//        return findTraderbyId(userId).getRequestedItems();
//    }





    /**
     * Gets a hashmap of trader ids to an arraylist of their available items
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
     * @return An arraylist of Trader IDs
     */
    public ArrayList<String> getAllTraders() {
        ArrayList<String> allTraders = new ArrayList<>();
        for (User user : userDatabase.getItems())
            if(user instanceof Trader)
                allTraders.add(user.getId());
        return allTraders;
    }


    /**
     * Gets an arraylist of requested Database.trades of the specified trader
     * @param userId the id of the trader
     * @return an arraylist of requested Database.trades of the specified trader
     * @throws EntryNotFoundException if the trader with the given userId is not found
     */
    public ArrayList<String> getRequestedTrades(String userId) throws EntryNotFoundException{
        return findTraderbyId(userId).getRequestedTrades();
    }

    /**
     * Gets an arraylist of accepted Database.trades of the specified trader
     * @return an arraylist of accepted Database.trades of the specified trader
     * @throws EntryNotFoundException if the trader with the given userId is not found
     */
    public ArrayList<String> getAcceptedTrades() throws EntryNotFoundException{
        return findTraderbyId(traderId).getAcceptedTrades();

    }
    /**
     * Gets an arraylist of completed Database.trades of the specified trader
     * @return an arraylist of accepted Database.trades of the specified trader
     * @throws EntryNotFoundException if the trader with the given userId is not found
     */
    public ArrayList<String> getCompletedTrades() throws EntryNotFoundException{
        return findTraderbyId(traderId).getCompletedTrades();
    }

    /**
     * get all available items
     * @return arraylist of all items in all inventories
     * @throws EntryNotFoundException user id not found
     */
    public ArrayList<String> getAvailableItems() throws EntryNotFoundException {
        return findTraderbyId(traderId).getAvailableItems();
    }

    /**
     * gett all wish list items
     * @return arraylist of all wish list item
     * @throws EntryNotFoundException user id not found
     */
    public ArrayList<String> getWishlist() throws EntryNotFoundException {
        return findTraderbyId(traderId).getWishlist();
    }

    /**
     * Adds an item to this trader's wishlist
     * @param tradableItemId the item to be added to this user's wishlist
     * @throws EntryNotFoundException if the trader with the given userId is not found
     */
    public void addToWishList(String tradableItemId) throws EntryNotFoundException{
        Trader trader = findTraderbyId(traderId);
        trader.getWishlist().add(tradableItemId);
        userDatabase.update(trader);
    }



    /**
     * Helper function to find a trader by id
     *
     * @param userId the id of the trader to find
     * @return the trader that was found
     * @throws EntryNotFoundException if a trader with the given userId was not
     *                                found
     */
    private Trader findTraderbyId(String userId) throws EntryNotFoundException {
        User user = userDatabase.populate(userId);
        if (user instanceof Trader) {
            return (Trader) user;
        }
        throw new EntryNotFoundException("Could not find " + userId + " + in the system.");
    }


}
