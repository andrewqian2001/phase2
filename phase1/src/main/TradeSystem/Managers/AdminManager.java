package main.TradeSystem.Managers;

import Database.Database;
import Database.tradableitems.TradableItem;
import Database.trades.Trade;
import Database.users.Admin;
import Database.users.Trader;
import Database.users.User;
import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import exceptions.UserNotFoundException;
import main.DatabaseFilePaths;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class AdminManager {
    private Database<User> userDatabase;
    private Database<Trade> tradeDatabase;
    private Database<TradableItem> tradableItemDatabase;
    private String adminId;

    /**
     * This is used for the actions that an admin user can do
     *
     * @param adminId this is the user id of the admin account
     * @throws IOException            if something goes wrong with getting database
     * @throws UserNotFoundException  if the user id passed in doesn't exist
     * @throws AuthorizationException if the user id is of the wrong type
     */
    public AdminManager(String adminId) throws IOException, UserNotFoundException, AuthorizationException {
        userDatabase = new Database<User>(DatabaseFilePaths.USER.getFilePath());
        tradeDatabase = new Database<Trade>(DatabaseFilePaths.TRADE.getFilePath());
        tradableItemDatabase = new Database<TradableItem>(DatabaseFilePaths.TRADABLE_ITEM.getFilePath());
        try {
            User tmp = userDatabase.populate(adminId);
            if (!(tmp instanceof Admin))
                throw new AuthorizationException("This account is not an admin type.");
            this.adminId = tmp.getId();
        } catch (EntryNotFoundException e) {
            throw new UserNotFoundException(adminId);
        }
    }

    /**
     * Freeze or unfreeze a user
     *
     * @param userID       the user id
     * @param freezeStatus to freeze the user
     * @throws UserNotFoundException can't find user id
     */
    public void setFrozen(String userID, boolean freezeStatus) throws UserNotFoundException {
        try {
            User user = userDatabase.populate(userID);
            user.setFrozen(freezeStatus);
            userDatabase.update(user);
        } catch (EntryNotFoundException e) {
            throw new UserNotFoundException(userID);
        }
    }

    /**
     * Gets a list of all Unfreeze Request
     *
     * @return a list of all unfreeze requests
     */
    public ArrayList<String> getAllUnfreezeRequests() {
        ArrayList<String> result = new ArrayList<>();
        for (User user : userDatabase.getItems()) {
            if (user.isUnfrozenRequested()) {
                result.add(user.getUsername());
            }
        }
        return result;
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
     * Process the item request of a user
     *
     * @param traderID   ID of the trader
     * @param itemIndex  index of the item
     * @param isAccepted true if item is accepted, false if rejected
     * @throws EntryNotFoundException traderName / itemName not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void processItemRequest(String traderID, int itemIndex, boolean isAccepted) throws EntryNotFoundException, AuthorizationException {
        Trader trader = getTrader(traderID);
        ArrayList<String> itemIDs = trader.getRequestedItems();
        if (itemIndex < 0 || itemIndex >= itemIDs.size()) {
            throw new EntryNotFoundException("Invalid index.");
        }
        String reqItemID = itemIDs.get(itemIndex);
        if (isAccepted) {
            trader.getAvailableItems().add(reqItemID);
        }
        trader.getRequestedItems().remove(reqItemID);
        userDatabase.update(trader);
    }

    /**
     * Sets the current weekly trade limit for all traders
     *
     * @param tradeLimit the new weekly trade limit
     * @throws UserNotFoundException couldn't get traders
     */
    public void setTradeLimit(int tradeLimit) throws UserNotFoundException {
        LinkedList<User> allUsers = userDatabase.getItems();
        for (User user : allUsers)
            if (user instanceof Trader) {
                ((Trader) user).setTradeLimit(tradeLimit);
            }
        try {
            userDatabase.save(allUsers);
        } catch (FileNotFoundException e) {
            throw new UserNotFoundException();
        }
    }

    /**
     * Changes the specified user's trade limit
     * @param userId the user who's trade limit will be changed
     * @param newLimit the new trade limit
     * @throws UserNotFoundException if the user isn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void changeTraderLimits(String userId, int newLimit) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(userId);
        trader.setIncompleteTradeLim(newLimit);
        userDatabase.update(trader);
    }

    /**
     * Return traders that should be frozen
     *
     * @return true if the user should be frozen, false otherwise
     */
    public ArrayList<String> getFreezable() {
        ArrayList<String> freezable = new ArrayList<>();
        for (User user : userDatabase.getItems()) {
            if (user instanceof Trader && ((Trader) user).getIncompleteTradeCount() > ((Trader) user).getIncompleteTradeLim()) {
                freezable.add(user.getId());
            }
        }
        return freezable;
    }

    /**
     * Changes the specified user's incomplete trade limit
     *
     * @param userId   the user who's trade limit will be changed
     * @param newLimit the new trade limit
     * @throws UserNotFoundException if the user isn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void changeIncompleteTradeLimit(String userId, int newLimit) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(userId);
        trader.setIncompleteTradeLim(newLimit);
        userDatabase.update(trader);
    }

    /**
     * Changes the specified user's weekly trade limit
     *
     * @param userId   the user who's trade limit will be changed
     * @param newLimit the new trade limit
     * @throws UserNotFoundException if the user isn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void changeWeeklyTradeLimit(String userId, int newLimit) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(userId);
        trader.setTradeLimit(newLimit);
        userDatabase.update(trader);
    }

    /**
     * For getting a user object from a user id
     *
     * @param userID the user id
     * @return the user object
     * @throws UserNotFoundException  if the user id wasn't found
     * @throws AuthorizationException this is not a trader
     */
    public Trader getTrader(String userID) throws UserNotFoundException, AuthorizationException {
        try {
            User tmp = userDatabase.populate(userID);
            if (!(tmp instanceof Trader)) throw new AuthorizationException("This is not a trader");
            return (Trader) tmp;
        } catch (EntryNotFoundException e) {
            throw new UserNotFoundException(userID);
        }
    }

    /**
     * Gets the userID of a user given their username
     *
     * @param username the username of the user
     * @return the ID of the user
     * @throws UserNotFoundException could not find user
     */
    public String getUserId(String username) throws UserNotFoundException {
        LinkedList<User> users = userDatabase.getItems();
        for (User user : users)
            if (user.getUsername().equals(username))
                return user.getId();
        throw new UserNotFoundException();
    }

    /**
     * Gets the username of a User given their ID NOTE: This will most likely be
     * deleted before rollout since theres no use for this
     *
     * @param userId id of the User
     * @return username of the User
     * @throws EntryNotFoundException cant find user id
     */
    public String getUsername(String userId) throws EntryNotFoundException {
        return userDatabase.populate(userId).getUsername();
    }

}
