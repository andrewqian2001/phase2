package main.TradeSystem.Managers;

import Database.Database;
import Database.users.Trader;
import Database.users.User;
import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import exceptions.UserNotFoundException;
import main.DatabaseFilePaths;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used for the actions of a Trader
 */
public class TraderManager {
    private final Database<User> userDatabase;
    private final String traderId;

    /**
     * This is used for the actions that a trader user can do
     *
     * @param traderId this is the user id of the trader account
     * @throws IOException            if something goes wrong with getting database
     * @throws UserNotFoundException if the user id passed in doesn't exist
     * @throws AuthorizationException if the user is not a trader or if the user is frozen
     */
    public TraderManager(String traderId) throws IOException, UserNotFoundException, AuthorizationException {
        userDatabase = new Database<User>(DatabaseFilePaths.USER.getFilePath());
        this.traderId = getTrader(traderId).getId();
    }

    /**
     * Gets the tradeID given the index of the users requested trade
     * @param requestedTradeIndex index of the requested trade
     * @return the trade ID
     * @throws UserNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     * @throws AuthorizationException if the user isn't a trader
     */
    public String getRequestedTradeId(int requestedTradeIndex) throws UserNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return getTrader().getRequestedTrades().get(requestedTradeIndex);
    }


    /**
     * Performs the trade action between two Database.users
     *
     * @param user1 the first user's id
     * @param item1 the id of the item that user1 will be giving to user2
     * @param user2 the second user's id
     * @param item2 the id of the item that user2 will be giving to user1
     * @return user1's id
     * @throws EntryNotFoundException Database.users / items not found
     */

    public String trade(String user1, String item1, String user2, String item2) throws EntryNotFoundException {
        Trader trader1 = findTraderbyId(user1);
        Trader trader2 = findTraderbyId(user2);
        if (!trader1.getAvailableItems().remove(item1)) {
            throw new EntryNotFoundException("Item " + item1 + " not found");
        }
        if (!trader2.getAvailableItems().remove(item2)) {
            trader1.getAvailableItems().add(item1);
            throw new EntryNotFoundException("Item " + item2 + " not found");
        }
        trader1.getAvailableItems().add(item2);
        trader2.getAvailableItems().add(item1);
        trader1.getWishlist().remove(item2);
        trader2.getWishlist().remove(item1);
        userDatabase.update(trader1);
        userDatabase.update(trader2);
        return user1;
    }


    /**
     * Performs the action of user1 lending an item from user2
     *
     * @param user1  the id of the user lending an item
     * @param user2  the id of the user borrowing the item
     * @param itemId the id of the item
     * @param threshold the value for how many items can be borrowed before starting to lend
     * @return true if the item was successfully lent
     * @throws EntryNotFoundException if the itemId or one of the two user IDs were
     *                                not found.
     */
    public boolean lendItem(String user1, String user2, String itemId, int threshold) throws EntryNotFoundException {
        return borrowItem(user2, user1, itemId, threshold);
    }

    /**
     * Performs the action of user1 borrowing an item from user2
     *
     * @param user1  the id of the user borrowing an item
     * @param user2  the id of the user lending the item
     * @param itemId the id of the item
     * @param threshold the value for how many items can be borrowed before starting to lend
     * @return true if the item was successfully borrowed
     * @throws EntryNotFoundException if the itemId or one of the two user IDs were
     *                                not found.
     */
    public boolean borrowItem(String user1, String user2, String itemId, int threshold) throws EntryNotFoundException {
        Trader trader1 = findTraderbyId(user1);
        Trader trader2 = findTraderbyId(user2);

        if (trader1.getTotalItemsLent() - trader1.getTotalItemsBorrowed() < threshold) {
            return false;
        }
        if (!trader2.getAvailableItems().remove(itemId)) {
            throw new EntryNotFoundException("Item " + itemId + " not found");
        }

        trader1.getAvailableItems().add(itemId);
        trader1.setTotalItemsBorrowed(trader1.getTotalItemsBorrowed() + 1);
        trader2.setTotalItemsLent(trader2.getTotalItemsLent() + 1);
        userDatabase.update(trader2);
        userDatabase.update(trader1);
        return true;
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
        User user = findUserById(userId);
        if (user instanceof Trader) {
            return (Trader) user;
        }
        throw new EntryNotFoundException("Could not find " + userId + " + in the system.");
    }

    /**
     * Gets the tradeID given the index of the Database.users accepted trade
     * @param userId id of the user
     * @param acceptedTradeIndex index of the accepted trade
     * @return the trade ID
     * @throws UserNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getAcceptedTradeId(String userId, int acceptedTradeIndex) throws UserNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return getTrader().getAcceptedTrades().get(acceptedTradeIndex);
    }

    /**
     * Makes this user request an item
     *
     * @param itemId id of the item to add
     * @return the user's id
     * @throws UserNotFoundException if the user was not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void addRequestItem(String itemId) throws UserNotFoundException, AuthorizationException{
        Trader trader = getTrader();
        trader.getRequestedItems().add(itemId);
        userDatabase.update(trader);
    }



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
     *
     * @param itemName is the name of the item
     * @return
     */
    public String getItemId(String itemName){

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
     * gett all wish list items
     * @return arraylist of all wish list item
     * @throws UserNotFoundException user id not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getWishlist() throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getWishlist();
    }

    /**
     * Adds an item to this trader's wishlist
     * @param tradableItemId the item to be added to this user's wishlist
     * @throws UserNotFoundException if the trader with the given userId is not found
     */
    public void addToWishList(String tradableItemId)  throws UserNotFoundException, AuthorizationException{
        Trader trader = getTrader(traderId);
        trader.getWishlist().add(tradableItemId);
        userDatabase.update(trader);
    }


    /**
     * For getting the trader object
     * @param id the id of the trader
     * @return the trader object
     * @throws UserNotFoundException if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    private Trader getTrader(String id) throws UserNotFoundException, AuthorizationException{
        try {

            User tmp = userDatabase.populate(id);
            if (tmp instanceof Trader) return (Trader) tmp;
            throw new AuthorizationException("The user requested is not a trader");
        }
        catch (EntryNotFoundException e){
            throw new UserNotFoundException(id);
        }
    }

    /**
     * get all available items
     * @param userID user Id
     * @return arraylist of all items in all inventories
     * @throws EntryNotFoundException user id not found
     */
    public ArrayList<String> getAvailableItems(String userID) throws EntryNotFoundException {
        return findTraderbyId(userID).getAvailableItems();
    }

    /**
     * For getting the trader object
     * @return the trader object
     * @throws UserNotFoundException if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public Trader getTrader() throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId);
    }

}
