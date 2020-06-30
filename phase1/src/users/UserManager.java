package users;

import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import main.Database;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Used to manage and store Users.
 */
public class UserManager extends Database<User> implements Serializable {


    public UserManager(String filePath) throws IOException {
        super(filePath);
    }

    public String registerUser(String username, String password) throws UserAlreadyExistsException {
        if (isUsernameUnique(username)) return update(new User(username, password)).getId();
        throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
    }

    protected boolean isUsernameUnique(String username) {
        for (User user : getItems())
            if (user.getUsername().equals(username))
                return false;
        return true;
    }

    public String login(String username, String password) throws EntryNotFoundException {
        LinkedList<User> users = getItems();
        for (User user : users)
            if (user.getUsername().equals(username) && (user.getPassword().equals(password)))
                return user.getId();
        throw new EntryNotFoundException("Bad credentials.");
    }

    public void freezeUser(String loggedInUserId, String userId, boolean frozenStatus) throws EntryNotFoundException, AuthorizationException {
        User userCallingAction = populate(loggedInUserId);
        if (!userCallingAction.hasPermission((Permission.FREEZE_USER)) || userCallingAction.isFrozen())
            throw new AuthorizationException(loggedInUserId + " has no permission.");
        User user = populate(userId);
        user.setFrozen(frozenStatus);
        update(user);
    }
    public String getUsername(String userId) throws EntryNotFoundException{
        User user = super.populate(userId);
        return user.getUsername();
    }
    public String getUserId(String username) throws EntryNotFoundException{
        LinkedList<User> users = getItems();
        for (User user : users)
            if (user.getUsername().equals(username))
                return user.getId();
        throw new EntryNotFoundException("User with the username " + username + " not found.");
    }
    public void setRequestFrozenStatus(String userId, boolean status) throws EntryNotFoundException{
        User user = populate(userId);
        user.setUnfrozenRequested(status);
        update(user);
    }

    /**
     * return if the user is frozen
     * @param userId user Id
     * @return
     * @throws EntryNotFoundException
     */
    public boolean isFrozen(String userId) throws EntryNotFoundException {
        User user = populate(userId);
        return user.isFrozen();
    }

    /**
     * return if the user is an admin
     * @param userId user Id
     * @return
     * @throws EntryNotFoundException
     */
    public boolean isAdmin(String userId) throws EntryNotFoundException {
        User user = populate(userId);
        return user instanceof Admin;
    }
    /**
     * Gets all Unfreeze Requests
     * @return a list of all traders who have requested their account to be unfrozen
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getAllUnFreezeRequests() {
        ArrayList<String> allUnFrozenList = new ArrayList<>();
        for(User user: getItems()) {
            if(user.isUnfrozenRequested()) allUnFrozenList.add(user.getId());
        }
        return allUnFrozenList;
    }
    /**
     * Helper function to find a User by id
     *
     * @param userId the id of the trader to find
     * @return the User that was found
     * @throws EntryNotFoundException if user was not found
     */
    public User findUserById(String userId) throws EntryNotFoundException {
        return populate(userId);
    }

}
