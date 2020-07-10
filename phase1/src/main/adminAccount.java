package main;

import java.io.IOException;

import exceptions.AuthorizationException;
import exceptions.CannotTradeException;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import trades.TradeManager;
import users.AdminManager;
import users.UserManager;

public class adminAccount extends TradeSystem{
    private String loggedInUserId;
    private static final String USERS_FILE_PATH = "./phase1/src/users/users.ser";
    private static final String TRADE_FILE_PATH = "./phase1/src/trades/trades.ser";
    private static final String TRADABLE_ITEM_FILE_PATH = "./phase1/src/tradableitems/tradableitems.ser";
    private UserManager userManager;
    private TradeManager tradeManager;
    public adminAccount() throws IOException {

        super();
    }
    /**
     * Registers a new Admin into the system
     *
     * @param username username of new admin
     * @param password password for new admin
     * @throws IOException file path is bad
     * @throws UserAlreadyExistsException username already exists
     */
    public void register(String username, String password) throws IOException, UserAlreadyExistsException {
        userManager = new AdminManager(USERS_FILE_PATH);
        adminAccount adminAccount = new adminAccount();
        userManager.registerUser(username, password);
    }

}
