package backend.tradesystem.managers;

import backend.exceptions.EntryNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.users.Trader;
import backend.models.users.User;

import java.io.IOException;
import java.util.ArrayList;

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
     * Return traders that should be considered to be frozen due to too many incomplete trades
     *
     * @return true if the user should be frozen, false otherwise
     */
    public ArrayList<Trader> getIncompleteTraders() {
        ArrayList<Trader> freezable = new ArrayList<>();
        for (User user : getUserDatabase().getItems()) {
            if (user instanceof Trader && ((Trader) user).getIncompleteTradeCount() > ((Trader) user).getIncompleteTradeLim()) {
                freezable.add((Trader) user);
            }
        }
        return freezable;
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
     * Gets a list of all users that requested to be unfrozen
     *
     * @return a list of all users that requested to be unfrozen
     */
    public ArrayList<User> getAllUnfreezeRequests() {
        ArrayList<User> result = new ArrayList<>();
        for (User user : getUserDatabase().getItems())
            if (user.isUnfrozenRequested())
                result.add(user);
        return result;
    }
}
