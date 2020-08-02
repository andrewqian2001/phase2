package frontend;

import backend.DatabaseFilePaths;
import backend.exceptions.*;
import backend.tradesystem.TraderProperties;
import backend.tradesystem.UserTypes;
import backend.tradesystem.admin_managers.HandleFrozenManager;
import backend.tradesystem.admin_managers.HandleItemRequestsManager;
import backend.tradesystem.general_managers.LoginManager;
import backend.tradesystem.general_managers.MessageManager;
import backend.tradesystem.general_managers.ReportManager;
import backend.tradesystem.queries.UserQuery;
import backend.tradesystem.trader_managers.TraderManager;
import backend.tradesystem.trader_managers.TradingManager;


import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * This class is not used in production and is only used to have an example interface full of users
 */
public class TemporarySetup {

    /**
     * Used to set up users
     */
    public TemporarySetup() {
        refreshFiles(); // Deletes existing data in the ser files
        try {
            String[] traders = new String[10];
            String[] admins = new String[5];
            LoginManager loginManager = new LoginManager();
            TraderManager traderManager = new TraderManager();
            HandleItemRequestsManager handleRequestsManager = new HandleItemRequestsManager();
            HandleFrozenManager handleFrozenManager = new HandleFrozenManager();
            MessageManager messageManager = new MessageManager();
            TradingManager tradingManager = new TradingManager();
            ReportManager reportManager = new ReportManager();
            UserQuery userQuery = new UserQuery();
            // Each trader has some items that are confirmed and not confirmed
            // Username is trader{index here from 0 to 9 inclusive}
            // Password is 'userPassword1'
            loginManager.registerUser("demo", "userPassword1", UserTypes.TRADER);
            for (int i = 0; i < traders.length; i++) {
                traders[i] = loginManager.registerUser("trader" + i, "userPassword1", UserTypes.TRADER);
                traderManager.addRequestItem(traders[i], "apple" + i, "sweet" + i);
                traderManager.addRequestItem(traders[i], "banananana" + i, "disgusting" + i);
                traderManager.addRequestItem(traders[i], "kiwi" + i, "from oceania" + i);
                handleRequestsManager.processItemRequest(traders[i], userQuery.getRequestedItems(traders[i]).get(0), true);
                handleRequestsManager.processItemRequest(traders[i], userQuery.getRequestedItems(traders[i]).get(0), true);
                handleRequestsManager.processItemRequest(traders[i], userQuery.getRequestedItems(traders[i]).get(0), true);
                traderManager.addRequestItem(traders[i], "requested" + i, "requested desc" + i);
                traderManager.addRequestItem(traders[i], "another requested" + i, "bad desc requested" + i);
                traderManager.setCity(traders[i], "Toronto");
            }
            Date goodDate = new Date(System.currentTimeMillis() + 99999999);
            Date goodDate2 = new Date(System.currentTimeMillis() + 999999999);
            // Trades
            for (int i = 1; i < traders.length / 2; i++) {
                try {
                    String acceptThis = tradingManager.requestTrade(traders[i], traders[traders.length - 1 - i], goodDate, goodDate2,
                            "123 bay street", userQuery.getAvailableItems(traders[i]).get(0), userQuery.getAvailableItems(traders[traders.length - 1 - i]).get(0),
                            3, "give me your apple " + i); // This is a temp trade
                    String ongoing = tradingManager.requestTrade(traders[i], traders[traders.length - 1 - i], goodDate, null,
                            "123 bay street", userQuery.getAvailableItems(traders[i]).get(1), userQuery.getAvailableItems(traders[traders.length - 1 - i]).get(1),
                            3, "give me your banana " + i); // This is a perma trade
                    String requestedOnly = tradingManager.requestTrade(traders[i], traders[traders.length - 1 - i], goodDate, goodDate2,
                            "123 bay street", userQuery.getAvailableItems(traders[i]).get(2), userQuery.getAvailableItems(traders[traders.length - 1 - i]).get(2),
                            3, "I give you my kiwi " + i); // this is for requesting temp trade
                    // Only accepts request and doesn't confirm meetings so trade is ongoing
                    tradingManager.acceptRequest(traders[traders.length - 1 - i], ongoing);
                    // Confirms four meetings for a temporary trade and accepts request, meaning the trade is complete
                    tradingManager.acceptRequest(traders[traders.length - 1 - i], acceptThis);
                    tradingManager.confirmMeetingGeneral(traders[i], acceptThis, true);
                    tradingManager.confirmMeetingGeneral(traders[traders.length - 1 - i], acceptThis, true);
                    tradingManager.confirmMeetingGeneral(traders[i], acceptThis, true);
                    tradingManager.confirmMeetingGeneral(traders[traders.length - 1 - i], acceptThis, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            // Each trader has a wishlist of one item
            for (int i = 0; i < traders.length; i++)
                traderManager.addToWishList(traders[i], userQuery.getAvailableItems(traders[i - 1 == -1 ? traders.length - 1 : i - 1]).get(0));
            // For changing cities
            traders[3] = traderManager.setCity(traders[3], "new york");
            traders[4] = traderManager.setCity(traders[4], "new york");
            traders[5] = traderManager.setCity(traders[5], "new york");
            traders[6] = traderManager.setCity(traders[6], "dallas");
            traders[7] = traderManager.setCity(traders[7], "dallas");
            // For changing idle status
            traders[0] = traderManager.setIdle(traders[0], true);
            // For adding reviews
            traderManager.addReview(traders[0], traders[3], 5.3, "This guy was rude");
            traderManager.addReview(traders[2], traders[3], 2.3, "This guy attacked me");
            traderManager.addReview(traders[1], traders[4], 9.3, "This guy gave me free money");
            // For setting frozen status
            handleFrozenManager.setFrozen(traders[8], true);
            // For reporting users
            reportManager.reportUser(traders[3], traders[6], "This user drove off with my lambo and never gave me what I wanted");
            reportManager.reportUser(traders[1], traders[6], "This user flew away with my helicopter and never gave me what I wanted");
            // For messaging users
            messageManager.sendMessage(traders[5], traders[7], "Dallas is pretty far can you come to New York instead");
            messageManager.sendMessage(traders[5], traders[7], "Ik its a lot to ask but like yeehaw");
            messageManager.sendMessage(traders[5], traders[7], "Dplease i got covid19 come to new yorkkk");
            messageManager.sendMessage(traders[5], traders[7], "uk what fine, i never liked you anyway");
            messageManager.sendMessage(traders[0], traders[1], "Can I buy your Ryerson hat for my pokemon cards");
            // List of admins
            for (int i = 0; i < admins.length; i++) {
                admins[i] = loginManager.registerUser("admin" + i, "userPassword1", UserTypes.ADMIN);
            }
        } catch (IOException | UserAlreadyExistsException | BadPasswordException | UserNotFoundException | AuthorizationException | TradableItemNotFoundException e) {
            System.out.println("Temporary set up failed");
            e.printStackTrace();
        }
    }

    // Deletes info in the ser files to reset it
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
