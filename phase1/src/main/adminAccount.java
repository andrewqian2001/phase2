package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import exceptions.AuthorizationException;
import exceptions.CannotTradeException;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import tradableitems.TradableItemManager;
import trades.TradeManager;
import users.AdminManager;
import users.UserManager;

public class adminAccount extends TradeSystem{

    private static final String USERS_FILE_PATH = "./phase1/src/users/users.ser";

    public adminAccount() throws IOException {

        super();
    }
    /**
     * Registers a new Admin into the system
     *
     * @param username username of new admin
     * @param password password for new admin
     * @throws IOException file path is bad
     * @throws UserAlreadyExistsException username already exists
     */
    public void register(String username, String password) throws IOException, UserAlreadyExistsException {
        userManager = new AdminManager(USERS_FILE_PATH);

        userManager.registerUser(username, password);
    }
    /**
     * Freezes/Unfreezes a Trader given their username Requirement: Only an Admin
     * Account can preform this action
     *
     * @param username     the username of the Trader that needs to be (un-)frozen
     * @param freezeStatus if true, method will freeze the Trader, else it will
     *                     unFreeze
     * @throws EntryNotFoundException can't find username
     * @throws AuthorizationException not allowed to freeze user
     */
    public void freezeUser(String username, boolean freezeStatus)
            throws EntryNotFoundException, AuthorizationException {
        String userId = getIdFromUsername(username);
        userManager.freezeUser(loggedInUserId, userId, freezeStatus);
    }
    /**
     * Gets a list of all Unfreeze Request
     *
     * @return a list of all unfreeze requests
     */
    public ArrayList<String> getAllUnfreezeRequests()  {
        return  userManager.getAllUnFreezeRequests();
    }

    /**
     * Gets a Map of key=id of user, value=list of their item requests
     * @return a list of item requests mapping to each user
     */
    public HashMap<String, ArrayList<String>> getAllItemRequests()  {
        return ((AdminManager) userManager).getAllItemRequests();
    }

    /**
     * Process the item request of a user
     * @param traderName username of the trader
     * @param itemName name of the item
     * @param isAccepted true if item is accepted, false if rejected
     * @throws EntryNotFoundException traderName / itemName not found
     */
    public void processItemRequest(String traderName, String itemName, boolean isAccepted) throws EntryNotFoundException {
        String traderID = userManager.getUserId(traderName);
        ArrayList<String> reqItems = ((AdminManager)userManager).getRequestedItems(traderID);
        String reqItemID = "";
        for(String itemID : reqItems) {
            if(itemName.equals(tradableItemManager.getName(itemID))) {
                reqItemID = itemID;
                break;
            }
        } if(!reqItemID.trim().equals("")) {
            if(isAccepted) {
                ((AdminManager) userManager).acceptRequestItem(traderID, reqItemID);
            } else {
                ((AdminManager) userManager).rejectRequestItem(traderID, reqItemID);
                tradableItemManager.deleteItem(reqItemID);
            }
        } else {
            throw new EntryNotFoundException(itemName + " was not found in the user's requested items list");
        }
    }

    /**
     * Sets the new weekly trade limit (Admin method)
     * @param tradeLimit the new weekly trade limit
     * @throws EntryNotFoundException Can't find traders
     */
    public void setTradeLimit(int tradeLimit) throws EntryNotFoundException {
        ((AdminManager) userManager).setTradeLimit(tradeLimit);
    }



}
