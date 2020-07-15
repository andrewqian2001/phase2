package backend.models;


import java.io.Serializable;

/**
 * Represents an item that is supposed to be traded
 */
public class TradableItem extends DatabaseItem implements Serializable {

    private final String name;
    private final String description;

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
     * name of the item
     *
     * @return name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * description of the item
     *
     * @return description of the item
     */
    public String getDesc() {
        return description;
    }
}
