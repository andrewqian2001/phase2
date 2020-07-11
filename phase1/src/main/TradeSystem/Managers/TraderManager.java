package main.TradeSystem.Managers;

import Database.Database;
import Database.users.AdminManager;
import Database.users.Trader;
import Database.users.User;
import exceptions.AuthorizationException;
import exceptions.CannotTradeException;
import exceptions.EntryNotFoundException;
import exceptions.UserNotFoundException;
import main.DatabaseFilePaths;
import java.io.IOException;
import java.util.*;

import main.TradeSystem.Managers.TradeManager;
/**
 * Used for the actions of a Trader
 */
public class TraderManager {
    private final Database<User> userDatabase;
    private final String traderId;
    private final TradeManager tradeManager;
    /**
     * This is used for the actions that a trader user can do
     *
     * @param traderId this is the user id of the trader account
     * @throws IOException            if something goes wrong with getting database
     * @throws UserNotFoundException if the user id passed in doesn't exist
     * @throws AuthorizationException if the user is not a trader or if the user is frozen
     */
    public TraderManager(String traderId) throws IOException, UserNotFoundException, AuthorizationException {
        userDatabase = new Database<User>(DatabaseFilePaths.USER.getFilePath());
        this.traderId = getTrader(traderId).getId();
        tradeManager = new TradeManager(traderId);
    }

