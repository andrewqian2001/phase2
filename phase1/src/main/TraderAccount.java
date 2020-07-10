package main;

import exceptions.UserAlreadyExistsException;
import tradableitems.TradableItem;
import tradableitems.TradableItemManager;
import trades.TradeManager;
import users.TraderManager;
import java.io.IOException;
import java.util.*;

import exceptions.AuthorizationException;
import exceptions.CannotTradeException;
import exceptions.EntryNotFoundException;
import users.UserManager;

public class TraderAccount extends TradeSystem{

    private static final String USERS_FILE_PATH = "./phase1/src/users/users.ser";
    private static final String TRADE_FILE_PATH = "./phase1/src/trades/trades.ser";
    private static final String TRADABLE_ITEM_FILE_PATH = "./phase1/src/tradableitems/tradableitems.ser";

    public TraderAccount() throws IOException {
        super();
    }
    /**
     * Registers a new trader into the system
     *
     * @param username username of new trader
     * @param password password for new trader
     * @return id of the newly registered trader
     * @throws IOException file path is bad
     * @throws UserAlreadyExistsException can't register a user that already exists
     */
    public String register(String username, String password) throws IOException, UserAlreadyExistsException {
        userManager = new TraderManager(USERS_FILE_PATH);

        this.loggedInUserId = ((TraderManager) userManager).registerUser(username, password, 3);

        return this.loggedInUserId;
    }

    /**
     * Requests that the item be added to the user's inventory
     *
     * @param userID user ID
     * @param itemName  name of tradable item
     * @param itemDesc description of tradableItem
     * @throws EntryNotFoundException can't find user id
     */
    public void requestItem(String userID, String itemName, String itemDesc) throws EntryNotFoundException{
        TradableItem newItem = tradableItemManager.addItem(itemName, itemDesc);
        ((TraderManager) userManager).addRequestItem(userID, newItem.getId());
    }

    /**
     * Adds item to wishList
     *
     * @param userID   user ID
     * @param itemName name of tradable item
     * @throws EntryNotFoundException can't find user id or cna't find item name
     */
    public void addToWishList(String userID, String itemName) throws EntryNotFoundException {
        ArrayList<String> itemIDs = tradableItemManager.getIdsWithName(itemName);
        if (itemIDs.size() == 0)
            throw new EntryNotFoundException("No items found with name " + itemName);
        ((TraderManager) userManager).addToWishList(userID, itemIDs.get(0));
    }

    /**
     * @param userId the user that wants to be unfrozen
     * @param status if the user requested to be unfrozen
     * @throws EntryNotFoundException can't find user id
     */
    public void requestUnfreeze(String userId, boolean status) throws EntryNotFoundException {
        userManager.setRequestFrozenStatus(userId, status);
    }

    /**
     * return the 3 most traded with Traders
     *
     * @param userID user Id
     * @return a String array of the usernames of the 3 most traded with Traders
     * @throws EntryNotFoundException cant find user id
     */
    public String[] getFrequentTraders(String userID) throws EntryNotFoundException {
        String[] frequentTraders = new String[3];
        ArrayList<String> users = new ArrayList<>();

        // converts trade-id to other users' id
        for(String trade_id : ((TraderManager) userManager).getCompletedTrades(userID)){
            users.add(tradeManager.getOtherUser(trade_id, userID));
        }

        Set<String> distinct = new HashSet<>(users);
        int highest = 0;
        for (int i = 0; i < 3; i++) {
            for (String user_id : distinct) {
                int possible_high = Collections.frequency(users, user_id);
                if (possible_high > highest) {
                    frequentTraders[i] = user_id;
                    highest = possible_high;
                }
            }
            distinct.remove(frequentTraders[i]);
        }

        //converts frequentTraders from ID array to username array
        for (int i = 0; i < 3 && frequentTraders[i] != null; i++){
            frequentTraders[i] = userManager.getUsername(frequentTraders[i]);
        }

        return frequentTraders;
    }

    /**
     * Gets a list of the items used in trades
     *
     * @param userId id of the user
     * @return list of unique items that the user has traded/received from a trade
     * @throws EntryNotFoundException cant find user id
     */
    public Set<String> getRecentTradeItems(String userId) throws EntryNotFoundException {
        ArrayList<String> completedTrades = ((TraderManager) userManager).getCompletedTrades(userId);
        Set<String> recentTradeItemNames = new HashSet<>();
        for (String tradeID : completedTrades) {
            String[] tradableItemIDs = tradeManager.getItemsFromTrade(tradeID);
            recentTradeItemNames.add(getTradableItemName(tradableItemIDs[0]));
            if (!tradableItemIDs[1].equals(""))
                recentTradeItemNames.add(getTradableItemName(tradableItemIDs[1]));
        }
        return recentTradeItemNames;
    }

