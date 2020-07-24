package backend.models;
import java.io.Serializable;

/**
 * Represents an item that is supposed to be sold
 */
public class PurchaseableItem extends DatabaseItem implements Serializable {
    private final String NAME;
    private final String DESCRIPTION;
    private double price;
    /**
     * Constructs a tradable item.
     *
     * @param name        The name of the TradableItem
     * @param description The description of the TradableItem
     */
    public PurchaseableItem(String name, String description, double price) {
        this.NAME = name;
        this.DESCRIPTION = description;
        this.price = price;
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

    /**
     *
     * @return the price of the item
     */
    public double getPrice(){return price;}
}