    /**
     * Gets the tradeID given the index of the users requested trade
     * @param requestedTradeIndex index of the requested trade
     * @return the trade ID
     * @throws UserNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     * @throws AuthorizationException if the user isn't a trader
     */
    public String getRequestedTradeId(int requestedTradeIndex) throws UserNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return getTrader().getRequestedTrades().get(requestedTradeIndex);
    }


    /**
     * Performs the trade action between two Database.users
     *
     * @param user1 the first user's id
     * @param item1 the id of the item that user1 will be giving to user2
     * @param user2 the second user's id
     * @param item2 the id of the item that user2 will be giving to user1
     * @return user1's id
     * @throws EntryNotFoundException Database.users / items not found
     */

    public String tradeItems(String user1, String item1, String user2, String item2) throws EntryNotFoundException {
        Trader trader1 = findTraderbyId(user1);
        Trader trader2 = findTraderbyId(user2);
        if (!trader1.getAvailableItems().remove(item1)) {
            throw new EntryNotFoundException("Item " + item1 + " not found");
        }
        if (!trader2.getAvailableItems().remove(item2)) {
            trader1.getAvailableItems().add(item1);
            throw new EntryNotFoundException("Item " + item2 + " not found");
        }
        //Arnt we supposed to add the items after we confirm trade
        trader1.getAvailableItems().add(item2);
        trader2.getAvailableItems().add(item1);
        trader1.getWishlist().remove(item2);
        trader2.getWishlist().remove(item1);
        userDatabase.update(trader1);
        userDatabase.update(trader2);
        return user1;
    }

    /**
     *
     * @param traderId is the id of trader1
     * @param trader2Id is the id of trader2
     * @param tradeID
     * @param firstMeeting date
     * @param secondMeeting date
     * @param meetingLocation
     * @param inventoryItemIndex
     * @param traderInventoryItemIndex
     * @return true if edit was sucessful
     * @throws CannotTradeException
     * @throws EntryNotFoundException
     */
    public boolean editTrade(String traderId, String trader2Id,  String tradeID, Date firstMeeting,
                            Date secondMeeting, String meetingLocation, int inventoryItemIndex, int traderInventoryItemIndex)throws CannotTradeException, EntryNotFoundException {
        ArrayList<String> traderInventory = ((Database.users.TraderManager) userManager).getInventory(traderId);
        ArrayList<String> trader2Inventory = ((Database.users.TraderManager) userManager).getInventory(trader2Id);
        tradeManager.editTrade(tradeID, firstMeeting, secondMeeting, meetingLocation,
                traderInventory.get(inventoryItemIndex), trader2Inventory.get(traderInventoryItemIndex));


        ((Database.users.TraderManager)userManager).removeAcceptedTrade(traderId, tradeID);
        ((Database.users.TraderManager)userManager).addRequestTrade(traderId, tradeID);
        ((Database.users.TraderManager)userManager).removeRequestTrade(traderId, tradeID);
        ((Database.users.TraderManager)userManager).acceptTradeRequest(traderId, tradeID);
        return true;
    }
    /**
     * Confirms an accepted trade took place outside of the program
     * @param tradeID id of the trade
     * @return true if the trade was successfully confirmed
     * @throws EntryNotFoundException user id / trade id not found
     */
    public boolean confirmTrade(String tradeID) throws EntryNotFoundException, AuthorizationException{
        String trader2Id = tradeManager.getOtherUser(tradeID, traderId);

        if(!((Database.users.TraderManager)userManager).getAcceptedTrades(trader2Id).contains(tradeID)){//other user has no accepted the trade
            return false;
        }
        String itemsFromTrade[] = tradeManager.getItemsFromTrade(tradeID);


        if (tradeManager.getFirstMeetingConfirmed(tradeID, traderId) && tradeManager.hasSecondMeeting(tradeID)){

            tradeManager.confirmSecondMeeting(tradeID, true);
            if(tradeManager.isSecondMeetingConfirmed(tradeID) && tradeManager.hasSecondMeeting(tradeID)){

                ((Database.users.TraderManager)userManager).trade(traderId, itemsFromTrade[1], trader2Id, itemsFromTrade[0]);
                ((Database.users.TraderManager)userManager).addToCompletedTradesList(traderId,tradeID);
                ((Database.users.TraderManager)userManager).addToCompletedTradesList(trader2Id,tradeID);

            }
        } else
            tradeManager.confirmFirstMeeting(tradeID, userID, true);
        if(tradeManager.isFirstMeetingConfirmed(tradeID)){ //once both Database.users have confirmed the trade has taken place, the inventories(avalible items list) should update

            ((Database.users.TraderManager)userManager).trade(traderId, itemsFromTrade[0], trader2Id, itemsFromTrade[1]);
            if(!tradeManager.hasSecondMeeting(tradeID)){

                ((Database.users.TraderManager)userManager).addToCompletedTradesList(traderId,tradeID);
                ((Database.users.TraderManager)userManager).addToCompletedTradesList(trader2Id,tradeID);
            }
        }
        return true;
    }

    /**
     * Removes a trade from the two user's requested (or accepted) Database.trades
     *
     * @param userID  id of user
     * @param user2ID  id of user2
     * @param tradeId id of trade to deny
     * @return true if a trade was removed, false otherwise
     * @throws EntryNotFoundException if the user was not found
     */
    public boolean denyTrade(String userID, String user2ID, String tradeId) throws EntryNotFoundException {
        Trader trader = findTraderbyId(userID);
        Trader trader2 = findTraderbyId(user2ID);
        trader2.setTradeCount(trader2.getTradeCount() - 1);
        boolean removed_request = trader.getRequestedTrades().remove(tradeId);
        boolean removed_accepted = trader.getAcceptedTrades().remove(tradeId);
        boolean removed_request2 = trader2.getRequestedTrades().remove(tradeId);
        boolean removed_accepted2 = trader2.getRequestedTrades().remove(tradeId);
        userDatabase.update(trader);
        userDatabase.update(trader2);
        return (removed_request || removed_accepted) && (removed_request2 || removed_accepted2);
    }

    /**
     *
     * @param user1 is the ID of the first trader
     * @param tradeId id of the trade
     * @return true if trade was successful
     * @throws EntryNotFoundException user1 / tradeId not found
     */

    public boolean acceptTradeRequest(String user1, String tradeId) throws EntryNotFoundException {
        Trader trader1 = findTraderbyId(user1);
        if (trader1.isFrozen() || trader1.getTradeLimit() <= trader1.getTradeCount()) {
            return false;
        }

        trader1.getRequestedTrades().remove(tradeId);
        trader1.getAcceptedTrades().add(tradeId);
        trader1.setTradeCount(trader1.getTradeCount() + 1);
        userDatabase.update(trader1);
        return true;
    }


    /**
     * Performs the action of user1 lending an item from user2
     *
     * @param user1  the id of the user lending an item
     * @param user2  the id of the user borrowing the item
     * @param itemId the id of the item
     * @param threshold the value for how many items can be borrowed before starting to lend
     * @return true if the item was successfully lent
     * @throws EntryNotFoundException if the itemId or one of the two user IDs were
     *                                not found.
     */
    public boolean lendItem(String user1, String user2, String itemId, int threshold) throws EntryNotFoundException {
        return borrowItem(user2, user1, itemId, threshold);
    }

    /**
     * Performs the action of user1 borrowing an item from user2
     *
     * @param user1  the id of the user borrowing an item
     * @param user2  the id of the user lending the item
     * @param itemId the id of the item
     * @param threshold the value for how many items can be borrowed before starting to lend
     * @return true if the item was successfully borrowed
     * @throws EntryNotFoundException if the itemId or one of the two user IDs were
     *                                not found.
     */
    public boolean borrowItem(String user1, String user2, String itemId, int threshold) throws EntryNotFoundException {
        Trader trader1 = findTraderbyId(user1);
        Trader trader2 = findTraderbyId(user2);

        if (trader1.getTotalItemsLent() - trader1.getTotalItemsBorrowed() < threshold) {
            return false;
        }
        if (!trader2.getAvailableItems().remove(itemId)) {
            throw new EntryNotFoundException("Item " + itemId + " not found");
        }

        trader1.getAvailableItems().add(itemId);
        trader1.setTotalItemsBorrowed(trader1.getTotalItemsBorrowed() + 1);
        trader2.setTotalItemsLent(trader2.getTotalItemsLent() + 1);
        userDatabase.update(trader2);
        userDatabase.update(trader1);
        return true;
    }

    /**
     * Helper function to find a trader by id
     *
     * @param userId the id of the trader to find
     * @return the trader that was found
     * @throws EntryNotFoundException if a trader with the given userId was not
     *                                found
     */
    private Trader findTraderbyId(String userId) throws EntryNotFoundException {
        User user = findUserById(userId);
        if (user instanceof Trader) {
            return (Trader) user;
        }
        throw new EntryNotFoundException("Could not find " + userId + " + in the system.");
    }

    /**
     * Gets the tradeID given the index of the Database.users accepted trade
     * @param userId id of the user
     * @param acceptedTradeIndex index of the accepted trade
     * @return the trade ID
     * @throws UserNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getAcceptedTradeId(String userId, int acceptedTradeIndex) throws UserNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return getTrader().getAcceptedTrades().get(acceptedTradeIndex);
    }

    /**
     * Makes this user request an item
     *
     * @param itemId id of the item to add
     * @return the user's id
     * @throws UserNotFoundException if the user was not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void addRequestItem(String itemId) throws UserNotFoundException, AuthorizationException{
        Trader trader = getTrader();
        trader.getRequestedItems().add(itemId);
        userDatabase.update(trader);
    }

    /**
     * Adds an item to this trader's wishlist
     * @param userId the trader's id
     * @param tradableItemId the item to be added to this user's wishlist
     * @throws EntryNotFoundException if the trader with the given userId is not found
     */
    public void addToWishList(String userId, String tradableItemId) throws EntryNotFoundException{
        Trader trader = findTraderbyId(userId);
        trader.getWishlist().add(tradableItemId);
        userDatabase.update(trader);
    }

    /**
     * Gets a hashmap of trader ids to an arraylist of their available items
     * @return a hashmap of trader ids to an arraylist of their available items
     */
    public HashMap<String, ArrayList<String>> getAllItemsInInventories() {
        HashMap<String, ArrayList<String>> allItems = new HashMap<>();

        for (User user : userDatabase.getItems()) {
            if (user instanceof Trader)
                allItems.put(user.getId(), ((Trader) user).getAvailableItems());
        }
        return allItems;
    }

    /**
     *
     * @param itemName is the name of the item
     * @return
     */
    public String getItemId(String itemName){

    }


    /**
     * Gets the IDs of all Traders in the database
     * @return An arraylist of Trader IDs
     */
    public ArrayList<String> getAllTraders() {
        ArrayList<String> allTraders = new ArrayList<>();
        for (User user : userDatabase.getItems())
            if(user instanceof Trader)
                allTraders.add(user.getId());
        return allTraders;
    }


    /**
     * gett all wish list items
     * @return arraylist of all wish list item
     * @throws UserNotFoundException user id not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public ArrayList<String> getWishlist() throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId).getWishlist();
    }

    /**
     * Adds an item to this trader's wishlist
     * @param tradableItemId the item to be added to this user's wishlist
     * @throws UserNotFoundException if the trader with the given userId is not found
     */
    public void addToWishList(String tradableItemId)  throws UserNotFoundException, AuthorizationException{
        Trader trader = getTrader(traderId);
        trader.getWishlist().add(tradableItemId);
        userDatabase.update(trader);
    }


    /**
     * For getting the trader object
     * @param id the id of the trader
     * @return the trader object
     * @throws UserNotFoundException if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    private Trader getTrader(String id) throws UserNotFoundException, AuthorizationException{
        try {

            User tmp = userDatabase.populate(id);
            if (tmp instanceof Trader) return (Trader) tmp;
            throw new AuthorizationException("The user requested is not a trader");
        }
        catch (EntryNotFoundException e){
            throw new UserNotFoundException(id);
        }
    }

    /**
     * get all available items
     * @param userID user Id
     * @return arraylist of all items in all inventories
     * @throws EntryNotFoundException user id not found
     */
    public ArrayList<String> getAvailableItems(String userID) throws EntryNotFoundException {
        return findTraderbyId(userID).getAvailableItems();
    }

    /**
     * For getting the trader object
     * @return the trader object
     * @throws UserNotFoundException if the trader doesn't exist
     * @throws AuthorizationException if the user isn't a trader
     */
    public Trader getTrader() throws UserNotFoundException, AuthorizationException {
        return getTrader(traderId);
    }

    /**
     * Gets a list of the items used in Database.trades
     *
     * @param userId id of the user
     * @return list of unique items that the user has traded/received from a trade
     * @throws EntryNotFoundException cant find user id
     */
    public Set<String> getRecentTradeItems(String userId) throws EntryNotFoundException {
        ArrayList<String> completedTrades = ((Database.users.TraderManager) userManager).getCompletedTrades(userId);
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
     * Gets the id of a User given their username
     *
     * @param username username of the User
     * @return id of the User
     * @throws EntryNotFoundException cant find user id
     */
    public String getIdFromUsername(String username) throws EntryNotFoundException {
        return userManager.getUserId(username);
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
    public boolean confirmTrade(String userID, String tradeID) throws EntryNotFoundException, AuthorizationException {
        return traderManager.confirmTrade(tradeID);
    }

    /**
     * Rejects a requested trade
     * @param userID id of the user
     * @param tradeID id of the trade
     * @return true if the trade request was successfully rejected
     * @throws EntryNotFoundException could not find trade id / user id
     */
    public boolean rejectTrade(String userID, String tradeID) throws EntryNotFoundException  {
        return ((Database.users.TraderManager) userManager).denyTrade(loggedInUserId, userID, tradeID);
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
    public boolean editTrade(String userID, String traderId,  String tradeID, Date firstMeeting, Date secondMeeting, String meetingLocation, int inventoryItemIndex, int traderInventoryItemIndex) throws CannotTradeException, EntryNotFoundException {

        traderManager.editTrade(userID, traderId, tradeID, firstMeeting, secondMeeting, meetingLocation, inventoryItemIndex, traderInventoryItemIndex);

        return true;
    }

    /**
     * Gets the tradeID given the index of the Database.users requested trade
     * @param userId id of the user
     * @param requestedTradeIndex index of the requested trade
     * @return the trade ID
     * @throws EntryNotFoundException userId not found
     * @throws IndexOutOfBoundsException index out of bounds
     */
    public String getRequestedTradeId(int requestedTradeIndex) throws EntryNotFoundException, IndexOutOfBoundsException, AuthorizationException {
        return traderManager.getRequestedTradeId(requestedTradeIndex);
    }

    /**
     * Checks if user can trade
     * @param userID
     * @return
     */
    public boolean canTrade(String userID) throws EntryNotFoundException {
        return ((Database.users.TraderManager) userManager).canTrade(userID);
    }
    /**
     * Get userId of the other user in the trade
     * @param userID id of the user
     * @param tradeID id of the trade
     * @return userId of the other user
     * @throws EntryNotFoundException if the user or trade can not be found
     */
    public String getTraderIdFromTrade(String userID, String tradeID) throws EntryNotFoundException {
        return tradeManager.getOtherUser(tradeID, userID);
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

    public boolean trade(String traderId, String secondUserName, Date firstMeeting, Date secondMeeting, String meetingLocation, int lendItemIndex, int borrowItemIndex) throws EntryNotFoundException, IndexOutOfBoundsException {



        return true;
    }

    /**
     * Accepts a requested trade
     * @param tradeID id of the trade
     * @return true if the trade request was sucessfully confirmed
     * @throws EntryNotFoundException could not find trade id
     */
    public boolean acceptTrade(String tradeID) throws EntryNotFoundException {


        return ((Database.users.TraderManager) userManager).acceptTradeRequest(loggedInUserId, tradeID);

        //-------------------------------version that solves 1 bug but creates more------------------------------------
        /*
            String user2 = tradeManager.getOtherUser(tradeID,loggedInUserId);
            return ((TraderManager) userManager).acceptTradeRequest(loggedInUserId, user2, tradeID);
         */


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
            tradeManager.confirmSecondMeeting(tradeID, userID, false); //maybe ill need to change this to input the other Database.users ID?
        }else{
            tradeManager.confirmFirstMeeting(tradeID, userID, false);
        }

        String trader2Id = tradeManager.getOtherUser(tradeID, userID);
        ((Database.users.TraderManager)userManager).addToIncompleteTradeCount(trader2Id);
        return true;
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
     * get the item index of the trade item
     * @param userID id of the user
     * @param tradeId id of the trade
     * @return item index of the trade item
     * @throws EntryNotFoundException if the user or trade can not be found
     */
    public int getUserTradeItemIndex(String userID, String tradeId) throws EntryNotFoundException {
        String[] items = tradeManager.getItemsFromTrade(tradeId);
        ArrayList<String> inventory = ((Database.users.TraderManager) userManager).getInventory(userID);
        if(tradeManager.isFirstUser(tradeId, userID)) {
            if (items[0].equals("")){
                return -1;
            }

            for(int i = 0; i < inventory.size(); i++) {
                if(items[0].equals(inventory.get(i))) return i;
            }
        }
        else {
            if (items[1].equals("")){
                return -1;
            }
            for(int i = 0; i < inventory.size(); i++) {
                if(items[1].equals(inventory.get(i))) return i;
            }
        }
        throw new EntryNotFoundException("The trade item could not be found in the inventory.");
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

        // converts trade-id to other Database.users' id
        for(String trade_id : ((Database.users.TraderManager) userManager).getCompletedTrades(userID)){
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
     * return if trade is temporary
     * @param tradeID id of the trade
     * @return true if trade is temporary
     * @throws EntryNotFoundException if the user or trade can not be found
     */
    public boolean isTradeTemporary(String tradeID) throws EntryNotFoundException {
        return tradeManager.hasSecondMeeting(tradeID);
    }

    /**
     * get first meeting time of the trade
     *
     * @param tradeID id of the trade
     * @return Date object of the firstmeeting time
     * @throws EntryNotFoundException if the user or trade can not be found
     */
    public Date getFirstMeeting(String tradeID) throws EntryNotFoundException {
        return tradeManager.getFirstMeetingTime(tradeID);
    }

    /**
     * get second meeting time of the trade
     * @param tradeID id of the trade
     * @return Date object of the second meeting time
     * @throws EntryNotFoundException if the user or trade can not be found
     */
    public Date getSecondMeeting(String tradeID) throws EntryNotFoundException {
        return tradeManager.getSecondMeetingTime(tradeID);
    }

    /**
     * get meeting location
     * @param tradeId of the trade
     * @return the meeting location
     * @throws EntryNotFoundException if the user or the trade can not be found
     */
    public String getMeetingLocation(String tradeId) throws EntryNotFoundException {
        return tradeManager.getMeetingLocation(tradeId);
    }

    /**
     * get the item offered in the trade by the user
     * @param userID id of the user
     * @param tradeId id of the trade
     * @return the item id
     * @throws EntryNotFoundException if the user or the trade can not be found
     */
    public String getUserOffer(String userID, String tradeId) throws EntryNotFoundException {
        String[] items = tradeManager.getItemsFromTrade(tradeId);
        if(tradeManager.isFirstUser(tradeId, userID)) {
            return items[0];
        }
        else {
            return items[1];
        }
    }

    /**
     * Gets the username of a User given their ID NOTE: This will most likely be
     * deleted before rollout since theres no use for this
     *
     * @param userId id of the User
     * @return username of the User
     * @throws EntryNotFoundException cant find user id
     */
    public String getUsername(String userId) throws EntryNotFoundException {
        return userManager.getUsername(userId);
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
        return traderManager.getAcceptedTradeId(userId, acceptedTradeIndex);
    }

    /**
     * Checks if the trade is still in progress
     * @param tradeID id of the trade
     * @return true if the trade is in progress, false else
     * @throws EntryNotFoundException trade is not found
     */
    public boolean isTradeInProgress(String tradeID) throws EntryNotFoundException {
        return tradeManager.isTradeInProgress(tradeID);
    }

    /**
     * checks if the given user has confirmed all meetings took place
     * @param userID  id of the user
     * @param tradeID id of the trade
     * @return true if the user has confirmed all meetings took place, false else
     * @throws EntryNotFoundException user id / trade id not found
     */
    public boolean hasUserConfirmedAllMeetings(String userID, String tradeID) throws EntryNotFoundException {
        if(tradeManager.hasSecondMeeting(tradeID))
            return tradeManager.getFirstMeetingConfirmed(tradeID, userID) && tradeManager.getSecondMeetingConfirmed(tradeID, userID);
        return tradeManager.getFirstMeetingConfirmed(tradeID, userID);
    }

    /**
     * Gets the name of the tradable item given its id
     *
     * @param tradableItemId id of the tradable item
     * @return name of the tradable item
     * @throws EntryNotFoundException cant find tradable item id
     */
    public String getTradableItemName(String tradableItemId) throws EntryNotFoundException {
        return tradableItemManager.getName(tradableItemId);
    }

    /**
     * Gets the description of the tradable item given its id
     *
     * @param tradableItemId id of the tradable item
     * @return description of the tradable item
     * @throws EntryNotFoundException cant find tradable item id
     */
    public String getTradableItemDesc(String tradableItemId) throws EntryNotFoundException {
        return tradableItemManager.getDesc(tradableItemId);
    }

    /**
     * Gets a Map of key=id of user, value=list of their item requests
     * @return a list of item requests mapping to each user
     */
    public HashMap<String, ArrayList<String>> getAllItemRequests()  {
        return ((AdminManager) userManager).getAllItemRequests();
    }

}
