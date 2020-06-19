package tradableitems;

import main.Manager;

import java.io.IOException;
import java.io.Serializable;

public class TradableItemManager extends Manager<TradableItem> implements Serializable {

    public TradableItemManager(String filePath) throws IOException {
        super(filePath);
    }

    public int addItem(String name, String description){
        TradableItem item = new TradableItem(name, description);
        try{
            update(item);
        }
        catch (ClassNotFoundException e) {
            System.err.println("Class TradableItem is not found.");
            return -1;
        }
        return item.getId();
    }
}
