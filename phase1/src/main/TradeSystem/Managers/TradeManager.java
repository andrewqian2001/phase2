package main.TradeSystem.Managers;

import Database.Database;
import Database.tradableitems.TradableItem;
import Database.trades.Trade;
import Database.users.Trader;
import Database.users.User;
import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import main.DatabaseFilePaths;

import java.io.IOException;

public class TradeManager {
    private Database<User> userDatabase;
    private Database<Trade> tradeDatabase;
    private Database<TradableItem> tradableItemDatabase;
    private Trader trader;

    /**
     * For making trades
     *
     * @param traderId the user id of the trader
     * @throws IOException            for errors in getting info from database
     * @throws EntryNotFoundException if the user id passed in doesn't exist
     * @throws AuthorizationException if the user id is of the wrong type
     */
    public TradeManager(String traderId) throws IOException, EntryNotFoundException, AuthorizationException {
        userDatabase = new Database<User>(DatabaseFilePaths.USER.getFilePath());
        tradeDatabase = new Database<Trade>(DatabaseFilePaths.TRADE.getFilePath());
        tradableItemDatabase = new Database<TradableItem>(DatabaseFilePaths.TRADABLE_ITEM.getFilePath());

        User tmp = userDatabase.populate(traderId);
        if (!(tmp instanceof Trader))
            throw new AuthorizationException("This account is not a trader type.");
        else
            trader = (Trader) tmp;

    }
}
