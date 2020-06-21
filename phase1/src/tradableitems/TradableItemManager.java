package tradableitems;

import exceptions.EntryNotFoundException;
import main.Manager;

import java.io.IOException;
import java.io.Serializable;

/**
 * Used to manage and store items that are tradable
 */
public class TradableItemManager extends Manager<TradableItem> implements Serializable {
    /**
     * For storing the file path of the .ser file
     *
     * @param filePath must take in .ser file
     * @throws IOException if creating a file causes issues
     */
    public TradableItemManager(String filePath) throws IOException {
        super(filePath);
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
        return update(item);
    }

    /**
     * Removes a TradableItem from storage
     *
     * @param id the item's id that is being removed
     * @return the TradableItem that got deleted
     * @throws EntryNotFoundException if the id doesn't refer to anything
     */
    public TradableItem deleteItem(String id) throws EntryNotFoundException {
        return super.delete(id);
    }
}
