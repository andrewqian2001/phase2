package backend;


/**
 * For paths to database files
 */
public enum DatabaseFilePaths {
    /**
     * For the file path of all users
     */
    USER("./src/backend/databasefiles/users.ser"),
    /**
     * file path of all trades
     */
    TRADE("./src/backend/databasefiles/trades.ser"),
    /**
     * file path for all items that are traded
     */
    TRADABLE_ITEM("./src/backend/databasefiles/tradableitems.ser"),

    /**
     * file path for all items that are to be purchased
     */
    PURCHASABLE_ITEM("./src/backend/databasefiles/purchasableItems.ser"),

    /**
     * file path for all purchases
     */
    PURCHASE("./src/backend/databasefiles/purchases.ser"),
    /**
     * file path for trader config file
     */
    TRADER_CONFIG("./src/backend/tradesystem/trader.properties");
    private final String FILE_PATH;

    DatabaseFilePaths(String filePath) {
        this.FILE_PATH = filePath;
    }

    /**
     * Gets the file path
     * @return file path
     */
    public String getFilePath() {
        return FILE_PATH;
    }
}
