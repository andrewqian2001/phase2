package main.TradeSystem.Accounts;

import exceptions.*;
import java.io.IOException;

import main.TradeSystem.Managers.TraderManager;

/**
 * For interacting with traders
 */
public class TraderAccount implements Account{

    private final TraderManager traderManager;
    /**
     * For accessing actions that a trader can do
     * @param traderId the id of the trader
     * @throws IOException if database file has issues
     * @throws UserNotFoundException if this trader doesn't exist
     * @throws AuthorizationException if this user isn't a trader or if the account is frozen
     */
    public TraderAccount(String traderId) throws IOException, UserNotFoundException, AuthorizationException {
        traderManager = new TraderManager(traderId);
    }
    @Override
    public UserTypes getAccountType() {
        return UserTypes.TRADER;
    }

}
