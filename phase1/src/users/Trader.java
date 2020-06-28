package users;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * represents a trader
 */

public class Trader extends User implements Serializable {

    private final ArrayList<String> wishList = new ArrayList<>(); // Ids
    private final ArrayList<String> availableItems = new ArrayList<>();
    private final ArrayList<String> requestedItems = new ArrayList<>();
    private final ArrayList<String> acceptedTrades = new ArrayList<>();
    private final ArrayList<String> requestedTrades = new ArrayList<>();
    private int tradeLimit;
    private int incompleteTradeLim;
    private int totalItemsBorrowed;
    private int totalItemsLent;

    /**
     * Constructs a trader with its own username and password, with permissions to add item in their list and trade items.
     *
     * @param name     the trader's username
     * @param password the trader's password
     */

    public Trader(String name, String password) {
        super(name, password);
    }

    /**
     * @return the trader's wishlist
     */

    ArrayList<String> getWishlist() {
        return wishList;
    }

    /**
     * @return list of available items this trader has
     */

    ArrayList<String> getAvailableItems() {
        return availableItems;
    }


    /**
     * @return list of items this trader requested to borrow/trade
     */


    ArrayList<String> getRequestedItems() {
        return requestedItems;
    }


    /**
     * @return list of trades accepted
     */


    ArrayList<String> getAcceptedTrades() {
        return acceptedTrades;
    }

    /**
     * @return list of trades requested by this trader
     */


    ArrayList<String> getRequestedTrades() {
        return requestedTrades;
    }

    /**
     * @return how many transactions this trader can conduct in 1 week
     */

    int getTradeLimit() {
        return tradeLimit;
    }

    /**
     * @param tradeLimit number of transactions this trader can conduct in 1 week
     */


    void setTradeLimit(int tradeLimit) {
        this.tradeLimit = tradeLimit;
    }

    /**
     * @return how many transactions can be incomplete before this trader's account is frozen
     */


    int getIncompleteTradeLim() {
        return incompleteTradeLim;
    }

    /**
     * @param incompleteTradeLim how many transactions can be incomplete before this trader's account is frozen
     */


    void setIncompleteTradeLim(int incompleteTradeLim) {
        this.incompleteTradeLim = incompleteTradeLim;
    }

    /**
     * @return list of total items borrowed by the trader
     */


    int getTotalItemsBorrowed() {
        return totalItemsBorrowed;
    }

    /**
     * @param totalItemsBorrowed list of total items borrowed by the trader
     */


    void setTotalItemsBorrowed(int totalItemsBorrowed) {
        this.totalItemsBorrowed = totalItemsBorrowed;
    }

    /**
     * @return list of total items lent by the trader
     */


    int getTotalItemsLent() {
        return totalItemsLent;
    }

    /**
     * @param totalItemsLent list of total items lent by the trader
     */


    void setTotalItemsLent(int totalItemsLent) {
        this.totalItemsLent = totalItemsLent;
    }

    /**
     * @param permission to be checked
     * @return if the trader can add item to their list/trade items
     */


    public boolean hasPermission(Permission permission) {
        Permission permissions[] = {Permission.ADD_ITEM, Permission.TRADE};
        for (Permission p : permissions) {
            if (p == permission) {
                return true;
            }
        }
        return false;
    }


}
