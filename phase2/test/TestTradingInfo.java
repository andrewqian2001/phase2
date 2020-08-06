import backend.exceptions.*;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.TraderProperties;
import backend.tradesystem.UserTypes;
import backend.tradesystem.admin_managers.HandleItemRequestsManager;
import backend.tradesystem.general_managers.LoginManager;
import backend.Database;

import java.lang.reflect.Array;
import java.util.Date;

import backend.tradesystem.trader_managers.TraderManager;
import backend.tradesystem.trader_managers.TradingInfoManager;
import backend.tradesystem.trader_managers.TradingManager;
import org.junit.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.Assert.*;

public class TestTradingInfo extends TestManager {
    private TraderManager traderManager;
    private LoginManager loginManager;
    private HandleItemRequestsManager handleRequestsManager;
    private TradingInfoManager tradingInfoManager;
    private TradingManager tradingManager;
    private Database<TradableItem> tradableItemDatabase;

    private Trader[] traders;

    //Variables for testAutomatedTrade below
    Trader t1;
    Trader t2;
    Trader t3;
    Trader t4;
    Trader t5;
    Trader t6;
    Trader t7;




    private Admin admin;
    private final String USER_PATH = "./test/testUsers.ser";
    private final String TRADABLE_ITEM_PATH = "./test/testTradableItems.ser";
    private final String TRADE_PATH = "./test/testTrades.ser";
    private final String TRADER_PROPERTY_FILE_PATH = "./test/trader.properties";
    private Date goodDate = new Date(System.currentTimeMillis() + 99999999);
    private Date goodDate2 = new Date(System.currentTimeMillis() + 999999999);

    public TestTradingInfo() throws IOException {
        super();
    }

