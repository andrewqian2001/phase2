package backend.tradesystem.managers;


import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.TradableItem;
import backend.models.users.Trader;
import java.io.IOException;

/**
 * Used for the actions of a Trader
 */
public class TraderManager extends Manager {

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public TraderManager() throws IOException {
        super();
    }


    /**
     * Makes this user request an item
     *
     * @param name name of the item
     * @param desc description of the item
     * @throws UserNotFoundException  if the user was not found
     * @throws AuthorizationException if the user isn't a trader
     * @params id trader id
     */
    public void addRequestItem(String id, String name, String desc) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(id);
        TradableItem item = new TradableItem(name, desc);
        updateTradableItemDatabase(item);
        trader.getRequestedItems().add(item.getId());
        updateUserDatabase(trader);
    }


    /**
     * Adds an item to this trader's wishlist
     *
     * @param traderId the trader id
     * @param itemId   the tradable item id to be added
     * @throws UserNotFoundException         if the trader with the given userId is not found
     * @throws AuthorizationException        if the user isn't a trader
     * @throws TradableItemNotFoundException the item wasn't found
     */
    public void addToWishList(String traderId, String itemId) throws UserNotFoundException, AuthorizationException,
            TradableItemNotFoundException {
        Trader trader = getTrader(traderId);
        if (!getTradableItemDatabase().contains(itemId)) throw new TradableItemNotFoundException(itemId);
        trader.getWishlist().add(itemId);
        updateUserDatabase(trader);
    }





}
