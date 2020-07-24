package backend.tradesystem.managers;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.TraderProperties;

import java.io.*;
import java.util.ArrayList;

/**
 * This deals with everything relating to trade limits
 */
public class HandleTradeLimitsManager extends Manager{
    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public HandleTradeLimitsManager() throws IOException {
        super();
    }
    /**
     * Making the database objects with set file paths
     * @param userFilePath the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath the trade database file path
     * @throws IOException issues with getting the file path
     */
    public HandleTradeLimitsManager(String userFilePath, String tradableItemFilePath, String tradeFilePath, String purchasableItemFilePath, String purchaseFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath, purchasableItemFilePath, purchaseFilePath);
    }

    /**
     * Sets the specified limit for all traders
     *
     * @param traderProperty the property (limit) to change
     * @param limit the new value of the specified property
     * @throws UserNotFoundException couldn't get traders
     */
    public void setLimit(TraderProperties traderProperty, int limit) throws UserNotFoundException {
        ArrayList<User> allUsers = getUserDatabase().getItems();
        for (User user : allUsers)
            if (user instanceof Trader) {
                Trader t = (Trader) user;
                switch(traderProperty){
                    case TRADE_LIMIT:
                        //the following line makes it so that if the trader had "x" more trades left this week,
                        // they will still have "x" trades left
                        t.setTradeCount(t.getTradeCount() + limit - t.getTradeLimit());
                        t.setTradeLimit(limit);
                        break;
                    case INCOMPLETE_TRADE_LIM:
                        ((Trader) user).setIncompleteTradeLim(limit);
                        break;
                    case MINIMUM_AMOUNT_NEEDED_TO_BORROW:
                        ((Trader) user).setMinimumAmountNeededToBorrow(limit);
                }

            }
        try {
            getUserDatabase().save(allUsers);
        } catch (FileNotFoundException e) {
            throw new UserNotFoundException();
        }
    }

    /**
     * Changes the specified property of the specified user
     *
     * @param property the property (limit) to change
     * @param userId   the user who's property will be changed
     * @param newLimit the new limit
     * @throws UserNotFoundException  if the user isn't found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void setLimitSpecific(TraderProperties property, String userId, int newLimit) throws UserNotFoundException, AuthorizationException {
        Trader trader = getTrader(userId);
        switch (property){
            case MINIMUM_AMOUNT_NEEDED_TO_BORROW:
                trader.setMinimumAmountNeededToBorrow(newLimit);
                break;
            case INCOMPLETE_TRADE_LIM:
                trader.setIncompleteTradeLim(newLimit);
                break;
            case TRADE_LIMIT:
                trader.setTradeLimit(newLimit);
        }
        updateUserDatabase(trader);
    }
}
