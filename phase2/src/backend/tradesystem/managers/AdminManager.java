package backend.tradesystem.managers;

import backend.Database;
import backend.DatabaseFilePaths;
import backend.exceptions.AuthorizationException;
import backend.exceptions.EntryNotFoundException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.TradableItem;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.TraderProperties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Used for executing actions that an admin has
 */
public class AdminManager extends Manager{


    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public AdminManager() throws IOException {
        super();
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
            User user = getUser(userID);
            user.setFrozen(freezeStatus);
            updateUserDatabase(user);
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
        for (User user : getUserDatabase().getItems()) {
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

        for (User user : getUserDatabase().getItems()) {
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
     * @param reqItemID  the requested item to be confirmed or rejected
     * @param isAccepted true if item is accepted, false if rejected
     * @throws TradableItemNotFoundException tradable item id isn't found
     * @throws AuthorizationException if the user isn't a trader
     * @throws UserNotFoundException trader isn't found
     */
    public void processItemRequest(String traderID, String reqItemID, boolean isAccepted) throws TradableItemNotFoundException, AuthorizationException, UserNotFoundException {
        Trader trader = getTrader(traderID);
        ArrayList<String> itemIDs = trader.getRequestedItems();
        if (!itemIDs.contains(reqItemID)) throw new TradableItemNotFoundException(reqItemID);
        if (isAccepted) {
            trader.getAvailableItems().add(reqItemID);
        }
        trader.getRequestedItems().remove(reqItemID);
        updateUserDatabase(trader);
    }

    /**
     * Sets the specified limit for all traders
     *
     * @param traderProperty the property (limit) to change
     * @param limit the new value of the specified property
     * @throws UserNotFoundException couldn't get traders
     */
    public void setLimit(TraderProperties traderProperty, int limit) throws UserNotFoundException {
        ArrayList<User> allUsers = getUserDatabase().getItems();
        for (User user : allUsers)
            if (user instanceof Trader) {
                Trader t = (Trader) user;
                switch(traderProperty){
                    case TRADE_LIMIT:
                        //the following line makes it so that if the trader had "x" more trades left this week,
                        // they will still have "x" trades left
                        t.setTradeCount(t.getTradeCount() + limit - t.getTradeLimit());
                        t.setTradeLimit(limit);
                        break;
                    case INCOMPLETE_TRADE_LIM:
                        ((Trader) user).setIncompleteTradeLim(limit);
                        break;
                    case MINIMUM_AMOUNT_NEEDED_TO_BORROW:
                        ((Trader) user).setMinimumAmountNeededToBorrow(limit);
                }

            }
        try {
            getUserDatabase().save(allUsers);
        } catch (FileNotFoundException e) {
            throw new UserNotFoundException();
        }
    }

    /**
     * Changes the specified property of the specified user
     *
     * @param property the property (limit) to change
     * @param userId   the user who's property will be changed
     * @param newLimit the new limit
     * @throws UserNotFoundException  if the user isn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void setLimitSpecific(TraderProperties property, String userId, int newLimit) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(userId);
        switch (property){
            case MINIMUM_AMOUNT_NEEDED_TO_BORROW:
                trader.setMinimumAmountNeededToBorrow(newLimit);
                break;
            case INCOMPLETE_TRADE_LIM:
                trader.setIncompleteTradeLim(newLimit);
                break;
            case TRADE_LIMIT:
                trader.setTradeLimit(newLimit);
        }
        updateUserDatabase(trader);
    }



    /**
     * Return traders that should be frozen
     *
     * @return true if the user should be frozen, false otherwise
     */
    public ArrayList<String> getFreezable() {
        ArrayList<String> freezable = new ArrayList<>();
        for (User user : getUserDatabase().getItems()) {
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
     * @throws UserNotFoundException  if the user isn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void changeIncompleteTradeLimit(String userId, int newLimit) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(userId);
        trader.setIncompleteTradeLim(newLimit);
        updateUserDatabase(trader);
    }

    /**
     * Changes the specified user's weekly trade limit
     *
     * @param userId   the user who's trade limit will be changed
     * @param newLimit the new trade limit
     * @throws UserNotFoundException  if the user isn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void changeWeeklyTradeLimit(String userId, int newLimit) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(userId);
        trader.setTradeLimit(newLimit);
        updateUserDatabase(trader);
    }

    /**
     * Get all usernames of users who should be frozen
     * @return A list of usernames of users who should be frozen
     */
    public ArrayList<String> getShouldBeFrozen(){
        ArrayList<String> result = new ArrayList<>();
        for (User user : getUserDatabase().getItems()){
            if (user instanceof Trader && ((Trader) user).shouldBeFrozen()){
                result.add(user.getUsername());
            }
        }
        return result;
    }

}
