package users;

import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import main.Manager;
import exceptions.UserNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Used to manage and store Users.
 */
public class UserManager extends Manager<User> implements Serializable {

    /**
     * For storing the file path of the .ser file
     *
     * @param filePath must take in .ser file
     * @throws IOException if creating a file causes issues
     */
    public UserManager(String filePath) throws IOException {
        super(filePath);
    }

    /**
     * Saves a new user if the username is unique
     *
     * @param username the new user's username
     * @param password the new user's password
     * @param userType the specific type of User to be added
     * @return the new user that was added
     * @throws FileNotFoundException      if the specified file path was not found
     * @throws ClassNotFoundException     if there is a class that is not defined
     * @throws UserAlreadyExistsException if a user with the same username exists
     */
    public String registerUser(String username, String password, String userType) throws FileNotFoundException, ClassNotFoundException, UserAlreadyExistsException {
        for (User user : getItems())
            if (user.getUsername().equals(username))
                throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
        switch (userType) {
            case "Admin":
                return update(new Admin(username, password)).getId();
            case "Trader":
            default:
                return update(new Trader(username, password)).getId();
        }
    }

    /**
     * Returns a user in the system with the given username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the user id
     * @throws UserNotFoundException  if the user was not found
     * @throws FileNotFoundException  if the specified file path could not be found
     * @throws ClassNotFoundException if there is a class that is not defined
     */
    public String login(String username, String password) throws UserNotFoundException, FileNotFoundException, ClassNotFoundException {
        LinkedList<User> users = getItems();
        for (User user : users) {
            if (user.getUsername().equals(username) && (user.getPassword().equals(password))) {
                return user.getId();
            }
        }
        throw new UserNotFoundException("Bad credentials.");
    }

    /**
     * Freezes the user
     *
     * @param userId       the user being mutated
     * @param frozenStatus whether the user is frozen or not
     * @throws UserNotFoundException  if the user id is bad
     * @throws ClassNotFoundException if there is a class that is not defined
     */
    public void freezeUser(String userId, boolean frozenStatus) throws UserNotFoundException, ClassNotFoundException {
        try {
            User user = populate(userId);
            user.setFrozen(frozenStatus);
            update(user);
        } catch (EntryNotFoundException e) {
            throw new UserNotFoundException("Could not find user " + userId);
        }
    }

    /**
     * Deletes a user with the given username.
     *
     * @param userId the user id
     * @throws UserNotFoundException  if the user was not found
     * @throws FileNotFoundException  if the specified file path could not be found
     * @throws ClassNotFoundException if there is a class that is not defined
     */
    public void deleteUser(String userId) throws FileNotFoundException, ClassNotFoundException, UserNotFoundException {
        LinkedList<User> users = getItems();
        try {
            users.remove(populate(userId));
        } catch (EntryNotFoundException e) {
            throw new UserNotFoundException((userId + " not found."));
        }
        save(users);
    }
}
