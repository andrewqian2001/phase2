package main.TradeSystem.Accounts;

import java.io.IOException;

import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import exceptions.UserNotFoundException;
import main.TradeSystem.Managers.AdminManager;

public class AdminAccount {

    private final AdminManager adminManager;

    /**
     * For accessing actions that an admin can do
     * @param adminId the id of the admin
     * @throws IOException if database file has issues
     * @throws UserNotFoundException if this admin doesn't exist
     * @throws AuthorizationException if this user isn't an admin
     */
    public AdminAccount(String adminId) throws IOException, EntryNotFoundException, AuthorizationException {
        adminManager = new AdminManager(adminId);
    }

}
