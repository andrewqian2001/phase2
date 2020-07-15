package backend.tradesystem.managers;


import backend.exceptions.*;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Trader;

import java.io.IOException;
import java.util.*;

/**
 * Used for trading
 */
public class TradeManager extends Manager {

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public TradeManager() throws IOException {
        super();
    }

    /**
     * Creates a new temporary trade
     *
     * @param traderId          the trader confirming the meeting
     * @param userId            the id of the user being traded with
     * @param meetingTime       when the meeting takes place
     * @param secondMeetingTime when the second meeting takes place (make this time to be the same or earlier
     *                          than the first meeting time for a permanent trade)
     * @param meetingLocation   where the meeting takes place
     * @param thisUserOfferId   the item id of that this current user is willing to offer
     * @param secondUserOfferId the item id that the user who got sent the trade is willing to offer
     * @param allowedEdits      number of edits allowed before the trade is cancelled
     * @return the trade object
     * @throws TradableItemNotFoundException the items passed in for trading doesn't exist
     * @throws UserNotFoundException         the user that wants to be traded with doesn't exist
     * @throws AuthorizationException        the item for trading cannot be traded
     * @throws CannotTradeException          cannot request a trade
     */
    public Trade requestTrade(String traderId, String userId,
                              Date meetingTime, Date secondMeetingTime,
                              String meetingLocation, String thisUserOfferId, String secondUserOfferId, int allowedEdits)
            throws TradableItemNotFoundException, UserNotFoundException, AuthorizationException, CannotTradeException {
        Trader trader = getTrader(traderId);
        Trader secondTrader = getTrader(userId);
        if (userId.equals(traderId)) throw new CannotTradeException("Cannot trade with yourself");

        if (!trader.canTrade()) throw new CannotTradeException("This user cannot trade due to trading restrictions");
        if (!secondTrader.canTrade())
            throw new CannotTradeException("The user requested cannot trade due to trading restrictions");

        // This is used to check if the items are valid to trade
        if (!trader.getAvailableItems().contains(thisUserOfferId) ||
                !trader.getAvailableItems().contains(secondUserOfferId))
            throw new AuthorizationException("The trade offer contains an item that the user does not have");

        Trade trade = new Trade(traderId, userId,
                meetingTime, secondMeetingTime,
                meetingLocation, thisUserOfferId, secondUserOfferId, allowedEdits);
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
     * @return the trade object
     * @throws TradableItemNotFoundException the items passed in for trading doesn't exist
     * @throws UserNotFoundException         the user that wants to be traded with doesn't exist
     * @throws AuthorizationException        the item for trading cannot be traded
     * @throws CannotTradeException          cannot request a trade
     */
    public Trade requestLend(String traderId, String userId,
                             Date meetingTime, Date secondMeetingTime,
                             String meetingLocation, String thisUserOfferId, int allowedEdits)
            throws TradableItemNotFoundException, UserNotFoundException, AuthorizationException, CannotTradeException {
        Trader trader = getTrader(traderId);//lender
        Trader secondTrader = getTrader(userId);//borrower
        if (!secondTrader.canTrade())
            throw new CannotTradeException("Cannot trade due to trading restrictions");
        if (userId.equals(traderId)) throw new CannotTradeException("Cannot lend to yourself");
//        if (secondTrader.getTradeLimit() < 0)
//            throw new CannotTradeException("There is a trading limit restriction");
        if (trader.getAcceptedTrades().size() >= trader.getIncompleteTradeLim() ||
                secondTrader.getAcceptedTrades().size() >= secondTrader.getIncompleteTradeLim())
            throw new CannotTradeException("Too many active trades.");

        // This is used to check if the items are valid to trade
        if (!trader.getAvailableItems().contains(thisUserOfferId))
            throw new AuthorizationException("The trade offer contains an item that the user does not have");

        Trade trade = new Trade(traderId, userId,
                meetingTime, secondMeetingTime,
                meetingLocation, thisUserOfferId, "", allowedEdits);
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
     * @return the trade object
     * @throws TradableItemNotFoundException the items passed in for trading doesn't exist
     * @throws UserNotFoundException         the user that wants to be traded with doesn't exist
     * @throws AuthorizationException        the item for trading cannot be traded
     * @throws CannotTradeException          cannot request a trade
     */
    public Trade requestBorrow(String traderId, String userId,
                               Date meetingTime, Date secondMeetingTime,
                               String meetingLocation, String thatUserOfferId, int allowedEdits) throws
            TradableItemNotFoundException, UserNotFoundException, AuthorizationException, CannotTradeException {
        return requestLend(traderId, userId, meetingTime, secondMeetingTime, meetingLocation, thatUserOfferId, allowedEdits);
    }

    /**
     * For denying a requested trade
     *
     * @param tradeId trade id
     * @throws TradeNotFoundException if the trade id wasn't found
     * @throws AuthorizationException if the trade doesn't belong to this user
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
        if (trade.getFirstUserId().equals(traderId)) trade.setFirstUserConfirmed1(status);
        else if ((trade.getSecondUserId().equals(traderId))) trade.setSecondUserConfirmed1(status);
        if (trade.isFirstUserConfirmed1() && trade.isSecondUserConfirmed1()) {
            Trader trader1 = getTrader(trade.getFirstUserId());
            Trader trader2 = getTrader(trade.getSecondUserId());
            trader1.getAvailableItems().add(trade.getSecondUserOffer());
            trader2.getAvailableItems().add(trade.getFirstUserOffer());
            trader1.getWishlist().remove(trade.getSecondUserOffer());
            trader2.getWishlist().remove(trade.getFirstUserOffer());
            if (trade.getSecondMeetingTime() != null) {
                trader1.getAcceptedTrades().remove(tradeId);
                trader2.getAcceptedTrades().remove(tradeId);
                trader1.getCompletedTrades().add(tradeId);
                trader2.getCompletedTrades().add(tradeId);
            }
            updateUserDatabase(trader1);
            updateUserDatabase(trader2);
        }
        updateTradeDatabase(trade);
    }


    /**
     * For confirming a trade request
     *
     * @param traderId the trader confirming the meeting
     * @param tradeId  the trade id
     * @return if the request was confirmed
     * @throws TradeNotFoundException if the trade wasn't found
     * @throws AuthorizationException if the user is not a trader
     * @throws UserNotFoundException  if the user doesn't exist
     * @throws CannotTradeException   if trading limitations prevent the trade from happening
     */
    public boolean confirmRequest(String traderId, String tradeId) throws TradeNotFoundException, AuthorizationException, UserNotFoundException, CannotTradeException {
        Trade trade = getTrade(tradeId);
        Trader trader = getTrader(trade.getFirstUserId());
        Trader trader2 = getTrader(trade.getSecondUserId());

        if (!trader.canTrade() || !trader2.canTrade())
            throw new CannotTradeException("Trade limitations prevent this trade from being accepted.");

        if (trade.getFirstUserId().equals(traderId))
            trade.setHasFirstUserConfirmedRequest(true);
        else trade.setHasSecondUserConfirmedRequest(true);

        // This should always be true but this is a check anyway
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
     * edits the trade object
     *
     * @param traderId                 the trader confirming the meeting
     * @param tradeID                  id of the trade
     * @param firstMeeting             first meeting date object
     * @param secondMeeting            second meeting date object
     * @param meetingLocation          String of the meeting location
     * @param inventoryItemIndex       index of the user's trade item
     * @param traderInventoryItemIndex index of the trader's trade item
     * @throws CannotTradeException          if the trade is not allowed
     * @throws AuthorizationException        if the user cannot access this trade
     * @throws TradableItemNotFoundException couldn't find the item
     * @throws UserNotFoundException         if the user isn't found
     * @throws TradeNotFoundException        if the trade isn't found
     */
    public void editTrade(String traderId, String tradeID, Date firstMeeting, Date secondMeeting, String meetingLocation,
                          int inventoryItemIndex, int traderInventoryItemIndex)
            throws CannotTradeException, TradeNotFoundException, UserNotFoundException, AuthorizationException, TradableItemNotFoundException {

        Trade trade = getTrade(tradeID);
        Trader trader1 = getTrader(trade.getFirstUserId());
        Trader trader2 = getTrader(trade.getSecondUserId());
        ArrayList<String> items1 = trader2.getAvailableItems();
        ArrayList<String> items2 = trader1.getAvailableItems();
        if (trader1.getId().equals(traderId)) {
            ArrayList<String> tmp = items1;
            items2 = tmp;
            items1 = items2;
        }
        try {
            counterTradeOffer(traderId, tradeID, firstMeeting, secondMeeting, meetingLocation,
                    items1.get(inventoryItemIndex), items2.get(traderInventoryItemIndex));
        } catch (IndexOutOfBoundsException e) {
            throw new TradableItemNotFoundException();
        }
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
        if (!trade.isFirstUserConfirmed1() || !trade.isSecondUserConfirmed1())
            throw new AuthorizationException("First meeting hasn't been confirmed");
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
     * @param firstUserOffer    the tradable item id of the first user offer
     * @param secondUserOffer   the tradable item id of the second user offer
     * @return the id of the trade
     * @throws CannotTradeException   too many edits
     * @throws TradeNotFoundException this trade doesn't exist
     * @throws AuthorizationException this trade doesn't belong to this user
     */
    public String counterTradeOffer(String traderId, String tradeId, Date meetingTime, Date secondMeetingTime, String
            meetingLocation,
                                    String firstUserOffer, String secondUserOffer) throws
            CannotTradeException, TradeNotFoundException, AuthorizationException, UserNotFoundException {
        Trade trade = getTrade(tradeId);
        if (trade.getNumEdits() >= trade.getMaxAllowedEdits()) {
            this.denyTrade(tradeId);
            throw new CannotTradeException("Too many edits. Trade is cancelled.");
        }
        if (trade.getUserTurnToEdit().equals(traderId)) trade.changeUserTurn();
        else throw new CannotTradeException("A previous trade offer has already been sent");
        trade.setMeetingTime(meetingTime);
        trade.setSecondMeetingTime(secondMeetingTime);
        trade.setMeetingLocation(meetingLocation);
        trade.setFirstUserOffer(firstUserOffer);
        trade.setSecondUserOffer(secondUserOffer);
        trade.setNumEdits(trade.getNumEdits() + 1);

        updateTradeDatabase(trade);
        return trade.getId();
    }


    /**
     * Gets a list of the items used in trades
     *
     * @return list of tradable items that were recently traded
     * @throws TradeNotFoundException        trade wasn't found
     * @throws AuthorizationException        trade doesn't belong to this user
     * @throws UserNotFoundException         user wasn't found
     * @throws TradableItemNotFoundException tradable item not found
     * @params traderId the trader id
     */
    public ArrayList<TradableItem> getRecentTradeItems(String traderId) throws AuthorizationException, TradeNotFoundException,
            UserNotFoundException, TradableItemNotFoundException {
        ArrayList<String> completedTrades = getTrader(traderId).getCompletedTrades();
        ArrayList<TradableItem> recentTradeItems = new ArrayList<>();
        for (String tradeID : completedTrades) {
            Trade trade = getTrade(tradeID);
            String firstItemId = trade.getFirstUserOffer();
            String secondItemId = trade.getSecondUserOffer();
            try {
                if (!firstItemId.equals(""))
                    recentTradeItems.add(getTradableItem(firstItemId));
                if (!secondItemId.equals(""))
                    recentTradeItems.add(getTradableItem(secondItemId));
            } catch (EntryNotFoundException e) {
                throw new TradableItemNotFoundException();
            }
        }
        return recentTradeItems;
    }

//    /**
//     * When a meeting is confirmed by both trader, then both get updated
//     * @param tradeId id of first trader
//     * @param trader2Id id of 2nd trader
//     * @param item1 item of first trader
//     * @param item2 item of second trader
//     * @param lastMeeting is if the meeting is the last one
//     * @throws UserNotFoundException
//     * @throws AuthorizationException
//     */
//    public void confirmMeeting(String tradeId, String trader2Id, String item1, String item2, boolean lastMeeting) throws UserNotFoundException, AuthorizationException {
//        Trader trader1 = getTrader(traderId);
//        Trader trader2 = getTrader(trader2Id);
//        trader1.getAvailableItems().add(item2);
//        System.out.println(item1);
//        System.out.println(item2);
//        trader2.getAvailableItems().add(item1);
//        if(lastMeeting){
//            trader1.getAcceptedTrades().remove(tradeId);
//            trader2.getAcceptedTrades().remove(tradeId);
//            trader1.getCompletedTrades().add(tradeId);
//            trader2.getCompletedTrades().add(tradeId);
//        }
//        userDatabase.update(trader1);
//        userDatabase.update(trader2);
//    }

}
