package backend.tradesystem.queries;

import backend.exceptions.TradableItemNotFoundException;
import backend.tradesystem.Manager;

import java.io.IOException;

/**
 * For getting info about a specific item
 */
public class ItemQuery extends Manager {

    /**
     * Create an instance of ItemQuery with preset file paths from Databse enum
     *
     * @throws IOException issues with getting the file path
     */
    public ItemQuery() throws IOException {
        super();
    }

    /**
     * name of the item
     *
     * @param itemId The id of the item being checked
     * @return name of the item
     * @throws TradableItemNotFoundException If the tradable item could not be found in the database
     */
    public String getName(String itemId) throws TradableItemNotFoundException {
        return getTradableItem(itemId).getName();
    }

    /**
     * description of the item
     *
     * @param itemId The id of the item being checked
     * @return description of the item
     * @throws TradableItemNotFoundException If the tradable item could not be found in the database
     */
    public String getDesc(String itemId) throws TradableItemNotFoundException {
        return getTradableItem(itemId).getDesc();
    }

}
