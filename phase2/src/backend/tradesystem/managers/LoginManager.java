package backend.tradesystem.managers;


import backend.DatabaseFilePaths;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.TraderProperties;
import backend.tradesystem.UserTypes;
import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Used for logging in and registering, as well as any setup that has be done
 */
public class LoginManager extends Manager{

    private UserTypes lastLoggedInType = null;

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public LoginManager() throws IOException {
        super();
    }

    /**
     * Registers a user
     *
     * @param username username of new user
     * @param password password of new user
     * @param type     the type of user
     * @return The ID of the newly created user
     * @throws UserAlreadyExistsException username is not unique
     */
    public User registerUser(String username, String password, UserTypes type) throws UserAlreadyExistsException {

        int defaultTradeLimit = getProperty(TraderProperties.TRADE_LIMIT);
        int defaultIncompleteTradeLim = getProperty(TraderProperties.INCOMPLETE_TRADE_LIM);
        int defaultMinimumAmountNeededToBorrow = getProperty(TraderProperties.MINIMUM_AMOUNT_NEEDED_TO_BORROW);

        if (!isUsernameUnique(username))
            throw new UserAlreadyExistsException();
        switch (type) {
            case ADMIN:
                lastLoggedInType = UserTypes.ADMIN;
                return updateUserDatabase(new Admin(username, password));
            case TRADER:
                tryToRefreshTradeCount();
            default:
                lastLoggedInType = UserTypes.TRADER;
                return updateUserDatabase(new Trader(username, password, defaultTradeLimit, defaultIncompleteTradeLim,
                        defaultMinimumAmountNeededToBorrow));
        }

    }

    /**
     * Ensuring user credentials is correct and returns the user id
     *
     * @param username username of user
     * @param password password of user
     * @return the user id of the logged in user
     * @throws UserNotFoundException could not find the user
     */
    public String login(String username, String password) throws UserNotFoundException {
        ArrayList<User> users = getUserDatabase().getItems();
        for (User user : users)
            if (user.getUsername().equals(username) && (user.getPassword().equals(password))) {
                if (user instanceof Admin)
                    lastLoggedInType = UserTypes.ADMIN;
                else{
                    tryToRefreshTradeCount();
                    lastLoggedInType = UserTypes.TRADER;
                }
                return user.getId();
            }
        throw new UserNotFoundException();
    }

    /**
     * The last account that logged in
     *
     * @return last account type that logged in
     */
    public UserTypes getLastLoggedInType() {
        return lastLoggedInType;
    }

    /**
     * Checks if the username exists in the database file
     *
     * @param username username to check for
     * @return if username is unique
     */
    public boolean isUsernameUnique(String username) {
        for (User user : getUserDatabase().getItems())
            if (user.getUsername().equals(username))
                return false;
        return true;
    }


    /**
     *Tries to refresh the trade count of all traders (this only happens every week).
     */
    private void tryToRefreshTradeCount(){
        Date date = new Date();
        int curr_time = (int)(date.getTime()/(1000 * 60 * 60 * 24 * 7));
        int last_time = getProperty(TraderProperties.LAST_TRADE_COUNT_UPDATE);
        if(last_time - curr_time != 0) {
            refreshTradeCount();
            setProperty(TraderProperties.LAST_TRADE_COUNT_UPDATE, curr_time);
        }
    }

    /**
     * Gets the current value of the specified trader property
     * @param propertyType the type of property
     * @return the value of the specified trader property
     */
    private int getProperty(TraderProperties propertyType){
        try {
            // get the file
            File propertyFile = new File(DatabaseFilePaths.TRADER_CONFIG.getFilePath());
            // initialize the reader of this file
            FileReader reader = new FileReader(propertyFile);
            // initialize properties object
            Properties properties = new Properties();
            // associate properties object with this file.
            properties.load(reader);
            // we're not going to use reader anymore, so close it
            reader.close();
            // return the integer value of that property
            return Integer.parseInt(properties.getProperty(propertyType.getProperty()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Sets the value of a property.
     * @param propertyName the property to change
     * @param propertyValue the new value of that property
     */
    private void setProperty(TraderProperties propertyName, int propertyValue){
        try {
            // get the file
            File propertyFile = new File(DatabaseFilePaths.TRADER_CONFIG.getFilePath());
            // initialize reader
            FileReader reader = new FileReader(propertyFile);
            // initialize properties object (to set data)
            Properties properties = new Properties();
            // associate this properties object with the file
            properties.load(reader);
            // set the property
            properties.setProperty(propertyName.getProperty(), "" + propertyValue);

            //update the file
            FileWriter writer = new FileWriter(propertyFile);
            properties.store(writer, "");
            reader.close();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Refreshes the trade count of all the traders
     */
    private void refreshTradeCount(){
        ArrayList<User> users = getUserDatabase().getItems();
        for(int i = 0; i < users.size(); i++){
            User user = users.get(i);
            if (user instanceof Trader){
                ((Trader)user).setTradeCount(0);
                users.set(i, user);
            }
        }

        try {
            getUserDatabase().save(users);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
