package backend.tradesystem;


/**
 * Represents properties that are stored in a file
 */
public enum TraderProperties {

    /**
     * tradeLimit property
     */
    TRADE_LIMIT("defaultTradeLimit"),
    /**
     * incompleteTradeLimit property
     */
    INCOMPLETE_TRADE_LIM("defaultIncompleteTradeLim"),
    /**
     * minimumAmountNeededToBorrow property
     */
    MINIMUM_AMOUNT_NEEDED_TO_BORROW("defaultMinimumAmountNeededToBorrow");


    private final String PROPERTY;
    TraderProperties(String property) {
        this.PROPERTY = property;
    }

    /**
     * Gets the property
     * @return property
     */
    public String getProperty() {
        return PROPERTY;
    }
}
