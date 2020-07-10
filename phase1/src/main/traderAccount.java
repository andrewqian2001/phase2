package main;

import exceptions.UserAlreadyExistsException;
import tradableitems.TradableItemManager;
import trades.TradeManager;
import users.TraderManager;
import java.io.IOException;
import exceptions.AuthorizationException;
import exceptions.CannotTradeException;
import exceptions.EntryNotFoundException;
import users.UserManager;

public class traderAccount extends TradeSystem{

    private static final String USERS_FILE_PATH = "./phase1/src/users/users.ser";
    private static final String TRADE_FILE_PATH = "./phase1/src/trades/trades.ser";
    private static final String TRADABLE_ITEM_FILE_PATH = "./phase1/src/tradableitems/tradableitems.ser";
    private String loggedInUserId;
    protected UserManager userManager;
    private TradeManager tradeManager;
    private TradableItemManager tradableItemManager;
    public traderAccount() throws IOException {
        super();
    }
    /**
     * Registers a new trader into the system
     *
     * @param username username of new trader
     * @param password password for new trader
     * @return id of the newly registered trader
     * @throws IOException file path is bad
     * @throws UserAlreadyExistsException can't register a user that already exists
     */
    public String register(String username, String password) throws IOException, UserAlreadyExistsException {
        userManager = new TraderManager(USERS_FILE_PATH);

        this.loggedInUserId = ((TraderManager) userManager).registerUser(username, password, 3);
        traderAccount traderAccount = new traderAccount();
        return this.loggedInUserId;
    }
}
