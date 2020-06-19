package tradableitems;

import main.DatabaseItem;

import java.io.Serializable;

public class TradableItem extends DatabaseItem implements Serializable {

    private String name;
    private String description;

    /**
     * Constructs a tradable item.
     * @param name The name of the TradableItem
     * @param description The description of the TradableItem
     */
    public TradableItem(String name, String description){
        this.name = name;
        this.description = description;
    }

}
