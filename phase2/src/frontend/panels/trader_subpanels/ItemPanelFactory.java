package frontend.panels.trader_subpanels;

import backend.models.users.Trader;
import java.awt.Font;
import java.util.ArrayList;

public class ItemPanelFactory {

    private Trader trader;
    private Font regular, bold, italic, boldItalic;

    public ItemPanelFactory(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) {
        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;
    }

    public ItemPanel create(String type) {
        ArrayList<String> itemList;
        if(type.equals("Inventory")) {
            itemList = trader.getAvailableItems();
        } else if(type.equals("Wishlist")) {
            itemList = trader.getWishlist();
        }  else {
            // if the type isn't found by now, the Item Panel won't make sense
            itemList = trader.getCompletedTrades();
        }
        return new ItemPanel(trader, itemList, type, regular, bold, italic, boldItalic);
    }
}