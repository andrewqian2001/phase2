package Database.tradableitems;

import Database.trades.Trade;
import exceptions.EntryNotFoundException;
import Database.Database;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Used to manage and store items that are tradable
 */
public class TradableItemManager implements Serializable {
    protected Database<TradableItem> tradableItemDatabase;
    /**
     * For storing the file path of the .ser file
     *
     * @param filePath must take in .ser file
     * @throws IOException if creating a file causes issues
     */
    public TradableItemManager(String filePath) throws IOException {
        this.tradableItemDatabase = new Database(filePath);
    }

    /**
     * Creates a new TradableItem.
     *
     * @param name        name of the TradableItem to be added
     * @param description description of the TradableItem to be added
     * @return the object added
     */
    public TradableItem addItem(String name, String description) {
        TradableItem item = new TradableItem(name, description);
        return tradableItemDatabase.update(item);
    }

    /**
     * Removes a TradableItem from storage
     *
     * @param tradableItemId the item's id that is being removed
     * @return the TradableItem that got deleted
     * @throws EntryNotFoundException if the id doesn't refer to anything
     */
    public TradableItem deleteItem(String tradableItemId) throws EntryNotFoundException {
        return tradableItemDatabase.delete(tradableItemId);
    }

    /**
     * gets the name of the tradable item
     * @param tradableItemId id of the item
     * @return the name of the item
     * @throws EntryNotFoundException tradableItemId not found
     */
    public String getName (String tradableItemId) throws EntryNotFoundException{
        TradableItem item = tradableItemDatabase.populate(tradableItemId);
        return item.getName();
    }

    /**
     * gets the description of the tradable item
     * @param tradableItemId id of the item
     * @return description of the item
     * @throws EntryNotFoundException tradableItemId not found
     */
    public String getDesc (String tradableItemId) throws EntryNotFoundException{
        TradableItem item = tradableItemDatabase.populate(tradableItemId);
        return item.getDesc();
    }

    /**
     * @param name the name to check for
     * @return a list of all ids that have that name
     */
    public ArrayList<String> getIdsWithName(String name) {
        ArrayList<String> items = new ArrayList<>();
        for (TradableItem item : tradableItemDatabase.getItems()){
            if (item.getName().equals(name))
                items.add(item.getId());
        }
        return items;
    }
}
