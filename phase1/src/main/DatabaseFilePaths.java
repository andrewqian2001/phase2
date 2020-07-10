package main;

public enum DatabaseFilePaths {
    User("./phase1/src/Database.users/Database.users.ser"),
    Trade("./phase1/src/Database.trades/Database.trades.ser"),
    TradableItem("./phase1/src/Database.tradableitems/Database.tradableitems.ser");

    private final String FILE_PATH;

    DatabaseFilePaths(String filePath) {
        this.FILE_PATH = filePath;
    }

    public String getFilePath() {
        return FILE_PATH;
    }
}
