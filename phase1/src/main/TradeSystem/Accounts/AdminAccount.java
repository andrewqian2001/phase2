package main.TradeSystem.Accounts;

import java.io.IOException;

import exceptions.AuthorizationException;
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
     * @return type admin
     */
    @Override
    public UserTypes getAccountType() {
        return UserTypes.ADMIN;
    }
}
