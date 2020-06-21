package trades;

import exceptions.CannotTradeException;
import main.DatabaseItem;
import users.NormalUser;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a trade between two users
 */
public class Trade extends DatabaseItem implements Serializable {
    private Date meetingTime = null;
    private Date secondMeetingTime = null;

    private String meetingLocation = "";
    private int numEdits = 0;
    private String firstUserOffer = "", secondUserOffer = "";
    private boolean isFirstUserConfirmed1 = false;
    private boolean isSecondUserConfirmed1 = false;
    private boolean isFirstUserConfirmed2 = false;
    private boolean isSecondUserConfirmed2 = false;
    private final String FIRST_USER_ID, SECOND_USER_ID;
    private final int MAX_ALLOWED_NUM_EDITS;

    private String userTurnToEdit;

    /**
     * /**
     * Saves user ids
     *
     * @param firstUser       the user id of the person initializing the trade
     * @param secondUser     the user id of the person the trade is being sent to
     * @param meetingTime       when the meeting takes place
     * @param secondMeetingTime when the second meeting takes place
     * @param meetingLocation   where the meeting takes place
     * @param firstUserOffer    the item id that the user who initialized the trade is willing to offer
     * @param secondUserOffer   the item id that the user who got sent the trade is willing to offer
     * @param allowedEdits      number of edits allowed before the trade is cancelled
     */
    public Trade(NormalUser firstUser, NormalUser secondUser,
                 Date meetingTime, Date secondMeetingTime,
                 String meetingLocation, String firstUserOffer, String secondUserOffer, int allowedEdits) {
        super();
        this.FIRST_USER_ID = firstUser.getId();
        this.SECOND_USER_ID = secondUser.getId();
        this.MAX_ALLOWED_NUM_EDITS = allowedEdits * 2;
        this.userTurnToEdit = secondUser.getId();
        this.meetingTime = meetingTime;
        this.secondMeetingTime = secondMeetingTime;
        this.meetingLocation = meetingLocation;
        this.firstUserOffer = firstUserOffer;
        this.secondUserOffer = secondUserOffer;
    }

    public void editTrade(Date meetingTime, Date secondMeetingTime, String meetingLocation,
                          String firstUserOffer, String secondUserOffer) throws CannotTradeException {
        if (this.numEdits >= MAX_ALLOWED_NUM_EDITS) throw new CannotTradeException("Too many edits");
        if (this.userTurnToEdit.equals(FIRST_USER_ID)) this.userTurnToEdit = SECOND_USER_ID;
        else this.userTurnToEdit = FIRST_USER_ID;
        this.meetingTime = meetingTime;
        this.secondMeetingTime = secondMeetingTime;
        this.meetingLocation = meetingLocation;
        this.firstUserOffer = firstUserOffer;
        this.secondUserOffer = secondUserOffer;
        this.numEdits++;
    }

    /**
     * @return if the user that initialized the trade confirmed the first meeting
     */
    public boolean isFirstUserConfirmed1() {
        return isFirstUserConfirmed1;
    }

    /**
     * @param firstUserConfirmed1 if the user that initialized the trade confirmed the first meeting
     */
    public void setFirstUserConfirmed1(boolean firstUserConfirmed1) {
        isFirstUserConfirmed1 = firstUserConfirmed1;
    }

    /**
     * @return if the user that got sent the trade confirmed the first meeting
     */
    public boolean isSecondUserConfirmed1() {
        return isSecondUserConfirmed1;
    }

    /**
     * @param secondUserConfirmed1 if the user that got sent the trade confirmed the first meeting
     */
    public void setSecondUserConfirmed1(boolean secondUserConfirmed1) {
        isSecondUserConfirmed1 = secondUserConfirmed1;
    }

    /**
     * @return if the user that initialized the trade confirmed the second meeting
     */
    public boolean isFirstUserConfirmed2() {
        return isFirstUserConfirmed2;
    }

    /**
     * @param firstUserConfirmed2 if the user that initialized the trade confirmed the second meeting
     */
    public void setFirstUserConfirmed2(boolean firstUserConfirmed2) {
        isFirstUserConfirmed2 = firstUserConfirmed2;
    }

    /**
     * @return if the user that initialized the trade confirmed the second meeting
     */
    public boolean isSecondUserConfirmed2() {
        return isSecondUserConfirmed2;
    }

    /**
     * @param secondUserConfirmed2 if the user that got sent the trade confirmed the second meeting
     */
    public void setSecondUserConfirmed2(boolean secondUserConfirmed2) {
        isSecondUserConfirmed2 = secondUserConfirmed2;
    }

    /**
     * @return the user id of the person initializing the trade
     */
    public String getFirstUserId() {
        return FIRST_USER_ID;
    }

    /**
     * @return the user id of the person the trade is being sent to
     */
    public String getSecondUserId() {
        return SECOND_USER_ID;
    }

    /**
     * @return number of times the trade has been edited
     */
    public int getNumEdits() {
        return numEdits;
    }

    /**
     * @return when the first trade is taking place
     */
    public Date getMeetingTime() {
        return meetingTime;
    }

    /**
     * @return when the second trade is taking place
     */
    public Date getSecondMeetingTime() {
        return secondMeetingTime;
    }

    /**
     * @return where the trade is taking place
     */
    public String getMeetingLocation() {
        return meetingLocation;
    }

    /**
     * @return the id of the item that the user that initialized the trade is willing to offer
     */
    public String getFirstUserOffer() {
        return firstUserOffer;
    }

    /**
     * @return the id of the item that the user that got sent the trade is willing to offer
     */
    public String getSecondUserOffer() {
        return secondUserOffer;
    }


    /**
     * @return the user id of the person's turn to edit the trade
     */
    public String getUserTurnToEdit() {
        return userTurnToEdit;
    }

}
