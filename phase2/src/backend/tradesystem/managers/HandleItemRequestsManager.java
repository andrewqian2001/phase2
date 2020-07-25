package backend.tradesystem.managers;

import backend.exceptions.AuthorizationException;
import backend.exceptions.PurchaseableItemNotFoundException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.TradableItem;
import backend.models.users.Trader;
import backend.models.users.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * For handling new TradableItem + purchasable items requests
 */

public class HandleItemRequestsManager extends  Manager{
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
     * @param userFilePath the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath the trade database file path
     * @throws IOException issues with getting the file path
     */
    public HandleItemRequestsManager(String userFilePath, String tradableItemFilePath, String tradeFilePath, String purchasableItemFilePath, String purchaseFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath, purchasableItemFilePath, purchaseFilePath);
    }

    /**
     * Gets a hashmap of trader ids to an arraylist of their requested items
     *
     * @return a hashmap of trader ids to an arraylist of their requested items
     * @throws TradableItemNotFoundException if the tradable item doesn't exist in the database
     */
    public HashMap<Trader, ArrayList<TradableItem>> getAllItemRequests() throws TradableItemNotFoundException {
        HashMap<Trader, ArrayList<TradableItem>> allItems = new HashMap<>();

        for (User user : getUserDatabase().getItems()) {
            if (user instanceof Trader) {
                // Get requested item IDs
                ArrayList<String> requestedItems = ((Trader) user).getRequestedItems();
                ArrayList<TradableItem> populatedItems = new ArrayList<>();

                // Populate the item
                for (String item: requestedItems){
                    populatedItems.add(getTradableItem(item));
                }

                // Add the populated list to the result
                if (populatedItems.size() > 0)
                    allItems.put((Trader) user, populatedItems);
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
     * @return the updated trader
     */
    public Trader processItemRequest(String traderID, String reqItemID, boolean isAccepted) throws TradableItemNotFoundException, AuthorizationException, UserNotFoundException {
        Trader trader = getTrader(traderID);
        ArrayList<String> itemIDs = trader.getRequestedItems();
        if (!itemIDs.contains(reqItemID)) throw new TradableItemNotFoundException(reqItemID);
        if (isAccepted) {
            trader.getAvailableItems().add(reqItemID);
        }
        trader.getRequestedItems().remove(reqItemID);
        updateUserDatabase(trader);
        return trader;
    }

    /**
     *  the admin accepts or denies a users request to add a purchasable item to be able to be sold
     * @param traderID is the id of the trader
     * @param itemId is the id of the item
     * @param isAccepted is true if they admin accepted
     * @return the trader that has been updated
     * @throws TradableItemNotFoundException
     * @throws AuthorizationException
     * @throws UserNotFoundException
     */
    public Trader processPurchasableItemRequest(String traderID, String itemId, boolean isAccepted) throws TradableItemNotFoundException, AuthorizationException, UserNotFoundException, PurchaseableItemNotFoundException {
        Trader trader = getTrader(traderID);
        ArrayList<String> itemIDs = trader.getRequestedPurchasableItems();
        if (!itemIDs.contains(itemId)) throw new PurchaseableItemNotFoundException(itemId);
        if (isAccepted) {
            trader.getAvailableItems().add(itemId);
        }
        trader.getRequestedItems().remove(itemId);
        updateUserDatabase(trader);
        return trader;
    }
}
