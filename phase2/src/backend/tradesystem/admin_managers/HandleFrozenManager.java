package backend.tradesystem.admin_managers;

import backend.exceptions.AuthorizationException;
import backend.exceptions.EntryNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.Manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Handles anything relating to freezing a user
 */
public class HandleFrozenManager extends Manager {

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public HandleFrozenManager() throws IOException {
        super();
    }

    /**
     * @param userId the user id
     * @param status if the user requested to be unfrozen
     * @throws UserNotFoundException if the user wasn't found
     */
    public void requestUnfreeze(String userId, boolean status) throws UserNotFoundException {
        User user = getUser(userId);
        user.setUnfrozenRequested(status);
        updateUserDatabase(user);
    }

    /**
     * Freeze or unfreeze a user
     *
     * @param userID       the user id
     * @param freezeStatus to freeze the user
     * @throws UserNotFoundException can't find user id
     */
    public void setFrozen(String userID, boolean freezeStatus) throws UserNotFoundException {
        try {
            User user = getUser(userID);
            user.setFrozen(freezeStatus);
            updateUserDatabase(user);
        } catch (EntryNotFoundException e) {
            throw new UserNotFoundException(userID);
        }
    }

    /**
     * Gets a list of all user ids that requested to be unfrozen
     *
     * @return a list of all user ids that requested to be unfrozen
     */
    public ArrayList<String> getAllUnfreezeRequests() {
        ArrayList<String> result = new ArrayList<>();
        Set<String> items = getAllUsers();
        for (String userId : items) {
            try {
                if (getTrader(userId).isUnfrozenRequested())
                    result.add(userId);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            } catch (AuthorizationException e){

            }
        }
        return result;
    }

    /**
     * Return trader ids that should be considered to be frozen due to too many incomplete trades
     *
     * @return true if the user should be frozen, false otherwise
     */
    public ArrayList<String> getShouldBeFrozen() {
        ArrayList<String> freezable = new ArrayList<>();
        Set<String> items = getAllUsers();
        for (String userId : items) {
            User user = null;
            try {
                user = getUser(userId);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
            if (user instanceof Trader && ((Trader) user).hasSurpassedIncompleteTradeLimit()) {
                freezable.add(userId);
            }
        }
        return freezable;
    }

}
