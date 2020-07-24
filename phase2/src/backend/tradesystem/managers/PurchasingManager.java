package backend.tradesystem.managers;


import backend.exceptions.*;
import backend.models.Trade;
import backend.models.users.Trader;
import java.io.IOException;
import java.util.*;

public class PurchasingManager extends Manager {
    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public TradingManager() throws IOException {
        super();
    }

    /**
     * Making the database objects with set file paths
     * @param userFilePath the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath the trade database file path
     * @throws IOException issues with getting the file path
     */
    public PurchasingManager(String userFilePath, String tradableItemFilePath, String tradeFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath);
    }

    public void buyItem (Purchase purchase) throws UserNotFoundException, PurchaseableItemNotFoundException {
        String buyer = purchase.getBUYER_ID();
        String seller = purchase.getSELLER_ID();

        if (buyer.equals(seller)) throw new CannotPurchaseException("Cannot purchase with yourself");

    }
}