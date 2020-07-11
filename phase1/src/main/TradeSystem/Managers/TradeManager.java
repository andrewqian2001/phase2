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
     * @throws EntryNotFoundException if the user id passed in doesn't exist
     * @throws AuthorizationException if the user id is of the wrong type
     */
    public TradeManager(String traderId) throws IOException, EntryNotFoundException, AuthorizationException {
        userDatabase = new Database<User>(DatabaseFilePaths.USER.getFilePath());
        tradeDatabase = new Database<Trade>(DatabaseFilePaths.TRADE.getFilePath());
        tradableItemDatabase = new Database<TradableItem>(DatabaseFilePaths.TRADABLE_ITEM.getFilePath());

        User tmp = userDatabase.populate(traderId);
        if (!(tmp instanceof Trader))
            throw new AuthorizationException("This account is not a trader type.");
        else
            traderId = tmp.getId();
    }


    /**
     * Creates a new trade and
     *
     * @param userId      the id of the user being traded with
     * @param meetingTime       when the meeting takes place
     * @param secondMeetingTime when the second meeting takes place (make this time to be the same or earlier
     *                          than the first meeting time for a permanent trade)
     * @param meetingLocation   where the meeting takes place
     * @param firstUserOfferId    the item id that the user who initialized the trade is willing to offer
     * @param secondUserOfferId   the item id that the user who got sent the trade is willing to offer
     * @param allowedEdits      number of edits allowed before the trade is cancelled
     */
    public void requestTrade(String userId,
                           Date meetingTime, Date secondMeetingTime,
                           String meetingLocation, String firstUserOfferId, String secondUserOfferId, int allowedEdits)
            throws EntryNotFoundException, AuthorizationException {
        Trader secondTrader = getTrader(userId);
        if (!tradableItemDatabase.contains(firstUserOfferId)) throw new TradableItemNotFoundException(firstUserOfferId);
        if (!tradableItemDatabase.contains(secondUserOfferId)) throw new TradableItemNotFoundException(secondUserOfferId);
        Trade trade = new Trade(traderId, userId,
                meetingTime, secondMeetingTime,
                meetingLocation, firstUserOfferId, secondUserOfferId, allowedEdits);
        String tradeId = tradeDatabase.update(trade).getId();
        Trader trader = (Trader) userDatabase.populate((traderId));
        trader.getRequestedTrades().add(tradeId);
        userDatabase.update(trader);
        secondTrader.getRequestedTrades().add(tradeId);
        userDatabase.update(secondTrader);
    }
    public void denyTrade(String tradeId) throws TradeNotFoundException{
        try {
            tradeDatabase.delete(tradeId);
        }
        catch(EntryNotFoundException e){
            throw new TradeNotFoundException(tradeId);
        }
    }

    /**
     * Removes a Trade from storage
     *
     * @param tradeId the trade id that is being removed
     * @throws EntryNotFoundException if the id doesn't refer to anything
     */
    public void deleteTrade(String tradeId) throws EntryNotFoundException {
        tradeDatabase.delete(tradeId);
    }

    /**
     * Gets the items from a trade
     *
     * @param tradeId ID of the trade
     * @return an array of itemIDs of the two items that were involved in the trade
     * @throws EntryNotFoundException trade id wasn't found
     */
    public String[] getItemsFromTrade(String tradeId) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeId);
        return new String[]{trade.getFirstUserOffer(), trade.getSecondUserOffer()};
    }
    /**
     * Gets the Ids of traders from trade
     *
     * @param tradeId ID of the trade
     * @return an array of itemIDs of the two items that were involved in the trade
     * @throws EntryNotFoundException trade id wasn't found
     */
    public String[] getTraderIDsFromTrade(String tradeId) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeId);
        return new String[]{trade.getFirstUserId(), trade.getSecondUserId()};
    }

    /**
     * Gets if the first meeting was confirmed
     *
     * @param tradeID id of the trade
     * @param userID  id of the user
     * @return true if the user confirmed the first meeting
     * @throws EntryNotFoundException trade id wasn't found
     */
    public boolean getFirstMeetingConfirmed(String tradeID, String userID) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeID);
        if (trade.getFirstUserId().equals(userID)) return trade.isFirstUserConfirmed1();
        else if (trade.getSecondUserId().equals(userID)) return trade.isSecondUserConfirmed1();
        else throw new EntryNotFoundException("The user " + userID + " was not found.");
    }

    /**
     * Gets if the second meeting was confirmed
     *
     * @param tradeID id of the trade
     * @param userID  id of the user
     * @return true if the user confirmed the second meeting
     * @throws EntryNotFoundException trade id wasn't found
     */
    public boolean getSecondMeetingConfirmed(String tradeID, String userID) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeID);
        if (trade.getFirstUserId().equals(userID)) return trade.isFirstUserConfirmed2();
        else if (trade.getSecondUserId().equals(userID)) return trade.isSecondUserConfirmed2();
        else throw new EntryNotFoundException("The user " + userID + " was not found.");
    }

    /**
     * Confirms the first meeting
     *
     * @param tradeId id of the trade
     * @param userId  id of the user
     * @param status  status of the meeting
     * @throws EntryNotFoundException trade id wasn't found
     */
    public void confirmFirstMeeting(String tradeId, String userId, boolean status) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeId);
        if (userId.equals(trade.getFirstUserId())) trade.setFirstUserConfirmed1(status);
        else if (userId.equals(trade.getSecondUserId())) trade.setSecondUserConfirmed1(status);
        else throw new EntryNotFoundException("The user " + userId + " was not found.");
        tradeDatabase.update(trade);
    }

    /**
     *
     * @param tradeId is the id of the trade
     * @return if the first meeting happened
     * @throws EntryNotFoundException trade id wasn't found
     */
    public boolean isFirstMeetingConfirmed(String tradeId) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeId);
        if(trade.isFirstUserConfirmed1() && trade.isSecondUserConfirmed1()){
            return true;
        }
        return false;
    }

    /**
     *
     * @param tradeId is the id of the trade
     * @return if the second meeting happened
     * @throws EntryNotFoundException trade id wasn't found
     */
    public boolean isSecondMeetingConfirmed(String tradeId) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeId);
        if(trade.isFirstUserConfirmed2() && trade.isSecondUserConfirmed2()){
            return true;
        }
        return false;
    }

    /**
     * Confirms the second meeting, if there was a second meeting
     *
     * @param tradeId id of the trade
     * @param userId  id of the user
     * @param status  status of the meeting
     * @throws EntryNotFoundException trade id wasn't found
     */
    public void confirmSecondMeeting(String tradeId, String userId, boolean status) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeId);
        if (userId.equals(trade.getFirstUserId())) trade.setFirstUserConfirmed2(status);
        else if (userId.equals(trade.getSecondUserId())) trade.setSecondUserConfirmed2(status);
        else throw new EntryNotFoundException("The user " + userId + " was not found.");
        tradeDatabase.update(trade);
    }

    /**
     * Gets the other user in the trade
     *
     * @param tradeID id of the trade
     * @param userId id of the user
     * @return the user id of the other user
     * @throws EntryNotFoundException trade id / user id  wasn't found
     */
    public String getOtherUser(String tradeID, String userId) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeID);
        if (userId.equals(trade.getFirstUserId())) return trade.getSecondUserId();
        else return trade.getFirstUserId();
    }

    /**
     * Gets the first meeting time
     *
     * @param tradeID id of the trade
     * @return the Date of the first meeting time
     * @throws EntryNotFoundException trade id wasn't found
     */
    public Date getFirstMeetingTime(String tradeID) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeID);
        return trade.getMeetingTime();
    }

    /**
     * Gets the second meeting time
     *
     * @param tradeID id of the trade
     * @return the Date of the second meeting time
     * @throws EntryNotFoundException trade id wasnt found
     */
    public Date getSecondMeetingTime(String tradeID) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeID);
        return trade.getSecondMeetingTime();
    }

    /**
     * Checks if the given trade is temporary (has a second meeting)
     *
     * @param tradeID id of the trade
     * @return true if the trade is temporary
     * @throws EntryNotFoundException trade id wasn't found
     */
    public boolean hasSecondMeeting(String tradeID) throws EntryNotFoundException {
        return getSecondMeetingTime(tradeID) != null;
    }

    /**
     * Editing an existing trade
     * @param tradeId the trade id
     * @param meetingTime the new time of the trade
     * @param secondMeetingTime the second meeting time of the trade
     * @param meetingLocation the meeting location of the trade
     * @param firstUserOffer the tradableitem id of the first user offer
     * @param secondUserOffer the tradableitem id of the second user offer
     * @return the id of the trade
     * @throws CannotTradeException too many edits
     * @throws EntryNotFoundException trade id wasn't found
     */
    public String editTrade(String tradeId, Date meetingTime, Date secondMeetingTime, String meetingLocation,
                            String firstUserOffer, String secondUserOffer) throws CannotTradeException, EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeId);
        if (trade.getNumEdits() >= trade.getMaxAllowedEdits()) throw new CannotTradeException("Trade not allowed");
        if (trade.getUserTurnToEdit().equals(trade.getFirstUserId())) trade.changeUserTurn();
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
     * @param tradeID the id of the trade
     * @return meeting location of the trade
     * @throws EntryNotFoundException tradeId is not found
     */
    public String getMeetingLocation(String tradeID) throws EntryNotFoundException {
        return tradeDatabase.populate(tradeID).getMeetingLocation();
    }

    /**
     * Checks if trade is in progress
     *
     * @param tradeId id of the trade
     * @return true if the trade is in progress, false else
     * @throws EntryNotFoundException tradeId is not found
     */
    public boolean isTradeInProgress(String tradeId) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeId);

        // If permanent trade, then the first meeting must be confirmed by both Database.users for it to not be in progress.
        if (trade.getSecondMeetingTime()== null) {
            return !(trade.isFirstUserConfirmed1() && trade.isSecondUserConfirmed1());
        }
        // Checks for temporary Database.trades, both meetings must be confirmed by both Database.users for it to not be in progress.
        return !(trade.isFirstUserConfirmed2() && trade.isFirstUserConfirmed2() &&
                trade.isSecondUserConfirmed1() && trade.isSecondUserConfirmed2());
    }

    /**
     * Gets the user turn to edit
     *
     * @param tradeID id of the trade
     * @return the user turn to edit
     * @throws EntryNotFoundException tradeId not found
     */
    public String getUserTurnToEdit(String tradeID) throws EntryNotFoundException {
        Trade trade = tradeDatabase.populate(tradeID);
        return trade.getUserTurnToEdit();
    }

    /**
     * @param tradeId the id of the trade
     * @param userId the id of the user
     * @return if the user that started the trade
     * @throws EntryNotFoundException tradeId is not found
     */
    public boolean isFirstUser(String tradeId, String userId) throws EntryNotFoundException {
        return userId.equals(tradeDatabase.populate(tradeId).getFirstUserId());
    }

    /**
     * Gets the tradeID given the index of the Database.users requested trade
     * @param userId id of the user
     * @param requestedTradeIndex index of the requested trade
     * @return the trade ID
     * @throws EntryNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getRequestedTradeId(String userId, int requestedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return getTrader(userId).getRequestedTrades().get(requestedTradeIndex);
    }

    /**
     * Gets the tradeID given the index of the Database.users accepted trade
     * @param userId id of the user
     * @param acceptedTradeIndex index of the accepted trade
     * @return the trade ID
     * @throws EntryNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getAcceptedTradeId(String userId, int acceptedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return getTrader(userId).getAcceptedTrades().get(acceptedTradeIndex);
    }

    /**
     * checks if the given user has confirmed all meetings took place
     * @param userID  id of the user
     * @param tradeID id of the trade
     * @return true if the user has confirmed all meetings took place, false else
     * @throws EntryNotFoundException user id / trade id not found
     */
    public boolean hasUserConfirmedAllMeetings(String userID, String tradeID) throws EntryNotFoundException {
        if(this.hasSecondMeeting(tradeID))
            return this.getFirstMeetingConfirmed(tradeID, userID) && this.getSecondMeetingConfirmed(tradeID, userID);
        return this.getFirstMeetingConfirmed(tradeID, userID);
    }

    private Trader getTrader(String userId) throws UserNotFoundException, AuthorizationException{
        User trader;
        try {
            trader = userDatabase.populate(userId);
        }
        catch(EntryNotFoundException e){
            throw new UserNotFoundException(userId);
        }
        if (!(trader instanceof Trader)) throw new AuthorizationException("This user is not a trader");
        return (Trader) trader;
    }
    private Trade getTrade(String tradeId) throws TradeNotFoundException{
        Trade trade;
        try {
            trade = tradeDatabase.populate(tradeId);
        }
        catch(EntryNotFoundException e){
            throw new TradeNotFoundException(tradeId);
        }
        return trade;
    }
}
