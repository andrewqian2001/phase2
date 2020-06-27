package trades;

import exceptions.EntryNotFoundException;
import main.Database;
import users.Trader;

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
     * @param secondMeetingTime when the second meeting takes place
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
     * @param id the item's id that is being removed
     * @return the Trade that got deleted
     * @throws EntryNotFoundException if the id doesn't refer to anything
     */
    public Trade deleteTrade(String id) throws EntryNotFoundException {
        return super.delete(id);
    }

}
