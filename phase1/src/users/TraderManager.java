package users;

import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

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

    public String registerUser(String username, String password, int tradeLimit) throws UserAlreadyExistsException {
        if (isUsernameUnique(username))
            return update(new Trader(username, password, tradeLimit)).getId();
        throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
    }

    /**
     * Adds one incomplete trade to this user
     * @param userId the id of the user
     * @throws EntryNotFoundException if the user can not be found
     */
    public void addToIncompleteTradeCount(String userId) throws EntryNotFoundException {
        Trader trader = findTraderbyId(userId);
        trader.setIncompleteTradeCount(trader.getIncompleteTradeCount() + 1);
        update(trader);
    }

    public void addToCompletedTradesList(String traderId, String tradeID) throws EntryNotFoundException {
        getCompletedTrades(traderId).add(tradeID);
        Trader trader = findTraderbyId(traderId);
        update(trader);
    }

    /**
     * Return whether this user should be frozen
     * @param userId the id of the user
     * @return true if the user should be frozen, false otherwise
     * @throws EntryNotFoundException if the user can not be found
     */
    public boolean shouldBeFrozen(String userId) throws EntryNotFoundException {
        Trader trader = findTraderbyId(userId);
        return trader.getIncompleteTradeCount() > trader.getIncompleteTradeLim();
    }


    /**
     * Checks whether this user can borrow items
     * @param userID id of the user
     * @return true if the user can borrow, false else
     * @throws EntryNotFoundException
     */
    public boolean canBorrow(String userID) throws EntryNotFoundException {
        Trader trader = findTraderbyId(userID);
        return trader.getTradeCount() >= trader.getTradeLimit();
    }

    public ArrayList<String> getInventory(String userId) throws EntryNotFoundException {
        Trader trader = findTraderbyId(userId);
        return trader.getAvailableItems();
    }


    /**
     *
     * @param user1 is the ID of the first trader
     * @param tradeId
     * @return true if trade was successful
     * @throws EntryNotFoundException
     */
    public boolean acceptTradeRequest(String user1, String tradeId) throws EntryNotFoundException {
        Trader trader1 = findTraderbyId(user1);
        if (trader1.isFrozen() || trader1.getTradeLimit() <= trader1.getTradeCount()) {
            return false;
        }
        trader1.getRequestedTrades().remove(tradeId);
        trader1.getAcceptedTrades().add(tradeId);
        trader1.setTradeCount(trader1.getTradeCount() + 1);
        update(trader1);
        return true;
    }

    /**
     * Removes a specified trade from a user's accepted trades
     * @param user1 the id of the user
     * @param tradeId the id of the trade to be removed
     * @return true if the trade was removed, false otherwise
     * @throws EntryNotFoundException if the user can not be found
     */
    public boolean removeAcceptedTrade(String user1, String tradeId) throws EntryNotFoundException{
        Trader trader1 = findTraderbyId(user1);
        boolean removed = trader1.getAcceptedTrades().remove(tradeId);
        update(trader1);
        return removed;
    }

    /**
     * Removes a trade from the two user's requested (or accepted) trades
     * 
     * @param userID  id of user
     * @param user2ID  id of user2
     * @param tradeId id of trade to deny
     * @return true if a trade was removed, false otherwise
     * @throws EntryNotFoundException if the user was not found
     */
    public boolean denyTrade(String userID, String user2ID, String tradeId) throws EntryNotFoundException {
        Trader trader = findTraderbyId(userID);
        Trader trader2 = findTraderbyId(user2ID);
        boolean removed_request = trader.getRequestedTrades().remove(tradeId);
        boolean removed_accepted = trader.getAcceptedTrades().remove(tradeId);
        boolean removed_request2 = trader2.getRequestedTrades().remove(tradeId);
        boolean removed_accepted2 = trader2.getRequestedTrades().remove(tradeId);
        update(trader);
        update(trader2);
        return (removed_request || removed_accepted) && (removed_request2 || removed_accepted2);
    }

    /**
     * Adds a trade to the specified user requested trades
     * @param userId id of user
     * @param tradeId id of the trade to add to requested trades
     * @return id of the user
     * @throws EntryNotFoundException if the user was not found
     */
    public String addRequestTrade(String userId, String tradeId) throws EntryNotFoundException{
        Trader trader =  findTraderbyId(userId);
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
        Trader trader = findTraderbyId(userId);
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
        return findTraderbyId(userId).getRequestedItems();
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
        Trader trader = findTraderbyId(userId);
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
        update(trader2);
        update(trader1);
        return true;
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
     * Performs the trade action between two users
     * 
     * @param user1 the first user's id
     * @param item1 the id of the item that user1 will be giving to user2
     * @param user2 the second user's id
     * @param item2 the id of the item that user2 will be giving to user1
     * @return user1's id
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
            if(user instanceof Trader)
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
        return findTraderbyId(userId).getRequestedTrades();
    }

    /**
     * Gets an arraylist of accepted trades of the specified trader
     * @param userId the id of the trader
     * @return an arraylist of accepted trades of the specified trader
     * @throws EntryNotFoundException if the trader with the given userId is not found
     */
    public ArrayList<String> getAcceptedTrades(String userId) throws EntryNotFoundException{
        return findTraderbyId(userId).getAcceptedTrades();

    }
    /**
     * Gets an arraylist of completed trades of the specified trader
     * @param userId the id of the trader
     * @return an arraylist of accepted trades of the specified trader
     * @throws EntryNotFoundException if the trader with the given userId is not found
     */
    public ArrayList<String> getCompletedTrades(String userId) throws EntryNotFoundException{
        return findTraderbyId(userId).getCompletedTrades();
    }

    /**
     * Adds an item to this trader's wishlist
     * @param userId the trader's id
     * @param tradableItemId the item to be added to this user's wishlist
     * @throws EntryNotFoundException if the trader with the given userId is not found
     */
    public void addToWishList(String userId, String tradableItemId) throws EntryNotFoundException{
        Trader trader = findTraderbyId(userId);
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
        Trader trader = findTraderbyId(userId);
        trader.setIncompleteTradeLim(newLimit);
    }


    /**
     * get all available items
     * @param userID user Id
     * @return arraylist of all items in all inventories
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getAvailableItems(String userID) throws EntryNotFoundException {
        return findTraderbyId(userID).getAvailableItems();
    }

    /**
     * gett all wish list items
     * @param userID user Id
     * @return arraylist of all wish list item
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getWishlist(String userID) throws EntryNotFoundException {
        return findTraderbyId(userID).getWishlist();
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

}
