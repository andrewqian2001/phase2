package frontend;

import backend.models.Trade;

import java.util.Date;

public class TradeBuilder {

    private String firstUserId;
    private String secondUserId;
    private int allowedEditsPerUser;
    private String location;
    private Date meeting1;
    private Date meeting2;
    private String firstUserOffer;
    private String secondUserOffer;
    private String message;

    public void setFirstUser(String firstUserId){
        this.firstUserId = firstUserId;
    }

    public void setSecondUser(String secondUserId){
        this.secondUserId = secondUserId;
    }

    public void setAllowedEditsPerUser(int allowedEditsPerUser){
        this.allowedEditsPerUser = allowedEditsPerUser;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setMeeting1(Date meeting1){
        this.meeting1 = meeting1;
    }

    public void setMeeting2(Date meeting2){
        this.meeting2 = meeting2;
    }

    public void setFirstUserOffer(String firstUserOffer){
        this.firstUserOffer = firstUserOffer;
    }

    public void setSecondUserOffer(String secondUserOffer){
        this.secondUserOffer = secondUserOffer;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public Trade createPermanentTrade(){
        return new Trade(firstUserId, secondUserId, meeting1, null, location, firstUserOffer, secondUserOffer, allowedEditsPerUser, message);
    }

    public Trade createPermanentBorrow() {
        return new Trade(firstUserId, secondUserId, meeting1, null, location, "", secondUserOffer, allowedEditsPerUser, message);
    }

    public Trade createPermanentLend(){
        return new Trade(firstUserId, secondUserId, meeting1, null, location, firstUserOffer, "", allowedEditsPerUser, message);
    }

    public Trade createTemporaryTrade(){
        return new Trade(firstUserId, secondUserId, meeting1, meeting2, location, firstUserOffer, secondUserOffer, allowedEditsPerUser, message);
    }

    public Trade createTemporaryBorrow(){
        return new Trade(firstUserId, secondUserId, meeting1, meeting2, location, "", secondUserOffer, allowedEditsPerUser, message);
    }

    public Trade createTemporaryLend(){
        return new Trade(firstUserId, secondUserId, meeting1, meeting2, location, firstUserOffer, "", allowedEditsPerUser, message);
    }

}
