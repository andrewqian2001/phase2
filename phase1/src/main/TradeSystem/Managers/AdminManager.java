package main.TradeSystem.Managers;

import Database.Database;
import Database.tradableitems.TradableItem;
import Database.trades.Trade;
import Database.users.Admin;
import Database.users.User;
import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import main.DatabaseFilePaths;

import java.io.IOException;

public class AdminManager {
    private Database<User> userDatabase;
    private Database<Trade> tradeDatabase;
    private Database<TradableItem> tradableItemDatabase;
    private Admin admin;

    /**
     * This is used for the actions that an admin user can do
     *
     * @param adminId this is the user id of the admin account
     * @throws IOException            if something goes wrong with getting database
     * @throws EntryNotFoundException if the user id passed in doesn't exist
     * @throws AuthorizationException if the user id is of the wrong type
     */
    public AdminManager(String adminId) throws IOException, EntryNotFoundException, AuthorizationException {
        userDatabase = new Database<User>(DatabaseFilePaths.USER.getFilePath());
        tradeDatabase = new Database<Trade>(DatabaseFilePaths.TRADE.getFilePath());
        tradableItemDatabase = new Database<TradableItem>(DatabaseFilePaths.TRADABLE_ITEM.getFilePath());
        User tmp = userDatabase.populate(adminId);
        if (!(tmp instanceof Admin))
            throw new AuthorizationException("This account is not an admin type.");
        else
            admin = (Admin) tmp;

    }
}
