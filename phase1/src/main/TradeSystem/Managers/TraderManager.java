package main.TradeSystem.Managers;

import Database.Database;
import Database.tradableitems.TradableItem;
import Database.trades.Trade;
import Database.users.AdminManager;
import Database.users.Trader;
import Database.users.User;
import exceptions.AuthorizationException;
import exceptions.CannotTradeException;
import exceptions.EntryNotFoundException;
import exceptions.UserNotFoundException;
import main.DatabaseFilePaths;
import java.io.IOException;
import java.util.*;

import main.TradeSystem.Managers.TradeManager;
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
     * @throws UserNotFoundException if the user id passed in doesn't exist
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

    /**
     * Gets the tradeID given the index of the Database.users accepted trade
     * @param acceptedTradeIndex index of the accepted trade
     * @return the trade ID
     * @throws UserNotFoundException userId not found
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
     * get all wish list items
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

    /**
     * TODO: FIX or move to another class (i.e. TradeManager)
     * Gets a list of the items used in Database.trades
     *
     * @return list of unique items that the user has traded/received from a trade
     * @throws EntryNotFoundException cant find user id
     */
    public Set<String> getRecentTradeItems() throws EntryNotFoundException, AuthorizationException {
        ArrayList<String> completedTrades = getTrader().getCompletedTrades();
        Set<String> recentTradeItemNames = new HashSet<>();
        for (String tradeID : completedTrades) {
            String[] tradableItemIDs = tradeManager.getItemsFromTrade(tradeID);
            recentTradeItemNames.add(trade(tradableItemIDs[0]));
            if (!tradableItemIDs[1].equals(""))
                recentTradeItemNames.add(getTradableItemName(tradableItemIDs[1]));
        }
        return recentTradeItemNames;
    }

    /**
     * Gets the id of a User given their username
     *
     * @param username username of the User
     * @return id of the User
     * @throws EntryNotFoundException cant find user id
     */
    public String getIdFromUsername(String username) throws UserNotFoundException {
        LinkedList<User> users = userDatabase.getItems();
        for (User user : users)
            if (user.getUsername().equals(username))
                return user.getId();
        throw new UserNotFoundException("User with the username " + username + " not found.");
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
     * @param status if the user requested to be unfrozen
     * @throws EntryNotFoundException can't find user id
     */
    public void requestUnfreeze(boolean status) throws EntryNotFoundException, AuthorizationException {
        Trader t = getTrader();
        getTrader().setUnfrozenRequested(status);
        userDatabase.update(t);
    }

    /**TODO: fix or move to another class
     * get the item index of the trade item
     * @param userID id of the user
     * @param tradeId id of the trade
     * @return item index of the trade item
     * @throws EntryNotFoundException if the user or trade can not be found
     */
    public int getUserTradeItemIndex(String userID, String tradeId) throws EntryNotFoundException, AuthorizationException {
        String[] items = tradeManager.getItemsFromTrade(tradeId);
        ArrayList<String> inventory = getTrader().getAvailableItems();
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

    /**TODO: fix or move to another class
     * return the 3 most traded with Traders
     *
     * @return a String array of the usernames of the 3 most traded with Traders
     * @throws EntryNotFoundException cant find user id
     */
    public String[] getFrequentTraders() throws EntryNotFoundException, AuthorizationException {
        String[] frequentTraders = new String[3];
        ArrayList<String> users = new ArrayList<>();

        // converts trade-id to other Database.users' id
        for(String trade_id : getTrader().getCompletedTrades()){
            users.add(tradeManager.getOtherUser(trade_id, traderId));
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
            frequentTraders[i] = userDatabase.populate(frequentTraders[i]).getUsername();
        }

        return frequentTraders;
    }

    /**
     * Gets the username of a User given their ID
     *
     * @return username of the User
     * @throws EntryNotFoundException cant find user id
     */
    public String getUsername() throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getUsername();
    }

    /**
     * Gets a hashmap of trader ids to an arraylist of their requested items
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
     *
     * @return whether or not the current user can trade
     * @throws UserNotFoundException if the current user could not be found
     * @throws AuthorizationException
     */
    public boolean canTrade() throws UserNotFoundException, AuthorizationException {
        return getTrader().canTrade();
    }

    /**
     * Gets the name of this item
     * @param itemID the ID of this item
     * @return the name of this item
     * @throws EntryNotFoundException if the item could not be found in the database
     */
    public String getTradableItemName(String itemID) throws EntryNotFoundException {
        return tradableItemDatabase.populate(itemID).getName();
    }

    /**
     * Gets the description of this item
     * @param itemID the ID of this item
     * @return the description of this item
     * @throws EntryNotFoundException if the item could not be found in the database
     */
    public String getTradableItemDesc(String itemID) throws EntryNotFoundException {
        return tradableItemDatabase.populate(itemID).getDesc();
    }



}
