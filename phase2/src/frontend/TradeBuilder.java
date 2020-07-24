package frontend;

import backend.models.Trade;

import java.util.Date;

/**
 * TradeBuilder class for creating an instance of trade.
 */
public class TradeBuilder {

    private String fromUser;
    private String toUser;
    private int allowedEditsPerUser;
    private String location;
    private Date meeting1;
    private Date meeting2;
    private String firstUserOffer;
    private String secondUserOffer;
    private String message;

    /**
     * Stores the first user (the trade requester).
     * @param fromUser The id of the user who proposed the trade
     */
    public void fromUser(String fromUser){
        this.fromUser = fromUser;
    }

    /**
     * Stores the second user (the user in which this request will be sent)
     * @param toUser The id of the second user participating in this trade.
     */
    public void toUser(String toUser){
        this.toUser = toUser;
    }

    /**
     * Stores the amount of edits allowed per user for this trade.
     * @param allowedEditsPerUser The amount of edits each user can do.
     */
    public void setAllowedEditsPerUser(int allowedEditsPerUser){
        this.allowedEditsPerUser = allowedEditsPerUser;
    }

    /**
     * Stores the location this trade will take place.
     * @param location The location this trade will take place.
     */
    public void setLocation(String location){
        this.location = location;
    }

    /**
     * Stores the first meeting time.
     * @param meeting1 The first meeting time.
     */
    public void setMeeting1(Date meeting1){
        this.meeting1 = meeting1;
    }

    /**
     * Stores the second meeting time.
     * @param meeting2 The second meeting time.
     */
    public void setMeeting2(Date meeting2){
        this.meeting2 = meeting2;
    }

    /**
     * Stores the item id of what the first user is offering (if anything)
     * @param firstUserOffer The id of the item the first user is offering.
     */
    public void setFirstUserOffer(String firstUserOffer){
        this.firstUserOffer = firstUserOffer;
    }

    /**
     * Stores the item id of what the first user wants from the second user (if anything).
     * @param secondUserOffer The id of the item the first user wants from the second user.
     */
    public void setSecondUserOffer(String secondUserOffer){
        this.secondUserOffer = secondUserOffer;
    }

    /**
     * Stores the message that goes along with this trade.
     * @param message The message that will go along with this trade
     */
    public void setMessage(String message){
        this.message = message;
    }

    /**
     * Create a trade object using the trade parameters stored in this object.
     * @return A new trade object with the parameters stored in this object.
     */
    public Trade createTrade(){
        return new Trade(fromUser, toUser, meeting1, meeting2, location, firstUserOffer, secondUserOffer, allowedEditsPerUser, message);
    }

}
