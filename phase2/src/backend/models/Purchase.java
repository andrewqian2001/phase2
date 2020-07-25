package backend.models;


import java.io.Serializable;
import java.util.Date;

public class Purchase extends DatabaseItem implements Serializable {
    private String BUYER_ID;
    private String SELLER_ID;
    private Date meetingTime;
    private String meetingLocation;
    private boolean isBuyerConfirmed; //this is if the buyer confirmed the meeting
    private boolean isSellerConfirmed; //this is if the seller confirmed the meeting

    /**
     *
     * @param buyerID is the id of the trader who is buying an item
     * @param sellerID is the id of the trader who is selling an item
     * @param meetingTime is the date of when they will exchange cash and item
     */
    public Purchase (String buyerID, String sellerID, Date meetingTime, String meetingLocation) {
        super();
        BUYER_ID = buyerID;
        SELLER_ID = sellerID;
        this.meetingTime = meetingTime;
        this.meetingLocation = meetingLocation;
    }

    /**
     *
     * @return buyer id
     */
    public String getBUYER_ID() {
        return BUYER_ID;
    }

    /**
     *
     * @return seller id
     */
    public String getSELLER_ID() {
        return SELLER_ID;
    }

    /**
     *
     * @return the meeting time
     */
    public Date getMeetingTime(){return meetingTime;}

    /**
     *
     * @return true if the buyer has confirmed the meeting
     */
    public  boolean isBuyerConfirmed(){return isBuyerConfirmed;}

    /**
     *
     * @return true if the seller has confirmed the meeting
     */
    public boolean isSellerConfirmed(){return isSellerConfirmed;}

    public boolean isTraderInPurchase(String traderId){
        return this.getBUYER_ID().equals(traderId) || this.getSELLER_ID().equals(traderId);
    }

}