package backend.tradesystem.trader_managers;


import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.Review;
import backend.models.TradableItem;
import backend.models.users.Trader;
import backend.tradesystem.Manager;

import java.io.IOException;
import java.util.List;

/**
 * Used for the actions of a Trader
 */
public class TraderManager extends Manager {

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public TraderManager() throws IOException {
        super();
    }

    /**
     * Making the database objects with set file paths
     *
     * @param userFilePath         the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath        the trade database file path
     * @throws IOException issues with getting the file path
     */
    public TraderManager(String userFilePath, String tradableItemFilePath, String tradeFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath);
    }


    /**
     * Makes this user request an item
     *
     * @param id   trader id
     * @param name name of the item
     * @param desc description of the item
     * @return the id
     * @throws UserNotFoundException  if the user was not found
     * @throws AuthorizationException not allowed to request an item
     */
    public String addRequestItem(String id, String name, String desc) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(id);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        TradableItem item = new TradableItem(name, desc);
        trader.getRequestedItems().add(item.getId());
        updateTradableItemDatabase(item);
        updateUserDatabase(trader);
        return id;
    }




    /**
     * Adds an item to this trader's wishlist
     *
     * @param traderId the trader id
     * @param itemId   the tradable item id to be added
     * @return the traderId
     * @throws UserNotFoundException         if the trader with the given userId is not found
     * @throws AuthorizationException        not allowed to add to the wishlist
     * @throws TradableItemNotFoundException the item wasn't found
     */
    public String addToWishList(String traderId, String itemId) throws UserNotFoundException, AuthorizationException,
            TradableItemNotFoundException {
        Trader trader = getTrader(traderId);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        if (!getTradableItemDatabase().contains(itemId)) throw new TradableItemNotFoundException(itemId);
        if (!trader.getWishlist().contains(itemId)) {
            trader.getWishlist().add(itemId);
            updateUserDatabase(trader);
        }
        return traderId;
    }

    /**
     * Remove item from trader's wishlist
     *
     * @param traderId the trader
     * @param itemId   the item being removed
     * @return the trader id
     * @throws UserNotFoundException  if the trader isn't found
     * @throws AuthorizationException frozen account or if the user can't do this action
     */
    public String removeFromWishList(String traderId, String itemId) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(traderId);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        trader.getWishlist().remove(itemId);
        updateUserDatabase(trader);
        return traderId;
    }

    /**
     * Remove an item from the trader's inventory
     *
     * @param traderId the trader
     * @param itemId   the item being removed
     * @return the trader id
     * @throws UserNotFoundException  if the trader isn't found
     * @throws AuthorizationException frozen account or if the user can't do this action
     */
    public String removeFromInventory(String traderId, String itemId) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(traderId);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        trader.getAvailableItems().remove(itemId);
        updateUserDatabase(trader);
        return traderId;
    }

    /**
     * Set idle status, an idle trader has some limitations such as being unable to trade
     *
     * @param traderId the trader
     * @param status   whether the trader is idle
     * @return the trader id
     * @throws UserNotFoundException  if the trader isn't found
     * @throws AuthorizationException if unable to go idle
     */
    public String setIdle(String traderId, boolean status) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(traderId);
        if (status && trader.getAcceptedTrades().size() > 0)
            throw new AuthorizationException("Cannot go idle until ongoing trades have been resolved");
        trader.setIdle(status);
        updateUserDatabase(trader);
        return traderId;
    }

    /**
     * Sets the city of the trader
     *
     * @param traderId the trader
     * @param city     the city
     * @return the trader id
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public String setCity(String traderId, String city) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(traderId);
        trader.setCity(city);
        updateUserDatabase(trader);
        return traderId;
    }

    /**
     * Adds a new review
     *
     * @param fromUser the user who sent the review
     * @param toUser   the user who received the review
     * @param rating   rating must be between 0 to 10, if greater or less than those bounds then it will assume those bounds
     * @param message  the message of the review
     * @return the new review id
     * @throws UserNotFoundException  if the user ids don't exist
     * @throws AuthorizationException if the users aren't traders
     */
    public String addReview(String fromUser, String toUser, double rating, String message) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(toUser);
        getTrader(fromUser); // Makes sure this trader exists
        if (rating < 0) rating = 0;
        else if (rating > 10) rating = 10;
        Review review = new Review(fromUser, toUser, rating, message);
        trader.addReview(review);
        updateUserDatabase(trader);
        return review.getId();
    }

    /**
     * Remove a review
     *
     * @param userThatHasReview the user that needs to have a review removed
     * @param reviewId          the review being removed
     * @throws UserNotFoundException  if the user isn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void removeReview(String userThatHasReview, String reviewId) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(userThatHasReview);
        List<Review> reviews = trader.getReviews();
        for (Review review : reviews) {
            if (review.getId().equals(reviewId)) {
                reviews.remove(review);
                updateUserDatabase(trader);
                return;
            }
        }
    }
}
