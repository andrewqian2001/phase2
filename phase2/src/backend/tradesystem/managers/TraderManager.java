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
     * Making the database objects with set file paths
     * @param userFilePath the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath the trade database file path
     * @throws IOException issues with getting the file path
     */
    public TraderManager(String userFilePath, String tradableItemFilePath, String tradeFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath);
    }


    /**
     * Makes this user request an item
     *
     * @param id trader id
     * @param name name of the item
     * @param desc description of the item
     * @throws UserNotFoundException  if the user was not found
     * @throws AuthorizationException not allowed to request an item
     * @return the item that was requested
     */
    public Trader addRequestItem(String id, String name, String desc) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(id);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        TradableItem item = new TradableItem(name, desc);
        updateTradableItemDatabase(item);
        trader.getRequestedItems().add(item.getId());
        updateUserDatabase(trader);
        return trader;
    }


    /**
     * Adds an item to this trader's wishlist
     *
     * @param traderId the trader id
     * @param itemId   the tradable item id to be added
     * @throws UserNotFoundException         if the trader with the given userId is not found
     * @throws AuthorizationException        not allowed to add to the wishlist
     * @throws TradableItemNotFoundException the item wasn't found
     */
    public void addToWishList(String traderId, String itemId) throws UserNotFoundException, AuthorizationException,
            TradableItemNotFoundException {
        Trader trader = getTrader(traderId);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        if (!getTradableItemDatabase().contains(itemId)) throw new TradableItemNotFoundException(itemId);
        trader.getWishlist().add(itemId);
        updateUserDatabase(trader);
    }

    /**
     * Set idle status, an idle trader has some limitations such as being unable to trade
     * @param traderId the trader
     * @param status whether the trader is idle
     * @throws UserNotFoundException if the trader isn't found
     * @throws AuthorizationException if unable to go idle
     */
    public void setIdle(String traderId, boolean status) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(traderId);
        if (status && trader.getAcceptedTrades().size() > 0)
            throw new AuthorizationException("Cannot go idle until ongoing trades have been resolved");
        trader.setIdle(status);
        updateUserDatabase(trader);
    }

    /**
     * Sets the city of the trader
     * @param traderId the trader
     * @param city the city
     * @throws UserNotFoundException if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public void setCity(String traderId, String city) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(traderId);
        trader.setCity(city);
        updateUserDatabase(trader);
    }
}
