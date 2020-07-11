package Database.users;

import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import Database.Database;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Used to manage and store Users.
 */
public class UserManager implements Serializable {

    protected Database<User> userDatabase;
    /**
     * Constructor for UserManager
     * @param filePath path of Database.users.ser
     * @throws IOException bad file path
     */
    public UserManager(String filePath) throws IOException {
        userDatabase = new Database(filePath);
    }

    /**
     * Registers a user
     * @param username username of new user
     * @param password password of new user
     * @return The ID of the newly created user
     * @throws UserAlreadyExistsException username is not unique
     */
    public String registerUser(String username, String password) throws UserAlreadyExistsException {
        if (isUsernameUnique(username)) return userDatabase.update(new User(username, password)).getId();
        throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
    }

    /**
     * @param userId the id of the user
     * @return the user object
     * @throws EntryNotFoundException if the user isn't found
     */
    public User getUser(String userId) throws EntryNotFoundException{
        return userDatabase.populate(userId);
    }

    /**
     * Checks if the username is unique
     * @param username username to check for
     * @return True if username is unique, else false
     */
    protected boolean isUsernameUnique(String username) {
        for (User user : userDatabase.getItems())
            if (user.getUsername().equals(username))
                return false;
        return true;
    }

    /**
     * Logs a user into the system
     * @param username username of user
     * @param password password of user
     * @return the Id of the newly logged in user
     * @throws EntryNotFoundException could not find the user
     */
    public String login(String username, String password) throws EntryNotFoundException {
        LinkedList<User> users = userDatabase.getItems();
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
     * @throws EntryNotFoundException could not find the user
     * @throws AuthorizationException this user has no authority to freeze
     */
    public void freezeUser(String loggedInUserId, String userId, boolean frozenStatus) throws EntryNotFoundException, AuthorizationException {
        User userCallingAction = userDatabase.populate(loggedInUserId);
        if (!userCallingAction.hasPermission((Permission.FREEZE_USER)) || userCallingAction.isFrozen())
            throw new AuthorizationException(loggedInUserId + " has no permission.");
        User user = userDatabase.populate(userId);
        user.setFrozen(frozenStatus);
        userDatabase.update(user);
    }
    /**
     * Gets the username of the user
     * @param userId ID of the user
     * @return the username of the user
     * @throws EntryNotFoundException could not find user
     */
    public String getUsername(String userId) throws EntryNotFoundException{
        User user = userDatabase.populate(userId);
        return user.getUsername();
    }

    /**
     * Gets the userID of a user given their username
     * @param username the username of the user
     * @return the ID of the user
     * @throws EntryNotFoundException could not find user
     */
    public String getUserId(String username) throws EntryNotFoundException{
        LinkedList<User> users = userDatabase.getItems();
        for (User user : users)
            if (user.getUsername().equals(username))
                return user.getId();
        throw new EntryNotFoundException("User with the username " + username + " not found.");
    }

    /**
     * Sets the request frozen status of a user
     * @param userId Id of the user
     * @param status true if the user requested to be un-frozen, false else
     * @throws EntryNotFoundException could not find user
     */
    public void setRequestFrozenStatus(String userId, boolean status) throws EntryNotFoundException{
        User user = userDatabase.populate(userId);
        user.setUnfrozenRequested(status);
        userDatabase.update(user);
    }

    /**
     * @param userId user Id
     * @return if the user is an admin
     * @throws EntryNotFoundException could not find user
     */
    public boolean isFrozen(String userId) throws EntryNotFoundException {
        User user = userDatabase.populate(userId);
        return user.isFrozen();
    }

    /**
     * @param userId user Id
     * @return if the user is an admin
     * @throws EntryNotFoundException could not find user
     */
    public boolean isAdmin(String userId) throws EntryNotFoundException {
        User user = userDatabase.populate(userId);
        return user instanceof Admin;
    }
    /**
     * Gets all Unfreeze Requests
     * @return a list of all traders who have requested their account to be unfrozen
     */
    public ArrayList<String> getAllUnFreezeRequests() {
        ArrayList<String> allUnFrozenList = new ArrayList<>();
        for(User user: userDatabase.getItems()) {
            if(user.isUnfrozenRequested()) allUnFrozenList.add(user.getId());
        }
        return allUnFrozenList;
    }







}
