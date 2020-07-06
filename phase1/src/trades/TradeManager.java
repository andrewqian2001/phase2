package trades;

import exceptions.CannotTradeException;
import exceptions.EntryNotFoundException;
import main.Database;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Used to manage and store all trades
 */
public class TradeManager extends Database<Trade> implements Serializable {
    /**
     * For storing the file path of the .ser file
     *
     * @param filePath must take in .ser file
     * @throws IOException if creating a file causes issues
     */
    public TradeManager(String filePath) throws IOException {
        super(filePath);
    }

    /**
     * Creates a new Trade
     *
     * @param firstUserId         the user of the person initializing the trade
     * @param secondUserId        the user of the person the trade is being sent to
     * @param meetingTime       when the meeting takes place
     * @param secondMeetingTime when the second meeting takes place (make this time to be the same or earlier
     *                          than the first meeting time for a permanent trade)
     * @param meetingLocation   where the meeting takes place
     * @param firstUserOffer    the item id that the user who initialized the trade is willing to offer
     * @param secondUserOffer   the item id that the user who got sent the trade is willing to offer
     * @param allowedEdits      number of edits allowed before the trade is cancelled
     * @return the object added
     */
    public String addTrade(String firstUserId, String secondUserId,
                          Date meetingTime, Date secondMeetingTime,
                          String meetingLocation, String firstUserOffer, String secondUserOffer, int allowedEdits) {
        Trade trade = new Trade(firstUserId, secondUserId,
                meetingTime, secondMeetingTime,
                meetingLocation, firstUserOffer, secondUserOffer, allowedEdits);
        return update(trade).getId();
    }

    /**
     * Removes a Trade from storage
     *
     * @param tradeId the trade id that is being removed
     * @return the Trade that got deleted
     * @throws EntryNotFoundException if the id doesn't refer to anything
     */
    public void deleteTrade(String tradeId) throws EntryNotFoundException {
        super.delete(tradeId);
    }

    /**
     * Gets the items from a trade
     * @param tradeId ID of the trade
     * @return an array of itemIDs of the two items that were involved in the trade
     * @throws EntryNotFoundException
     */
    public String[] getItemsFromTrade(String tradeId) throws EntryNotFoundException{
        Trade trade = populate(tradeId);
        return new String[]{trade.getFirstUserOffer(), trade.getSecondUserOffer()};
    }

    /**
     * Gets if the first meeting was confirmed 
     * @param tradeID id of the trade
     * @param userID id of the user
     * @return true if the user confirmed the first meeting
     * @throws EntryNotFoundException
     */
    public boolean getFirstMeetingConfirmed(String tradeID, String userID) throws EntryNotFoundException {
        Trade trade = populate(tradeID);
        if(trade.getFirstUserId().equals(userID)) return trade.isFirstUserConfirmed1();
        else if(trade.getSecondUserId().equals(userID)) return trade.isSecondUserConfirmed1();
        else throw new EntryNotFoundException("The user " + userID + " was not found.");
    }

    /**
     * Gets if the second meeting was confirmed
     * @param tradeID id of the trade
     * @param userID id of the user
     * @return true if the user confirmed the second meeting
     * @throws EntryNotFoundException
     */
    public boolean getSecondMeetingConfirmed(String tradeID, String userID) throws EntryNotFoundException {
        Trade trade = populate(tradeID);
        if(trade.getFirstUserId().equals(userID)) return trade.isFirstUserConfirmed2();
        else if(trade.getSecondUserId().equals(userID)) return trade.isSecondUserConfirmed2();
        else throw new EntryNotFoundException("The user " + userID + " was not found.");
    }

    /**
     * Confirms the first meeting
     * @param tradeId id of the trade
     * @param userId id of the user
     * @param status status of the meeting
     * @throws EntryNotFoundException
     */
    public void confirmFirstMeeting(String tradeId, String userId, boolean status) throws EntryNotFoundException{
        Trade trade = populate(tradeId);
        if (userId.equals(trade.getFirstUserId())) trade.setFirstUserConfirmed1(status);
        else if (userId.equals(trade.getSecondUserId())) trade.setSecondUserConfirmed1(status);
        else throw new EntryNotFoundException("The user " + userId + " was not found.");
        update(trade);
    }

