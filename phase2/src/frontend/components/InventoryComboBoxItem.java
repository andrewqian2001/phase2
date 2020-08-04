package frontend.components;

import java.io.IOException;

import backend.exceptions.TradableItemNotFoundException;
import backend.tradesystem.queries.ItemQuery;

public class InventoryComboBoxItem {
    final String id;
    private ItemQuery itemQuery = new ItemQuery();

    public InventoryComboBoxItem(String id) throws IOException {
        this.id = id;
    }

    public String toString() {
        try {
            return itemQuery.getName(id);
        } catch (TradableItemNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getId() {
        return id;
    }
    
}