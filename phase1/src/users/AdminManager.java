package users;

import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminManager extends UserManager implements Serializable {
    /**
     * Constructor for AdminManager
     * @param filePath the path of the users.ser file
     * @throws IOException
     */
    public AdminManager(String filePath) throws IOException {
        super(filePath);
    }
    
    /**
     * Registers a new Admin Account
     * 
     * @param username username of the new admin
     * @param password password of the new admin
     * @throws UserAlreadyExistsException
     */
    public String registerUser(String username, String password) throws UserAlreadyExistsException {
        if (isUsernameUnique(username)) return update(new Admin(username, password)).getId();
        throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
    }



}
