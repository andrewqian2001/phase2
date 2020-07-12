package main;

/**
 * For paths to database files
 */
public enum DatabaseFilePaths {
    /**
     * For the file path of all users
     */
    USER("./phase1/src/Database/users/users.ser"),
    /**
     * file path of all trades
     */
    TRADE("./phase1/src/Database/trades/trades.ser"),
    /**
     * file path for all items that are traded
     */
    TRADABLE_ITEM("./phase1/src/Database/tradableitems/tradableitems.ser");

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
