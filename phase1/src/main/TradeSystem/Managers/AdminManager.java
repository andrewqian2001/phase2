package main.TradeSystem.Managers;

import Database.Database;
import Database.tradableitems.TradableItem;
import Database.trades.Trade;
import Database.users.Admin;
import Database.users.Trader;
import Database.users.User;
import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import main.DatabaseFilePaths;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminManager {
    private Database<User> userDatabase;
    private Database<Trade> tradeDatabase;
    private Database<TradableItem> tradableItemDatabase;
    private String adminId;
    private Admin admin;

    /**
     * This is used for the actions that an admin user can do
     *
     * @param adminId this is the user id of the admin account
     * @throws IOException            if something goes wrong with getting database
     * @throws EntryNotFoundException if the user id passed in doesn't exist
     * @throws AuthorizationException if the user id is of the wrong type
     */
    public AdminManager(String adminId) throws IOException, EntryNotFoundException, AuthorizationException {
        userDatabase = new Database<User>(DatabaseFilePaths.USER.getFilePath());
        tradeDatabase = new Database<Trade>(DatabaseFilePaths.TRADE.getFilePath());
        tradableItemDatabase = new Database<TradableItem>(DatabaseFilePaths.TRADABLE_ITEM.getFilePath());
        User tmp = userDatabase.populate(adminId);
        if (!(tmp instanceof Admin))
            throw new AuthorizationException("This account is not an admin type.");
        else
            adminId = tmp.getId();
        admin = (Admin) tmp;
    }

    /**
     * Freezes/Unfreezes a Trader given their username Requirement: Only an Admin
     * Account can preform this action
     *
     * @param userID     the username of the Trader that needs to be (un-)frozen
     * @param freeze_status if true, method will freeze the Trader, else it will
     *                     unFreeze
     * @throws EntryNotFoundException can't find username
     * @throws AuthorizationException not allowed to freeze user
     */
    public void setFrozen(String userID, boolean freeze_status) throws EntryNotFoundException {
        findUserByID(userID).setFrozen(freeze_status);
    }

    /**
     * Gets a list of all Unfreeze Request
     *
     * @return a list of all unfreeze requests
     */
    public ArrayList<String> getAllUnfreezeRequests()  {
        ArrayList<String> result = new ArrayList<>();
        for (User user : userDatabase.getItems()){
            if (user.isUnfrozenRequested()){
                result.add(user.getUsername());
            }
        }
        return result;
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
     * Process the item request of a user
     * @param traderID ID of the trader
     * @param itemIndex index of the item
     * @param isAccepted true if item is accepted, false if rejected
     * @throws EntryNotFoundException traderName / itemName not found
     */
    public void processItemRequest(String traderID, int itemIndex, boolean isAccepted) throws EntryNotFoundException {
        Trader trader = (Trader) this.findUserByID(traderID);
        ArrayList<String> itemIDs = trader.getRequestedItems();
        if (itemIndex < 0 || itemIndex >= itemIDs.size()){
            throw new EntryNotFoundException("Invalid index.");
        }
        String reqItemID = itemIDs.get(itemIndex);
        if(isAccepted) {
            trader.getAvailableItems().add(reqItemID);
        }
        trader.getRequestedItems().remove(reqItemID);
        userDatabase.update(trader);
    }

    /**
     * Sets the current weekly trade limit for all traders
     * @param tradeLimit the new weekly trade limit
     * @throws EntryNotFoundException couldn't get traders
     */
    public void setTradeLimit(int tradeLimit) throws EntryNotFoundException {
        for(User user : userDatabase.getItems()) {
            if (user instanceof Trader) {
                ((Trader) user).setTradeLimit(tradeLimit);
                userDatabase.update(user);
            }
        }
    }

    /**
     * Return traders that should be frozen
     * @return true if the user should be frozen, false otherwise
     * @throws EntryNotFoundException if the user can not be found
     */
    public ArrayList<String> getFreezable() {
        ArrayList<String> freezable = new ArrayList<>();
        for(User user : userDatabase.getItems()){
            if (user instanceof Trader && ((Trader) user).getIncompleteTradeCount() > ((Trader) user).getIncompleteTradeLim()){
                freezable.add(user.getId());
            }
        }
        return freezable;
    }

    /**
     * Changes the specified user's incomplete trade limit
     * @param userId the user who's trade limit will be changed
     * @param newLimit the new trade limit
     * @throws EntryNotFoundException if the trader could not be found
     */
    public void changeIncompleteTradeLimit(String userId, int newLimit) throws EntryNotFoundException {
        User trader = findUserByID(userId);
        if (!(trader instanceof Trader)){
            throw new EntryNotFoundException("The specified user is not a trader.");
        }
        ((Trader) trader).setIncompleteTradeLim(newLimit);
        userDatabase.update(trader);
    }

    /**
     * Changes the specified user's weekly trade limit
     * @param userId the user who's trade limit will be changed
     * @param newLimit the new trade limit
     * @throws EntryNotFoundException if the trader could not be found
     */
    public void changeWeeklyTradeLimit(String userId, int newLimit) throws EntryNotFoundException {
        User trader = findUserByID(userId);
        if (!(trader instanceof Trader)) {
            throw new EntryNotFoundException("The specified user is not a trader.");
        }
        ((Trader) trader).setTradeLimit(newLimit);
        userDatabase.update(trader);
    }

    private User findUserByID(String userID) throws EntryNotFoundException {
        return userDatabase.populate(userID);
    }



}
