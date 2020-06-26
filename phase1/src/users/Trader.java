package users;

import tradableitems.TradableItem;

import java.io.Serializable;
import java.util.ArrayList;

public class Trader extends User implements Serializable {

    ArrayList<String> wishList = new ArrayList<String>(); // I assume TradableItems ID are strings?
    ArrayList<String> inventory = new ArrayList<String>();
    ArrayList<String> unconfirmedInv = new ArrayList<String>();
    ArrayList<String> acceptedTrades = new ArrayList<String>();
    ArrayList<String> requestedTrades = new ArrayList<String>();
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

    public ArrayList<String> getInventory() {
        return inventory;
    }

    public void setInventory(ArrayList<String> inventory) {
        this.inventory = inventory;
    }

    public ArrayList<String> getUnconfirmedInv() {
        return unconfirmedInv;
    }

    public void setUnconfirmedInv(ArrayList<String> unconfirmedInv) {
        this.unconfirmedInv = unconfirmedInv;
    }

    public ArrayList<String> getAcceptedTrades() {
        return acceptedTrades;
    }

    public void setAcceptedTrades(ArrayList<String> acceptedTrades) {
        this.acceptedTrades = acceptedTrades;
    }

    public ArrayList<String> getRequestedTrades() {
        return requestedTrades;
    }

    public void setRequestedTrades(ArrayList<String> requestedTrades) {
        this.requestedTrades = requestedTrades;
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
