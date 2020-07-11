package Database.users;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * represents a trader(user who can trade)
 */

public class Trader extends User implements Serializable {
    private final ArrayList<String> wishList = new ArrayList<>();  //items that this trader wants.
    private final ArrayList<String> availableItems = new ArrayList<>(); // items that the trader is willing to trade,lend etc
    private final ArrayList<String> requestedItems = new ArrayList<>(); // items that the this trader wishes to be added to availableItems list
    private final ArrayList<String> acceptedTrades = new ArrayList<>(); // trades that are ongoing
    private final ArrayList<String> requestedTrades = new ArrayList<>(); // trades yet to be accepted or denied
    private final ArrayList<String> completedTrades = new ArrayList<>(); // trades where meetings are finished and confirmed by both sides and transaction has concluded
    private int tradeLimit;
    private int incompleteTradeLim;
    private int totalItemsBorrowed;
    private int totalItemsLent;
    private int minimumAmountNeededToBorrow;
    private int tradeCount;

    /**
     * Constructs a trader with its own username and password.
     *
     * @param name     the trader's username
     * @param password the trader's password
     * @param tradeLimit number of trades that can be done
     */

    public Trader(String name, String password, int tradeLimit, int incompleteTradeLim, int minimumAmountNeededToBorrow) {
        super(name, password);
        this.tradeLimit = tradeLimit;
        this.incompleteTradeLim = incompleteTradeLim;
        this.minimumAmountNeededToBorrow = minimumAmountNeededToBorrow;
    }


    public void setMinimumAmountNeededToBorrow(int minimumAmountNeededToBorrow) {
        this.minimumAmountNeededToBorrow = minimumAmountNeededToBorrow;
    }

    public boolean canBorrow(){
        return canTrade() && totalItemsLent - totalItemsBorrowed > minimumAmountNeededToBorrow;
    }

    public boolean canTrade(){
        return !isFrozen() && acceptedTrades.size() < tradeLimit;
    }


    /**
     * @return the number of incomplete trades this trader has done
     */
    public int getIncompleteTradeCount(){
        return acceptedTrades.size();
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
     * @return list of items this trader requested to borrow/trade
     */


    public ArrayList<String> getRequestedItems() {
        return requestedItems;
    }


    /**
     * @return list of Database.trades accepted
     */


    public ArrayList<String> getAcceptedTrades() {
        return acceptedTrades;
    }
    /**
     * @return list of Database.trades that are completed (ie confirmed by both Database.users)
     */


    public ArrayList<String> getCompletedTrades() {
        return completedTrades;
    }


    /**
     * @return list of Database.trades requested by this trader
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
     * set a new tradeLimit to this trader
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
     * set a new incomplete trade limit value to this trader
     * @param incompleteTradeLim how many transactions can be incomplete before this trader's account is frozen
     */
    public void setIncompleteTradeLim(int incompleteTradeLim) {
        this.incompleteTradeLim = incompleteTradeLim;
    }



    /**
     * @return total number of items borrowed by the trader
     */
    public int getTotalItemsBorrowed() {
        return totalItemsBorrowed;
    }

    /**
     * @param totalItemsBorrowed total number of items borrowed by the trader
     */


    public void setTotalItemsBorrowed(int totalItemsBorrowed) {
        this.totalItemsBorrowed = totalItemsBorrowed;
    }

    /**
     * @return total number of items lent by the trader
     */


    public int getTotalItemsLent() {
        return totalItemsLent;
    }

    /**
     * set a new value to total number of items lent by this trader
     * @param totalItemsLent total number of items lent by the trader
     */


    public void setTotalItemsLent(int totalItemsLent) {
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
