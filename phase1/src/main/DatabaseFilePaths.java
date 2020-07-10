package main;

public enum DatabaseFilePaths {
    USER("./phase1/src/Database.users/Database.users.ser"),
    TRADE("./phase1/src/Database.trades/Database.trades.ser"),
    TRADABLE_ITEM("./phase1/src/Database.tradableitems/Database.tradableitems.ser");

    private final String FILE_PATH;

    DatabaseFilePaths(String filePath) {
        this.FILE_PATH = filePath;
    }

    public String getFilePath() {
        return FILE_PATH;
    }
}
