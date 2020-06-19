package tradableitems;

import main.Manager;

import java.io.IOException;
import java.io.Serializable;

public class TradableItemManager extends Manager<TradableItem> implements Serializable {
    public TradableItemManager(String filePath) throws IOException {
        super(filePath);
    }
}
