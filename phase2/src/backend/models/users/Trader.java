package backend.models.users;


import backend.models.Review;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * represents a trader (user who can trade)
 */

public class Trader extends User implements Serializable {
    private final ArrayList<String> wishList = new ArrayList<>();  // Items that this trader wants.
    private final ArrayList<String> availableItems = new ArrayList<>(); // Items that the trader is willing to trade,lend etc
    private final ArrayList<String> requestedItems = new ArrayList<>(); // Items that the this trader wishes to be added to availableItems list
    private final ArrayList<String> acceptedTrades = new ArrayList<>(); // Trades that are ongoing
    private final ArrayList<String> requestedTrades = new ArrayList<>(); // Trades yet to be accepted or denied
    private final ArrayList<String> completedTrades = new ArrayList<>(); // Trades where meetings are finished and confirmed by both sides and transaction has concluded
    private final ArrayList<Review> reviews = new ArrayList<>(); // List of reviews
    private int tradeLimit; // This trader's trade limit (total amount of trades that can be conducted per week)
    private int incompleteTradeLim; // This trader's incomplete trade limit
    private int totalItemsBorrowed;
    private int totalItemsLent;
    private int minimumAmountNeededToBorrow; // The minimum value totalItemsLent - totalItemsBorrowed needs to be to borrow
    private int tradeCount;
    private boolean isIdle = false;
    private String city;
    private int money;


    /**
     * Constructs a trader with its own username and password.
     *
     * @param name                        the trader's username
     * @param password                    the trader's password
     * @param tradeLimit                  number of trades that can be done
     * @param incompleteTradeLim          the limit for how many incomplete trades can be done
     * @param minimumAmountNeededToBorrow the minimum amount of items that must be lent before borrowing is allowed
     * @param city                        the city of the trader
     * @param money                       the amount of maoney in the traders account
     */
    public Trader(String name, String password, String city, int tradeLimit, int incompleteTradeLim, int minimumAmountNeededToBorrow, int money) {
        super(name, password);
        this.tradeLimit = tradeLimit;
        this.incompleteTradeLim = incompleteTradeLim;
        this.minimumAmountNeededToBorrow = minimumAmountNeededToBorrow;
        this.city = city;
        this.money = money;
    }

    /**
     * Gets all reviews
     * @return all reviews
     */
    public ArrayList<Review> getReviews() {
        return reviews;
    }

    /**
     * Add a review
     * @param review the review being added
     */
    public void addReview(Review review){
        reviews.add(review);
    }

    /**
     * the city of the trader
     *
     * @return city of the trader
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city of the trader
     *
     * @param city city of the trader
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * the amount of money for the trader
     *
     * @return the amount of money the trader has
     */
    public int getMoney() { return money; }

    /**
     * Sets the amount of money for the trader
     *
     * @param money amount of money for the trader
     */
    public void setMoney(int money) { this.money = money; }

    /**
     * Makes the trader have an idle status
     *
     * @param idle whether the trader is idle
     */
    public void setIdle(boolean idle) {
        isIdle = idle;
    }

    /**
     * if the user is idle
     *
     * @return if the user is idle
     */
    public boolean isIdle() {
        return isIdle;
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
        return canTrade() && totalItemsLent - totalItemsBorrowed >= minimumAmountNeededToBorrow;
    }

    /**
     * if the trader can trade
     *
     * @return if the trader can trade
     */
    public boolean canTrade() {
        return !isFrozen() && tradeCount < tradeLimit && !isIdle();
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