    @Before
    public void beforeEach() {
        try {
            traderManager = new TraderManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            loginManager = new LoginManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH, TRADER_PROPERTY_FILE_PATH);
            tradingManager = new TradingManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            handleRequestsManager = new HandleItemRequestsManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            tradingInfoManager = new TradingInfoManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            tradableItemDatabase = new Database<>(TRADABLE_ITEM_PATH);
            traders = new Trader[10];
            for (int i = 0; i < traders.length; i++) {
                traders[i] = getTrader(loginManager.registerUser("user" + i, "passssssssS11", UserTypes.TRADER));
                traderManager.addRequestItem(traders[i].getId(), "apple" + i, "desc" + i);
                traderManager.addRequestItem(traders[i].getId(), "fruit" + i, "desc second" + i);
                traderManager.addRequestItem(traders[i].getId(), "another" + i, "desc third" + i);
                traders[i] = getTrader(traders[i].getId());
                handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
                handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(1), true);
                handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(2), true);
                traders[i] = getTrader(traders[i].getId());
            }
            for (int i = 1; i < traders.length - 1; i++) {
                traderManager.addToWishList(traders[i].getId(), traders[i + 1].getAvailableItems().get(0));
//                traderManager.addToWishList(traders[i].getId(), traders[i - 1].getAvailableItems().get(0));
                traders[i] = getTrader(traders[i].getId());
            }
            admin = (Admin) getUser(loginManager.registerUser("admin", "PASDASDFDSAFpadsf1", UserTypes.ADMIN));

            //-------------Tests for automatedTradeSuggestion below----------------------------------------

            /*
                        - info for createNewTrader helper function
                 createNewTrader(String name, String city, ArrayList<String> listOfNamesInventory, ArrayList<String> listOfNamesWishlist)
                 * @param name is the name of the trader
                 * @param city is the city of the trader
                 * @param listOfNamesInventory is a list of the names of the items that will be added to this traders inventory
                 * @param listOfNamesWishlist is a list of the names of the items that will be added to this traders wishlist
                 * @return the updated trader
             */

            ArrayList<String> t1Inventory = new ArrayList<>();
            t1Inventory.add("ball");
            t1Inventory.add("rocketz");
            ArrayList<String> t1Wishlist = new ArrayList<>();
            t1Wishlist.add("johns dad");
            t1Wishlist.add("nice iphone");
            t1 = createNewTrader("john", "toronto", t1Inventory, t1Wishlist);
            ArrayList<String> t2Inventory = new ArrayList<>();
            t2Inventory.add("nice iphone7");
            t2Inventory.add("sandwich");
            t2Inventory.add("mouse");
            ArrayList<String> t2Wishlist = new ArrayList<>();
            t2Wishlist.add("ballz");
            t2Wishlist.add("rocket");
            t2 = createNewTrader("johns dad", "toronto", t2Inventory, t2Wishlist);
            ArrayList<String> t3Inventory = new ArrayList<>();
            ArrayList<String> t3Wishlist = new ArrayList<>();
            t3Inventory.add("red apple");
            t3Inventory.add("wallets");
            t3Inventory.add("acer computer");
            t3Wishlist.add("bandage");
            t3 = createNewTrader("trader3", "Markham", t3Inventory, t3Wishlist);
            ArrayList<String> t4Inventory = new ArrayList<>();
            ArrayList<String> t4Wishlist = new ArrayList<>();
            t4Inventory.add("red bandage");
            t4Wishlist.add("wallet");
            t4 = createNewTrader("trader4", "Toronto", t4Inventory, t4Wishlist);
            ArrayList<String> t5Inventory = new ArrayList<>();
            ArrayList<String> t5Wishlist = new ArrayList<>();
            t5Inventory.add("WHAT I WANT YOU CANNOT GIVE ME");
            t5Wishlist.add("WEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            t5 = createNewTrader("trader5", "Toronto", t5Inventory, t5Wishlist);
            ArrayList<String> t6Inventory = new ArrayList<>();
            ArrayList<String> t6Wishlist = new ArrayList<>();
            t5Inventory.add("12345678");
            t5Inventory.add("1234567");



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
                    Trade trade = getTrade(tradingManager.requestTrade(traders[i].getId(), traders[traders.length - i - 1].getId(), goodDate, goodDate2, "home",
                            item1, item2, 3, "This is a trade"));
                    tradingManager.acceptRequest(traders[traders.length - i - 1].getId(), trade.getId());
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[traders.length - i - 1].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[traders.length - i - 1].getId(), trade.getId(), true);
                }
            }
            for (int i = 0; i < n; i++) {
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(0), traders[n - 1 - i].getId());
            }

            for (int j = 0; j < 2; j++) {
                for (int i = 0; i < traders.length - 1; i += 2) {
                    String item1 = traders[i].getAvailableItems().get(2);
                    String item2 = traders[i + 1].getAvailableItems().get(2);
                    Trade trade = getTrade(tradingManager.requestTrade(traders[i].getId(), traders[i + 1].getId(), goodDate, goodDate2, "home",
                            item1, item2, 3, "This is a trade"));
                    tradingManager.acceptRequest(traders[i + 1].getId(), trade.getId());
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i + 1].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i + 1].getId(), trade.getId(), true);
                }
            }
            for (int i = 0; i < n; i++) {
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(0), traders[n - 1 - i].getId());
            }
            for (int i = 0; i < n - 1; i += 2) {
                if (i == 4) {
                    // This is becasue 4->5, and so 4 traded with 5 3 times
                    assertEquals(tradingInfoManager.getFrequentTraders(traders[4].getId()).get(0), traders[5].getId());
                    continue;
                }
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(1), traders[i + 1].getId());
            }


            update();
            for (int i = 0; i < traders.length - 2; i += 3) {
                String item1 = traders[i].getAvailableItems().get(0);
                String item2 = traders[i + 2].getAvailableItems().get(0);
                Trade trade = getTrade(tradingManager.requestTrade(traders[i].getId(), traders[i + 2].getId(), goodDate, goodDate2, "home",
                        item1, item2, 3, "This is a trade"));
                tradingManager.acceptRequest(traders[i + 2].getId(), trade.getId());
                tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                tradingManager.confirmMeetingGeneral(traders[i + 2].getId(), trade.getId(), true);
                tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                tradingManager.confirmMeetingGeneral(traders[i + 2].getId(), trade.getId(), true);
            }

            for (int i = 0; i < n; i++) {
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(0), traders[n - 1 - i].getId());
            }
            for (int i = 0; i < n - 1; i += 2) {
                if (i == 4) {
                    // This is becasue 4->5, and so 4 traded with 5 3 times
                    assertEquals(tradingInfoManager.getFrequentTraders(traders[4].getId()).get(0), traders[5].getId());
                    continue;
                }
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(1), traders[i + 1].getId());
            }
            for (int i = 0; i < traders.length - 2; i += 3) {
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(2), traders[i + 2].getId());
            }

        } catch (UserNotFoundException | AuthorizationException | CannotTradeException | TradeNotFoundException e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }


    @Test
    public void testSuggestLend() {
        try {
            for (int i = 2; i < traders.length - 2; i++) {
                String[] suggested = tradingInfoManager.suggestLend(traders[i].getId(), true);
                assertEquals(suggested[0], traders[i].getId());
                assertEquals(suggested[1], traders[i - 1].getId());
                assertEquals(suggested[2], traders[i - 1].getWishlist().get(0));

            }
        } catch (Exception e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }


    @Test
    public void testSuggestTrade() {
        try {
            for (int i = 1; i < traders.length - 1; i++) {
                traderManager.addToWishList(traders[i].getId(), traders[i - 1].getAvailableItems().get(0));
                traders[i] = getTrader(traders[i].getId());
            }
            for (int i = 2; i < traders.length - 2; i++) {
                String[] suggested = tradingInfoManager.suggestTrade(traders[i].getId(), true);
                //[thisTraderId, toTraderId, itemIdToGive, itemIdToReceive]
                assertEquals(suggested[0], traders[i].getId());
                if (suggested[1].equals(traders[i + 1].getId())) {
                    assertEquals(suggested[2], traders[i].getAvailableItems().get(0));
                    assertEquals(suggested[3], traders[i + 1].getAvailableItems().get(0));
                } else if (suggested[1].equals(traders[i - 1].getId())) {
                    assertEquals(suggested[2], traders[i].getAvailableItems().get(0));
                    assertEquals(suggested[3], traders[i - 1].getAvailableItems().get(0));
                } else {
                    fail();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());

        }
    }

    @Test
    public void testAutomatedTradeSuggestion() throws UserNotFoundException, AuthorizationException, TradableItemNotFoundException {

        //testAutomatedTradeSuggestion(Trader t1, Trader t2, String itemT1Name, String itemT2Name, boolean filter)


        testingAutomatedTradeSuggestion(t1, t2 ,"rocketz", "nice iphone7", false);
        testingAutomatedTradeSuggestion(t3,t4,"wallets", "red bandage", false);
        testingAutomatedTradeSuggestion(t3,t4,"", "", true);
        testingAutomatedTradeSuggestion(t5, t4, "", "", false);
    }

    //@Test !make sim search public
    public void testSimilarSearch() throws TradableItemNotFoundException, AuthorizationException, UserNotFoundException {
        // Wishlist is example
        // emample would return 6/7 (replaced char)
        // exawmple would return 6/7 (extra char)
        // examle would return 5/7 (removing a char)
        // If the similarity score isn't at least 80% of the wishlist string length, then the similarity should be 0
        ArrayList<Object[]> objectList = new ArrayList<>();
//        objectList.add(new Object[]{"exampleexample", 14});
//        objectList.add(new Object[]{"this", 0});
//        objectList.add(new Object[]{"examplexample", 12});
//        objectList.add(new Object[]{"axampleaxample", 12});
//        objectList.add(new Object[]{"exampleeexample", 13});
//        objectList.add(new Object[]{"xampleexamp", 11});
//        objectList.add(new Object[]{"eyampleexayple", 12});
//        objectList.add(new Object[]{"eyampleyxayple", 11});
//        objectList.add(new Object[]{"eyampleyxayples", 11});
//        objectList.add(new Object[]{"aaaaeyampleyxayplesaaaaa", 11});
        objectList.add(new Object[]{"nice iphone7", 7});
        //objectList.add(new Object[]{"examplZZexample", 11}); //oopsies
        confirmSimilarSearchWithList("nice iphone7", objectList);
    }

    private void update() {
        try {
            for (int i = 0; i < traders.length; i++) {
                traders[i] = getTrader(traders[i].getId());
            }
            admin = (Admin) getUser(admin.getId());

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


    private void confirmSimilarSearch(String itemToSearchId, String expectedItemName, int expectedSimilarityScore, ArrayList<String> list) throws TradableItemNotFoundException, AuthorizationException, UserNotFoundException {
        /*
        Object[] similarItem = tradingInfoManager.similarSearch(itemToSearchId, list);//tests for missing char
        String expectedItemId = null;
        for (String itemIds : list) {
            String itemName = getTradableItem(itemIds).getName();
            if (itemName.equals(expectedItemName)) {
                expectedItemId = itemIds;
            }
        }
        if ((similarItem == null || expectedItemId == null)) { // Threshold test
            assertEquals("Below threshold " + expectedItemName, expectedSimilarityScore, 0);
        } else {
            assertEquals(expectedItemName, expectedItemId, similarItem[0]);
            assertEquals(expectedItemName, expectedSimilarityScore, similarItem[1]);
        }

        */

    }


    private void confirmSimilarSearchWithList(String itemToSearchName, ArrayList<Object[]> itemNameAndScore) throws TradableItemNotFoundException, UserNotFoundException, AuthorizationException {
        TradableItem item = new TradableItem(itemToSearchName, "desc");
        tradableItemDatabase.update(item);
        for (Object[] items : itemNameAndScore) {
            ArrayList<String> list = new ArrayList<>();
            list = addToItemList(list, (String) items[0], "desc");
            confirmSimilarSearch(item.getId(), (String) items[0], (int) items[1], list);
        }

    }

    private ArrayList<String> addToItemList(ArrayList<String> list, String itemName, String itemDesc) {
        TradableItem item = new TradableItem(itemName, itemDesc);
        tradableItemDatabase.update(item);
        list.add(item.getId());
        return list;
    }

    private void testingAutomatedTradeSuggestion(Trader t1, Trader t2, String itemT1Name, String itemT2Name, boolean filter) throws UserNotFoundException, AuthorizationException, TradableItemNotFoundException {

        String[] test = tradingInfoManager.automatedTradeSuggestion(t1.getId(), filter);
        String itemT1Id = null;
        String itemT2Id = null;
        for(String ids: getTradableItemDatabase().getItems().keySet()){
            if(getTradableItem(ids).getName().equals(itemT1Name)){
                itemT1Id = ids;
            }
            if(getTradableItem(ids).getName().equals(itemT2Name)){
                itemT2Id = ids;
            }
        }

        if(itemT1Name.length() == 0 && itemT2Name.length() == 0 && test.length == 0){
            return;
        }

        t1 = getTrader(t1.getId());
        t2 = getTrader(t2.getId());
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.println("item name expected: " + itemT1Name + "--- item id expected: " + itemT1Id);
        System.out.println("item name actual: " + getTradableItem(test[2]).getName() + "--- item id expected: " + test[2]);
        assertEquals(t1.getId(), test[0]);
        assertEquals(t2.getId(), test[1]);
        System.out.println("item2 name expected: " + itemT2Name +  "--- item id expected: " + itemT2Id);
        System.out.println("item2 name actual: " + getTradableItem(test[3]).getName() + "--- item id expected: " + test[3]);
        System.out.println("---------------------------------------------------------------------------------------------");
        assertEquals(itemT1Id, test[2]);
        assertEquals(itemT2Id, test[3]);

    }
    /**
     *
     * @param name is the name of the trader
     * @param city is the city of the trader
     * @param listOfNamesInventory is a list of the names of the items that will be added to this traders inventory
     * @param listOfNamesWishlist is a list of the names of the items that will be added to this traders wishlist
     * @return the updated trader
     * @throws UserNotFoundException
     * @throws AuthorizationException
     */
    private Trader createNewTrader(String name, String city, ArrayList<String> listOfNamesInventory, ArrayList<String> listOfNamesWishlist) throws UserNotFoundException, AuthorizationException, TradableItemNotFoundException {
        ArrayList<String> listIdsInventory = new ArrayList<>();
        ArrayList<String> listIdsWishlist = new ArrayList<>();

        Trader trader = new Trader(name,"passssssssS11", city, 99, 99, 0);
        updateUserDatabase(trader);
        for(String itemNamesInventory: listOfNamesInventory){
            listIdsInventory = addToItemList(listIdsInventory , itemNamesInventory, "desc");
        }
        trader.getAvailableItems().addAll(listIdsInventory);
        for(String itemNamesWishlist: listOfNamesWishlist){
            listIdsWishlist = addToItemList(listIdsWishlist , itemNamesWishlist, "desc");
        }
        trader.getWishlist().addAll(listIdsWishlist);


        updateUserDatabase(trader);
        return getTrader(trader.getId());
    }


}
