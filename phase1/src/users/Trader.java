package users;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * represents a trader(user who can trade)
 */

public class Trader extends User implements Serializable {
    //all of the arraylist below store IDs
    private final ArrayList<String> wishList = new ArrayList<>();  //items that this trader wants.
    private final ArrayList<String> availableItems = new ArrayList<>(); //items that the trader is willing to trade,lend etc
    private final ArrayList<String> requestedItems = new ArrayList<>(); //items that the this trader wishes to be added to availableItems list
    private final ArrayList<String> acceptedTrades = new ArrayList<>(); //trades that this user has accepted
    private final ArrayList<String> requestedTrades = new ArrayList<>(); //trades that a trader has invited this trader to do
    private final ArrayList<String> completedTrades = new ArrayList<>(); //trades that has happened IRL confirmed by both users
    private final ArrayList<String> incompleteTrades = new ArrayList<>(); //trades that are incomplete
    private int tradeLimit;
    private int incompleteTradeCount;
    private int incompleteTradeLim;
    private int totalItemsBorrowed;
    private int totalItemsLent;
    private int tradeCount;

    /**
     * Constructs a trader with its own username and password.
     *
     * @param name     the trader's username
     * @param password the trader's password
     * @param tradeLimit number of trades that can be done
     */

    public Trader(String name, String password, int tradeLimit) {
        super(name, password);
        this.tradeLimit = tradeLimit;
    }

    /**
     * @return the number of incomplete trades this trader has done
     */
    public int getIncompleteTradeCount(){
        return incompleteTradeCount;
    }

    /**
     * set a new incompleteTradeCount to this trader
     * @param incompleteTradeCount the new value of incompleteTradeCount
     */
    public void setIncompleteTradeCount(int incompleteTradeCount){
        this.incompleteTradeCount = incompleteTradeCount;
    }

    /**
     * @return total completed trade count
     */
    public int getTradeCount(){
        return tradeCount;
    }

    /**
     * Sets the value of this user's tradeCount
     * @param tradeCount the new value of this user's tradeCount
     */
    public void setTradeCount(int tradeCount){
        this.tradeCount = tradeCount;
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
     * @return list of trades that are completed (ie confirmed by both users)
     */


    ArrayList<String> getCompletedTrades() {
        return completedTrades;
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
     * set a new tradeLimit to this trader
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
     * set a new incomplete trade limit value to this trader
     * @param incompleteTradeLim how many transactions can be incomplete before this trader's account is frozen
     */
    void setIncompleteTradeLim(int incompleteTradeLim) {
        this.incompleteTradeLim = incompleteTradeLim;
    }


    /**
     *
     * @return an arraylist of Trade IDs that represent the traders unsuccessful trade items
     */
    ArrayList<String> getIncompleteTrades(){return incompleteTrades;}

    /**
     * @return total number of items borrowed by the trader
     */
    int getTotalItemsBorrowed() {
        return totalItemsBorrowed;
    }

    /**
     * @param totalItemsBorrowed total number of items borrowed by the trader
     */


    void setTotalItemsBorrowed(int totalItemsBorrowed) {
        this.totalItemsBorrowed = totalItemsBorrowed;
    }

    /**
     * @return total number of items lent by the trader
     */


    int getTotalItemsLent() {
        return totalItemsLent;
    }

    /**
     * set a new value to total number of items lent by this trader
     * @param totalItemsLent total number of items lent by the trader
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
