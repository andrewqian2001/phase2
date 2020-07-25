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
     * Adds a new trade to the system and acknowledges that it is a requested trade between two users in the trade.
     * A trade is a borrow if the the item the first user is offering is "".
     * A trade is a lend if the item the second user is offering is "".
     * @param trade the trade that is to be added to the traders.
     * @return the trade object
     * @throws UserNotFoundException         the user that wants to be traded with doesn't exist
     * @throws AuthorizationException        the item for trading cannot be traded
     * @throws CannotTradeException          cannot request a trade
     */
    public Trade requestTrade(Trade trade)
            throws  UserNotFoundException, AuthorizationException, CannotTradeException {
        String traderId1 = trade.getFirstUserId();
        String traderId2 = trade.getSecondUserId();
        String firstUserOfferId = trade.getFirstUserOffer();
        String secondUserOfferId = trade.getSecondUserOffer();
        Date meetingTime = trade.getMeetingTime();
        Date secondMeetingTime = trade.getSecondMeetingTime();
        Trader trader = getTrader(traderId1);
        Trader secondTrader = getTrader(traderId2);

        if (traderId2.equals(traderId1)) throw new CannotTradeException("Cannot trade with yourself");

        // If neither trader can trade, throw an exception
        if (!trader.canTrade())
            throw new CannotTradeException("This user cannot trade due to trading restrictions");
        if (!secondTrader.canTrade())
            throw new CannotTradeException("The user requested cannot trade due to trading restrictions");
        if (firstUserOfferId.equals("") && !trader.canBorrow())
            throw new CannotTradeException("You have not lent enough to borrow");

        // This is used to check if the items are in each user's inventory
        if ((!firstUserOfferId.equals("") && !trader.getAvailableItems().contains(firstUserOfferId)) ||
                (!secondUserOfferId.equals("") && !secondTrader.getAvailableItems().contains(secondUserOfferId)))
            throw new AuthorizationException("The trade offer contains an item that the user does not have");

        // Check whether the two dates are valid.
        if (!datesAreValid(meetingTime, secondMeetingTime)){
            throw new CannotTradeException("The suggested date(s) are not possible");
        }

        // Check whether the trader has too many incomplete trades pending
        if (trader.getIncompleteTradeCount() >= trader.getIncompleteTradeLim() ||
                secondTrader.getIncompleteTradeCount() >= secondTrader.getIncompleteTradeLim())
            throw new CannotTradeException("One of the two users has too many active trades.");

        // This trade has now been requested, so add it to the requested trades of each trader
        trader.getRequestedTrades().add(trade.getId());
        secondTrader.getRequestedTrades().add(trade.getId());

        updateUserDatabase(trader);
        updateTradeDatabase(trade);
        updateUserDatabase(secondTrader);
        return trade;
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

        // Check that this trader has the ability to accept this trade
        if (!trader.canTrade() || !trader2.canTrade())
            throw new CannotTradeException("Trade limitations prevent this trade from being accepted.");

        // Check to see that the items are available to trade
        if (!hasItem(trader, trade.getFirstUserOffer()) || !hasItem(trader2, trade.getSecondUserOffer())){
            throw new CannotTradeException("One of the traders no longer has the required item for the trade");
        }

        if (trade.getFirstUserId().equals(traderId))
            trade.setHasFirstUserConfirmedRequest(true);
        else
            trade.setHasSecondUserConfirmedRequest(true);

        updateUserDatabase(trader);
        updateUserDatabase(trader2);

        // If both users accepted then move items out of the inventory
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
     * Confirms the first meeting
     *
     * @param traderId the trader confirming the meeting
     * @param tradeId  id of the trade
     * @param status   if the meeting is confirmed and the trade happened
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException this trade doesn't belong to this user
     * @throws UserNotFoundException  if the user doesn't exist
     */
    private void confirmFirstMeeting(String traderId, String tradeId, boolean status) throws TradeNotFoundException, AuthorizationException, UserNotFoundException {
        Trade trade = getTrade(tradeId);
        if (!trade.isTraderInTrade(traderId)) throw new AuthorizationException("This trader doesn't belong to this trade");
        if (trade.getFirstUserId().equals(traderId)) trade.setFirstUserConfirmed1(status);
        else if ((trade.getSecondUserId().equals(traderId))) trade.setSecondUserConfirmed1(status);

        // If both users confirmed the first meeting meeting...
        if (trade.isFirstUserConfirmed1() && trade.isSecondUserConfirmed1()) {
            Trader trader1 = getTrader(trade.getFirstUserId());
            Trader trader2 = getTrader(trade.getSecondUserId());

            // Add the necessary items to each traders inventory
            if (!trade.getSecondUserOffer().equals(""))
                trader1.getAvailableItems().add(trade.getSecondUserOffer());
            if (!trade.getFirstUserOffer().equals(""))
                trader2.getAvailableItems().add(trade.getFirstUserOffer());

            trader1.getWishlist().remove(trade.getSecondUserOffer());
            trader2.getWishlist().remove(trade.getFirstUserOffer());

            // If the trade happened to be permanent...
            if (trade.getSecondMeetingTime() == null) {
                trader1.getAcceptedTrades().remove(tradeId);
                trader2.getAcceptedTrades().remove(tradeId);
                trader1.getCompletedTrades().add(tradeId);
                trader2.getCompletedTrades().add(tradeId);
                trader1.setTradeCount(trader1.getTradeCount() + 1);
                trader2.setTradeCount(trader2.getTradeCount() + 1);
                if (trade.getFirstUserOffer().equals(""))
                    trader1.setTotalItemsBorrowed(trader1.getTotalItemsBorrowed()+1);
                if (trade.getSecondUserOffer().equals(""))
                    trader1.setTotalItemsLent(trader1.getTotalItemsLent() + 1);
            }

            updateUserDatabase(trader1);
            updateUserDatabase(trader2);
        }
        updateTradeDatabase(trade);
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
    private void confirmSecondMeeting(String traderId, String tradeId, boolean status) throws TradeNotFoundException, AuthorizationException, UserNotFoundException, CannotTradeException {
        Trade trade = getTrade(tradeId);
        Trader trader1 = getTrader(trade.getFirstUserId());
        Trader trader2 = getTrader(trade.getSecondUserId());
        if (trade.getSecondMeetingTime() == null) return;

        if (!trade.isTraderInTrade(traderId)) throw new AuthorizationException("This trader doesn't belong to this trade");
        if (!trade.isFirstUserConfirmed1() || !trade.isSecondUserConfirmed1()) {
            throw new AuthorizationException("First meeting hasn't been confirmed");
        }
        if (!hasItem(trader1, trade.getSecondUserOffer()) || !hasItem(trader2, trade.getFirstUserOffer()))
            throw new CannotTradeException("One of the two users does not have the required items to trade.");
        if (trade.getFirstUserId().equals(traderId)) {
            trade.setFirstUserConfirmed2(status);
        } else if ((trade.getSecondUserId().equals(traderId))) {
            trade.setSecondUserConfirmed2(status);
        }

        // If the second meeting has been confirmed...
        if (trade.isFirstUserConfirmed1() && trade.isSecondUserConfirmed1() &&
                trade.isFirstUserConfirmed2() && trade.isSecondUserConfirmed2()) {

            trader1.getCompletedTrades().add(tradeId);
            trader2.getCompletedTrades().add(tradeId);
            trader1.getAcceptedTrades().remove(tradeId);
            trader2.getAcceptedTrades().remove(tradeId);

            // Update available items of first user / borrowed count
            if (!trade.getFirstUserOffer().equals(""))
                trader1.getAvailableItems().add(trade.getFirstUserOffer());
            else
                trader1.setTotalItemsBorrowed(trader1.getTotalItemsBorrowed()+1);

            // Update available items of the second trader / lent item count
            if(!trade.getSecondUserOffer().equals(""))
                trader2.getAvailableItems().add(trade.getSecondUserOffer());
            else
                trader1.setTotalItemsLent(trader1.getTotalItemsLent()+1);

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
     * Confirms either the first or second meeting depending on which one is not confirmed.
     * This method makes sure that the user can only confirm a second meeting once both traders
     * have confirmed the first.
     * @param traderId the trader confirming the meeting
     * @param tradeId  id of the trade
     * @param status   if the meeting is confirmed and the trade happened
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException this trade doesn't belong to this user
     * @throws UserNotFoundException  if the other user of the trade is not found
     */
    public void confirmMeetingGeneral(String traderId, String tradeId, boolean status) throws TradeNotFoundException, AuthorizationException, UserNotFoundException, CannotTradeException {
        Trade t = getTrade(tradeId);
        if (t.isFirstUserConfirmed1() && t.isSecondUserConfirmed1()) {
            confirmSecondMeeting(traderId, tradeId, status);
        }
        else{
            confirmFirstMeeting(traderId, tradeId, status);
        }
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
            trade.setHasFirstUserConfirmedRequest(true);
            trade.setHasSecondUserConfirmedRequest(false);
        }
        else {
            trade.setSecondUserOffer(thisTraderOffer);
            trade.setFirstUserOffer(thatTraderOffer);
            trade.setHasFirstUserConfirmedRequest(false);
            trade.setHasSecondUserConfirmedRequest(true);
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
        if (!trade.getFirstUserOffer().equals(""))
            firstTrader.getAvailableItems().add(trade.getFirstUserOffer());
        if (!trade.getSecondUserOffer().equals(""))
            secondTrader.getAvailableItems().add(trade.getSecondUserOffer());

        // Update database
        getTradeDatabase().delete(trade.getId());
        updateUserDatabase(firstTrader);
        updateUserDatabase(secondTrader);

    }

    /**
     * Checks whether two given dates are valid
     * @param d1 the first date time
     * @param d2 the second date time
     * @return true if the two dates are valid dates
     */
    private boolean datesAreValid(Date d1, Date d2){
        return System.currentTimeMillis() <= d1.getTime() && (d2 == null || d1.getTime() < d2.getTime());
    }


    /**
     * Return whether this trader has this item, or if the item is just "" (meaning no item).
     * @param trader the
     * @param item
     * @return
     */
    private boolean hasItem(Trader trader, String item){
        return (item.equals("") || trader.getAvailableItems().contains(item));
    }

}
