package backend.tradesystem.suggestion_strategies;

/**
 * This class represents a suggestion for a trade
 */
public class Suggestion {

    private final String FROM_TRADER_ID;
    private final String TO_TRADER_ID;
    private final String FROM_TRADER_OFFER_ID;
    private final String TO_TRADER_OFFER_ID;


    /**
     * Constructs a trade suggestion
     * @param fromTraderId the id of the trader who will offer this suggested trade
     * @param toTraderId the id of the trader who will be sent this suggested trade
     * @param fromTraderOfferId the id of the item of the trader that will offer this suggested trade
     * @param toTraderOfferId the id of the item of the trader who will be sent this suggested trade
     */
    public Suggestion(String fromTraderId, String toTraderId, String fromTraderOfferId, String toTraderOfferId){
        this.FROM_TRADER_ID = fromTraderId;
        this.TO_TRADER_ID = toTraderId;
        this.FROM_TRADER_OFFER_ID = fromTraderOfferId;
        this.TO_TRADER_OFFER_ID = toTraderOfferId;
    }

    /**
     * Constructs a lend suggestion
     * @param fromTraderId the id of the trader who will offer this suggested trade
     * @param toTraderId the id of the trader who will be sent this suggested trade
     * @param fromTraderOfferId the id of the item of the trader that will offer this suggested trade
     */
    public Suggestion(String fromTraderId, String toTraderId, String fromTraderOfferId){
        this(fromTraderId, toTraderId, fromTraderOfferId, "");
    }

    /**
     * Get the id of the trader who will offer this suggested trade
     * @return the id of the trader who will offer this suggested trade
     */
    public String getFromTraderId() {
        return FROM_TRADER_ID;
    }

    /**
     * Get the id of the item of the trader that will offer this suggested trade
     * @return the id of the item of the trader that will offer this suggested trade
     */
    public String getFromTraderOfferId() {
        return FROM_TRADER_OFFER_ID;
    }

    /**
     * Get the id of the trader who will be sent this suggested trade
     * @return the id of the trader who will be sent this suggested trade
     */
    public String getToTraderId() {
        return TO_TRADER_ID;
    }

    /**
     * Get the id of the item of the trader who will be sent this suggested trade
     * @return the id of the item of the trader who will be sent this suggested trade
     */
    public String getToTraderOfferId() {
        return TO_TRADER_OFFER_ID;
    }
    
}
