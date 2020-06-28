package users;

import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class TraderManager extends UserManager implements Serializable {

    /**
     * For storing the file path of the .ser file
     *
     * @param filePath must take in .ser file
     * @throws IOException if creating a file causes issues
     */
    public TraderManager(String filePath) throws IOException {
        super(filePath);
    }

    public String registerUser(String username, String password) throws UserAlreadyExistsException {
        if (isUsernameUnique(username))
            return update(new Trader(username, password)).getId();
        throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
    }

    /**
     * Adds one of user1's requested trades to user1's accepted trade
     * @param user1   id of user
     * @param tradeId id of trade to accept
     * @return true if the trade was successfully accepted
     * @throws EntryNotFoundException if the user was not found
     */
    public boolean acceptTrade(String user1, String tradeId) throws EntryNotFoundException {
        Trader trader1 = findUserById(user1);
        if (trader1.isFrozen()) {
            return false;
        }
        trader1.getRequestedTrades().remove(tradeId);
        trader1.getAcceptedTrades().add(tradeId);
        update(trader1);
        return true;
    }

    /**
     * Removes a trade from the user's requested trades
     * 
     * @param userId  id of user
     * @param tradeId id of trade to deny
     * @return true if a trade was removed, false otherwise
     * @throws EntryNotFoundException if the user was not found
     */
    public boolean denyTrade(String userId, String tradeId) throws EntryNotFoundException {
        Trader trader = findUserById(userId);
        boolean removed = trader.getRequestedTrades().remove(tradeId);
        update(trader);
        return removed;
    }

    /**
     * Adds a trade to the specified user requested trades
     * @param userId id of user
     * @param tradeId id of the trade to add to requested trades
     * @return id of the user
     * @throws EntryNotFoundException if the user was not found
     */
    public String addRequestTrade(String userId, String tradeId) throws EntryNotFoundException{
        Trader trader =  findUserById(userId);
        trader.getRequestedTrades().add(tradeId);
        update(trader);
        return userId;
    }

    /**
     * Makes this user request an item
     * 
     * @param userId id of the user
     * @param itemId id of the item to add
     * @return the user's id
     * @throws EntryNotFoundException if the user was not found
     */
    public String addRequestItem(String userId, String itemId) throws EntryNotFoundException {
        Trader trader = findUserById(userId);
        trader.getRequestedItems().add(itemId);
        update(trader);
        return userId;
    }

    /**
     * Return the list of requested items for this user
     * 
     * @param userId the id of the user
     * @return the list of requested items for this user
     * @throws EntryNotFoundException if the user was not found
     */
    public ArrayList<String> getRequestedItems(String userId) throws EntryNotFoundException {
        return findUserById(userId).getRequestedItems();
    }

    /**
     * Accepts one of this user's requested items (allows it to be used in trades)
     * 
     * @param userId the id of the user
     * @param itemId the id of the item
     * @return the user's id
     * @throws EntryNotFoundException if the itemId or the userId could not be found
     */
    public String acceptRequestItem(String userId, String itemId) throws EntryNotFoundException {
        Trader trader = findUserById(userId);
        if (!trader.getRequestedItems().remove(itemId)) {
            throw new EntryNotFoundException("Could not find item " + itemId);
        }
        trader.getAvailableItems().add(itemId);

        update(trader);
        return userId;
    }

    /**
     * Performs the action of user1 borrowing an item from user2
     * 
     * @param user1  the id of the user borrowing an item
     * @param user2  the id of the user lending the item
     * @param itemId the id of the item
     * @return user1's id
     * @throws EntryNotFoundException if the itemId or one of the two user IDs were
     *                                not found.
     */
    public String borrowItem(String user1, String user2, String itemId, int threshold) throws EntryNotFoundException {
        Trader trader1 = findUserById(user1);
        Trader trader2 = findUserById(user2);
        if (!trader2.getAvailableItems().remove(itemId)) {
            throw new EntryNotFoundException("Item " + itemId + " not found");
        }
        if (trader1.getTotalItemsBorrowed() < threshold) {
            return user1;
        }
        trader1.getAvailableItems().add(itemId);
        trader1.setTotalItemsBorrowed(trader1.getTotalItemsBorrowed() + 1);
        trader2.setTotalItemsLent(trader2.getTotalItemsLent() + 1);
        update(trader2);
        update(trader1);
        return user1;
    }

    /**
     * Performs the action of user1 lending an item from user2
     * 
     * @param user1  the id of the user lending an item
     * @param user2  the id of the user borrowing the item
     * @param itemId the id of the item
     * @return user1's id
     * @throws EntryNotFoundException if the itemId or one of the two user IDs were
     *                                not found.
     */
    public String lendItem(String user1, String user2, String itemId, int threshold) throws EntryNotFoundException {
        return borrowItem(user2, user1, itemId, threshold);
    }

    /**
     * Performs the trade action between two users
     * 
     * @param user1 the first user's id
     * @param item1 the id of the item that user1 will be giving to user2
     * @param user2 the second user's id
     * @param item2 the id of the item that user2 will be giving to user1
     * @return user1's id
     */
    public String trade(String user1, String item1, String user2, String item2) throws EntryNotFoundException {
        Trader trader1 = findUserById(user1);
        Trader trader2 = findUserById(user2);
        if (!trader1.getAvailableItems().remove(item1)) {
            throw new EntryNotFoundException("Item " + item1 + " not found");
        }
        if (!trader2.getAvailableItems().remove(item2)) {
            trader1.getAvailableItems().add(item1);
            throw new EntryNotFoundException("Item " + item2 + " not found");
        }
        trader1.getAvailableItems().add(item2);
        trader2.getAvailableItems().add(item1);
        update(trader1);
        update(trader2);
        return user1;
    }

    /**
     * Gets a hashmap of trader ids to an arraylist of their available items
     * @return a hashmap of trader ids to an arraylist of their available items
     */
    public HashMap<String, ArrayList<String>> getAllItemsInInventories() {
        HashMap<String, ArrayList<String>> allItems = new HashMap<>();

        for (User user : getItems()) {
            if (user instanceof Trader)
                allItems.put(user.getId(), ((Trader) user).getAvailableItems());
        }
        return allItems;
    }

    /**
     * Gets a hashmap of trader ids to an arraylist of their requested items
     * @return a hashmap of trader ids to an arraylist of their requested items
     */
    public HashMap<String, ArrayList<String>> getAllRequestedItems() {
        HashMap<String, ArrayList<String>> allItems = new HashMap<>();

        for (User user : getItems()) {
            if (user instanceof Trader) {
                ArrayList<String> requestedItems = ((Trader) user).getRequestedItems();
                if (requestedItems.size() > 0)
                    allItems.put(user.getId(), requestedItems);
            }
        }
        return allItems;
    }

    /**
     * Gets the IDs of all Traders in the database
     * @return An arraylist of Trader IDs
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getAllTraders() {
        ArrayList<String> allTraders = new ArrayList<>();
        for (User user : getItems())
            allTraders.add(user.getId());

        return allTraders;
    }


    /**
     * Gets an arraylist of requested trades of the specified trader
     * @param userId the id of the trader
     * @return an arraylist of requested trades of the specified trader
     * @throws EntryNotFoundException if the trader with the given userId is not found
     */
    public ArrayList<String> getRequestedTrades(String userId) throws EntryNotFoundException{
        return findUserById(userId).getRequestedTrades();
    }

    /**
     * Gets an arraylist of accepted trades of the specified trader
     * @param userId the id of the trader
     * @return an arraylist of accepted trades of the specified trader
     * @throws EntryNotFoundException if the trader with the given userId is not found
     */
    public ArrayList<String> getAcceptedTrades(String userId) throws EntryNotFoundException{
        return findUserById(userId).getAcceptedTrades();
    }

    /**
     * Adds an item to this trader's wishlist
     * @param userId the trader's id
     * @param tradableItemId the item to be added to this user's wishlist
     * @throws EntryNotFoundException if the trader with the given userId is not found
     */
    public void addToWishList(String userId, String tradableItemId) throws EntryNotFoundException{
        Trader trader = findUserById(userId);
        trader.getWishlist().add(tradableItemId);
        update(trader);
    }


    /**
     * Changes the specified user's trade limit
     * @param userId the user who's trade limit will be changed
     * @param newLimit the new trade limit
     * @throws EntryNotFoundException if the trader could not be found
     */
    public void changeTraderLimits(String userId, int newLimit) throws EntryNotFoundException {
        Trader trader = findUserById(userId);
        trader.setIncompleteTradeLim(newLimit);
    }
    /**
     * Helper function to find a trader by id
     * 
     * @param userId the id of the trader to find
     * @return the trader that was found
     * @throws EntryNotFoundException if a trader with the given userId was not
     *                                found
     */
    private Trader findUserById(String userId) throws EntryNotFoundException {
        User user = populate(userId);
        if (user instanceof Trader) {
            return (Trader) user;
        }
        throw new EntryNotFoundException("Could not find " + userId + " + in the system.");
    }

    /**
     * get all available items
     * @param userID user Id
     * @return arraylist of all items in all inventories
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getAvailableItems(String userID) throws EntryNotFoundException {
        Trader user = (Trader)populate(userID);
        return user.getAvailableItems();
    }

    /**
     * gett all wish list items
     * @param userID user Id
     * @return arraylist of all wish list item
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getWishlist(String userID) throws EntryNotFoundException {
        Trader user = (Trader)populate(userID);
        return user.getWishlist();
    }

}
