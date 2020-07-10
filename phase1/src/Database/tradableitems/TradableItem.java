package Database.tradableitems;

import Database.DatabaseItem;

import java.io.Serializable;

/**
 * Represents an item that is supposed to be traded
 */
public class TradableItem extends DatabaseItem implements Serializable {

    private String name;
    private String description;

    /**
     * Constructs a tradable item.
     *
     * @param name        The name of the TradableItem
     * @param description The description of the TradableItem
     */
    public TradableItem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * @return name of the item
     */
    String getName() {
        return name;
    }

    /**
     * @return description of the item
     */
    String getDesc() {
        return description;
    }
}
