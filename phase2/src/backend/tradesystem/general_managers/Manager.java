package backend.tradesystem.general_managers;

import backend.Database;
import backend.DatabaseFilePaths;
import backend.exceptions.*;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.models.users.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is used to help make accessing and modifying the database files to be easier,
 * while giving the generic EntryNotFoundException more meaning.
 * <p>
 * This contains general methods that is useful in all applications.
 */
public class Manager {

    private final Database<User> userDatabase;
    private final Database<TradableItem> tradableItemDatabase;
    private final Database<Trade> tradeDatabase;

    /**
     * Making the database objects with set file paths
     *
     * @param userFilePath         the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath        the trade database file path
     * @throws IOException issues with getting the file path
     */
    public Manager(String userFilePath, String tradableItemFilePath, String tradeFilePath) throws IOException {
        userDatabase = new Database<>(userFilePath);
        tradableItemDatabase = new Database<>(tradableItemFilePath);
        tradeDatabase = new Database<>(tradeFilePath);

    }

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public Manager() throws IOException {


        this(DatabaseFilePaths.USER.getFilePath(), DatabaseFilePaths.TRADABLE_ITEM.getFilePath(), DatabaseFilePaths.TRADE.getFilePath());
    }


    /**
     * For getting the trader object
     *
     * @param id the id of the trader
     * @return the trader object
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    protected Trader getTrader(String id) throws UserNotFoundException, AuthorizationException {
        User tmp = getUser(id);
        if (tmp instanceof Trader) return (Trader) tmp;
        throw new AuthorizationException("The user requested is not a trader");
    }

    /**
     * For getting a user object from a user id
     *
     * @param id the user id
     * @return the user object
     * @throws UserNotFoundException if the user id wasn't found
     */
    protected User getUser(String id) throws UserNotFoundException {
        try {
            return userDatabase.populate(id);
        } catch (EntryNotFoundException e) {
            throw new UserNotFoundException(id);
        }
    }

    /**
     * Gets the tradable item object
     *
     * @param id the ID of this item
     * @return the item object
     * @throws TradableItemNotFoundException if the item could not be found in the database
     */
    protected TradableItem getTradableItem(String id) throws TradableItemNotFoundException {
        try {
            return tradableItemDatabase.populate(id);
        } catch (EntryNotFoundException e) {
            throw new TradableItemNotFoundException(id);
        }
    }

    /**
     * Getting the trade from trade id
     *
     * @param id trade id
     * @return the trade object
     * @throws TradeNotFoundException if the trade wasn't found
     */
    protected Trade getTrade(String id) throws TradeNotFoundException {
        Trade trade;
        try {
            trade = tradeDatabase.populate(id);
        } catch (EntryNotFoundException e) {
            throw new TradeNotFoundException(id);
        }
        return trade;
    }

    /**
     * Gets the user database
     *
     * @return the user database
     */
    protected Database<User> getUserDatabase() {
        return userDatabase;
    }

    /**
     * Gets the tradable item database
     *
     * @return the tradable item database
     */
    protected Database<TradableItem> getTradableItemDatabase() {
        return tradableItemDatabase;
    }

    /**
     * Gets the trade database
     *
     * @return trade database
     */
    protected Database<Trade> getTradeDatabase() {
        return tradeDatabase;
    }

    /**
     * updates the user database
     *
     * @param user the user object to be updated
     * @return the old user object if it exists, otherwise the new user object
     */
    protected User updateUserDatabase(User user) {
        return userDatabase.update(user);
    }

    /**
     * updates the trade database
     *
     * @param trade the trade object to be updated
     * @return the old trade object if it exists, otherwise the new trade object
     */
    protected Trade updateTradeDatabase(Trade trade) {
        return tradeDatabase.update(trade);
    }

    /**
     * Updates the tradable item database
     *
     * @param item the tradable item to be updated
     * @return the old TradableItem object if it exists, otherwise the new TradableItem object
     */
    protected TradableItem updateTradableItemDatabase(TradableItem item) {
        return tradableItemDatabase.update(item);
    }

    /**
     * Gets a user by username, this runs in *n* time complexity
     *
     * @param username username of the User
     * @return the user id
     * @throws UserNotFoundException cant find username
     */
    public String getUserByUsername(String username) throws UserNotFoundException {
        for (String userId : getUserDatabase().getItems().keySet())
            if (getUser(userId).getUsername().equals(username))
                return userId;
        throw new UserNotFoundException();
    }
}
