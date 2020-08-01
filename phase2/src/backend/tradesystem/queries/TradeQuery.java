package backend.tradesystem.queries;

import backend.exceptions.TradeNotFoundException;
import backend.tradesystem.Manager;

import java.io.IOException;
import java.util.Date;

/**
 *  For getting info about a specific trade
 */
public class TradeQuery extends Manager {

    /**
     * Create an instance of TradeQuery with preset file paths from Databse enum
     * @throws IOException issues with getting the file path
     */
    public TradeQuery() throws IOException {
        super();
    }

    /**
     * Making the database objects with set file paths
     * @param userFilePath the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath the trade database file path
     * @throws IOException issues with getting the file path
     */
    public TradeQuery(String userFilePath, String tradableItemFilePath, String tradeFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath);

    }

    /**
     * The message along with the trade offer
     * @param tradeId The id of the trade which is being checked
     * @return the message
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getMessage(String tradeId) throws TradeNotFoundException{
        return getTrade(tradeId).getMessage();
    }

    /**
     * if first meeting is confirmed by the first user
     * @param tradeId The id of the trade which is being checked
     * @return if the user that initialized the trade confirmed the first meeting
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public boolean isFirstUserConfirmed1(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).isFirstUserConfirmed1();
    }

    /**
     * if the user that got sent the trade confirmed the first meeting
     * @param tradeId The id of the trade which is being checked
     * @return if the user that got sent the trade confirmed the first meeting
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public boolean isSecondUserConfirmed1(String tradeId) throws TradeNotFoundException{
        return getTrade(tradeId).isSecondUserConfirmed1();
    }

    /**
     * if the user that initialized the trade confirmed the second meeting
     * @param tradeId The id of the trade which is being checked
     * @return if the user that initialized the trade confirmed the second meeting
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public boolean isFirstUserConfirmed2(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).isFirstUserConfirmed2();
    }

    /**
     * if the user that initialized the trade confirmed the second meeting
     * @param tradeId The id of the trade which is being checked
     * @return if the user that initialized the trade confirmed the second meeting
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public boolean isSecondUserConfirmed2(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).isSecondUserConfirmed2();
    }

    /**
     * number of times the trade has been edited
     * @param tradeId The id of the trade which is being checked
     * @return number of times the trade has been edited
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public int getNumEdits(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getNumEdits();
    }

    /**
     * when the first trade is taking place
     * @param tradeId The id of the trade which is being checked
     * @return when the first trade is taking place
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public Date getMeetingTime(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getMeetingTime();
    }

    /**
     * when the second trade is taking place
     *@param tradeId The id of the trade which is being checked
     * @return when the second trade is taking place
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public Date getSecondMeetingTime(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getSecondMeetingTime();
    }

    /**
     * where the trade is taking place
     * @param tradeId The id of the trade which is being checked
     * @return where the trade is taking place
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getMeetingLocation(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getMeetingLocation();
    }


    /**
     * the id of the item that the user that initialized the trade is willing to offer
     * @param tradeId The id of the trade which is being checked
     * @return the id of the item that the user that initialized the trade is willing to offer
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getFirstUserOffer(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getFirstUserOffer();
    }

    /**
     * the id of the item that the user that got sent the trade is willing to offer
     * @param tradeId The id of the trade which is being checked
     * @return the id of the item that the user that got sent the trade is willing to offer
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getSecondUserOffer(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getSecondUserOffer();
    }

    /**
     * how many edits can be done
     * @param tradeId The id of the trade which is being checked
     * @return how many edits can be done
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public int getMaxAllowedEdits(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getMaxAllowedEdits();
    }

    /**
     * the user id of the person's turn to edit the trade
     * @param tradeId The id of the trade which is being checked
     * @return the user id of the person's turn to edit the trade
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getUserTurnToEdit(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getUserTurnToEdit();
    }

    /**
     * if the first user has confirmed the trade request
     * @param tradeId The id of the trade which is being checked
     * @return if the first user has confirmed the trade request
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public boolean isHasFirstUserConfirmedRequest(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).isHasFirstUserConfirmedRequest();
    }

    /**
     * if the second user has confirmed the trade request
     * @param tradeId The id of the trade which is being checked
     * @return if the second user has confirmed the trade request
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public boolean isHasSecondUserConfirmedRequest(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).isHasSecondUserConfirmedRequest();
    }

    /**
     * the user id of the person initializing the trade
     * @param tradeId The id of the trade which is being checked
     * @return the user id of the person initializing the trade
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getFirstUserId(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getFirstUserId();
    }

    /**
     * the user id of the person the trade is being sent to
     * @param tradeId The id of the trade which is being checked
     * @return the user id of the person the trade is being sent to
     * @throws TradeNotFoundException if the trade could not be found in the system
     */
    public String getSecondUserId(String tradeId) throws TradeNotFoundException {
        return getTrade(tradeId).getSecondUserId();
    }

}
