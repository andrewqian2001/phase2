package Database.trades;

import Database.DatabaseItem;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a trade between two Database.users
 */
public class Trade extends DatabaseItem implements Serializable {
    private Date meetingTime = null;
    private Date secondMeetingTime = null;
    private String meetingLocation = "";
    private int numEdits = 0;
    private String firstUserOffer = "";
    private String secondUserOffer = "";
    private boolean hasFirstUserConfirmedRequest = true;
    private boolean hasSecondUserConfirmedRequest = false;
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
     * @param meetingTime the new meeting time for the first meeting
     */
    public void setMeetingTime(Date meetingTime){
        this.meetingTime = meetingTime;
    }

    /**
     * @param meetingTime the new meeting time for the second meeting
     */
    public void setSecondMeetingTime(Date meetingTime){
        secondMeetingTime = meetingTime;
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
    String getMeetingLocation() {
        return meetingLocation;
    }

    /**
     * @param location new meeting location
     */
    public void setMeetingLocation(String location){
        meetingLocation = location;
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
     * @param firstUserOffer what the first user gives away (id)
     */
    public void setFirstUserOffer(String firstUserOffer) {
        this.firstUserOffer = firstUserOffer;
    }

    /**
     * @param secondUserOffer what the second user gives away (id)
     */
    public void setSecondUserOffer(String secondUserOffer) {
        this.secondUserOffer = secondUserOffer;
    }

    /**
     * @return how many edits can be done
     */
    int getMaxAllowedEdits() {
        return MAX_ALLOWED_NUM_EDITS;
    }

    /**
     * @return the user id of the person's turn to edit the trade
     */
    public String getUserTurnToEdit() {
        return userTurnToEdit;
    }


    /**
     * Change user's turn to edit trade
     */
    public void changeUserTurn() {
        if (userTurnToEdit.equals(FIRST_USER_ID)) userTurnToEdit = SECOND_USER_ID;
        else userTurnToEdit = FIRST_USER_ID;
    }

    /**
     * @param num number of edits
     */
    public void setNumEdits(int num){
        this.numEdits = num;
    }

    /**
     * @return if the first user has confirmed the trade request
     */
    public boolean isHasFirstUserConfirmedRequest() {
        return hasFirstUserConfirmedRequest;
    }

    /**
     * @param hasFirstUserConfirmedRequest changing the status if the first user confirmed the trade
     */
    public void setHasFirstUserConfirmedRequest(boolean hasFirstUserConfirmedRequest) {
        this.hasFirstUserConfirmedRequest = hasFirstUserConfirmedRequest;
    }

    /**
     * @return if the second user has confirmed the trade request
     */
    public boolean isHasSecondUserConfirmedRequest() {
        return hasSecondUserConfirmedRequest;
    }
    /**
     * @param hasSecondUserConfirmedRequest changing the status if the second user confirmed the trade
     */
    public void setHasSecondUserConfirmedRequest(boolean hasSecondUserConfirmedRequest) {
        this.hasSecondUserConfirmedRequest = hasSecondUserConfirmedRequest;
    }
}
