package users;

import exceptions.UserAlreadyExistsException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AdminManager extends UserManager {
    public AdminManager(String filePath) throws IOException {
        super(filePath);
    }
    @Override
    public String registerUser(String username, String password) throws UserAlreadyExistsException, FileNotFoundException, ClassNotFoundException {
        if (isUsernameUnique(username)) return update(new Admin(username, password)).getId();
        throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
    }
}
