package backend.tradesystem.managers;


import backend.exceptions.*;
import backend.models.Purchase;
import backend.models.users.Trader;
import java.io.IOException;
import java.util.*;

public class PurchasingManager extends Manager {
    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */

    /**
     * Making the database objects with set file paths
     * @param userFilePath the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath the trade database file path
     * @throws IOException issues with getting the file path
     */
    public PurchasingManager(String userFilePath, String tradableItemFilePath, String tradeFilePath, String purchasableItemFilePath, String purchaseFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath, purchasableItemFilePath, purchaseFilePath);
    }

    /**
     *
     * @param purchase
     * @throws UserNotFoundException
     * @throws PurchaseableItemNotFoundException
     * @throws CannotPurchaseException
     */
    public void sendPurchaseRequest (Purchase purchase) throws UserNotFoundException, PurchaseableItemNotFoundException, CannotPurchaseException, AuthorizationException {
        String buyerId = purchase.getBUYER_ID();
        String sellerId = purchase.getSELLER_ID();
        Trader buyer = getTrader(buyerId);
        Trader seller = getTrader(sellerId);
        if (buyer.equals(seller)) throw new CannotPurchaseException("Cannot purchase with yourself");

        buyer.getPurchaseRequests().add(purchase.getId());
        seller.getRequestedTrades().add(purchase.getId());
        updatePurchaseDatabase(purchase);
        updateUserDatabase(buyer);
        updateUserDatabase(seller);
    }

    /**
     * accepts the purchase request from other trader
     * @param purchase is the actual purchase object
     */
    public void acceptPurchaseRequest(Purchase purchase){

    }

    /**
     * accepts the purchase request from other trader
     * @param purchase is the actual purchase object
     */
    public void denyPurchaseRequest(Purchase purchase){

    }
}