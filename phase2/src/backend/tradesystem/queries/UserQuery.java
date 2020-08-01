package backend.tradesystem.queries;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
import backend.models.Review;
import backend.tradesystem.Manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * For getting info about a specific user
 */
public class UserQuery extends Manager {
    /**
     * Create an instance of ItemQuery with preset file paths from Databse enum
     *
     * @throws IOException issues with getting the file path
     */
    public UserQuery() throws IOException {
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
    public UserQuery(String userFilePath, String tradableItemFilePath, String tradeFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath);
    }

    /**
     * a user's username
     *
     * @param userId The id of the user being checked
     * @return the user's username
     * @throws UserNotFoundException If the user could not be found in the database
     */
    public String getUsername(String userId) throws UserNotFoundException {
        return getUser(userId).getUsername();
    }

    /**
     * a user's password
     *
     * @param userId The id of the user being checked
     * @return the user's password
     * @throws UserNotFoundException If the user could not be found in the database
     */
    public String getPassword(String userId) throws UserNotFoundException {
        return getUser(userId).getPassword();
    }

    /**
     * a user's current frozen status
     *
     * @param userId The id of the user being checked
     * @return if the user is frozen
     * @throws UserNotFoundException If the user could not be found in the database
     */
    public boolean isFrozen(String userId) throws UserNotFoundException {
        return getUser(userId).isFrozen();
    }

    /**
     * if a user requested to be unfrozen
     *
     * @param userId The id of the user being checked
     * @return if the user requested to be unfrozen
     * @throws UserNotFoundException If the user could not be found in the database
     */
    public boolean isUnfrozenRequested(String userId) throws UserNotFoundException {
        return getUser(userId).isUnfrozenRequested();
    }


    /**
     * All messages that got sent to a user
     *
     * @param userId The id of the user being checked
     * @return all messages that got sent to the user
     * @throws UserNotFoundException If the user could not be found in the database
     */
    public HashMap<String, ArrayList<String>> getMessages(String userId) throws UserNotFoundException {
        return getUser(userId).getMessages();
    }

    /**
     * Gets all ongoing items of a trader
     *
     * @param traderId The id of the trader being checked
     * @return all ongoing items of this trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getOngoingItems(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getOngoingItems();
    }

    /**
     * Gets all reviews of a trader
     * It is returned in the form of [fromUserId, toUserId, message, rating, reportId] for each element in the list
     *
     * @param traderId The id of the trader being checked
     * @return all reviews of the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String[]> getReviews(String traderId) throws UserNotFoundException, AuthorizationException {
        ArrayList<String[]> reviews = new ArrayList<>();
        for (Review review : getTrader(traderId).getReviews()) {
            String[] items = {review.getFromUserId(), review.getReportOnUserId(), review.getMessage(),
                    review.getRating() + "", review.getId()};
            reviews.add(items);
        }
        return reviews;
    }


    /**
     * the city of a trader
     *
     * @param traderId The id of the trader being checked
     * @return city of the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public String getCity(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getCity();
    }

    /**
     * if a user is idle
     *
     * @param traderId The id of the trader being checked
     * @return if the user is idle
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public boolean isIdle(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).isIdle();
    }

    /**
     * the number of incomplete trades a trader has done
     *
     * @param traderId The id of the trader being checked
     * @return the number of incomplete trades the trader has done
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public int getIncompleteTradeCount(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getIncompleteTradeCount();
    }

    /**
     * total completed trade count of a trader
     *
     * @param traderId The id of the trader being checked
     * @return total completed trade count of the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public int getTradeCount(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getTradeCount();
    }


    /**
     * Gets a trader's wishlist
     *
     * @param traderId The id of the trader being checked
     * @return the trader's wishlist
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */

    public ArrayList<String> getWishlist(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getWishlist();
    }

    /**
     * list of available items a trader has
     *
     * @param traderId The id of the trader being checked
     * @return list of available items the trader has
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */

    public ArrayList<String> getAvailableItems(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getAvailableItems();
    }


    /**
     * list of items a trader requested to borrow/trade
     *
     * @param traderId The id of the trader being checked
     * @return list of items the trader requested to borrow/trade
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getRequestedItems(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getRequestedItems();
    }


    /**
     * list of trades accepted by a trader
     *
     * @param traderId The id of the trader being checked
     * @return list of trades accepted by the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getAcceptedTrades(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getAcceptedTrades();
    }

    /**
     * list of completed trades a trader has (ie confirmed by both users)
     *
     * @param traderId The id of the trader being checked
     * @return list of trades that are completed (ie confirmed by both users)
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getCompletedTrades(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getCompletedTrades();
    }


    /**
     * list of trades requested by a trader
     *
     * @param traderId The id of the trader being checked
     * @return list of trades requested by the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getRequestedTrades(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getRequestedTrades();
    }

    /**
     * how many transactions a trader can conduct in 1 week
     *
     * @param traderId The id of the trader being checked
     * @return how many transactions the trader can conduct in 1 week
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public int getTradeLimit(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getTradeLimit();
    }

    /**
     * how many transactions can be incomplete before a trader's account is frozen
     *
     * @param traderId The id of the trader being checked
     * @return how many transactions can be incomplete before the trader's account is frozen
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public int getIncompleteTradeLim(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getIncompleteTradeLim();
    }

    /**
     * total number of items borrowed by a trader
     *
     * @param traderId The id of the trader being checked
     * @return total number of items borrowed by the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public int getTotalItemsBorrowed(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getTotalItemsBorrowed();
    }

    /**
     * total number of items lent by a trader
     *
     * @param traderId The id of the trader being checked
     * @return total number of items lent by the trader
     * @throws UserNotFoundException  if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public int getTotalItemsLent(String traderId) throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getTotalItemsLent();
    }


}
