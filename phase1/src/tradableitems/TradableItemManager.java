package tradableitems;

import main.Manager;

import java.io.IOException;
import java.io.Serializable;

public class TradableItemManager extends Manager<TradableItem> implements Serializable {

    public TradableItemManager(String filePath) throws IOException {
        super(filePath);
    }

    /**
     * Creates a new TradableItem.
     * @param name of the TradableItem to be added
     * @param description of the TradableItem to be added
     * @return id of the item added
     */
    public String addItem(String name, String description){
        TradableItem item = new TradableItem(name, description);
        try{
            update(item);
        }
        catch (ClassNotFoundException e) {
            System.err.println("Class not found.");
        }
        return item.getId();
    }
}
