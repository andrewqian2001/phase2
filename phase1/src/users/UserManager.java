package users;
import main.Manager;
import exceptions.UserNotFoundException;
import trades.Trade;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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

    //
    /**
     * Takes in username and password, checks if the username exists
     * if it exists already, return false
     * else, create new User.
     * @param username the new user's username
     * @param password the new user's password
     * @param isAdmin whether or not the new user is an admin
     * @return true if the user was added, false otherwise
     * @throws FileNotFoundException if the specified file path was not found
     * @throws ClassNotFoundException if there is a class that is not defined
     */
    public boolean registerUser(String username, String password, boolean isAdmin) throws FileNotFoundException, ClassNotFoundException {
        LinkedList<User> users = getItems();
        for (User user : users){
            if (user.getUsername().equals(username)){
                return false;
            }
        }
        if (isAdmin)
            users.add(new Admin(username, password));
        else
            users.add(new Trader(username, password));
        save(users);
        return true;
    }

    //Takes in username and password, return user, if user doesn't exist, throw UserNotFoundException
    public User login(String username, String password) throws UserNotFoundException {
        return null;
    }
}
