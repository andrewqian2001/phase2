package backend.models;


import java.io.Serializable;

/**
 * Represents an item that is supposed to be traded
 */
public class TradableItem extends DatabaseItem implements Serializable {

    private final String NAME;
    private final String DESCRIPTION;
    private final int cost;

    /**
     * Constructs a tradable item.
     *
     * @param name        The name of the TradableItem
     * @param description The description of the TradableItem
     */
    public TradableItem(String name, String description, int cost) {
        this.NAME = name;
        this.DESCRIPTION = description;
        this.cost = cost;
    }

    /**
     * name of the item
     *
     * @return name of the item
     */
    public String getName() {
        return NAME;
    }

    /**
     * description of the item
     *
     * @return description of the item
     */
    public String getDesc() {
        return DESCRIPTION;
    }

    public int getCost() { return cost; }
}
