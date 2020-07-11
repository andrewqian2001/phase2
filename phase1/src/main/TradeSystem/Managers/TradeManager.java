package main.TradeSystem.Managers;

import Database.Database;
import Database.tradableitems.TradableItem;
import Database.trades.Trade;
import Database.users.Trader;
import Database.users.User;
import exceptions.*;
import main.DatabaseFilePaths;

import java.io.IOException;
import java.util.Date;

public class TradeManager {
    private Database<User> userDatabase;
    private Database<Trade> tradeDatabase;
    private Database<TradableItem> tradableItemDatabase;
    private String traderId;

    /**
     * For making trades
     *
     * @param traderId the user id of the trader
     * @throws IOException            for errors in getting info from database
     * @throws UserNotFoundException  if the user id passed in doesn't exist
     * @throws AuthorizationException if the user id is of the wrong type
     */
    public TradeManager(String traderId) throws IOException, UserNotFoundException, AuthorizationException {
        userDatabase = new Database<User>(DatabaseFilePaths.USER.getFilePath());
        tradeDatabase = new Database<Trade>(DatabaseFilePaths.TRADE.getFilePath());
        tradableItemDatabase = new Database<TradableItem>(DatabaseFilePaths.TRADABLE_ITEM.getFilePath());
        traderId = getTrader(traderId).getId();
    }


    /**
     * Creates a new temporary trade
     *
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
    public Trade requestTrade(String userId,
                              Date meetingTime, Date secondMeetingTime,
                              String meetingLocation, String thisUserOfferId, String secondUserOfferId, int allowedEdits)
            throws TradableItemNotFoundException, UserNotFoundException, AuthorizationException, CannotTradeException {
        Trader secondTrader = getTrader(userId);
        Trader trader = getTrader(traderId);
        if (userId.equals(traderId)) throw new CannotTradeException("Cannot trade with yourself");

        if (trader.getTradeLimit() < 0)
            throw new CannotTradeException("Your trading limit has passed, wait until it resets");
        if (secondTrader.getTradeLimit() < 0)
            throw new CannotTradeException("The user requested has a trading limit restriction");

        // This is used to check if the items are valid to trade
        getTradableItem(thisUserOfferId, traderId);
        getTradableItem(thisUserOfferId, secondUserOfferId);

        if (!tradableItemDatabase.contains(thisUserOfferId)) throw new TradableItemNotFoundException(thisUserOfferId);
        if (!tradableItemDatabase.contains(secondUserOfferId))
            throw new TradableItemNotFoundException(secondUserOfferId);
        Trade trade = new Trade(traderId, userId,
                meetingTime, secondMeetingTime,
                meetingLocation, thisUserOfferId, secondUserOfferId, allowedEdits);
        String tradeId = tradeDatabase.update(trade).getId();
        trader.getRequestedTrades().add(tradeId);
        secondTrader.getRequestedTrades().add(tradeId);
        trader.setTradeLimit(trader.getTradeLimit() - 1);
        secondTrader.setTradeLimit(secondTrader.getTradeLimit() - 1);
        userDatabase.update(trader);
        userDatabase.update(secondTrader);
        return trade;
    }

    /**
     *
     */
    public void requestLend() {
    }

    /**
     *
     */
    public void requestBorrow() {
    }

    /**
     * For denying a requested trade
     *
     * @param tradeId trade id
     * @throws TradeNotFoundException if the trade id wasn't found
     * @throws AuthorizationException if the trade doesn't belong to this user
     */
    public void denyTrade(String tradeId) throws TradeNotFoundException, AuthorizationException {
        try {
            Trade trade = getTrade(tradeId);
            Trader trader1 = getTrader(trade.getFirstUserId());
            Trader trader2 = getTrader(trade.getSecondUserId());
            trader1.getRequestedTrades().remove(tradeId);
            trader2.getRequestedItems().remove(tradeId);
            tradeDatabase.delete(tradeId);
        } catch (EntryNotFoundException e) {
            throw new TradeNotFoundException(tradeId);
        }
    }

    /**
     * Confirms the first meeting
     *
     * @param tradeId id of the trade
     * @param status  if the meeting is confirmed and the trade happened
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException this trade doesn't belong to this user
     */
    public void confirmFirstMeeting(String tradeId, boolean status) throws TradeNotFoundException, AuthorizationException {
        Trade trade = getTrade(tradeId);
        if (trade.getFirstUserId().equals(tradeId)) trade.setFirstUserConfirmed1(status);
        else if ((trade.getSecondUserId().equals(tradeId))) trade.setSecondUserConfirmed1(status);
        tradeDatabase.update(trade);
    }

    public boolean confirmRequest(String tradeId) throws TradeNotFoundException, AuthorizationException, UserNotFoundException {
        Trade trade = getTrade(tradeId);
        if (trade.getFirstUserId().equals(traderId))
            trade.setHasFirstUserConfirmedRequest(true);
        else trade.setHasSecondUserConfirmedRequest(true);

        // This should always be true but this is a check anyway
        if (trade.isHasFirstUserConfirmedRequest() && trade.isHasSecondUserConfirmedRequest()) {
            Trader trader = getTrader(trade.getFirstUserId());
            Trader trader2 = getTrader(trade.getSecondUserId());
            trader.getAvailableItems().remove(trade.getFirstUserOffer());
            trader2.getAvailableItems().remove(trade.getSecondUserOffer());
            trader.getAcceptedTrades().add(tradeId);
            trader2.getAcceptedTrades().add(tradeId);
            trader.setIncompleteTradeCount(trader.getIncompleteTradeCount() - 1);
            trader2.setIncompleteTradeCount(trader2.getIncompleteTradeCount() - 1);
            userDatabase.update(trader);
            userDatabase.update(trader2);
            return true;
        }
        return false;
    }

