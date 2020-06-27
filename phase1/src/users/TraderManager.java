package users;

import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

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

    @Override
    public String registerUser(String username, String password) throws UserAlreadyExistsException, FileNotFoundException, ClassNotFoundException {
        if (isUsernameUnique(username)) return update(new Trader(username, password)).getId();
        throw new UserAlreadyExistsException("A user with the username " + username + " exists already.");
    }


    public String acceptTrade(String userId, String tradeId) throws EntryNotFoundException, FileNotFoundException, ClassNotFoundException {
        Trader trader = findUserById(userId);
        if (trader.isFrozen()){

        }
        trader.requestedTrades.remove(tradeId);
        trader.acceptedTrades.add(tradeId);
        update(trader);
        return userId;
    }

    public String denyTrade(String userId, String tradeId) throws EntryNotFoundException, FileNotFoundException, ClassNotFoundException {
        Trader trader = findUserById(userId);
        trader.requestedTrades.remove(tradeId);
        update(trader);
        return userId;
    }

    public String addRequestItem(String userId, String itemId) throws EntryNotFoundException, FileNotFoundException, ClassNotFoundException {
        Trader trader = findUserById(userId);
        trader.requestedItems.add(itemId);
        update(trader);
        return userId;
    }

    public String acceptRequestItem(String userId, String itemId) throws EntryNotFoundException, FileNotFoundException, ClassNotFoundException {
        Trader trader = findUserById(userId);
        trader.availableItems.add(itemId);
        update(trader);
        return userId;
    }

    public String borrowItem(String user1, String user2, String itemId) throws FileNotFoundException, ClassNotFoundException, EntryNotFoundException {
        Trader trader1 = findUserById(user1);
        Trader trader2 = findUserById(user2);
        if (!trader2.availableItems.remove(itemId)) {
            throw new EntryNotFoundException("Item " + itemId + " not found");
        }
        trader1.availableItems.add(itemId);
        trader1.setTotalItemsBorrowed(trader1.getTotalItemsBorrowed() + 1);
        trader2.setTotalItemsLent(trader2.getTotalItemsLent() + 1);
        update(trader2);
        update(trader1);
        return user1;
    }

    public String lendItem(String user1, String user2, String itemId) throws FileNotFoundException, ClassNotFoundException, EntryNotFoundException {
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
    public String trade(String user1, String item1, String user2, String item2) throws FileNotFoundException, ClassNotFoundException, EntryNotFoundException {
        Trader trader1 = findUserById(user1);
        Trader trader2 = findUserById(user2);
        if (!trader1.availableItems.remove(item1)) {
            throw new EntryNotFoundException("Item " + item1 + " not found");
        }
        if (!trader2.availableItems.remove(item2)){
            trader1.availableItems.add(item1);
            throw new EntryNotFoundException("Item " + item2 + " not found");
        }
        trader1.availableItems.add(item2);
        trader2.availableItems.add(item1);
        update(trader1);
        update(trader2);
        return user1;
    }

    private Trader findUserById(String id) throws EntryNotFoundException, FileNotFoundException, ClassNotFoundException {
        User user = populate(id);
        if (user instanceof Trader){
            return (Trader) user;
        }
        throw new UserNotFoundException("Could not find user in the system.");
    }


    //
}
