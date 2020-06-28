package users;

import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class AdminManager extends UserManager implements Serializable {
    public AdminManager(String filePath) throws IOException {
        super(filePath);
    }

    public String registerUser(String username, String password) throws UserAlreadyExistsException {
        if (isUsernameUnique(username)) return update(new Admin(username, password)).getId();
        throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
    }

    /**
     *
     * @return All unfreeze requests
     * @throws EntryNotFoundException
     */
    public ArrayList<String> getAllUnFreezeRequests() throws EntryNotFoundException{
        ArrayList<String> allUsers = getAllUsers();
        ArrayList<String> allUnFrozenList = new ArrayList<>();
        for(String userID : allUsers) {
            if(populate(userID).isUnfrozenRequested()) allUnFrozenList.add(userID);
        }
        return allUsers;
    }



}