    /**
     * 2-way trade method
     * @param userId  id of the logged-in user
     * @param secondUserName username of the want-to-trade with user
     * @param firstMeeting Date of the first meeting
     * @param secondMeeting Date of the second meeting
     * @param meetingLocation Location of both meetings
     * @param lendItemIndex index of the item that will be lent to
     * @param borrowItemIndex index of the item that will be borrowed
     * @return true if the trade has been processed successfully
     * @throws EntryNotFoundException userId not found
     */

    public boolean trade(String userId, String secondUserName, Date firstMeeting, Date secondMeeting, String meetingLocation, int lendItemIndex, int borrowItemIndex) throws EntryNotFoundException, IndexOutOfBoundsException {
        String secondUserId = getIdFromUsername(secondUserName);
        String lendItemId = lendItemIndex == -1 ? "" : getAvailableItems(userId).get(lendItemIndex);
        String borrowItemId = borrowItemIndex == -1 ? "" : getAvailableItems(secondUserId).get(borrowItemIndex);

        String tradeId = tradeManager.addTrade(userId, secondUserId, firstMeeting, secondMeeting, meetingLocation, lendItemId, borrowItemId, 3);


        ((TraderManager) userManager).addRequestTrade(secondUserId, tradeId);
        ((TraderManager) userManager).acceptTradeRequest(userId, tradeId);

        //---------------------------------------------- solves 1 bug creates more ------------------------------
        /*
            ((TraderManager) userManager).addRequestTrade(secondUserId, tradeId);
         */




        return true;
    }

    /**
     * 1-way Trade Method: logged-in user giving an item to another user
     * @param userId id of the logged-in user
     * @param secondUserName username of the want-to-trade with user
     * @param firstMeeting Date of the first meeting
     * @param secondMeeting Date of the second meeting
     * @param meetingLocation Location of both meetings
     * @param lendItemIndex index of the item that will be lent
     * @return true if the trade was processed successfully
     * @throws EntryNotFoundException userId not found
     */
    public boolean lendItem(String userId, String secondUserName, Date firstMeeting, Date secondMeeting,
                            String meetingLocation, int lendItemIndex) throws EntryNotFoundException{
        return trade(userId, secondUserName, firstMeeting, secondMeeting, meetingLocation, lendItemIndex, -1);
    }

    /**
     * 1-way Trade Method: logged-in user wants an item from another user
     * @param userId id of the logged-in user
     * @param secondUserName username of the want-to-trade with user
     * @param firstMeeting Date of the first meeting
     * @param secondMeeting Date of the second meeting
     * @param meetingLocation Location of both meetings
     * @param borrowItemIndex index of the item that will be borrowed
     * @return true if the trade has been processed successfully
     * @throws EntryNotFoundException user id not found
     */
    public boolean borrowItem(String userId, String secondUserName, Date firstMeeting, Date secondMeeting,
                              String meetingLocation, int borrowItemIndex) throws EntryNotFoundException {
        return trade(userId, secondUserName, firstMeeting, secondMeeting, meetingLocation, -1, borrowItemIndex);
    }

    /**
     * Confirms an accepted trade took place outside of the program
     * @param userID id of the user
     * @param tradeID id of the trade
     * @return true if the trade was successfully confirmed
     * @throws EntryNotFoundException user id / trade id not found
     */
    public boolean confirmTrade(String userID, String tradeID) throws EntryNotFoundException {
        String trader2 = tradeManager.getOtherUser(tradeID, userID);

        if(((TraderManager)userManager).getAcceptedTrades(trader2).contains(tradeID) == false){//other user has no accepted the trade
            return false;
        }


        String itemsFromTrade[] = tradeManager.getItemsFromTrade(tradeID);
        String TraderIds[] = tradeManager.getTraderIDsFromTrade(tradeID);

        if (tradeManager.getFirstMeetingConfirmed(tradeID, userID) && tradeManager.hasSecondMeeting(tradeID)){

            tradeManager.confirmSecondMeeting(tradeID, userID, true);
            if(tradeManager.isSecondMeetingConfirmed(tradeID) && isTradeTemporary(tradeID)){

                ((TraderManager)userManager).trade(TraderIds[0], itemsFromTrade[1], TraderIds[1], itemsFromTrade[0]);
                ((TraderManager)userManager).addToCompletedTradesList(TraderIds[0],tradeID);
                ((TraderManager)userManager).addToCompletedTradesList(TraderIds[1],tradeID);

            }
        } else
            tradeManager.confirmFirstMeeting(tradeID, userID, true);
        if(tradeManager.isFirstMeetingConfirmed(tradeID)){ //once both users have confirmed the trade has taken place, the inventories(avalible items list) should update

            ((TraderManager)userManager).trade(TraderIds[0], itemsFromTrade[0], TraderIds[1], itemsFromTrade[1]);
            if(!isTradeTemporary(tradeID)){

                ((TraderManager)userManager).addToCompletedTradesList(TraderIds[0],tradeID);
                ((TraderManager)userManager).addToCompletedTradesList(TraderIds[1],tradeID);
            }
        }
        return true;
    }

