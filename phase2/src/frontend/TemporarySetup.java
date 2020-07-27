package frontend;

import backend.DatabaseFilePaths;
import backend.exceptions.*;
import backend.models.Trade;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.tradesystem.TraderProperties;
import backend.tradesystem.UserTypes;
import backend.tradesystem.managers.*;

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
            Trader[] traders = new Trader[10];
            Admin[] admins = new Admin[5];
            LoginManager loginManager = new LoginManager();
            TraderManager traderManager = new TraderManager();
            HandleItemRequestsManager handleRequestsManager = new HandleItemRequestsManager();
            HandleFrozenManager handleFrozenManager = new HandleFrozenManager();
            MessageManager messageManager = new MessageManager();
            TradingManager tradingManager = new TradingManager();
            // Each trader has some items that are confirmed and not confirmed
            // Username is trader{index here from 0 to 9 inclusive}
            // Password is 'userPassword1'
            for (int i = 0; i < traders.length; i++) {
                traders[i] = (Trader) loginManager.registerUser("trader" + i, "userPassword1", UserTypes.TRADER);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "apple" + i, "sweet" + i);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "banananana" + i, "disgusting" + i);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "kiwi" + i, "from oceania" + i);
                traders[i] = handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
                traders[i] = handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
                traders[i] = handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "requested" + i, "requested desc" + i);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "another requested" + i, "bad desc requested" + i);
                traders[i] = traderManager.setCity(traders[i].getId(), "Toronto");
            }
            Date goodDate = new Date(System.currentTimeMillis() + 99999999);
            Date goodDate2 = new Date(System.currentTimeMillis() + 999999999);
            // Trades
            for (int i = 1; i < traders.length / 2; i++){
                try {
                    Trade acceptThis = tradingManager.requestTrade(new Trade(traders[i].getId(), traders[traders.length - 1 - i].getId(), goodDate, goodDate2,
                            "123 bay street", traders[i].getAvailableItems().get(0), traders[traders.length - 1 - i].getAvailableItems().get(0),
                            3, "give me your apple " + i)); // This is a temp trade
                    Trade ongoing = tradingManager.requestTrade(new Trade(traders[i].getId(), traders[traders.length - 1 - i].getId(), goodDate, null,
                            "123 bay street", traders[i].getAvailableItems().get(1), traders[traders.length - 1 - i].getAvailableItems().get(1),
                            3, "give me your banana " + i)); // This is a perma trade
                    Trade requestedOnly = tradingManager.requestTrade(new Trade(traders[i].getId(), traders[traders.length - 1 - i].getId(), goodDate, goodDate2,
                            "123 bay street", traders[i].getAvailableItems().get(2), traders[traders.length - 1 - i].getAvailableItems().get(2),
                            3, "I give you my kiwi " + i)); // This is temporary lending
                    // Only accepts request and doesn't confirm meetings so trade is ongoing
                    tradingManager.acceptRequest(traders[traders.length - 1 - i].getId(), ongoing.getId());
                    // Confirms four meetings for a temporary trade and accepts request, meaning the trade is complete
                    tradingManager.acceptRequest(traders[traders.length - 1 - i].getId(), acceptThis.getId());
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), acceptThis.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[traders.length - 1 - i].getId(), acceptThis.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), acceptThis.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[traders.length - 1 - i].getId(), acceptThis.getId(), true);
                }
                catch(Exception e){
                    e.printStackTrace();
                }

            }
            // Each trader has a wishlist of one item
            for (int i = 0; i < traders.length; i++)
                traders[i] = traderManager.addToWishList(traders[i].getId(), traders[i - 1 == -1 ? traders.length - 1 : i - 1].getAvailableItems().get(0));
            // For changing cities
            traders[3] = traderManager.setCity(traders[3].getId(), "new york");
            traders[4] = traderManager.setCity(traders[4].getId(), "new york");
            traders[5] = traderManager.setCity(traders[5].getId(), "new york");
            traders[6] = traderManager.setCity(traders[6].getId(), "dallas");
            traders[7] = traderManager.setCity(traders[7].getId(), "dallas");
            // For changing idle status
            traders[0] = traderManager.setIdle(traders[0].getId(), true);
            // For adding reviews
            traderManager.addReview(traders[0].getId(), traders[3].getId(), 5.3, "This guy was rude");
            traderManager.addReview(traders[2].getId(), traders[3].getId(), 2.3, "This guy attacked me");
            traderManager.addReview(traders[1].getId(), traders[4].getId(), 9.3, "This guy gave me free money");
            // For setting frozen status
            handleFrozenManager.setFrozen(traders[8].getId(), true);
            // For reporting users
            messageManager.reportUser(traders[3].getId(), traders[6].getId(), "This user drove off with my lambo and never gave me what I wanted");
            messageManager.reportUser(traders[1].getId(), traders[6].getId(), "This user flew away with my helicopter and never gave me what I wanted");
            // For messaging users
            messageManager.sendMessage(traders[5].getId(), traders[7].getId(), "Dallas is pretty far can you come to New York instead");
            messageManager.sendMessage(traders[0].getId(), traders[1].getId(), "Can I buy your Ryerson hat for my pokemon cards");
            // List of admins
            for (int i = 0; i < admins.length; i++) {
                admins[i] = (Admin) loginManager.registerUser("admin" + i, "userPassword1", UserTypes.ADMIN);
            }
        } catch (IOException | UserAlreadyExistsException | BadPasswordException | UserNotFoundException | AuthorizationException | TradableItemNotFoundException e) {
            System.out.println("Temporary set up failed");
            e.printStackTrace();
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
