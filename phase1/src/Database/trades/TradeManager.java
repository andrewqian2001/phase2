package Database.trades;

import Database.users.User;
import exceptions.CannotTradeException;
import exceptions.EntryNotFoundException;
import Database.Database;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Used to manage and store all Database.trades
 */
public class TradeManager implements Serializable {
    protected Database<Trade> tradeDatabase;

    /**
     * For storing the file path of the .ser file
     *
     * @param filePath must take in .ser file
     * @throws IOException if creating a file causes issues
     */
    public TradeManager(String filePath) throws IOException {
        this.tradeDatabase = new Database(filePath);
    }

    /**
     * Creates a new Trade
     *
     * @param firstUserId       the user of the person initializing the trade
     * @param secondUserId      the user of the person the trade is being sent to
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
        return tradeDatabase.update(trade).getId();
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

}
