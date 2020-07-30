import backend.exceptions.*;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.TraderProperties;
import backend.tradesystem.UserTypes;
import backend.tradesystem.managers.*;
import backend.Database;

import java.util.Date;

import org.junit.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class TestTradingInfo {
    private TraderManager traderManager;
    private LoginManager loginManager;
    private HandleItemRequestsManager handleRequestsManager;
    private TradingInfoManager tradingInfoManager;
    private TradingManager tradingManager;
    private Database<User> userDatabase;
    private Database<Trade> tradeDatabase;
    private Database<TradableItem> tradableItemDatabase;

    private Trader[] traders;

    //testing for automatedTrade
    private Trader tHasWishlist;
    private Trader tHasInventory1;
    private Trader tHasInventory2;
    private Trader tHasInventory3;
    private Trader tHasInventory4;
    private Trader tHasInventory5;


    private Admin admin;
    private final String USER_PATH = "./test/testUsers.ser";
    private final String TRADABLE_ITEM_PATH = "./test/testTradableItems.ser";
    private final String TRADE_PATH = "./test/testTrades.ser";
    private final String TRADER_PROPERTY_FILE_PATH = "./test/trader.properties";
    private Date goodDate = new Date(System.currentTimeMillis() + 99999999);
    private Date goodDate2 = new Date(System.currentTimeMillis() + 999999999);

    @Before
    public void beforeEach() {
        try {
            traderManager = new TraderManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            loginManager = new LoginManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH, TRADER_PROPERTY_FILE_PATH);
            tradingManager = new TradingManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            handleRequestsManager = new HandleItemRequestsManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            tradingInfoManager = new TradingInfoManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            userDatabase = new Database<>(USER_PATH);
            tradeDatabase = new Database<>(TRADE_PATH);
            tradableItemDatabase = new Database<>(TRADABLE_ITEM_PATH);
            traders = new Trader[10];

            for (int i = 0; i < traders.length; i++) {
                traders[i] = (Trader) loginManager.registerUser("user" + i, "passssssssS11", UserTypes.TRADER);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "apple" + i, "desc" + i);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "fruit" + i, "desc second" + i);
                traders[i] = traderManager.addRequestItem(traders[i].getId(), "another" + i, "desc third" + i);
                traders[i] = handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
                traders[i] = handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
                traders[i] = handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);

            }
            admin = (Admin) loginManager.registerUser("admin", "PASDASDFDSAFpadsf1", UserTypes.ADMIN);

            //Testing for automatedTradeSuggestion below

            tHasWishlist = (Trader) loginManager.registerUser("tHasWishlist", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER);
            TradableItem crack = new TradableItem("crack", "crack");
            tradableItemDatabase.update(crack);
            System.out.println(crack.getName());
            tHasWishlist = traderManager.addRequestItem(tHasWishlist.getId(), crack.getId(), "desc");
            tHasWishlist = handleRequestsManager.processItemRequest(tHasWishlist.getId(), tHasWishlist.getRequestedItems().get(0), true);

            userDatabase.update(tHasWishlist);

            /*
            ???????????????????????????????????????
            why is the name of the items an id?
             */
            for(String ids: tHasWishlist.getAvailableItems()){
                System.out.println("id : " + ids);
                System.out.println("name : " + tradingInfoManager.getTradableItem(ids).getName());
            }
            /*


            tHasInventory1 = (Trader) loginManager.registerUser("tHasInventory1", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER);
            tHasInventory1 = traderManager.addRequestItem(tHasInventory1.getId(), "apple watch", "desc");
            tHasInventory1 = handleRequestsManager.processItemRequest(tHasInventory1.getId(), tHasInventory1.getRequestedItems().get(0), true);
            tHasInventory1 = traderManager.addRequestItem(tHasInventory1.getId(), "wack phonw", "desc");
            tHasInventory1 = handleRequestsManager.processItemRequest(tHasInventory1.getId(), tHasInventory1.getRequestedItems().get(0), true);
            tHasInventory1 = traderManager.addRequestItem(tHasInventory1.getId(), "wack waptop", "desc");
            tHasInventory1 = handleRequestsManager.processItemRequest(tHasInventory1.getId(), tHasInventory1.getRequestedItems().get(0), true);
            tHasInventory1 = traderManager.addRequestItem(tHasInventory1.getId(), "wack warbell", "desc");
            tHasInventory1 = handleRequestsManager.processItemRequest(tHasInventory1.getId(), tHasInventory1.getRequestedItems().get(0), true);
            tHasInventory1 = traderManager.addRequestItem(tHasInventory1.getId(), "wack ballet", "desc");
            tHasInventory1 = handleRequestsManager.processItemRequest(tHasInventory1.getId(), tHasInventory1.getRequestedItems().get(0), true);
            traderManager.addToWishList(tHasInventory1.getId(), crack.getId());
            userDatabase.update(tHasInventory1);

            tHasInventory2 = (Trader) loginManager.registerUser("tHasInventory2", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER);
            tHasInventory2 = traderManager.addRequestItem(tHasInventory2.getId(), "wack watct", "desc");
            tHasInventory2 = handleRequestsManager.processItemRequest(tHasInventory2.getId(), tHasInventory2.getRequestedItems().get(0), true);
            tHasInventory2 = traderManager.addRequestItem(tHasInventory2.getId(), "samsung phone", "desc");
            tHasInventory2 = handleRequestsManager.processItemRequest(tHasInventory2.getId(), tHasInventory2.getRequestedItems().get(0), true);
            tHasInventory2 = traderManager.addRequestItem(tHasInventory2.getId(), "wack haptop", "desc");
            tHasInventory2 = handleRequestsManager.processItemRequest(tHasInventory2.getId(), tHasInventory2.getRequestedItems().get(0), true);
            tHasInventory2 = traderManager.addRequestItem(tHasInventory2.getId(), "wack harhell", "desc");
            tHasInventory2 = handleRequestsManager.processItemRequest(tHasInventory2.getId(), tHasInventory2.getRequestedItems().get(0), true);
            tHasInventory2 = traderManager.addRequestItem(tHasInventory2.getId(), "wack callet", "desc");
            tHasInventory2 = handleRequestsManager.processItemRequest(tHasInventory2.getId(), tHasInventory2.getRequestedItems().get(0), true);
            traderManager.addToWishList(tHasInventory2.getId(), crack.getId());
            userDatabase.update(tHasInventory2);

            tHasInventory3 = (Trader) loginManager.registerUser("tHasInventory3", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER);
            tHasInventory3 = traderManager.addRequestItem(tHasInventory3.getId(), "wack watcr", "desc");
            tHasInventory3 = handleRequestsManager.processItemRequest(tHasInventory3.getId(), tHasInventory3.getRequestedItems().get(0), true);
            tHasInventory3 = traderManager.addRequestItem(tHasInventory3.getId(), "wack pwone", "desc");
            tHasInventory3 = handleRequestsManager.processItemRequest(tHasInventory3.getId(), tHasInventory3.getRequestedItems().get(0), true);
            tHasInventory3 = traderManager.addRequestItem(tHasInventory3.getId(), "acer LAPTOP", "desc");
            tHasInventory3 = handleRequestsManager.processItemRequest(tHasInventory3.getId(), tHasInventory3.getRequestedItems().get(0), true);
            tHasInventory3 = traderManager.addRequestItem(tHasInventory3.getId(), "wack hashell", "desc");
            tHasInventory3 = handleRequestsManager.processItemRequest(tHasInventory3.getId(), tHasInventory3.getRequestedItems().get(0), true);
            tHasInventory3 = traderManager.addRequestItem(tHasInventory3.getId(), "wack calret", "desc");
            tHasInventory3 = handleRequestsManager.processItemRequest(tHasInventory3.getId(), tHasInventory3.getRequestedItems().get(0), true);
            traderManager.addToWishList(tHasInventory3.getId(), crack.getId());
            userDatabase.update(tHasInventory3);

            tHasInventory4 = (Trader) loginManager.registerUser("tHasInventory4", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER);
            tHasInventory4 = traderManager.addRequestItem(tHasInventory4.getId(), "wack watct", "desc");
            tHasInventory4 = handleRequestsManager.processItemRequest(tHasInventory4.getId(), tHasInventory4.getRequestedItems().get(0), true);
            tHasInventory4 = traderManager.addRequestItem(tHasInventory4.getId(), "wack thone", "desc");
            tHasInventory4 = handleRequestsManager.processItemRequest(tHasInventory4.getId(), tHasInventory4.getRequestedItems().get(0), true);
            tHasInventory4 = traderManager.addRequestItem(tHasInventory4.getId(), "wack haptop", "desc");
            tHasInventory4 = handleRequestsManager.processItemRequest(tHasInventory4.getId(), tHasInventory4.getRequestedItems().get(0), true);
            tHasInventory4 = traderManager.addRequestItem(tHasInventory4.getId(), "45kg barbell", "desc");
            tHasInventory4 = handleRequestsManager.processItemRequest(tHasInventory4.getId(), tHasInventory4.getRequestedItems().get(0), true);
            tHasInventory4 = traderManager.addRequestItem(tHasInventory4.getId(), "wack callet", "desc");
            tHasInventory4 = handleRequestsManager.processItemRequest(tHasInventory4.getId(), tHasInventory4.getRequestedItems().get(0), true);
            traderManager.addToWishList(tHasInventory4.getId(), crack.getId());
            userDatabase.update(tHasInventory4);

            tHasInventory5 = (Trader) loginManager.registerUser("tHasInventory5", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER);
            tHasInventory5 = traderManager.addRequestItem(tHasInventory5.getId(), "wack watct", "desc");
            tHasInventory5 = handleRequestsManager.processItemRequest(tHasInventory5.getId(), tHasInventory5.getRequestedItems().get(0), true);
            tHasInventory5 = traderManager.addRequestItem(tHasInventory5.getId(), "wack thone", "desc");
            tHasInventory5 = handleRequestsManager.processItemRequest(tHasInventory5.getId(), tHasInventory5.getRequestedItems().get(0), true);
            tHasInventory5 = traderManager.addRequestItem(tHasInventory5.getId(), "wack haptop", "desc");
            tHasInventory5 = handleRequestsManager.processItemRequest(tHasInventory5.getId(), tHasInventory5.getRequestedItems().get(0), true);
            tHasInventory5 = traderManager.addRequestItem(tHasInventory5.getId(), "wack garbell", "desc");
            tHasInventory5 = handleRequestsManager.processItemRequest(tHasInventory5.getId(), tHasInventory5.getRequestedItems().get(0), true);
            tHasInventory5 = traderManager.addRequestItem(tHasInventory5.getId(), "leather wallet", "desc");
            tHasInventory5 = handleRequestsManager.processItemRequest(tHasInventory5.getId(), tHasInventory5.getRequestedItems().get(0), true);
            traderManager.addToWishList(tHasInventory5.getId(), crack.getId());
            userDatabase.update(tHasInventory5);
            */

        } catch (IOException ignored) {
            fail("ERRORS WITH SETTING UP DATABASE FILES");
        } catch (UserAlreadyExistsException ignored) {
            fail("REGISTERING USER ERROR");
        } catch (UserNotFoundException | AuthorizationException | TradableItemNotFoundException | BadPasswordException e) {
            e.printStackTrace();
            fail();
        }
    }

    @After
    public void afterEach() {
        String[] paths = {USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH};
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

    @Test
    public void testFrequentTraders() {
        try {
            int n = traders.length;
            for (int j = 0; j < 3; j++) {
                for (int i = 0; i < traders.length / 2; i++) {
                    String item1 = traders[i].getAvailableItems().get(0);
                    String item2 = traders[traders.length - i - 1].getAvailableItems().get(0);
                    Trade trade = tradingManager.requestTrade(trade(traders[i].getId(), traders[traders.length - i - 1].getId(), goodDate, goodDate2, "home",
                            item1, item2, 3, "This is a trade"));
                    tradingManager.acceptRequest(traders[traders.length - i - 1].getId(), trade.getId());
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[traders.length - i - 1].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[traders.length - i - 1].getId(), trade.getId(), true);
                }
            }
            for (int i = 0; i < n; i++){
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(0).getId(),traders[n-1-i].getId());
            }

            for (int j = 0; j < 2; j++) {
                for (int i = 0; i < traders.length - 1; i += 2) {
                    String item1 = traders[i].getAvailableItems().get(2);
                    String item2 = traders[i + 1].getAvailableItems().get(2);
                    Trade trade = tradingManager.requestTrade(trade(traders[i].getId(), traders[i + 1].getId(), goodDate, goodDate2, "home",
                            item1, item2, 3, "This is a trade"));
                    tradingManager.acceptRequest(traders[i + 1].getId(), trade.getId());
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i + 1].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i + 1].getId(), trade.getId(), true);
                }
            }
            for (int i = 0; i < n; i++){
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(0).getId(),traders[n-1-i].getId());
            }
            for (int i = 0; i < n -1; i+=2){
                if (i == 4){
                    // This is becasue 4->5, and so 4 traded with 5 3 times
                    assertEquals(tradingInfoManager.getFrequentTraders(traders[4].getId()).get(0).getId(),traders[5].getId());
                    continue;
                }
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(1).getId(), traders[i + 1].getId());
            }


            update();
            for (int i = 0; i < traders.length - 2; i += 3) {
                String item1 = traders[i].getAvailableItems().get(0);
                String item2 = traders[i + 2].getAvailableItems().get(0);
                Trade trade = tradingManager.requestTrade(trade(traders[i].getId(), traders[i + 2].getId(), goodDate, goodDate2, "home",
                        item1, item2, 3, "This is a trade"));
                tradingManager.acceptRequest(traders[i + 2].getId(), trade.getId());
                tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                tradingManager.confirmMeetingGeneral(traders[i + 2].getId(), trade.getId(), true);
                tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                tradingManager.confirmMeetingGeneral(traders[i + 2].getId(), trade.getId(), true);
            }

            for (int i = 0; i < n; i++){
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(0).getId(),traders[n-1-i].getId());
            }
            for (int i = 0; i < n -1; i+=2){
                if (i == 4){
                    // This is becasue 4->5, and so 4 traded with 5 3 times
                    assertEquals(tradingInfoManager.getFrequentTraders(traders[4].getId()).get(0).getId(),traders[5].getId());
                    continue;
                }
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(1).getId(), traders[i + 1].getId());
            }
            for (int i = 0; i < traders.length - 2; i += 3) {
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(2).getId(), traders[i + 2].getId());
            }

        } catch (UserNotFoundException | AuthorizationException | CannotTradeException | TradeNotFoundException e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testAutomatedTradeSuggestion() throws UserNotFoundException, AuthorizationException, TradableItemNotFoundException {

        /*
        THE TRADERS AND TRADABLE ITEMS ARE NOT PROPERLY SET UP
        Date date = new Date();
        String location = "Toronto";
        String message = "This is a trade";
        int allowedEdits = 3;
        Trade t1 = tradingInfoManager.automatedTradeSuggestion(tHasWishlist.getId(),"watch" ,"crack", date, date, location,3, message);
        Trade t2 = tradingInfoManager.automatedTradeSuggestion(tHasWishlist.getId(),"phone" ,"crack", date, date, location,3, message);
        Trade t3 = tradingInfoManager.automatedTradeSuggestion(tHasWishlist.getId(),"laptop" ,"crack", date, date, location,3, message);
        Trade t4 = tradingInfoManager.automatedTradeSuggestion(tHasWishlist.getId(),"barbell" ,"crack", date, date, location,3, message);
        Trade t5 = tradingInfoManager.automatedTradeSuggestion(tHasWishlist.getId(),"wallet" ,"crack", date, date, location,3, message);
        assertEquals(new Trade(tHasWishlist.getId(), tHasInventory1.getId(), date, date, location, "crack", "apple watch", allowedEdits, message), t1);
        assertEquals(new Trade(tHasWishlist.getId(), tHasInventory2.getId(), date, date, location, "crack", "samsung phone", allowedEdits, message),t2);
        assertEquals(new Trade(tHasWishlist.getId(), tHasInventory3.getId(), date, date, location, "crack", "acer LAPTOP", allowedEdits, message),t3);
        assertEquals(new Trade(tHasWishlist.getId(), tHasInventory4.getId(), date, date, location, "crack", "45kg barbell", allowedEdits, message),t4);
        assertEquals(new Trade(tHasWishlist.getId(), tHasInventory5.getId(), date, date, location, "crack", "leather wallet", allowedEdits, message),t5);

        */

    }

    @Test
    public void testSimilarSearch() throws TradableItemNotFoundException, AuthorizationException, UserNotFoundException {
        //need to change similarSearch to private after finished testing

        ArrayList<String> listNames = new ArrayList<>();

        //similar search rn still needs a way to deal with strings with missing chars and when the item in wishlist is larger then the item in inventory by a lot
        //apple watch, watch

/*      THE TRADERS ARE TRADABLE ITEMS ARNT PROPERLY SET UP, ALSO HAVE TO CHANGE LISTNAMES TO HOLD STRINGS
        listNames.add("andrer");
        listNames.add("ANDREW");
        Object[] name = tradingInfoManager.similarSearch("andrew", listNames); //tests for ignoring capital case
        assertEquals("ANDREW", name[0]);
        assertEquals(6, name[1]);

        listNames.add("plastic water bottle");
        listNames.add("bowtle");
        Object[] name2 = tradingInfoManager.similarSearch("bottle", listNames); //tests for multiple words case
        assertEquals("plastic water bottle", name2[0]);
        assertEquals(6, name2[1]);

        listNames.add("123456");
        listNames.add("1234567");
        Object[] name3 = tradingInfoManager.similarSearch("1234567", listNames); //tests for most accurate word with words with diff length
        assertEquals("1234567", name3[0]);
        assertEquals(7,name3[1]);

        listNames.add(("55554"));
        listNames.add("55544");
        Object[] name4 = tradingInfoManager.similarSearch(("55555"), listNames); //tests for misspelled strings (not the same as strings missing chars which is a problem rn)
        assertEquals("55554", name4[0]);
        assertEquals(4, name4[1]);

        listNames.add("Jan");
        listNames.add("January");
        Object[] name5 =  tradingInfoManager.similarSearch(("j"), listNames);//tests for most similar length if similarity score is the same
        // (ie if we search for a, with a list of andrew, an, it should return an)
        assertEquals("Jan", name5[0]);
        assertEquals(1, name5[1]);

        /*
            problem with similarSearch rn is when a string is missing a letter
         */

    }

    private void update() {
        try {
            for (int i = 0; i < traders.length; i++) {
                traders[i] = traderManager.getTrader(traders[i].getId());
            }
            admin = (Admin) traderManager.getUser(admin.getId());

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

    private Trade trade(String id1, String id2, Date date1, Date date2, String location, String item1, String item2, int edits, String message) {
        return new Trade(id1, id2, date1, date2, location,
                item1, item2, edits, message);
    }

}
