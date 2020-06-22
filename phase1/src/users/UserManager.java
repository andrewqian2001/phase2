package users;

import exceptions.UserNotFoundException;
import main.Manager;

import java.io.IOException;
import java.io.Serializable;

public class UserManager extends Manager<User> implements Serializable {

    public UserManager(String filePath) throws IOException {
        super(filePath);
    }

    //Takes in username and password, checks if the username exists
    //  if it exists already, return false
    //  else, create new User,
    public boolean registerUser(String username, String password, boolean isAdmin) {
        return false;
    }

    //Takes in username and password, return user, if user doesn't exist, throw UserNotFoundException
    public User login(String username, String password) throws UserNotFoundException {
        return null;
    }
}
