package backend.tradesystem.managers;


import backend.DatabaseFilePaths;
import backend.exceptions.*;
import backend.models.Trade;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.TraderProperties;
import backend.tradesystem.UserTypes;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Used for logging in and registering, as well as any setup that has be done
 */
public class LoginManager extends Manager{

    private final String TRADER_PROPERTY_FILE_PATH;

    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public LoginManager() throws IOException {
        super();
        TRADER_PROPERTY_FILE_PATH = DatabaseFilePaths.TRADER_CONFIG.getFilePath();
    }
    /**
     * Making the database objects with set file paths
     * @param userFilePath the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath the trade database file path
     * @param traderPropertyFilePath the path for the trader properties file
     * @throws IOException issues with getting the file path
     */
    public LoginManager(String userFilePath, String tradableItemFilePath, String tradeFilePath, String traderPropertyFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath);
        this.TRADER_PROPERTY_FILE_PATH = traderPropertyFilePath;
    }

    /**
     * Registers a user
     *
     * @param username username of new user
     * @param password password of new user
     * @param type     the type of user
     * @return The ID of the newly created user
     * @throws UserAlreadyExistsException username is not unique
     * @throws BadPasswordException password isn't valid
     */
    public User registerUser(String username, String password, UserTypes type) throws UserAlreadyExistsException, BadPasswordException {
        // Get current default limits
        int defaultTradeLimit = getProperty(TraderProperties.TRADE_LIMIT);
        int defaultIncompleteTradeLim = getProperty(TraderProperties.INCOMPLETE_TRADE_LIM);
        int defaultMinimumAmountNeededToBorrow = getProperty(TraderProperties.MINIMUM_AMOUNT_NEEDED_TO_BORROW);

        validatePassword(password);


        if (!isUsernameUnique(username))
            throw new UserAlreadyExistsException();
        switch (type) {
            case ADMIN:
                return updateUserDatabase(new Admin(username, password));
            case TRADER:
                tryToRefreshTradeCount();
            default:
                return updateUserDatabase(new Trader(username, password, "", defaultTradeLimit, defaultIncompleteTradeLim,
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
    public User login(String username, String password) throws UserNotFoundException {
        ArrayList<User> users = getUserDatabase().getItems();
        for (User user : users)
            if (user.getUsername().equals(username) && (user.getPassword().equals(password))) {
                if (user instanceof Trader){
                    tryToRefreshTradeCount();
                    removeInvalidRequests(user.getId());
                }
                return user;
            }
        throw new UserNotFoundException();
    }

    /**
     * Get the type of user
     * @param userId the user id
     * @return the user type of the user
     * @throws UserNotFoundException if the user id wasn't found
     */
    public UserTypes getType(String userId) throws UserNotFoundException {
        User user = getUser(userId);
        if (user instanceof Admin)
            return UserTypes.ADMIN;
        else{
            return UserTypes.TRADER;
        }

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
     * Change the username of an existing user
     * @param userId the existing user
     * @param username the new username
     * @return the new User
     * @throws UserAlreadyExistsException if the username is taken
     * @throws UserNotFoundException the userId wasn't found
     */
    public User changeUsername(String userId, String username) throws UserAlreadyExistsException, UserNotFoundException {
        if (!isUsernameUnique(username)) {
            throw new UserAlreadyExistsException();
        }
        User user = getUser(userId);
        user.setUsername(username);
        updateUserDatabase(user);
        return user;
    }

    /**
     * Change password of the user
     * @param userId the user
     * @param password the new password
     * @return the new user
     * @throws BadPasswordException if the password isn't valid
     * @throws UserNotFoundException if the user wasn't found
     */
    public User changePassword(String userId, String password) throws BadPasswordException, UserNotFoundException {
        validatePassword(password);
        User user = getUser(userId);
        user.setPassword(password);
        updateUserDatabase(user);
        return user;
    }

    /**
     * Checks if password is valid
     * @param password must have no white space, length greater than 11, has a capital letter, has a number
     * @throws BadPasswordException if the password is not valid
     */
    private void validatePassword(String password) throws BadPasswordException{
        if (password.contains(" ")) throw new BadPasswordException("No white space allowed");
        if (password.length() < 11) throw new BadPasswordException("Length of password must be at least 12");
        if (password.toLowerCase().equals(password)) throw new BadPasswordException("Must have at least one capital letter");
        if (!password.matches(".*[0-9]+.*")) throw new BadPasswordException("Must contain at least one number");
    }

    /**
     *Tries to refresh the trade count of all traders (this only happens every week).
     */
    private void tryToRefreshTradeCount(){
        Date date = new Date();

        // Gets the current time in weeks since 1970.
        int currTime = (int)(date.getTime()/(1000 * 60 * 60 * 24 * 7));

        // Gets the last time (in weeks) the trade count of every user has been updated
        int lastTime = getProperty(TraderProperties.LAST_TRADE_COUNT_UPDATE);

        // If the time is different (i.e. one week has passed)...
        if(lastTime - currTime != 0) {
            // Refresh the trade count
            refreshTradeCount();

            // Set the new date that the trade count was updated to currTime.
            setProperty(TraderProperties.LAST_TRADE_COUNT_UPDATE, currTime);
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
            File propertyFile = new File(TRADER_PROPERTY_FILE_PATH);
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
            File propertyFile = new File(TRADER_PROPERTY_FILE_PATH);
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
     * Refreshes the trade count of all the traders (sets the trade count to 0)
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

    /**
     * Removes all invalid trade requests that this user has
     * @param traderID the id of the trader
     */
    private void removeInvalidRequests(String traderID){
        try {
            Trader someTrader = getTrader(traderID);
            for (String tradeID : someTrader.getRequestedTrades()) {
                // Populate required variables.
                Trade t = getTrade(tradeID);
                Trader firstTrader = getTrader(t.getFirstUserId());
                Trader secondTrader = getTrader(t.getSecondUserId());

                // Figure out whether the trade is still valid.
                boolean isValid = (t.getFirstUserOffer().equals("") || firstTrader.getAvailableItems().contains(t.getFirstUserOffer())) &&
                        (t.getSecondUserOffer().equals("") || secondTrader.getAvailableItems().contains(t.getSecondUserOffer()));

                if (!isValid){
                    firstTrader.getRequestedTrades().remove(tradeID);
                    secondTrader.getRequestedTrades().remove(tradeID);
                    getTradeDatabase().delete(tradeID);
                    getUserDatabase().update(firstTrader);
                    getUserDatabase().update(secondTrader);
                }
            }
        }
        catch (EntryNotFoundException | AuthorizationException e) {
            e.printStackTrace();
        }
    }

}
