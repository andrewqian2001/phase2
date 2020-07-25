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
     * Sends a buy or sell request to a different trader
     * @param isSeller checks who is sending the request to who
     * @param buyerId is the id of the buyer
     * @param sellerId is the id of the seller
     * @param meetingTime is the date of the meeting
     * @param meetingLocation is the location of the meeting
     * @throws UserNotFoundException
     * @throws PurchaseableItemNotFoundException
     * @throws CannotPurchaseException
     * @throws AuthorizationException
     */
    public void sendPurchaseRequest (boolean isSeller, String buyerId, String sellerId, Date meetingTime, String meetingLocation) throws UserNotFoundException, PurchaseableItemNotFoundException, CannotPurchaseException, AuthorizationException {
        //The reason we have the isSeller variable is b/c I don't want to add the purchase into both of the traders lists
        // reason being is I think that the lists should only be for requests you get but not for the ones you send out

        Trader buyer = getTrader(buyerId);
        Trader seller = getTrader(sellerId);
        Purchase purchase = new Purchase(buyerId, sellerId, meetingTime, meetingLocation);
        if (buyer.equals(seller)) throw new CannotPurchaseException("Cannot purchase with yourself");

        if(isSeller){
            buyer.getPurchaseRequests().add(purchase.getId());
            updateUserDatabase(buyer);
        }else{
            seller.getPurchaseRequests().add(purchase.getId());
            updateUserDatabase(seller);
        }
        updatePurchaseDatabase(purchase);

    }



    /**
     * accepts a request to buy or sell an item another trader has sent
     * @param traderId is the id of the trader who is accepting the trade
     * @param purchaseId is the id of the purchase
     */
    public void acceptPurchaseRequest(String traderId, String purchaseId) throws UserNotFoundException, AuthorizationException, PurchaseNotFoundException {

        Purchase purchase = getPurchase(purchaseId);
        Trader receiver = getTrader(traderId); //the trader who is accepting the trade
        Trader sender = getTrader(getOtherTrader(traderId, purchase));
        if(purchase.isTraderInPurchase(traderId)){throw new AuthorizationException();}

        receiver.getPurchaseRequests().remove(purchaseId);
        receiver.getAcceptedPurchases().add(purchaseId);
        sender.getAcceptedPurchases().add(purchaseId);
        updateUserDatabase(receiver);
        updateUserDatabase(sender);

    }

    /**
     * denies a request to buy or sell an item another trader has sent
     * @param traderId is the id of the trader
     * @param purchaseId is the id of the purchase
     */
    public void denyPurchaseRequest(String traderId, String purchaseId) throws PurchaseNotFoundException, UserNotFoundException, AuthorizationException {

        Purchase purchase = getPurchase(purchaseId);
        Trader receiver = getTrader(traderId); //the trader who is accepting the trade
        if(purchase.isTraderInPurchase(traderId)){throw new AuthorizationException();}
        receiver.getPurchaseRequests().remove(purchaseId);

    }

    /**
     *
     * @param traderId is the id of the trader
     * @param purchase is the id of the purchase
     * @return the id of the other trader
     */
    public String getOtherTrader(String traderId, Purchase purchase){
        if(traderId == purchase.getBUYER_ID()){
            return purchase.getSELLER_ID();
        }else{
            return purchase.getSELLER_ID();
        }
    }

    /**
     *
     * @param traderId the id of the trader confirming the meeting took place
     * @param purchaseId is the id of the purchase
     */
    public void confirmMeeting(String traderId, String purchaseId){

    }
}