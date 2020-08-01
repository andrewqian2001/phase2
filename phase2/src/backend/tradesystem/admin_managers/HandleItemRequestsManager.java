package backend.tradesystem.admin_managers;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.Trader;
import backend.tradesystem.Manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * For handling new TradableItem + purchasable items requests
 */

public class HandleItemRequestsManager extends Manager {
    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public HandleItemRequestsManager() throws IOException {
        super();
    }

    /**
     * Making the database objects with set file paths
     *
     * @param userFilePath         the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath        the trade database file path
     * @throws IOException issues with getting the file path
     */
    public HandleItemRequestsManager(String userFilePath, String tradableItemFilePath, String tradeFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath);
    }

    /**
     * Gets a hashmap of trader ids to an arraylist of their requested item ids
     *
     * @return a hashmap of trader ids to an arraylist of their requested item ids
     */
    public HashMap<String, ArrayList<String>> getAllItemRequests() {
        HashMap<String, ArrayList<String>> allItems = new HashMap<>();

        for (String userId : getUserDatabase().getItems().keySet()) {
            try {
                if (getUser(userId) instanceof Trader) {
                    // Get requested item IDs
                    ArrayList<String> requestedItems = ((Trader) getUser(userId)).getRequestedItems();

                    // Add the populated list to the result
                    allItems.put(userId, requestedItems);
                }
            } catch (UserNotFoundException e) {
                e.printStackTrace();
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
     * @return the updated trader
     * @throws TradableItemNotFoundException tradable item id isn't found
     * @throws AuthorizationException        if the user isn't a trader
     * @throws UserNotFoundException         trader isn't found
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


}
