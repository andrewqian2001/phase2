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

    private Trader[] traders;
    private Admin[] admins;
    private LoginManager loginManager;
    private TraderManager traderManager;
    private HandleItemRequestsManager handleRequestsManager;

    public TemporarySetup() {
        traders = new Trader[10];
        admins = new Admin[5];
        refreshFiles();
        try {
            loginManager = new LoginManager();
            traderManager = new TraderManager();
            handleRequestsManager = new HandleItemRequestsManager();
            for (int i = 0; i < traders.length; i++){
                traders[i] = (Trader) loginManager.registerUser("trader" + i, "passssssssS11", UserTypes.TRADER);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "apple" + i, "sweet" + i);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "banananana" + i, "disgusting" + i);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "kiwi" + i, "from oceania" + i);
                traders[i] = handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
                traders[i] = handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
                traders[i] = handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "requested" + i, "requested desc" + i);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "another requested" + i, "bad desc requested" + i);
            }
            for (int i = 0; i < admins.length; i++){
                admins[i] = (Admin) loginManager.registerUser("admin" + i, "PasswordPassword1!", UserTypes.ADMIN);
            }
            for (int i = 0; i < traders.length; i++)
                traderManager.addToWishList(traders[i].getId(), traders[i - 1 == -1 ? traders.length - 1 : i].getAvailableItems().get(0));
        } catch (IOException | UserAlreadyExistsException | BadPasswordException | UserNotFoundException | AuthorizationException | TradableItemNotFoundException e) {

        }
    }


    private void refreshFiles() {
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

    private void update() {
        try {
            for (int i = 0; i < traders.length; i++)
                traders[i] = traderManager.getTrader(traders[i].getId());
            for (int i = 0; i < admins.length; i++)
                admins[i] = (Admin) traderManager.getUser(admins[i].getId());

        } catch (UserNotFoundException | AuthorizationException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sets the value of a property.
     *
     * @param propertyName  the property to change
     * @param propertyValue the new value of that property
     */
    private void setProperty(TraderProperties propertyName, int propertyValue) {
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
