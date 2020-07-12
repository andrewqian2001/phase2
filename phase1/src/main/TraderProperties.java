package main;

public enum TraderProperties {

    /**
     * tradeLimit property
     */
    TRADELIMIT("defaultTradeLimit"),
    /**
     * incompleteTradeLimit property
     */
    INCOMPLETETRADELIM("defaultIncompleteTradeLim"),
    /**
     * minimumAmountNeededToBorrow property
     */
    MINIMUMAMOUNTNEEDEDTOBORROW("defaultMinimumAmountNeededToBorrow");


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