    /**
     * Confirms that other trader did not show up to the trade
     * This method should increment the other traders incomplete trade count but not this traders
     * @param userID is the ID of the user
     * @param tradeID the trade id
     * @return true
     * @throws EntryNotFoundException could not find user id / trade id
     */
    public boolean confirmIncompleteTrade(String userID, String tradeID) throws EntryNotFoundException {

        if(tradeManager.isFirstMeetingConfirmed(tradeID)){
            tradeManager.confirmSecondMeeting(tradeID, userID, false); //maybe ill need to change this to input the other users ID?
        }else{
            tradeManager.confirmFirstMeeting(tradeID, userID, false);
        }

        String trader2Id = tradeManager.getOtherUser(tradeID, userID);
        ((TraderManager)userManager).addToIncompleteTradeCount(trader2Id);
        return true;
    }

    /**
     * Accepts a requested trade
     * @param tradeID id of the trade
     * @return true if the trade request was sucessfully confirmed
     * @throws EntryNotFoundException could not find trade id
     */
    public boolean acceptTrade(String tradeID) throws EntryNotFoundException {


        return ((TraderManager) userManager).acceptTradeRequest(loggedInUserId, tradeID);

        //-------------------------------version that solves 1 bug but creates more------------------------------------
        /*
            String user2 = tradeManager.getOtherUser(tradeID,loggedInUserId);
            return ((TraderManager) userManager).acceptTradeRequest(loggedInUserId, user2, tradeID);
         */


    }

    /**
     * Rejects a requested trade
     * @param userID id of the user
     * @param tradeID id of the trade
     * @return true if the trade request was successfully rejected
     * @throws EntryNotFoundException could not find trade id / user id
     */
    public boolean rejectTrade(String userID, String tradeID) throws EntryNotFoundException  {
        return ((TraderManager) userManager).denyTrade(loggedInUserId, userID, tradeID);
    }

    /**
     * edits the trade object
     * @param userID id of the user
     * @param traderId id of the other user of the trade
     * @param tradeID id of the trade
     * @param firstMeeting first meeting date object
     * @param secondMeeting second meeting date object
     * @param meetingLocation String of the meeting location
     * @param inventoryItemIndex index of the user's trade item
     * @param traderInventoryItemIndex index of the trader's trade item
     * @throws EntryNotFoundException if user or trade can  not be found
     * @throws CannotTradeException if the trade is not allowed
     * @return true
     */
    public boolean editTrade(String userID, String traderId,  String tradeID, Date firstMeeting,
                             Date secondMeeting, String meetingLocation, int inventoryItemIndex, int traderInventoryItemIndex) throws EntryNotFoundException, CannotTradeException {
        ArrayList<String> userInventory = ((TraderManager) userManager).getInventory(userID);
        ArrayList<String> traderInventory = ((TraderManager) userManager).getInventory(traderId);
        tradeManager.editTrade(tradeID, firstMeeting, secondMeeting, meetingLocation,
                userInventory.get(inventoryItemIndex), traderInventory.get(traderInventoryItemIndex));




        ((TraderManager)userManager).removeAcceptedTrade(traderId, tradeID);
        ((TraderManager)userManager).addRequestTrade(traderId, tradeID);
        ((TraderManager)userManager).removeRequestTrade(userID, tradeID);
        ((TraderManager)userManager).acceptTradeRequest(userID, tradeID);

        //----------------------- version that solves 1 bug but creates more ------------------------------//
        /*
            ((TraderManager)userManager).addRequestTrade(traderId, tradeID);
            ((TraderManager)userManager).removeRequestTrade(userID, tradeID);
         */





        return true;
    }

}
