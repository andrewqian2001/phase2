package backend.tradesystem.managers;

import backend.Database;
import backend.DatabaseFilePaths;
import backend.exceptions.*;
import backend.models.PurchaseableItem;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.models.Purchase;

import java.io.IOException;
import java.util.ArrayList;

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
    private final Database<PurchaseableItem> purchasableItemDatabase;
    private final Database<Purchase> purchaseDatabase;

    /**
     * Making the database objects with set file paths
     * @param userFilePath the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath the trade database file path
     * @throws IOException issues with getting the file path
     */
    public Manager(String userFilePath, String tradableItemFilePath, String tradeFilePath, String purchasableItemFilePath, String purchaseFilePath) throws IOException {
        userDatabase = new Database<>(userFilePath);
        tradableItemDatabase = new Database<>(tradableItemFilePath);
        tradeDatabase = new Database<>(tradeFilePath);
        purchasableItemDatabase = new Database<>(purchasableItemFilePath);
        purchaseDatabase = new Database<>(purchaseFilePath);
    }

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public Manager() throws IOException {


        this(DatabaseFilePaths.USER.getFilePath(), DatabaseFilePaths.TRADABLE_ITEM.getFilePath(), DatabaseFilePaths.TRADE.getFilePath(), DatabaseFilePaths.PURCHASABLE_ITEM.getFilePath(), DatabaseFilePaths.PURCHASE.getFilePath());
    }


    /**
     * For getting the trader object
     *
     * @param id the id of the trader
     * @return the trader object
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public Trader getTrader(String id) throws UserNotFoundException, AuthorizationException {
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
    public User getUser(String id) throws UserNotFoundException {
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
    public TradableItem getTradableItem(String id) throws TradableItemNotFoundException {
        try {
            return tradableItemDatabase.populate(id);
        } catch (EntryNotFoundException e) {
            throw new TradableItemNotFoundException(id);
        }
    }

    public PurchaseableItem getPurchasableItem(String id) throws PurchaseableItemNotFoundException {
        try {
            return purchasableItemDatabase.populate(id);
        } catch (EntryNotFoundException e) {
            throw new PurchaseableItemNotFoundException(id);
        }
    }

    /**
     * Getting the trade from trade id
     *
     * @param id trade id
     * @return the trade object
     * @throws TradeNotFoundException if the trade wasn't found
     */
    public Trade getTrade(String id) throws TradeNotFoundException {
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
     *
     * @return purchasableitemdatabase
     */
    protected Database<PurchaseableItem> getPurchasableItemDatabase(){return purchasableItemDatabase;}
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
     * updates the purchaseable item database
     * @param item is the purchaseable item to be updated
     * @return the old purchaseableItem object if it exists, otherwise the new purchaseable object
     */
    protected PurchaseableItem updatePurchasableItemDatabase(PurchaseableItem item){return purchasableItemDatabase.update(item);}

    /**
     * updates the purchase database
     * @param purchase is the purchase to be updated
     * @return the old purchase object if it exists, otherwise the new purchase object
     */
    protected Purchase updatePurchaseDatabase(Purchase purchase){return purchaseDatabase.update(purchase);}

    /**
     * Gets a user by username
     *
     * @param username username of the User
     * @return the user
     * @throws UserNotFoundException cant find username
     */
    public User getUserByUsername(String username) throws UserNotFoundException {
        ArrayList<User> users = getUserDatabase().getItems();
        for (User user : users)
            if (user.getUsername().equals(username))
                return user;
        throw new UserNotFoundException();
    }
}
