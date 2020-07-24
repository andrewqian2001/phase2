package frontend;

import backend.DatabaseFilePaths;
import backend.exceptions.*;
import backend.models.TradableItem;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.TraderProperties;
import backend.tradesystem.UserTypes;
import backend.tradesystem.managers.HandleItemRequestsManager;
import backend.tradesystem.managers.LoginManager;
import backend.tradesystem.managers.TraderManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class TemporarySetup {

    private Trader trader1;
    private Trader trader2;
    private Admin admin;
    private LoginManager loginManager;
    private TraderManager traderManager;
    private HandleItemRequestsManager handleRequestsManager;
    public TemporarySetup(){
        refreshFiles();
        try {
            loginManager = new LoginManager();
            traderManager = new TraderManager();
            handleRequestsManager = new HandleItemRequestsManager();
            trader1 = (Trader)loginManager.registerUser("user", "passssssssS11", UserTypes.TRADER);
            trader2 = (Trader)loginManager.registerUser("user1", "passssssssS11", UserTypes.TRADER);
            admin = (Admin) loginManager.registerUser("admin", "passssssssS11", UserTypes.ADMIN);
            traderManager.addRequestItem(trader1.getId(), "apple", "sweet");
            traderManager.addRequestItem(trader1.getId(), "apple1", "sweet1");
            traderManager.addRequestItem(trader1.getId(), "apple2", "sweet2");
            traderManager.addRequestItem(trader2.getId(), "pear1", "disgusting");
            traderManager.addRequestItem(trader2.getId(), "pear2", "disgusting2");
            traderManager.addRequestItem(trader2.getId(), "pear3", "disgusting3");


            update();
            handleRequestsManager.processItemRequest(trader1.getId(), trader1.getRequestedItems().get(0), true);
            handleRequestsManager.processItemRequest(trader1.getId(), trader1.getRequestedItems().get(0), true);
            handleRequestsManager.processItemRequest(trader1.getId(), trader1.getRequestedItems().get(0), true);
            handleRequestsManager.processItemRequest(trader2.getId(), trader2.getRequestedItems().get(0), true);
            handleRequestsManager.processItemRequest(trader2.getId(), trader2.getRequestedItems().get(0), true);
            handleRequestsManager.processItemRequest(trader2.getId(), trader2.getRequestedItems().get(0), true);

            update();
            traderManager.addToWishList(trader1.getId(), trader2.getAvailableItems().get(0));
            traderManager.addToWishList(trader1.getId(), trader2.getAvailableItems().get(1));
            traderManager.addToWishList(trader2.getId(), trader1.getAvailableItems().get(0));
            traderManager.addToWishList(trader2.getId(), trader1.getAvailableItems().get(1));



        }catch (IOException | UserAlreadyExistsException | BadPasswordException | UserNotFoundException | AuthorizationException | TradableItemNotFoundException e){

        }
    }


    private void refreshFiles(){
        String[] paths = {DatabaseFilePaths.TRADE.getFilePath(), DatabaseFilePaths.TRADABLE_ITEM.getFilePath(),
                DatabaseFilePaths.USER.getFilePath()};
        for (String path : paths) {
            try {
                OutputStream buffer = new BufferedOutputStream(new FileOutputStream(path));
                ObjectOutput output = new ObjectOutputStream(buffer);
                output.writeObject(new ArrayList<>());
                output.close();
            } catch (IOException ignored) {
            }
        }
        setProperty(TraderProperties.INCOMPLETE_TRADE_LIM, 3);
        setProperty(TraderProperties.MINIMUM_AMOUNT_NEEDED_TO_BORROW, 1);
        setProperty(TraderProperties.TRADE_LIMIT, 10);
    }
    private void update(){
        try {
            trader1 = traderManager.getTrader(trader1.getId());
            trader2 = traderManager.getTrader(trader2.getId());
            admin = (Admin) traderManager.getUser(admin.getId());

        } catch (UserNotFoundException | AuthorizationException e) {
            e.printStackTrace();
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

}
