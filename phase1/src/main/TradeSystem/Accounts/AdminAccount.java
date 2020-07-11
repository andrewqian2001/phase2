package main.TradeSystem.Accounts;

import java.io.IOException;
import java.util.ArrayList;

import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;
import main.TradeSystem.Managers.AdminManager;
import main.TradeSystem.Managers.LoginManager;

public class AdminAccount implements Account {

    private final AdminManager adminManager;
    private final LoginManager loginManager;

    /**
     * For accessing actions that an admin can do
     *
     * @param adminId the id of the admin
     * @throws IOException            if database file has issues
     * @throws UserNotFoundException  if this admin doesn't exist
     * @throws AuthorizationException if this user isn't an admin
     */
    public AdminAccount(String adminId) throws IOException, UserNotFoundException, AuthorizationException {
        adminManager = new AdminManager(adminId);
        loginManager = new LoginManager();
    }

    /**
     * Registers a new admin
     *
     * @param username username of new admin
     * @param password password of new admin
     * @return the id of the new admin
     * @throws UserAlreadyExistsException if the username already exists
     */
    public String registerAdmin(String username, String password) throws UserAlreadyExistsException {
        return loginManager.registerUser(username, password, UserTypes.ADMIN);
    }

    /**
     * For freezing a user
     * @param username the username of the user
     * @param freezeStatus whether to freeze or unfreeze
     * @throws UserNotFoundException if the user wasn't found
     */
    public void freezeUser(String username, boolean freezeStatus)
            throws UserNotFoundException {
        adminManager.setFrozen(adminManager.getUserId(username), freezeStatus);
    }
    /**
     * Sets the new weekly trade limit (Admin method)
     * @param tradeLimit the new weekly trade limit
     * @throws EntryNotFoundException Can't find traders
     */
    public void setTradeLimit(int tradeLimit) throws EntryNotFoundException {
        ((Database.users.AdminManager) userManager).setTradeLimit(tradeLimit);
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
     * Gets the current weekly trade limit
     * @return the current trade limit
     * @throws EntryNotFoundException Can't find traders
     */
    public int getCurrentTradeLimit() throws EntryNotFoundException {
        return ((Database.users.AdminManager) userManager).getTradeLimit();
    }


    /**
     * @return type admin
     */
    @Override
    public UserTypes getAccountType() {
        return UserTypes.ADMIN;
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
        ArrayList<String> reqItems = ((Database.users.AdminManager)userManager).getRequestedItems(traderID);
        String reqItemID = "";
        for(String itemID : reqItems) {
            if(itemName.equals(tradableItemManager.getName(itemID))) {
                reqItemID = itemID;
                break;
            }
        } if(!reqItemID.trim().equals("")) {
            if(isAccepted) {
                ((Database.users.AdminManager) userManager).acceptRequestItem(traderID, reqItemID);
            } else {
                ((Database.users.AdminManager) userManager).rejectRequestItem(traderID, reqItemID);
                tradableItemManager.deleteItem(reqItemID);
            }
        } else {
            throw new EntryNotFoundException(itemName + " was not found in the user's requested items list");
        }
    }
}
