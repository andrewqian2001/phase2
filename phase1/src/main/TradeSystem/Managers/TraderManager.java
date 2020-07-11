package main.TradeSystem.Managers;

import Database.Database;
import Database.users.Trader;
import Database.users.User;
import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import main.DatabaseFilePaths;

import java.io.IOException;

public class TraderManager {
    private Database<User> userDatabase;
    private String traderId;
    private Trader trader;

    /**
     * This is used for the actions that a trader user can do
     *
     * @param traderId this is the user id of the trader account
     * @throws IOException            if something goes wrong with getting database
     * @throws EntryNotFoundException if the user id passed in doesn't exist
     * @throws AuthorizationException if the user id is of the wrong type
     */
    public TraderManager(String traderId) throws IOException, EntryNotFoundException, AuthorizationException {
        userDatabase = new Database<User>(DatabaseFilePaths.USER.getFilePath());
        User tmp = userDatabase.populate(traderId);
        if (!(tmp instanceof Trader))
            throw new AuthorizationException("This account is not a trader type.");
        else
            traderId = tmp.getId();
        trader = (Trader) tmp;

    }

    /**
     * Gets the tradeID given the index of the users requested trade
     * @param requestedTradeIndex index of the requested trade
     * @return the trade ID
     * @throws EntryNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getRequestedTradeId(int requestedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return trader.getRequestedTrades().get(requestedTradeIndex);
    }

    /**
     * Gets the tradeID given the index of the Database.users accepted trade
     * @param userId id of the user
     * @param acceptedTradeIndex index of the accepted trade
     * @return the trade ID
     * @throws EntryNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getAcceptedTradeId(String userId, int acceptedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return trader.getAcceptedTrades().get(acceptedTradeIndex);
    }


}
