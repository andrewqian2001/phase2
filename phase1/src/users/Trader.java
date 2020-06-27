package users;

import java.io.Serializable;
import java.util.ArrayList;

public class Trader extends User implements Serializable {

    ArrayList<String> wishList = new ArrayList<>(); // Ids
    ArrayList<String> availableItems = new ArrayList<>();
    ArrayList<String> requestedItems = new ArrayList<>();
    ArrayList<String> acceptedTrades = new ArrayList<>();
    ArrayList<String> requestedTrades = new ArrayList<>();
    int tradeLimit;
    int incompleteTradeLim;
    int totalItemsBorrowed;
    int totalItemsLent;

    public Trader(String name, String password) {
        super(name, password);
    }

    public ArrayList<String> getWishlist() {
        return wishList;
    }

    public void setWishlist(ArrayList<String> wishlist) {
        this.wishList = wishlist;
    }

    public ArrayList<String> getAvailableItems() {
        return availableItems;
    }

    public void setAvailableItems(ArrayList<String> inventory) {
        this.availableItems = inventory;
    }

    public ArrayList<String> getRequestedItems() {
        return requestedItems;
    }

    public void setRequestedItems(ArrayList<String> requestedItems) {
        this.requestedItems = requestedItems;
    }

    public ArrayList<String> getAcceptedTrades() {
        return acceptedTrades;
    }

    public ArrayList<String> getRequestedTrades() {
        return requestedTrades;
    }
    public int getTradeLimit() {
        return tradeLimit;
    }

    public void setTradeLimit(int tradeLimit) {
        this.tradeLimit = tradeLimit;
    }

    public int getIncompleteTradeLim() {
        return incompleteTradeLim;
    }

    public void setIncompleteTradeLim(int incompleteTradeLim) {
        this.incompleteTradeLim = incompleteTradeLim;
    }

    public int getTotalItemsBorrowed() {
        return totalItemsBorrowed;
    }

    public void setTotalItemsBorrowed(int totalItemsBorrowed) {
        this.totalItemsBorrowed = totalItemsBorrowed;
    }

    public int getTotalItemsLent() {
        return totalItemsLent;
    }

    public void setTotalItemsLent(int totalItemsLent) {
        this.totalItemsLent = totalItemsLent;
    }


}
