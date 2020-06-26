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
     * Takes in username and password, checks if the username exists
     * if it exists already, return false
     * else, create new User.
     *
     * @param username the new user's username
     * @param password the new user's password
     * @param userType the specific type of User to be added
     * @return the new user that was added
     * @throws FileNotFoundException      if the specified file path was not found
     * @throws ClassNotFoundException     if there is a class that is not defined
     * @throws UserAlreadyExistsException if a user with the same username exists
     */
    public User registerUser(String username, String password, String userType) throws FileNotFoundException, ClassNotFoundException, UserAlreadyExistsException {
        for (User user : getItems())
            if (user.getUsername().equals(username))
                throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
        User newUser;
        switch (userType) {
            case "Admin":
                return update(new Admin(username, password));
            case "Trader":
            default:
                return update(new Trader(username, password));
        }
    }


    /**
     * Returns a user in the system with the given username and password.
     * Throws an error if the user is not found.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the user if the user was found.
     * @throws UserNotFoundException  if the user was not found
     * @throws FileNotFoundException  if the specified file path could not be found
     * @throws ClassNotFoundException if there is a class that is not defined
     */
    public User login(String username, String password) throws UserNotFoundException, FileNotFoundException, ClassNotFoundException {
        return findUser(username, password, false);
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
     * Returns a user in the system with the given username.
     * Throws an error if the user is not found.
     *
     * @param username the username of the user
     * @return the user if the user was found.
     * @throws UserNotFoundException  if the user was not found
     * @throws FileNotFoundException  if the specified file path could not be found
     * @throws ClassNotFoundException if there is a class that is not defined
     */
    public User find(String username) throws UserNotFoundException, FileNotFoundException, ClassNotFoundException {
        return findUser(username, "", true);
    }

    /**
     * Deletes a user with the given username.
     *
     * @param username the username of the user to be deleted
     * @throws UserNotFoundException  if the user was not found
     * @throws FileNotFoundException  if the specified file path could not be found
     * @throws ClassNotFoundException if there is a class that is not defined
     */
    public void deleteUser(String username) throws UserNotFoundException, FileNotFoundException, ClassNotFoundException {
        User deleteUser = find(username);
        LinkedList<User> users = getItems();
        users.remove(deleteUser);
        save(users);
    }

    /**
     * Helper function to return a user in the system with a specific username (and password if desired).
     * Throws an error if the user is not found.
     *
     * @param username   the username of the user
     * @param password   the password of the user
     * @param ignorePass whether to consider the password when finding a user
     * @return the user if the user was found.
     * @throws UserNotFoundException  if the user was not found
     * @throws FileNotFoundException  if the specified file path could not be found
     * @throws ClassNotFoundException if there is a class that is not defined
     */
    private User findUser(String username, String password, boolean ignorePass) throws UserNotFoundException, FileNotFoundException, ClassNotFoundException {
        LinkedList<User> users = getItems();
        for (User user : users) {
            if (user.getUsername().equals(username) && (user.getPassword().equals(password) || ignorePass)) {
                return user;
            }
        }
        throw new UserNotFoundException("Could not find user in the system.");
    }


}
