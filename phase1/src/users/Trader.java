package users;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * represents a trader
 */

public class Trader extends User implements Serializable {

    private ArrayList<String> wishList = new ArrayList<>(); // Ids
    private ArrayList<String> availableItems = new ArrayList<>();
    private ArrayList<String> requestedItems = new ArrayList<>();
    private ArrayList<String> acceptedTrades = new ArrayList<>();
    private ArrayList<String> requestedTrades = new ArrayList<>();
    private ArrayList<Permission> permissions = new ArrayList<>();
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
        permissions.add(Permission.ADD_ITEM);
        permissions.add(Permission.TRADE);
    }

    /**
     * @return the trader's wishlist
     */

    public ArrayList<String> getWishlist() {
        return wishList;
    }

    /**
     * @return list of available items this trader has
     */

    public ArrayList<String> getAvailableItems() {
        return availableItems;
    }

    /**
     * @param inventory list of available items this trader has
     */


    public void setAvailableItems(ArrayList<String> inventory) {
        this.availableItems = inventory;
    }

    /**
     * @return list of items this trader requested to borrow/trade
     */


    public ArrayList<String> getRequestedItems() {
        return requestedItems;
    }

    /**
     * @param requestedItems list of items this trader requested to borrow/trade
     */


    public void setRequestedItems(ArrayList<String> requestedItems) {
        this.requestedItems = requestedItems;
    }

    /**
     * @return list of trades accepted
     */


    public ArrayList<String> getAcceptedTrades() {
        return acceptedTrades;
    }

    /**
     * @return list of trades requested by this trader
     */


    public ArrayList<String> getRequestedTrades() {
        return requestedTrades;
    }

    /**
     * @return how many transactions this trader can conduct in 1 week
     */

    public int getTradeLimit() {
        return tradeLimit;
    }

    /**
     * @param tradeLimit number of transactions this trader can conduct in 1 week
     */


    public void setTradeLimit(int tradeLimit) {
        this.tradeLimit = tradeLimit;
    }

    /**
     * @return how many transactions can be incomplete before this trader's account is frozen
     */


    public int getIncompleteTradeLim() {
        return incompleteTradeLim;
    }

    /**
     * @param incompleteTradeLim how many transactions can be incomplete before this trader's account is frozen
     */


    public void setIncompleteTradeLim(int incompleteTradeLim) {
        this.incompleteTradeLim = incompleteTradeLim;
    }

    /**
     * @return list of total items borrowed by the trader
     */


    public int getTotalItemsBorrowed() {
        return totalItemsBorrowed;
    }

    /**
     * @param totalItemsBorrowed list of total items borrowed by the trader
     */


    public void setTotalItemsBorrowed(int totalItemsBorrowed) {
        this.totalItemsBorrowed = totalItemsBorrowed;
    }

    /**
     * @return list of total items lent by the trader
     */


    public int getTotalItemsLent() {
        return totalItemsLent;
    }

    /**
     * @param totalItemsLent list of total items lent by the trader
     */


    public void setTotalItemsLent(int totalItemsLent) {
        this.totalItemsLent = totalItemsLent;
    }

    /**
     * @param permission to be checked
     * @return if the trader can add item to their list/trade items
     */


    public boolean hasPermission(Permission permission) {
        for (Permission p : permissions) {
            if (p == permission) {
                return true;
            }
        }
        return false;
    }


}
