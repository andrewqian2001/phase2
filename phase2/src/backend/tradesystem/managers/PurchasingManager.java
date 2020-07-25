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
    public void sendPurchaseRequest (boolean isSeller, String buyerId, String sellerId, Date meetingTime, String meetingLocation, String sellerItemId) throws UserNotFoundException, PurchaseableItemNotFoundException, CannotPurchaseException, AuthorizationException {
        //The reason we have the isSeller variable is b/c I don't want to add the purchase into both of the traders lists
        // reason being is I think that the lists should only be for requests you get but not for the ones you send out
        //otherwise you might have a bug where you can accept your own purchase requests

        Trader buyer = getTrader(buyerId);
        Trader seller = getTrader(sellerId);
        Purchase purchase = new Purchase(buyerId, sellerId, meetingTime, meetingLocation, sellerItemId);
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
     *Trader confirms the meeting took place
     * @param traderId the id of the trader confirming the meeting took place
     * @param purchaseId is the id of the purchase
     */
    public void confirmMeeting(String traderId, String purchaseId, boolean confirmed) throws PurchaseNotFoundException, AuthorizationException, UserNotFoundException, PurchaseableItemNotFoundException {
        Purchase purchase = getPurchase(purchaseId);
        if(traderId == purchase.getSELLER_ID()){
            purchase.setSellerConfirmed(confirmed);
        }else if(traderId == purchase.getBUYER_ID()){
            purchase.setBuyerConfirmed(confirmed);
        }else{
            throw new AuthorizationException();
        }

        if(purchase.isMeetingSuccess()){
            Trader seller = getTrader(purchase.getSELLER_ID());
            Trader buyer = getTrader(purchase.getBUYER_ID());
            String item = purchase.getSellerItem();
            seller.getAcceptedPurchasableItems().remove(item);
            buyer.getAcceptedPurchases().add(item);
            double price = getPurchasableItem(item).getPrice();
            buyer.setMoney(buyer.getMoney() - price);
            seller.setMoney(seller.getMoney() + price);
        }

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


}