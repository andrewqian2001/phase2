package backend.tradesystem.managers;


import backend.exceptions.*;
import backend.models.Trade;
import backend.models.users.Trader;
import java.io.IOException;
import java.util.*;

/**
 * Used for trading
 */
public class TradingManager extends Manager {

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public TradingManager() throws IOException {
        super();
    }
    /**
     * Making the database objects with set file paths
     * @param userFilePath the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath the trade database file path
     * @throws IOException issues with getting the file path
     */
    public TradingManager(String userFilePath, String tradableItemFilePath, String tradeFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath);
    }

    /**
     * Creates a new temporary trade
     *
     * @param traderId          the trader confirming the meeting
     * @param userId            the id of the user being traded with
     * @param meetingTime       when the meeting takes place
     * @param secondMeetingTime when the second meeting takes place (this time should be null for a permanent trade)
     * @param meetingLocation   where the meeting takes place
     * @param thisUserOfferId   the item id of that this current user is willing to offer
     * @param secondUserOfferId the item id that the user who got sent the trade is willing to offer
     * @param allowedEdits      number of edits allowed before the trade is cancelled
     * @param message message sent with the trade offer
     * @return the trade object
     * @throws UserNotFoundException         the user that wants to be traded with doesn't exist
     * @throws AuthorizationException        the item for trading cannot be traded
     * @throws CannotTradeException          cannot request a trade
     */
    public Trade requestTrade(String traderId, String userId,
                              Date meetingTime, Date secondMeetingTime,
                              String meetingLocation, String thisUserOfferId, String secondUserOfferId, int allowedEdits, String message)
            throws  UserNotFoundException, AuthorizationException, CannotTradeException {
        Trader trader = getTrader(traderId);
        Trader secondTrader = getTrader(userId);
        if (userId.equals(traderId)) throw new CannotTradeException("Cannot trade with yourself");

        if (!trader.canTrade()) throw new CannotTradeException("This user cannot trade due to trading restrictions");
        if (!secondTrader.canTrade())
            throw new CannotTradeException("The user requested cannot trade due to trading restrictions");

        // This is used to check if the items are valid to trade
        if (!trader.getAvailableItems().contains(thisUserOfferId) ||
                !secondTrader.getAvailableItems().contains(secondUserOfferId))
            throw new AuthorizationException("The trade offer contains an item that the user does not have");

        Trade trade = new Trade(traderId, userId,
                meetingTime, secondMeetingTime,
                meetingLocation, thisUserOfferId, secondUserOfferId, allowedEdits, message);
        String tradeId = updateTradeDatabase(trade).getId();
        trader.getRequestedTrades().add(tradeId);
        secondTrader.getRequestedTrades().add(tradeId);
        updateUserDatabase(trader);
        updateUserDatabase(secondTrader);
        return trade;
    }

    /**
     * Requests to lend an item to someone
     *
     * @param traderId          the trader confirming the meeting
     * @param userId            the id of the user to lend to
     * @param meetingTime       when the meeting takes place
     * @param secondMeetingTime when the second meeting takes place (make this time to be the same or earlier
     *                          than the first meeting time for a permanent trade)
     * @param meetingLocation   where the meeting takes place
     * @param thisUserOfferId   the item id of that this current user is willing to offer
     * @param allowedEdits      number of edits allowed before the trade is cancelled
     * @param message message along with the lend offer
     * @return the trade object
     * @throws UserNotFoundException         the user that wants to be traded with doesn't exist
     * @throws AuthorizationException        no authority to lend
     * @throws CannotTradeException          cannot request a trade
     */
    public Trade requestLend(String traderId, String userId,
                             Date meetingTime, Date secondMeetingTime,
                             String meetingLocation, String thisUserOfferId, int allowedEdits, String message)
            throws UserNotFoundException, AuthorizationException, CannotTradeException {
        Trader trader = getTrader(traderId);//lender
        Trader secondTrader = getTrader(userId);//borrower
        if (!trader.canTrade()) throw new CannotTradeException("You cannot trade at the moment.");
        if (!secondTrader.canTrade())
            throw new CannotTradeException("The other trader cannot trade at the moment");
        if (userId.equals(traderId)) throw new CannotTradeException("Cannot lend to yourself");
        if (trader.getIncompleteTradeCount() >= trader.getIncompleteTradeLim() ||
                secondTrader.getIncompleteTradeCount() >= secondTrader.getIncompleteTradeLim())
            throw new CannotTradeException("Too many active trades.");

        // This is used to check if the items are valid to trade
        if (!trader.getAvailableItems().contains(thisUserOfferId))
            throw new AuthorizationException("The trade offer contains an item that the user does not have");

        Trade trade = new Trade(traderId, userId,
                meetingTime, secondMeetingTime,
                meetingLocation, thisUserOfferId, "", allowedEdits, message);

        String tradeId = updateTradeDatabase(trade).getId();
        trader.getRequestedTrades().add(tradeId);
        secondTrader.getRequestedTrades().add(tradeId);
        updateUserDatabase(trader);
        updateUserDatabase(secondTrader);
        return trade;
    }

    /**
     * Requests to borrow an item from someone
     *
     * @param traderId          the trader confirming the meeting
     * @param userId            the id of the user to borrow from
     * @param meetingTime       when the meeting takes place
     * @param secondMeetingTime when the second meeting takes place (make this time to be the same or earlier
     *                          than the first meeting time for a permanent trade)
     * @param meetingLocation   where the meeting takes place
     * @param thatUserOfferId   the item id to borrow
     * @param allowedEdits      number of edits allowed before the trade is cancelled
     * @param message message along with the borrow request
     * @return the trade object
     * @throws UserNotFoundException         the user that wants to be traded with doesn't exist
     * @throws AuthorizationException        the item for trading cannot be traded
     * @throws CannotTradeException          cannot request a trade
     */
    public Trade requestBorrow(String traderId, String userId,
                               Date meetingTime, Date secondMeetingTime,
                               String meetingLocation, String thatUserOfferId, int allowedEdits, String message) throws
            UserNotFoundException, AuthorizationException, CannotTradeException {
        Trader trader = getTrader(traderId);
        if (!trader.canBorrow()) throw new CannotTradeException("Too many borrows");
        return requestLend(traderId, userId, meetingTime, secondMeetingTime, meetingLocation, thatUserOfferId, allowedEdits, message);
    }

    /**
     * For denying a requested trade
     *
     * @param tradeId trade id
     * @throws TradeNotFoundException if the trade id wasn't found
     * @throws AuthorizationException if the trade doesn't belong to this user
     * @throws UserNotFoundException if the traders in the trade weren't found
     */
    public void denyTrade(String tradeId) throws TradeNotFoundException, AuthorizationException, UserNotFoundException {
        Trade trade = getTrade(tradeId);
        Trader trader1 = getTrader(trade.getFirstUserId());
        Trader trader2 = getTrader(trade.getSecondUserId());
        trader1.getRequestedTrades().remove(tradeId);
        trader2.getRequestedItems().remove(tradeId);
        getTradeDatabase().delete(tradeId);
    }

    /**
     * Confirms the first meeting
     *
     * @param traderId the trader confirming the meeting
     * @param tradeId  id of the trade
     * @param status   if the meeting is confirmed and the trade happened
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException this trade doesn't belong to this user
     * @throws UserNotFoundException  if the user doesn't exist
     */
    public void confirmFirstMeeting(String traderId, String tradeId, boolean status) throws TradeNotFoundException, AuthorizationException, UserNotFoundException {
        Trade trade = getTrade(tradeId);
        if (!trade.isTraderInTrade(traderId)) throw new AuthorizationException("This trader doesn't belong to this trade");
        if (trade.getFirstUserId().equals(traderId)) trade.setFirstUserConfirmed1(status);
        else if ((trade.getSecondUserId().equals(traderId))) trade.setSecondUserConfirmed1(status);
        if (trade.isFirstUserConfirmed1() && trade.isSecondUserConfirmed1()) {
            Trader trader1 = getTrader(trade.getFirstUserId());
            Trader trader2 = getTrader(trade.getSecondUserId());
            if (!trade.getSecondUserOffer().equals(""))
                trader1.getAvailableItems().add(trade.getSecondUserOffer());
            if (!trade.getFirstUserOffer().equals(""))
                trader2.getAvailableItems().add(trade.getFirstUserOffer());
            trader1.getWishlist().remove(trade.getSecondUserOffer());
            trader2.getWishlist().remove(trade.getFirstUserOffer());
            if (trade.getSecondMeetingTime() == null) {
                trader1.getAcceptedTrades().remove(tradeId);
                trader2.getAcceptedTrades().remove(tradeId);
                trader1.getCompletedTrades().add(tradeId);
                trader2.getCompletedTrades().add(tradeId);
                trader1.setTradeCount(trader1.getTradeCount() + 1);
                trader2.setTradeCount(trader2.getTradeCount() + 1);
            }
            updateUserDatabase(trader1);
            updateUserDatabase(trader2);
        }
        updateTradeDatabase(trade);
    }


    /**
     * For accepting a trade request
     *
     * @param traderId the trader confirming the meeting
     * @param tradeId  the trade id
     * @return if the request was confirmed
     * @throws TradeNotFoundException if the trade wasn't found
     * @throws AuthorizationException if the user is not a trader
     * @throws UserNotFoundException  if the user doesn't exist
     * @throws CannotTradeException   if trading limitations prevent the trade from happening
     */
    public boolean acceptRequest(String traderId, String tradeId) throws TradeNotFoundException, AuthorizationException, UserNotFoundException, CannotTradeException {
        Trade trade = getTrade(tradeId);
        if (!trade.isTraderInTrade(traderId)) throw new AuthorizationException("This trader doesn't belong to this trade");
        Trader trader = getTrader(trade.getFirstUserId());
        Trader trader2 = getTrader(trade.getSecondUserId());

        if (!trader.canTrade() || !trader2.canTrade())
            throw new CannotTradeException("Trade limitations prevent this trade from being accepted.");

        if ((!trader.getAvailableItems().contains(trade.getFirstUserOffer()) && !trade.getFirstUserOffer().equals("") )||
                (!trader2.getAvailableItems().contains(trade.getSecondUserOffer()) && !trade.getSecondUserOffer().equals(""))){
            throw new CannotTradeException("One of the traders no longer has the required item for the trade");
        }

        // if first user is borrowing
        if (trade.getFirstUserOffer().equals("")){
            if (trader.canBorrow())
                trader.setTotalItemsBorrowed(trader.getTotalItemsBorrowed() + 1);
            else {
                trade.setHasFirstUserConfirmedRequest(false);
                throw new CannotTradeException("Cannot lend/borrow due to one of the traders having trading limitations");
            }
        }
        // if second user is borrowing
        if (trade.getFirstUserOffer().equals("")){
            if (trader2.canBorrow())
                trader2.setTotalItemsBorrowed(trader.getTotalItemsBorrowed() + 1);
            else {
                trade.setHasSecondUserConfirmedRequest(false);
                throw new CannotTradeException("Cannot lend/borrow due to one of the traders having trading limitations");
            }
        }
        if (trade.getFirstUserId().equals(traderId))
            trade.setHasFirstUserConfirmedRequest(true);
        else
            trade.setHasSecondUserConfirmedRequest(true);

        updateUserDatabase(trader);
        updateUserDatabase(trader2);

        // If both users accepted then move items from the inventory
        if (trade.isHasFirstUserConfirmedRequest() && trade.isHasSecondUserConfirmedRequest()) {
            trader.getAvailableItems().remove(trade.getFirstUserOffer());
            trader2.getAvailableItems().remove(trade.getSecondUserOffer());
            trader.getAcceptedTrades().add(tradeId);
            trader2.getAcceptedTrades().add(tradeId);
            trader.getRequestedTrades().remove(tradeId);
            trader2.getRequestedTrades().remove(tradeId);
            updateUserDatabase(trader);
            updateUserDatabase(trader2);
            return true;
        }
        return false;
    }



    /**
     * Confirms the second meeting
     *
     * @param traderId the trader confirming the meeting
     * @param tradeId  id of the trade
     * @param status   if the meeting is confirmed and the trade happened
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException this trade doesn't belong to this user
     * @throws UserNotFoundException  if the other user of the trade is not found
     */
    public void confirmSecondMeeting(String traderId, String tradeId, boolean status) throws TradeNotFoundException, AuthorizationException, UserNotFoundException {
        Trade trade = getTrade(tradeId);
        if (trade.getSecondMeetingTime() == null) return;

        if (!trade.isTraderInTrade(traderId)) throw new AuthorizationException("This trader doesn't belong to this trade");
        if (!trade.isFirstUserConfirmed1() || !trade.isSecondUserConfirmed1()) {
            throw new AuthorizationException("First meeting hasn't been confirmed");
        }
        if (trade.getFirstUserId().equals(traderId)) {
            trade.setFirstUserConfirmed2(status);
        } else if ((trade.getSecondUserId().equals(traderId))) {
            trade.setSecondUserConfirmed2(status);
        }
        if (trade.isFirstUserConfirmed1() && trade.isSecondUserConfirmed1() &&
                trade.isFirstUserConfirmed2() && trade.isSecondUserConfirmed2()) {
            Trader trader1 = getTrader(trade.getFirstUserId());
            Trader trader2 = getTrader(trade.getSecondUserId());
            trader1.getCompletedTrades().add(tradeId);
            trader2.getCompletedTrades().add(tradeId);
            trader1.getAcceptedTrades().remove(tradeId);
            trader1.getAcceptedTrades().remove(tradeId);
            if (!trade.getFirstUserOffer().equals(""))
                trader1.getAvailableItems().add(trade.getFirstUserOffer());
            if(!trade.getSecondUserOffer().equals(""))
                trader2.getAvailableItems().add(trade.getSecondUserOffer());
            trader1.getAvailableItems().remove(trade.getSecondUserOffer());
            trader2.getAvailableItems().remove(trade.getFirstUserOffer());
            trader1.getWishlist().remove(trade.getFirstUserOffer());
            trader2.getWishlist().remove(trade.getSecondUserOffer());
            trader1.setTradeCount(trader1.getTradeCount() + 1);
            trader2.setTradeCount(trader2.getTradeCount() + 1);
            updateUserDatabase(trader1);
            updateUserDatabase(trader2);
        }
        updateTradeDatabase(trade);
    }

    /**
     * Sending a counter offer
     *
     * @param traderId          the trader id
     * @param tradeId           the trade id
     * @param meetingTime       the new time of the trade
     * @param secondMeetingTime the second meeting time of the trade
     * @param meetingLocation   the meeting location of the trade
     * @param thisTraderOffer    the tradable item id that the current trader is offering
     * @param thatTraderOffer   the tradable item id that the current trader wants from the other trader
     * @return the id of the trade
     * @throws CannotTradeException   too many edits
     * @throws TradeNotFoundException this trade doesn't exist
     * @throws AuthorizationException this trade doesn't belong to this user
     * @throws UserNotFoundException user wasn't found
     */
    public Trade counterTradeOffer(String traderId, String tradeId, Date meetingTime, Date secondMeetingTime, String
            meetingLocation, String thisTraderOffer, String thatTraderOffer) throws
            CannotTradeException, TradeNotFoundException, AuthorizationException, UserNotFoundException {
        Trade trade = getTrade(tradeId);
        Trader trader1 = getTrader(trade.getFirstUserId());
        Trader trader2 = getTrader(trade.getSecondUserId());

        if (!trade.isTraderInTrade(traderId)) throw new AuthorizationException("This trader doesn't belong to this trade");
        if (!getTrader(trade.getFirstUserId()).canTrade() || !getTrader(trade.getSecondUserId()).canTrade())
            throw new CannotTradeException("Could not send a counter trade offer, one of the two traders cannot trade");
        if (trade.getNumEdits() >= trade.getMaxAllowedEdits()) {
            throw new CannotTradeException("Too many edits. Trade is cancelled.");
        }
        // if the trader sending the request is the first user...
        if (trader1.getId().equals(traderId)){
            if (!trader1.getAvailableItems().contains(thisTraderOffer) || !trader2.getAvailableItems().contains(thatTraderOffer)){
                throw new CannotTradeException("One of the traders does not have the required item!");
            }
        }
        else{
            if (!trader1.getAvailableItems().contains(thatTraderOffer) || !trader2.getAvailableItems().contains(thisTraderOffer)){
                throw new CannotTradeException("One of the traders does not have the required item!");
            }
        }
        if (trade.getUserTurnToEdit().equals(traderId)) trade.changeUserTurn();
        else throw new CannotTradeException("A previous trade offer has already been sent");
        trade.setMeetingTime(meetingTime);
        trade.setSecondMeetingTime(secondMeetingTime);
        trade.setMeetingLocation(meetingLocation);
        if (trade.getFirstUserId().equals(traderId)) {
            trade.setFirstUserOffer(thisTraderOffer);
            trade.setSecondUserOffer(thatTraderOffer);
        }
        else {
            trade.setSecondUserOffer(thisTraderOffer);
            trade.setFirstUserOffer(thatTraderOffer);
        }

        trade.setNumEdits(trade.getNumEdits() + 1);
        updateTradeDatabase(trade);
        return trade;
    }


    /**
     * Cancels a trade request
     * @param tradeID The id of the trade request
     * @throws TradeNotFoundException trade doesn't exist in the user's requested trades
     * @throws UserNotFoundException the trader(s) can not be found
     * @throws AuthorizationException couldn't find a trader type associated with the trade
     */
    public void rescindTradeRequest(String tradeID) throws TradeNotFoundException, UserNotFoundException, AuthorizationException {
        Trade trade = getTrade(tradeID);
        Trader firstTrader = getTrader(trade.getFirstUserId());
        Trader secondTrader = getTrader(trade.getSecondUserId());
        if (!firstTrader.getRequestedTrades().remove(tradeID)){
            throw new TradeNotFoundException("Trade request wasn't found");
        }
        secondTrader.getRequestedTrades().remove(tradeID);
        getTradeDatabase().delete(tradeID);
        updateUserDatabase(firstTrader);
        updateUserDatabase(secondTrader);
    }

    /**
     * If the first meeting of a trade is not accepted yet, the trade can still be cancelled
     * This method only should be used carefully since rescinding an ongoing trade after the real life trade has
     * happened has dire consequences (scamming is able to happen)
     * @param tradeID the id of the trade to undo
     * @throws TradeNotFoundException trade doesn't exist in the user's accepted trades
     * @throws UserNotFoundException the trader(s) can not be found
     * @throws AuthorizationException couldn't find a trader type associated with the trade
     * @throws CannotTradeException cannot complete this request because the trade could not be reversed
     */
    public void rescindOngoingTrade(String tradeID) throws
            TradeNotFoundException, UserNotFoundException, AuthorizationException, CannotTradeException {
        Trade trade = getTrade(tradeID);
        Trader firstTrader = getTrader(trade.getFirstUserId());
        Trader secondTrader = getTrader(trade.getSecondUserId());
        if (!firstTrader.getAcceptedTrades().contains(trade.getId()))
            throw new CannotTradeException("The trade is not accepted");

        // Remove trades
        firstTrader.getAcceptedTrades().remove(tradeID);
        secondTrader.getAcceptedTrades().remove(tradeID);

        // Take back items
        firstTrader.getAvailableItems().add(trade.getFirstUserOffer());
        secondTrader.getAvailableItems().add(trade.getSecondUserOffer());

        // Update database
        getTradeDatabase().delete(trade.getId());
        updateUserDatabase(firstTrader);
        updateUserDatabase(secondTrader);

    }

}