    /**
     * Confirms the second meeting
     *
     * @param tradeId id of the trade
     * @param status  if the meeting is confirmed and the trade happened
     * @throws TradeNotFoundException trade wasn't found
     * @throws AuthorizationException this trade doesn't belong to this user
     * @throws UserNotFoundException  if the other user of the trade is not found
     */
    public void confirmSecondMeeting(String tradeId, boolean status) throws TradeNotFoundException, AuthorizationException, UserNotFoundException {
        Trade trade = getTrade(tradeId);
        if (trade.getFirstUserId().equals(tradeId)) {
            if (!trade.isFirstUserConfirmed1() || !trade.isSecondUserConfirmed1())
                throw new AuthorizationException("First meeting hasn't been confirmed");
            trade.setFirstUserConfirmed2(status);
        } else if ((trade.getSecondUserId().equals(tradeId))) {
            if (!trade.isFirstUserConfirmed1() || !trade.isSecondUserConfirmed1())
                throw new AuthorizationException("First meeting hasn't been confirmed");
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
            trader1.getAvailableItems().add(trade.getSecondUserOffer());
            trader2.getAvailableItems().add(trade.getFirstUserOffer());
            userDatabase.update(trader1);
            userDatabase.update(trader2);

        }
        tradeDatabase.update(trade);
    }

    /**
     * Sending a counter offer
     *
     * @param tradeId           the trade id
     * @param meetingTime       the new time of the trade
     * @param secondMeetingTime the second meeting time of the trade
     * @param meetingLocation   the meeting location of the trade
     * @param firstUserOffer    the tradable item id of the first user offer
     * @param secondUserOffer   the tradable item id of the second user offer
     * @return the id of the trade
     * @throws CannotTradeException   too many edits
     * @throws UserNotFoundException  couldn't find the user in the database
     * @throws TradeNotFoundException this trade doesn't exist
     * @throws AuthorizationException this trade doesn't belong to this user
     */
    public String counterTradeOffer(String tradeId, Date meetingTime, Date secondMeetingTime, String
            meetingLocation,
                                    String firstUserOffer, String secondUserOffer) throws
            CannotTradeException, UserNotFoundException, TradeNotFoundException, AuthorizationException {
        Trade trade = getTrade(tradeId);
        Trader currTrader = getTrader(traderId);
        Trader otherTrader;
        if (trade.getFirstUserId().equals(traderId)) otherTrader = getTrader(trade.getFirstUserId());
        else otherTrader = getTrader(trade.getSecondUserId());
        if (trade.getNumEdits() >= trade.getMaxAllowedEdits()) {
            this.denyTrade(tradeId);
            throw new CannotTradeException("Too many edits. Trade is cancelled.");
        }
        if (trade.getUserTurnToEdit().equals(trade.getFirstUserId())) trade.changeUserTurn();
        else throw new CannotTradeException("A previous trade offer has already been sent");
        trade.setMeetingTime(meetingTime);
        trade.setSecondMeetingTime(secondMeetingTime);
        trade.setMeetingLocation(meetingLocation);
        trade.setFirstUserOffer(firstUserOffer);
        trade.setSecondUserOffer(secondUserOffer);
        trade.setNumEdits(trade.getNumEdits() + 1);

        tradeDatabase.update(trade);
        return trade.getId();
    }


    /**
     * Getting the trader
     *
     * @param userId id of the trader
     * @return the trader object
     * @throws UserNotFoundException  the trader wasn't found
     * @throws AuthorizationException the user was not a trader
     */
    private Trader getTrader(String userId) throws UserNotFoundException, AuthorizationException {
        User trader;
        try {
            trader = userDatabase.populate(userId);
        } catch (EntryNotFoundException e) {
            throw new UserNotFoundException(userId);
        }
        if (!(trader instanceof Trader)) throw new AuthorizationException("This user is not a trader");
        return (Trader) trader;
    }

    /**
     * Getting the trade from trade id
     *
     * @param tradeId trade id
     * @return the trade object
     * @throws TradeNotFoundException if the trade wasn't found
     * @throws AuthorizationException if the trade doesn't belong to this user
     */
    private Trade getTrade(String tradeId) throws TradeNotFoundException, AuthorizationException {
        Trade trade;
        try {
            trade = tradeDatabase.populate(tradeId);
            if (!trade.getFirstUserId().equals(traderId) && !trade.getSecondUserId().equals(traderId))
                throw new AuthorizationException("The trade does not belong to this user");
        } catch (EntryNotFoundException e) {
            throw new TradeNotFoundException(tradeId);
        }
        return trade;
    }

    /**
     * Gets the tradable item object and making sure it belongs to the trader
     *
     * @param tradableItemId the id of the tradable item
     * @param traderId       the id of the trader
     * @return the tradable item object
     * @throws TradableItemNotFoundException if the tradable item object wasn't found
     * @throws AuthorizationException        if the tradable item doesn't belong to the trader
     */
    private TradableItem getTradableItem(String tradableItemId, String traderId) throws
            TradableItemNotFoundException, UserNotFoundException,
            AuthorizationException {
        Trader trader = getTrader(traderId);
        TradableItem item;
        try {
            if (!tradableItemDatabase.contains(tradableItemId))
                throw new TradableItemNotFoundException(tradableItemId);
            item = tradableItemDatabase.populate(tradableItemId);
            if (!trader.getAvailableItems().contains(tradableItemId))
                throw new AuthorizationException("This trader does not have that item");
        } catch (EntryNotFoundException e) {
            throw new TradableItemNotFoundException(tradableItemId);
        }
        return item;
    }
}
