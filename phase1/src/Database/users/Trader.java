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
     * @param name                        the trader's username
     * @param password                    the trader's password
     * @param tradeLimit                  number of trades that can be done
     * @param incompleteTradeLim          the limit for how many incomplete trades can be done
     * @param minimumAmountNeededToBorrow the minimum amount of items that must be lent before borrowing is allowed
     */
    public Trader(String name, String password, int tradeLimit, int incompleteTradeLim, int minimumAmountNeededToBorrow) {
        super(name, password);
        this.tradeLimit = tradeLimit;
        this.incompleteTradeLim = incompleteTradeLim;
        this.minimumAmountNeededToBorrow = minimumAmountNeededToBorrow;
    }

    /**
     * minimum amount needed to lend before borrowing
     *
     * @param minimumAmountNeededToBorrow minimum amount needed to lend before borrowing
     */
    public void setMinimumAmountNeededToBorrow(int minimumAmountNeededToBorrow) {
        this.minimumAmountNeededToBorrow = minimumAmountNeededToBorrow;
    }

    /**
     * If the trader can borrow
     *
     * @return if the trader can borrow
     */
    public boolean canBorrow() {
        return canTrade() && totalItemsLent - totalItemsBorrowed > minimumAmountNeededToBorrow;
    }

    /**
     * if the trader can trade
     *
     * @return if the trader can trade
     */
    public boolean canTrade() {
        return !isFrozen() && acceptedTrades.size() < tradeLimit;
    }


    /**
     * the number of incomplete trades this trader has done
     *
     * @return the number of incomplete trades this trader has done
     */
    public int getIncompleteTradeCount() {
        return acceptedTrades.size();
    }

    /**
     * total completed trade count
     *
     * @return total completed trade count
     */
    public int getTradeCount() {
        return tradeCount;
    }

    /**
     * Sets the value of this user's tradeCount
     *
     * @param tradeCount the new value of this user's tradeCount
     */
    public void setTradeCount(int tradeCount) {
        this.tradeCount = tradeCount;
    }


    /**
     * the trader's wishlist
     *
     * @return the trader's wishlist
     */

    public ArrayList<String> getWishlist() {
        return wishList;
    }

    /**
     * list of available items this trader has
     *
     * @return list of available items this trader has
     */

    public ArrayList<String> getAvailableItems() {
        return availableItems;
    }


    /**
     * list of items this trader requested to borrow/trade
     *
     * @return list of items this trader requested to borrow/trade
     */


    public ArrayList<String> getRequestedItems() {
        return requestedItems;
    }


    /**
     * list of trades accepted
     *
     * @return list of trades accepted
     */


    public ArrayList<String> getAcceptedTrades() {
        return acceptedTrades;
    }

    /**
     * list of trades that are completed (ie confirmed by both users)
     *
     * @return list of trades that are completed (ie confirmed by both users)
     */


    public ArrayList<String> getCompletedTrades() {
        return completedTrades;
    }


    /**
     * list of trades requested by this trader
     *
     * @return list of trades requested by this trader
     */


    public ArrayList<String> getRequestedTrades() {
        return requestedTrades;
    }

    /**
     * how many transactions this trader can conduct in 1 week
     *
     * @return how many transactions this trader can conduct in 1 week
     */

    public int getTradeLimit() {
        return tradeLimit;
    }

    /**
     * set a new tradeLimit to this trader
     *
     * @param tradeLimit number of transactions this trader can conduct in 1 week
     */


    public void setTradeLimit(int tradeLimit) {
        this.tradeLimit = tradeLimit;
    }

    /**
     * how many transactions can be incomplete before this trader's account is frozen
     *
     * @return how many transactions can be incomplete before this trader's account is frozen
     */
    public int getIncompleteTradeLim() {
        return incompleteTradeLim;
    }

    /**
     * set a new incomplete trade limit value to this trader
     *
     * @param incompleteTradeLim how many transactions can be incomplete before this trader's account is frozen
     */
    public void setIncompleteTradeLim(int incompleteTradeLim) {
        this.incompleteTradeLim = incompleteTradeLim;
    }


    /**
     * total number of items borrowed by the trader
     *
     * @return total number of items borrowed by the trader
     */
    public int getTotalItemsBorrowed() {
        return totalItemsBorrowed;
    }

    /**
     * total number of items borrowed by the trader
     *
     * @param totalItemsBorrowed total number of items borrowed by the trader
     */
    public void setTotalItemsBorrowed(int totalItemsBorrowed) {
        this.totalItemsBorrowed = totalItemsBorrowed;
    }

    /**
     * total number of items lent by the trader
     *
     * @return total number of items lent by the trader
     */


    public int getTotalItemsLent() {
        return totalItemsLent;
    }

    /**
     * set a new value to total number of items lent by this trader
     *
     * @param totalItemsLent total number of items lent by the trader
     */
    public void setTotalItemsLent(int totalItemsLent) {
        this.totalItemsLent = totalItemsLent;
    }

}