    /**
     * Confirms the second meeting, if there was a second meeting
     * @param tradeId id of the trade
     * @param userId id of the user
     * @param status status of the meeting
     * @throws EntryNotFoundException
     */
    public void confirmSecondMeeting(String tradeId, String userId, boolean status) throws EntryNotFoundException{
        Trade trade = populate(tradeId);
        if (userId.equals(trade.getFirstUserId())) trade.setFirstUserConfirmed2(status);
        else if (userId.equals(trade.getSecondUserId())) trade.setSecondUserConfirmed2(status);
        else throw new EntryNotFoundException("The user " + userId + " was not found.");
        update(trade);
    }

    /**
     * Gets the other user in the trade
     * @param tradeID id of the trade
     * @return the user id of the other user
     * @throws EntryNotFoundException
     */
    public String getOtherUser (String tradeID, String userId) throws EntryNotFoundException {
        Trade trade = populate(tradeID);
        if(userId.equals(trade.getFirstUserId())) return trade.getSecondUserId();
        else return trade.getFirstUserId();
    }

    /**
     * Gets the first meeting time
     * @param tradeID id of the trade
     * @return the Date of the first meeting time
     * @throws EntryNotFoundException
     */
    public Date getFirstMeetingTime (String tradeID) throws EntryNotFoundException {
        Trade trade = populate(tradeID);
        return trade.getMeetingTime();
    }

    /**
     * Gets the second meeting time
     * @param tradeID id of the trade
     * @return the Date of the second meeting time
     * @throws EntryNotFoundException
     */
    public Date getSecondMeetingTime (String tradeID) throws EntryNotFoundException {
        Trade trade = populate(tradeID);
        return trade.getSecondMeetingTime();
    }

    /**
     * Checks if the given trade is temporary (has a second meeting)
     * @param tradeID id of the trade
     * @return true if the trade is temporary
     * @throws EntryNotFoundException
     */
    public boolean hasSecondMeeting(String tradeID) throws EntryNotFoundException {
        return getSecondMeetingTime(tradeID) == null;
    }

    public String editTrade(String tradeId, Date meetingTime, Date secondMeetingTime, String meetingLocation,
                          String firstUserOffer, String secondUserOffer) throws CannotTradeException, EntryNotFoundException {
        Trade trade = populate(tradeId);
        if (trade.getNumEdits() >= trade.getMaxAllowedEdits()) throw new CannotTradeException("Trade not allowed");
        if (trade.getUserTurnToEdit().equals(trade.getFirstUserId())) trade.changeUserTurn();
        trade.setMeetingTime(meetingTime);
        trade.setSecondMeetingTime(secondMeetingTime);
        trade.setMeetingLocation(meetingLocation);
        trade.setFirstUserOffer(firstUserOffer);
        trade.setSecondUserOffer(secondUserOffer);
        trade.setNumEdits(trade.getNumEdits() + 1);
        update(trade);
        return trade.getId();
    }
    public String getMeetingLocation (String tradeID) throws EntryNotFoundException {
        return populate(tradeID).getMeetingLocation();
    }

    /**
     * Checks if trade is in progress
     * @param tradeId id of the trade
     * @return true if the trade is in progress, false else
     * @throws EntryNotFoundException
     */
    public boolean isTradeInProgress(String tradeId) throws EntryNotFoundException {
        Trade trade = populate(tradeId);
        if (!trade.isFirstUserConfirmed1() || !trade.isFirstUserConfirmed2()) return false;
        if (trade.getSecondMeetingTime().before(trade.getMeetingTime()) ||
                trade.getSecondMeetingTime().equals(trade.getMeetingTime()))
            return true;
        return trade.isFirstUserConfirmed2() && trade.isSecondUserConfirmed2();
    }

    /**
     * Gets the user turn to edit
     * @param tradeID id of the trade
     * @return the user turn to edit
     * @throws EntryNotFoundException
     */
    public String getUserTurnToEdit (String tradeID) throws EntryNotFoundException {
        Trade trade = populate(tradeID);
        return trade.getUserTurnToEdit();
    }

    public boolean isFirstUser (String tradeId, String userId) throws EntryNotFoundException {
        return userId.equals(populate(tradeId).getFirstUserId());
    }

}
