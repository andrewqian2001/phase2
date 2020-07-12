package main.TradeSystem.Accounts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import exceptions.*;
import main.TradeSystem.Managers.AdminManager;
import main.TradeSystem.Managers.LoginManager;

/**
 * Represents an admin account
 */
public class AdminAccount implements Account {

    private final AdminManager adminManager;
    private final LoginManager loginManager;
    private String adminId;

    /**
     * For accessing actions that an admin can do
     *
     * @param adminId the id of the admin
     * @throws IOException if database file has issues
     */
    public AdminAccount(String adminId) throws IOException {
        adminManager = new AdminManager();
        loginManager = new LoginManager();
        this.adminId = adminId;
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
     *
     * @param username     the username of the user
     * @param freezeStatus whether to freeze or unfreeze
     * @throws UserNotFoundException if the user wasn't found
     */
    public void freezeUser(String username, boolean freezeStatus)
            throws UserNotFoundException {
        adminManager.setFrozen(adminManager.getUserId(username), freezeStatus);
    }

    /**
     * Gets a Map of key=id of user, value=list of their item requests
     *
     * @return a list of item requests mapping to each user
     */
    public HashMap<String, ArrayList<String>> getAllItemRequests() {
        return adminManager.getAllItemRequests();
    }

    /**
     * Gets the name of the tradable item given its id
     *
     * @param tradableItemId id of the tradable item
     * @return name of the tradable item
     * @throws TradableItemNotFoundException cant find tradable item id
     */
    public String getTradableItemName(String tradableItemId) throws TradableItemNotFoundException {
        return adminManager.getTradableItem(tradableItemId).getName();
    }

    /**
     * Sets the new weekly trade limit (Admin method)
     *
     * @param tradeLimit the new weekly trade limit
     * @throws UserNotFoundException Can't find traders
     */
    public void setTradeLimit(int tradeLimit) throws UserNotFoundException {
        adminManager.setTradeLimit(tradeLimit);
        loginManager.setDefaultTradeLimit(tradeLimit);
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
        return adminManager.getUsername(userId);
    }

    /**
     * all unfreeze requests of every user
     *
     * @return all unfreeze requests of every user
     */
    public ArrayList<String> getAllUnfreezeRequests() {
        return adminManager.getAllUnfreezeRequests();
    }

    /**
     * Process the item request of a user
     *
     * @param traderID   ID of the trader
     * @param itemIndex  index of the item
     * @param isAccepted true if item is accepted, false if rejected
     * @throws EntryNotFoundException traderName / itemName not found
     * @throws AuthorizationException if the user isn't a trader
     */
    public void processItemRequest(String traderID, int itemIndex, boolean isAccepted) throws EntryNotFoundException, AuthorizationException {
        adminManager.processItemRequest(traderID, itemIndex, isAccepted);
    }

    /**
     * the current default trade limit
     *
     * @return the current default trade limit
     */
    public int getCurrentTradeLimit() {
        return loginManager.getDefaultTradeLimit();
    }

    /**
     * Gets the user id from username
     *
     * @param username the username
     * @return the user id
     * @throws UserNotFoundException if the user wasn't found
     */
    public String getUserId(String username) throws UserNotFoundException {
        return adminManager.getUserId(username);
    }

    /**
     * account type
     *
     * @return type admin
     */
    @Override
    public UserTypes getAccountType() {
        return UserTypes.ADMIN;
    }

}