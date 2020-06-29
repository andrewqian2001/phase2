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

    /**
     * Gets all Unfreeze Requests
     * @return a list of all traders who have requested their account to be unfrozen
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

    /**
     * Gets all Item requests for each user
     * @return a Map corresponding to a userId with a list of their item requests
     * @throws EntryNotFoundException
     */
    public HashMap<String, ArrayList<String>> getAllItemRequests() throws EntryNotFoundException {
        HashMap<String, ArrayList<String>> itemRequests = new HashMap<>();
        ArrayList<String> allUsers = getAllUsers();
        for(String userID : allUsers) {
            User user = populate(userID);
            if(user instanceof Trader) {
                itemRequests.put(userID, ((Trader) user).getRequestedItems());
            }
        }
        return itemRequests;
    }
}
