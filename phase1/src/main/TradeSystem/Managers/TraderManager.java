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
     * For getting the trader object
     * @return the trader object
     * @throws UserNotFoundException if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public Trader getTrader() throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId);
    }

}
