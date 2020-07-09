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

    /**
     * Constructor for UserManager
     * @param filePath path of users.ser
     * @throws IOException
     */
    public UserManager(String filePath) throws IOException {
        super(filePath);
    }

    /**
     * Registers a user
     * @param username username of new user
     * @param password password of new user
     * @return The ID of the newly created user
     * @throws UserAlreadyExistsException
     */
    public String registerUser(String username, String password) throws UserAlreadyExistsException {
        if (isUsernameUnique(username)) return update(new User(username, password)).getId();
        throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
    }

    /**
     * Checks if the username is unique
     * @param username username to check for
     * @return True if username is unique, else false
     */
    protected boolean isUsernameUnique(String username) {
        for (User user : getItems())
            if (user.getUsername().equals(username))
                return false;
        return true;
    }

    /**
     * Logs a user into the system
     * @param username username of user
     * @param password password of user
     * @return the Id of the newly logged in user
     * @throws EntryNotFoundException
     */
    public String login(String username, String password) throws EntryNotFoundException {
        LinkedList<User> users = getItems();
        for (User user : users)
            if (user.getUsername().equals(username) && (user.getPassword().equals(password)))
                return user.getId();
        throw new EntryNotFoundException("Bad credentials.");
    }

    /**
     * Freezes/Unfreezes a user
     * @param loggedInUserId the ID of the user calling the action
     * @param userId the ID of the user that is about to be (un-)frozen
     * @param frozenStatus true if want to freeze, false else
     * @throws EntryNotFoundException
     * @throws AuthorizationException
     */
    public void freezeUser(String loggedInUserId, String userId, boolean frozenStatus) throws EntryNotFoundException, AuthorizationException {
        User userCallingAction = populate(loggedInUserId);
        if (!userCallingAction.hasPermission((Permission.FREEZE_USER)) || userCallingAction.isFrozen())
            throw new AuthorizationException(loggedInUserId + " has no permission.");
        User user = populate(userId);
        user.setFrozen(frozenStatus);
        update(user);
    }
    /**
     * Gets the username of the user
     * @param userId ID of the user
     * @return the username of the user
     * @throws EntryNotFoundException
     */
    public String getUsername(String userId) throws EntryNotFoundException{
        User user = super.populate(userId);
        return user.getUsername();
    }

    /**
     * Gets the userID of a user given their username
     * @param username the username of the user
     * @return the ID of the user
     * @throws EntryNotFoundException
     */
    public String getUserId(String username) throws EntryNotFoundException{
        LinkedList<User> users = getItems();
        for (User user : users)
            if (user.getUsername().equals(username))
                return user.getId();
        throw new EntryNotFoundException("User with the username " + username + " not found.");
    }

    /**
     * Sets the request frozen status of a user
     * @param userId Id of the user
     * @param status true if the user requested to be un-frozen, false else
     * @throws EntryNotFoundException
     */
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
