package trades;

import exceptions.EntryNotFoundException;
import main.Manager;
import users.Trader;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Used to manage and store all trades
 */
public class TradeManager extends Manager<Trade> implements Serializable {
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
     * @param firstUser         the user of the person initializing the trade
     * @param secondUser        the user of the person the trade is being sent to
     * @param meetingTime       when the meeting takes place
     * @param secondMeetingTime when the second meeting takes place
     * @param meetingLocation   where the meeting takes place
     * @param firstUserOffer    the item id that the user who initialized the trade is willing to offer
     * @param secondUserOffer   the item id that the user who got sent the trade is willing to offer
     * @param allowedEdits      number of edits allowed before the trade is cancelled
     * @return the object added
     */
    public Trade addTrade(Trader firstUser, Trader secondUser,
                          Date meetingTime, Date secondMeetingTime,
                          String meetingLocation, String firstUserOffer, String secondUserOffer, int allowedEdits) {
        Trade trade = new Trade(firstUser, secondUser,
                meetingTime, secondMeetingTime,
                meetingLocation, firstUserOffer, secondUserOffer, allowedEdits);
//        firstUser.updateTradeList() or something
//        secondUser.updateTradeList() or something
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
