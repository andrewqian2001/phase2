package backend.models;

public class Suggestion {

    private final String FROM_TRADER_ID;
    private final String TO_TRADER_ID;
    private final String FROM_TRADER_OFFER_ID;
    private final String TO_TRADER_OFFER_ID;


    public Suggestion(String fromTraderId, String toTraderId, String fromTraderOfferId, String toTraderOfferId){
        this.FROM_TRADER_ID = fromTraderId;
        this.TO_TRADER_ID = toTraderId;
        this.FROM_TRADER_OFFER_ID = fromTraderOfferId;
        this.TO_TRADER_OFFER_ID = toTraderOfferId;
    }

    public Suggestion(String fromTraderId, String toTraderId, String fromTraderOfferId){
        this(fromTraderId, toTraderId, fromTraderOfferId, "");
    }

    public String getFromTraderId() {
        return FROM_TRADER_ID;
    }

    public String getFromTraderOfferId() {
        return FROM_TRADER_OFFER_ID;
    }

    public String getToTraderId() {
        return TO_TRADER_ID;
    }

    public String getToTraderOfferId() {
        return TO_TRADER_OFFER_ID;
    }
    
}
