package users;

import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class TraderManager extends UserManager {

    /**
     * For storing the file path of the .ser file
     *
     * @param filePath must take in .ser file
     * @throws IOException if creating a file causes issues
     */
    public TraderManager(String filePath) throws IOException {
        super(filePath);
    }

    public String registerUser(String username, String password) throws UserAlreadyExistsException {
        if (isUsernameUnique(username)) return update(new Trader(username, password)).getId();
        throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
    }


    public String acceptTrade(String user1, String tradeId) throws EntryNotFoundException{
        Trader trader1 = findUserById(user1);
        if (trader1.isFrozen()){

        }
        trader1.getRequestedTrades().remove(tradeId);
        trader1.getAcceptedTrades().add(tradeId);
        update(trader1);
        return user1;
    }

    public String denyTrade(String userId, String tradeId) throws EntryNotFoundException{
        Trader trader = findUserById(userId);
        trader.getRequestedTrades().remove(tradeId);
        update(trader);
        //other classes would need to modify the trade object (i.e. make sure to
        return userId;
    }

    public String addRequestItem(String userId, String itemId) throws EntryNotFoundException{
        Trader trader = findUserById(userId);
        trader.getRequestedItems().add(itemId);
        update(trader);
        return userId;
    }

    public ArrayList<String> getRequestedItems(String userId) throws EntryNotFoundException {
        return findUserById(userId).getRequestedItems();
    }

    public String acceptRequestItem(String userId, String itemId) throws EntryNotFoundException {
        Trader trader = findUserById(userId);
        if (!trader.getRequestedItems().remove(itemId)){
            throw new EntryNotFoundException("Could not find item " + itemId);
        }
        trader.getAvailableItems().add(itemId);

        update(trader);
        return userId;
    }

    public String borrowItem(String user1, String user2, String itemId) throws EntryNotFoundException {
        Trader trader1 = findUserById(user1);
        Trader trader2 = findUserById(user2);
        if (!trader2.getAvailableItems().remove(itemId)) {
            throw new EntryNotFoundException("Item " + itemId + " not found");
        }
        trader1.getAvailableItems().add(itemId);
        trader1.setTotalItemsBorrowed(trader1.getTotalItemsBorrowed() + 1);
        trader2.setTotalItemsLent(trader2.getTotalItemsLent() + 1);
        update(trader2);
        update(trader1);
        return user1;
    }

    public String lendItem(String user1, String user2, String itemId) throws  EntryNotFoundException {
        return borrowItem(user2, user1, itemId);
    }

    /**
     *
     * @param user1 the first user's id
     * @param item1 the id of the item first user is offering to trade
     * @param user2
     * @param item2
     * @return
     */
    public String trade(String user1, String item1, String user2, String item2) throws EntryNotFoundException {
        Trader trader1 = findUserById(user1);
        Trader trader2 = findUserById(user2);
        if (!trader1.getAvailableItems().remove(item1)) {
            throw new EntryNotFoundException("Item " + item1 + " not found");
        }
        if (!trader2.getAvailableItems().remove(item2)){
            trader1.getAvailableItems().add(item1);
            throw new EntryNotFoundException("Item " + item2 + " not found");
        }
        trader1.getAvailableItems().add(item2);
        trader2.getAvailableItems().add(item1);
        update(trader1);
        update(trader2);
        return user1;
    }

    private Trader findUserById(String id) throws EntryNotFoundException {
        User user = populate(id);
        if (user instanceof Trader){
            return (Trader) user;
        }
        throw new EntryNotFoundException("Could not find user in the system.");
    }


}
