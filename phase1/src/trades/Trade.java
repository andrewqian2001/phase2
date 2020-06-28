package trades;

import exceptions.CannotTradeException;
import main.DatabaseItem;

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
    private String firstUserOffer = "";
    private String secondUserOffer = "";
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
     * @param firstUserId       the user id of the person initializing the trade
     * @param secondUserId      the user id of the person the trade is being sent to
     * @param meetingTime       when the meeting takes place
     * @param secondMeetingTime when the second meeting takes place
     * @param meetingLocation   where the meeting takes place
     * @param firstUserOffer    the item id that the user who initialized the trade is willing to offer
     * @param secondUserOffer   the item id that the user who got sent the trade is willing to offer
     * @param allowedEdits      number of edits allowed before the trade is cancelled
     */
    public Trade(String firstUserId, String secondUserId,
                 Date meetingTime, Date secondMeetingTime,
                 String meetingLocation, String firstUserOffer, String secondUserOffer, int allowedEdits) {
        super();
        this.FIRST_USER_ID = firstUserId;
        this.SECOND_USER_ID = secondUserId;
        this.MAX_ALLOWED_NUM_EDITS = allowedEdits * 2;
        this.userTurnToEdit = secondUserId;
        this.meetingTime = meetingTime;
        this.secondMeetingTime = secondMeetingTime;
        this.meetingLocation = meetingLocation;
        this.firstUserOffer = firstUserOffer;
        this.secondUserOffer = secondUserOffer;
    }

    /**
     * @return if the user that initialized the trade confirmed the first meeting
     */
    boolean isFirstUserConfirmed1() {
        return isFirstUserConfirmed1;
    }

    /**
     * @param firstUserConfirmed1 if the user that initialized the trade confirmed the first meeting
     */
    void setFirstUserConfirmed1(boolean firstUserConfirmed1) {
        isFirstUserConfirmed1 = firstUserConfirmed1;
    }

    /**
     * @return if the user that got sent the trade confirmed the first meeting
     */
    boolean isSecondUserConfirmed1() {
        return isSecondUserConfirmed1;
    }

    /**
     * @param secondUserConfirmed1 if the user that got sent the trade confirmed the first meeting
     */
    void setSecondUserConfirmed1(boolean secondUserConfirmed1) {
        isSecondUserConfirmed1 = secondUserConfirmed1;
    }

    /**
     * @return if the user that initialized the trade confirmed the second meeting
     */
    boolean isFirstUserConfirmed2() {
        return isFirstUserConfirmed2;
    }

    /**
     * @param firstUserConfirmed2 if the user that initialized the trade confirmed the second meeting
     */
    void setFirstUserConfirmed2(boolean firstUserConfirmed2) {
        isFirstUserConfirmed2 = firstUserConfirmed2;
    }

    /**
     * @return if the user that initialized the trade confirmed the second meeting
     */
    boolean isSecondUserConfirmed2() {
        return isSecondUserConfirmed2;
    }

    /**
     * @param secondUserConfirmed2 if the user that got sent the trade confirmed the second meeting
     */
    void setSecondUserConfirmed2(boolean secondUserConfirmed2) {
        isSecondUserConfirmed2 = secondUserConfirmed2;
    }

    /**
     * @return the user id of the person initializing the trade
     */
    String getFirstUserId() {
        return FIRST_USER_ID;
    }

    /**
     * @return the user id of the person the trade is being sent to
     */
    String getSecondUserId() {
        return SECOND_USER_ID;
    }

    /**
     * @return number of times the trade has been edited
     */
    int getNumEdits() {
        return numEdits;
    }

    /**
     * @param meetingTime the new meeting time for the first meeting
     */
    void setMeetingTime(Date meetingTime){
        this.meetingTime = meetingTime;
    }

    /**
     * @param meetingTime the new meeting time for the second meeting
     */
    void setSecondMeetingTime(Date meetingTime){
        secondMeetingTime = meetingTime;
    }
    /**
     * @return when the first trade is taking place
     */
    Date getMeetingTime() {
        return meetingTime;
    }

    /**
     * @return when the second trade is taking place
     */
    Date getSecondMeetingTime() {
        return secondMeetingTime;
    }

    /**
     * @return where the trade is taking place
     */
    String getMeetingLocation() {
        return meetingLocation;
    }

    /**
     * @param location new meeting location
     */
    void setMeetingLocation(String location){
        meetingLocation = location;
    }

    /**
     * @return the id of the item that the user that initialized the trade is willing to offer
     */
    String getFirstUserOffer() {
        return firstUserOffer;
    }

    /**
     * @return the id of the item that the user that got sent the trade is willing to offer
     */
    String getSecondUserOffer() {
        return secondUserOffer;
    }

    /**
     * @param firstUserOffer what the first user gives away (id)
     */
     void setFirstUserOffer(String firstUserOffer) {
        this.firstUserOffer = firstUserOffer;
    }

    /**
     * @param secondUserOffer what the second user gives away (id)
     */
     void setSecondUserOffer(String secondUserOffer) {
        this.secondUserOffer = secondUserOffer;
    }

    int getMaxAllowedEdits() {
        return MAX_ALLOWED_NUM_EDITS;
    }

    /**
     * @return the user id of the person's turn to edit the trade
     */
    String getUserTurnToEdit() {
        return userTurnToEdit;
    }


    /**
     * Change user's turn to edit trade
     */
    void changeUserTurn() {
        if (userTurnToEdit.equals(FIRST_USER_ID)) userTurnToEdit = SECOND_USER_ID;
        else userTurnToEdit = FIRST_USER_ID;
    }
    void setNumEdits(int num){
        this.numEdits = num;
    }
}
