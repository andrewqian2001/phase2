package trades;

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
    public Trade addTrade(String firstUserId, String secondUserId,
                          Date meetingTime, Date secondMeetingTime,
                          String meetingLocation, String firstUserOffer, String secondUserOffer, int allowedEdits) {
        Trade trade = new Trade(firstUserId, secondUserId,
                meetingTime, secondMeetingTime,
                meetingLocation, firstUserOffer, secondUserOffer, allowedEdits);
        return update(trade);
    }

    /**
     * Removes a Trade from storage
     *
     * @param tradeId the trade id that is being removed
     * @return the Trade that got deleted
     * @throws EntryNotFoundException if the id doesn't refer to anything
     */
    public Trade deleteTrade(String tradeId) throws EntryNotFoundException {
        return super.delete(tradeId);
    }

    public String[] getItemsFromTrade(String tradeId) throws EntryNotFoundException{
        Trade trade = populate(tradeId);
        return new String[]{trade.getFirstUserOffer(), trade.getSecondUserOffer()};
    }
    public void confirmFirstMeeting(String tradeId, String userId, boolean status) throws EntryNotFoundException{
        Trade trade = populate(tradeId);
        if (userId.equals(trade.getFirstUserId())) trade.setFirstUserConfirmed1(status);
        else if (userId.equals(trade.getSecondUserId())) trade.setSecondUserConfirmed1(status);
        else throw new EntryNotFoundException("The user " + userId + " was not found.");
        update(trade);
    }
    public void confirmSecondMeeting(String tradeId, String userId, boolean status) throws EntryNotFoundException{
        Trade trade = populate(tradeId);
        if (userId.equals(trade.getFirstUserId())) trade.setFirstUserConfirmed2(status);
        else if (userId.equals(trade.getSecondUserId())) trade.setSecondUserConfirmed2(status);
        else throw new EntryNotFoundException("The user " + userId + " was not found.");
        update(trade);
    }
    public String getUserThatTradeSentTo (String tradeID) throws EntryNotFoundException {
        Trade trade = populate(tradeID);
        return trade.getSecondUserId();
    }

    public Date getFirstMeetingTime (String tradeID) throws EntryNotFoundException {
        Trade trade = populate(tradeID);
        return trade.getMeetingTime();
    }

    public Date getSecondMeetingTime (String tradeID) throws EntryNotFoundException {
        Trade trade = populate(tradeID);
        return trade.getSecondMeetingTime();
    }

    public boolean isTradeInProgress(String tradeId) throws EntryNotFoundException {
        Trade trade = populate(tradeId);
        if (!trade.isFirstUserConfirmed1() || !trade.isFirstUserConfirmed2()) return false;
        if (trade.getSecondMeetingTime().before(trade.getMeetingTime()) ||
                trade.getSecondMeetingTime().equals(trade.getMeetingTime()))
            return true;
        return trade.isFirstUserConfirmed2() && trade.isSecondUserConfirmed2();
    }
    public String getUserTurnToEdit (String tradeID) throws EntryNotFoundException {
        Trade trade = populate(tradeID);
        return trade.getUserTurnToEdit();
    }

}
