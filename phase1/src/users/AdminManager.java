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
     * Gets all Item requests for each user
     * @return a Map corresponding to a userId with a list of their item requests
     */
    public HashMap<String, ArrayList<String>> getAllItemRequests()  {
        HashMap<String, ArrayList<String>> itemRequests = new HashMap<>();
        for(User user : getItems()) {
            if(user instanceof Trader) {
                itemRequests.put(user.getId(), ((Trader) user).getRequestedItems());
            }
        }
        return itemRequests;
    }
    /**
     * Return the list of requested items for this user
     *
     * @param userId the id of the user
     * @return the list of requested items for this user
     * @throws EntryNotFoundException if the user was not found
     */
    public ArrayList<String> getRequestedItems(String userId) throws EntryNotFoundException {
        return findTraderById(userId).getRequestedItems();
    }

    /**
     * Accepts one of this user's requested items (allows it to be used in trades)
     *
     * @param userId the id of the user
     * @param itemId the id of the item
     * @return the user's id
     * @throws EntryNotFoundException if the itemId or the userId could not be found
     */
    public String acceptRequestItem(String userId, String itemId) throws EntryNotFoundException {
        Trader trader = findTraderById(userId);
        if (!trader.getRequestedItems().remove(itemId)) {
            throw new EntryNotFoundException("Could not find item " + itemId);
        }
        trader.getAvailableItems().add(itemId);

        update(trader);
        return userId;
    }

    /**
     * Rejects the user'r requested item
     * @param userId id of the user
     * @param itemId id of the item
     * @return the user's id
     * @throws EntryNotFoundException
     */
    public String rejectRequestItem(String userId, String itemId) throws EntryNotFoundException {
        Trader trader = findTraderById(userId);
        boolean removeReqItem = trader.getRequestedItems().remove(itemId);
        if (!removeReqItem) {
            throw new EntryNotFoundException("Could not find item " + itemId);
        }
        update(trader);
        return userId;
    }
    /**
     * Helper function to find a trader by id
     *
     * @param userId the id of the trader to find
     * @return the trader that was found
     * @throws EntryNotFoundException if a trader with the given userId was not
     *                                found
     */
    private Trader findTraderById(String userId) throws EntryNotFoundException {
        User user = findUserById(userId);
        if (user instanceof Trader) {
            return (Trader) user;
        }
        throw new EntryNotFoundException("Could not find " + userId + " + in the system.");
    }

}
