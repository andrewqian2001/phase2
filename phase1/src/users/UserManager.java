package users;

import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import main.Database;

import java.io.IOException;
import java.io.Serializable;
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
}
