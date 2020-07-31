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
import java.util.Properties;

import static org.junit.Assert.*;

public class TestTradingInfo extends TestManager{
    private TraderManager traderManager;
    private LoginManager loginManager;
    private HandleItemRequestsManager handleRequestsManager;
    private TradingInfoManager tradingInfoManager;
    private TradingManager tradingManager;
    private Database<User> userDatabase;
    private Database<Trade> tradeDatabase;
    private Database<TradableItem> tradableItemDatabase;

    private Trader[] traders;

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
            userDatabase = new Database<>(USER_PATH);
            tradeDatabase = new Database<>(TRADE_PATH);
            tradableItemDatabase = new Database<>(TRADABLE_ITEM_PATH);
            traders = new Trader[10];
            for (int i = 0; i < traders.length; i++) {
                traders[i] = getTrader(loginManager.registerUser("user" + i, "passssssssS11", UserTypes.TRADER));
                traderManager.addRequestItem(traders[i].getId(), "apple" + i, "desc" + i);
                traderManager.addRequestItem(traders[i].getId(), "fruit" + i, "desc second" + i);
                traderManager.addRequestItem(traders[i].getId(), "another" + i, "desc third" + i);
                handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
                handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
                handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
            }
            for (int i = 0; i < traders.length; i++) {
                traders[i] = traderManager.addToWishList(traders[i].getId(), traders[i + 1 == traders.length ? 0 : i + 1].getAvailableItems().get(0));
                traders[i] = traderManager.addToWishList(traders[i].getId(), traders[i - 1 == -1 ? traders.length - 1 : i - 1].getAvailableItems().get(0));
            }
            admin = (Admin) getUser(loginManager.registerUser("admin", "PASDASDFDSAFpadsf1", UserTypes.ADMIN));

            //Testing for automatedTradeSuggestion below

            tHasWishlist = getTrader(loginManager.registerUser("tHasWishlist", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            userDatabase.update(tHasWishlist);
            TradableItem crack = new TradableItem("crack", "crack");
            tradableItemDatabase.update(crack);
            tHasWishlist = traderManager.addRequestItem(tHasWishlist.getId(), crack.getName(), "desc");
            handleRequestsManager.processItemRequest(tHasWishlist.getId(), tHasWishlist.getRequestedItems().get(0), true);


            tHasInventory1 = getTrader(loginManager.registerUser("tHasInventory1", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            traderManager.addRequestItem(tHasInventory1.getId(), "apple watch", "desc");
            handleRequestsManager.processItemRequest(tHasInventory1.getId(), tHasInventory1.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory1.getId(), "wack phonw", "desc");
            handleRequestsManager.processItemRequest(tHasInventory1.getId(), tHasInventory1.getRequestedItems().get(0), true);
           traderManager.addRequestItem(tHasInventory1.getId(), "wack waptop", "desc");
            handleRequestsManager.processItemRequest(tHasInventory1.getId(), tHasInventory1.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory1.getId(), "wack warbell", "desc");
            handleRequestsManager.processItemRequest(tHasInventory1.getId(), tHasInventory1.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory1.getId(), "wack ballet", "desc");
            handleRequestsManager.processItemRequest(tHasInventory1.getId(), tHasInventory1.getRequestedItems().get(0), true);
            traderManager.addToWishList(tHasInventory1.getId(), crack.getId());

            userDatabase.update(tHasInventory1);

            tHasInventory2 = getTrader(loginManager.registerUser("tHasInventory2", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            traderManager.addRequestItem(tHasInventory2.getId(), "wack watct", "desc");
            handleRequestsManager.processItemRequest(tHasInventory2.getId(), tHasInventory2.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory2.getId(), "samsung phone", "desc");
            handleRequestsManager.processItemRequest(tHasInventory2.getId(), tHasInventory2.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory2.getId(), "wack haptop", "desc");
            handleRequestsManager.processItemRequest(tHasInventory2.getId(), tHasInventory2.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory2.getId(), "wack harhell", "desc");
            handleRequestsManager.processItemRequest(tHasInventory2.getId(), tHasInventory2.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory2.getId(), "wack callet", "desc");
            handleRequestsManager.processItemRequest(tHasInventory2.getId(), tHasInventory2.getRequestedItems().get(0), true);
            traderManager.addToWishList(tHasInventory2.getId(), crack.getId());
            userDatabase.update(tHasInventory2);
            /*
            ????????????????????????????????????????????
            Why is the wishlist empty when i added crack to the wishlist
             */
            for(String ids: tHasInventory2.getWishlist()){
                System.out.println("?????????????????????????????????????????????");
                //System.out.println("wishlist item : " + tradingInfoManager.getTradableItem(ids).getName());
            }



            tHasInventory3 = getTrader(loginManager.registerUser("tHasInventory3", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            traderManager.addRequestItem(tHasInventory3.getId(), "wack watcr", "desc");
             handleRequestsManager.processItemRequest(tHasInventory3.getId(), tHasInventory3.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory3.getId(), "wack pwone", "desc");
           handleRequestsManager.processItemRequest(tHasInventory3.getId(), tHasInventory3.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory3.getId(), "acer LAPTOP", "desc");
            handleRequestsManager.processItemRequest(tHasInventory3.getId(), tHasInventory3.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory3.getId(), "wack hashell", "desc");
            handleRequestsManager.processItemRequest(tHasInventory3.getId(), tHasInventory3.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory3.getId(), "wack calret", "desc");
             handleRequestsManager.processItemRequest(tHasInventory3.getId(), tHasInventory3.getRequestedItems().get(0), true);
            traderManager.addToWishList(tHasInventory3.getId(), crack.getId());
            userDatabase.update(tHasInventory3);

            tHasInventory4 = getTrader(loginManager.registerUser("tHasInventory4", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            traderManager.addRequestItem(tHasInventory4.getId(), "wack watct", "desc");
     handleRequestsManager.processItemRequest(tHasInventory4.getId(), tHasInventory4.getRequestedItems().get(0), true);
          traderManager.addRequestItem(tHasInventory4.getId(), "wack thone", "desc");
          handleRequestsManager.processItemRequest(tHasInventory4.getId(), tHasInventory4.getRequestedItems().get(0), true);
         traderManager.addRequestItem(tHasInventory4.getId(), "wack haptop", "desc");
    handleRequestsManager.processItemRequest(tHasInventory4.getId(), tHasInventory4.getRequestedItems().get(0), true);
       traderManager.addRequestItem(tHasInventory4.getId(), "45kg barbell", "desc");
     handleRequestsManager.processItemRequest(tHasInventory4.getId(), tHasInventory4.getRequestedItems().get(0), true);
     traderManager.addRequestItem(tHasInventory4.getId(), "wack callet", "desc");
        handleRequestsManager.processItemRequest(tHasInventory4.getId(), tHasInventory4.getRequestedItems().get(0), true);
            traderManager.addToWishList(tHasInventory4.getId(), crack.getId());
            userDatabase.update(tHasInventory4);

            tHasInventory5 = getTrader(loginManager.registerUser("tHasInventory5", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            traderManager.addRequestItem(tHasInventory5.getId(), "wack watct", "desc");
            handleRequestsManager.processItemRequest(tHasInventory5.getId(), tHasInventory5.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory5.getId(), "wack thone", "desc");
           handleRequestsManager.processItemRequest(tHasInventory5.getId(), tHasInventory5.getRequestedItems().get(0), true);
        traderManager.addRequestItem(tHasInventory5.getId(), "wack haptop", "desc");
             handleRequestsManager.processItemRequest(tHasInventory5.getId(), tHasInventory5.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory5.getId(), "wack garbell", "desc");
             handleRequestsManager.processItemRequest(tHasInventory5.getId(), tHasInventory5.getRequestedItems().get(0), true);
            traderManager.addRequestItem(tHasInventory5.getId(), "leather wallet", "desc");
             handleRequestsManager.processItemRequest(tHasInventory5.getId(), tHasInventory5.getRequestedItems().get(0), true);
            traderManager.addToWishList(tHasInventory5.getId(), crack.getId());
            userDatabase.update(tHasInventory5);


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
                    Trade trade = getTrade(tradingManager.requestTrade(trade(traders[i].getId(), traders[traders.length - i - 1].getId(), goodDate, goodDate2, "home",
                            item1, item2, 3, "This is a trade")));
                    tradingManager.acceptRequest(traders[traders.length - i - 1].getId(), trade.getId());
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[traders.length - i - 1].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[traders.length - i - 1].getId(), trade.getId(), true);
                }
            }
            for (int i = 0; i < n; i++) {
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(0).getId(), traders[n - 1 - i].getId());
            }

            for (int j = 0; j < 2; j++) {
                for (int i = 0; i < traders.length - 1; i += 2) {
                    String item1 = traders[i].getAvailableItems().get(2);
                    String item2 = traders[i + 1].getAvailableItems().get(2);
                    Trade trade = getTrade(tradingManager.requestTrade(trade(traders[i].getId(), traders[i + 1].getId(), goodDate, goodDate2, "home",
                            item1, item2, 3, "This is a trade")));
                    tradingManager.acceptRequest(traders[i + 1].getId(), trade.getId());
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i + 1].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                    tradingManager.confirmMeetingGeneral(traders[i + 1].getId(), trade.getId(), true);
                }
            }
            for (int i = 0; i < n; i++) {
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(0).getId(), traders[n - 1 - i].getId());
            }
            for (int i = 0; i < n - 1; i += 2) {
                if (i == 4) {
                    // This is becasue 4->5, and so 4 traded with 5 3 times
                    assertEquals(tradingInfoManager.getFrequentTraders(traders[4].getId()).get(0).getId(), traders[5].getId());
                    continue;
                }
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(1).getId(), traders[i + 1].getId());
            }


            update();
            for (int i = 0; i < traders.length - 2; i += 3) {
                String item1 = traders[i].getAvailableItems().get(0);
                String item2 = traders[i + 2].getAvailableItems().get(0);
                Trade trade = getTrade(tradingManager.requestTrade(trade(traders[i].getId(), traders[i + 2].getId(), goodDate, goodDate2, "home",
                        item1, item2, 3, "This is a trade")));
                tradingManager.acceptRequest(traders[i + 2].getId(), trade.getId());
                tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                tradingManager.confirmMeetingGeneral(traders[i + 2].getId(), trade.getId(), true);
                tradingManager.confirmMeetingGeneral(traders[i].getId(), trade.getId(), true);
                tradingManager.confirmMeetingGeneral(traders[i + 2].getId(), trade.getId(), true);
            }

            for (int i = 0; i < n; i++) {
                assertEquals(tradingInfoManager.getFrequentTraders(traders[i].getId()).get(0).getId(), traders[n - 1 - i].getId());
            }
            for (int i = 0; i < n - 1; i += 2) {
                if (i == 4) {
                    // This is becasue 4->5, and so 4 traded with 5 3 times
                    assertEquals(tradingInfoManager.getFrequentTraders(traders[4].getId()).get(0).getId(), traders[5].getId());
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
    public void testSuggestLend() {
        try {
            for (int i = 1; i < traders.length - 1; i++) {
                ArrayList<Trade> suggested = tradingInfoManager.suggestLend(traders[i].getId());
                assertEquals(suggested.size(), 2);
                assertEquals(suggested.get(0).getFirstUserId(), traders[i].getId());
                assertEquals(suggested.get(1).getFirstUserId(), traders[i].getId());
                assertEquals(suggested.get(0).getSecondUserId(), traders[i - 1].getId());
                assertEquals(suggested.get(1).getSecondUserId(), traders[i + 1].getId());

                assertEquals(suggested.get(0).getFirstUserOffer(), traders[i - 1].getWishlist().get(0));
                assertEquals(suggested.get(0).getSecondUserOffer(), "");
                assertEquals(suggested.get(1).getFirstUserOffer(), traders[i + 1].getWishlist().get(1));
                assertEquals(suggested.get(1).getSecondUserOffer(), "");

            }
        } catch (Exception e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }
    @Test
    public void testSuggestTrade(){
        try {
            for (int i = 1; i < traders.length - 1; i++) {
                ArrayList<Trade> suggested = tradingInfoManager.suggestTrade(traders[i].getId());
                assertEquals(suggested.size(), 2);
                assertEquals(suggested.get(0).getFirstUserId(), traders[i].getId());
                assertEquals(suggested.get(1).getFirstUserId(), traders[i].getId());
                assertEquals(suggested.get(0).getSecondUserId(), traders[i - 1].getId());
                assertEquals(suggested.get(1).getSecondUserId(), traders[i + 1].getId());

                assertEquals(suggested.get(0).getFirstUserOffer(), traders[i - 1].getWishlist().get(0));
                assertEquals(suggested.get(0).getSecondUserOffer(), traders[i - 1].getAvailableItems().get(0));
                assertEquals(suggested.get(1).getFirstUserOffer(), traders[i + 1].getWishlist().get(1));
                assertEquals(suggested.get(1).getSecondUserOffer(), traders[i + 1].getAvailableItems().get(0));

            }
        } catch (Exception e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testAutomatedTradeSuggestion() throws UserNotFoundException, AuthorizationException, TradableItemNotFoundException {
        Date date = new Date();
        String location = "Toronto";
        String message = "This is a trade";
        int allowedEdits = 3;
        Trade t1 = tradingInfoManager.automatedTradeSuggestion(tHasWishlist.getId(),"watch" ,"crack", date, date, location,allowedEdits, message);
        Trade t2 = tradingInfoManager.automatedTradeSuggestion(tHasWishlist.getId(),"phone" ,"crack", date, date, location,allowedEdits, message);
        Trade t3 = tradingInfoManager.automatedTradeSuggestion(tHasWishlist.getId(),"laptop" ,"crack", date, date, location,allowedEdits, message);
        Trade t4 = tradingInfoManager.automatedTradeSuggestion(tHasWishlist.getId(),"barbell" ,"crack", date, date, location,allowedEdits, message);
        Trade t5 = tradingInfoManager.automatedTradeSuggestion(tHasWishlist.getId(),"wallet" ,"crack", date, date, location,allowedEdits, message);

        assertEquals("crack", t1.getFirstUserOffer());
        assertEquals("apple watch", t1.getSecondUserOffer());
        assertEquals("tHasWishlist", getTrader(t1.getFirstUserId()).getUsername());
        assertEquals("tHasInventory1", getTrader(t1.getSecondUserId()).getUsername());

        assertEquals("crack", t2.getFirstUserOffer());
        assertEquals("samsung phone", t2.getSecondUserOffer());
        assertEquals("tHasWishlist", getTrader(t2.getFirstUserId()).getUsername());
        assertEquals("tHasInventory2", getTrader(t2.getSecondUserId()).getUsername());

        assertEquals("crack", t3.getFirstUserOffer());
        assertEquals("acer LAPTOP", t3.getSecondUserOffer());
        assertEquals("tHasWishlist", getTrader(t3.getFirstUserId()).getUsername());
        assertEquals("tHasInventory3", getTrader(t3.getSecondUserId()).getUsername());

        assertEquals("crack", t4.getFirstUserOffer());
        assertEquals("45kg barbell", t4.getSecondUserOffer());
        assertEquals("tHasWishlist", getTrader(t4.getFirstUserId()).getUsername());
        assertEquals("tHasInventory4", getTrader(t4.getSecondUserId()).getUsername());

        assertEquals("crack", t5.getFirstUserOffer());
        assertEquals("leather wallet", t5.getSecondUserOffer());
        assertEquals("tHasWishlist", getTrader(t5.getFirstUserId()).getUsername());
        assertEquals("tHasInventory5", getTrader(t5.getSecondUserId()).getUsername());


    }

    @Test
    public void testSimilarSearch() throws TradableItemNotFoundException, AuthorizationException, UserNotFoundException {
        //need to change similarSearch to private after finished testing

        ArrayList<String> listNames = new ArrayList<>();

        //similar search rn still needs a way to deal with strings with missing chars and when the item in wishlist is larger then the item in inventory by a lot
        //apple watch, watch

        TradableItem i1 = new TradableItem("andrer", "test");
        TradableItem i2 = new TradableItem("ANDREW", "test");
        tradableItemDatabase.update(i1);
        tradableItemDatabase.update(i2);
        listNames.add(i1.getId());
        listNames.add(i2.getId());
        Object[] name = tradingInfoManager.similarSearch("andrew", listNames); //tests for ignoring capital case
        assertEquals("ANDREW", name[0]);
        assertEquals(6, name[1]);

        TradableItem i3 = new TradableItem("plastic water bottle", "test");
        TradableItem i4 = new TradableItem("bowtle", "test");
        tradableItemDatabase.update(i3);
        tradableItemDatabase.update(i4);
        listNames.add(i3.getId());
        listNames.add(i4.getId());
        Object[] name2 = tradingInfoManager.similarSearch("bottle", listNames); //tests for multiple words case
        assertEquals("plastic water bottle", name2[0]);
        assertEquals(6, name2[1]);

        TradableItem i5 = new TradableItem("123456", "test");
        TradableItem i6 = new TradableItem("1234567", "test");
        tradableItemDatabase.update(i5);
        tradableItemDatabase.update(i6);
        listNames.add(i5.getId());
        listNames.add(i6.getId());
        Object[] name3 = tradingInfoManager.similarSearch("1234567", listNames); //tests for most accurate word with words with diff length
        assertEquals("1234567", name3[0]);
        assertEquals(7,name3[1]);

        TradableItem i7 = new TradableItem("55554", "test");
        TradableItem i8 = new TradableItem("55544", "test");
        tradableItemDatabase.update(i7);
        tradableItemDatabase.update(i8);
        listNames.add(i7.getId());
        listNames.add(i8.getId());
        Object[] name4 = tradingInfoManager.similarSearch(("55555"), listNames); //tests for misspelled strings (not the same as strings missing chars which is a problem rn)
        assertEquals("55554", name4[0]);
        assertEquals(4, name4[1]);

        TradableItem i9 = new TradableItem("Jan", "test");
        TradableItem i10 = new TradableItem("January", "test");
        tradableItemDatabase.update(i9);
        tradableItemDatabase.update(i10);
        listNames.add(i9.getId());
        listNames.add(i10.getId());
        Object[] name5 =  tradingInfoManager.similarSearch(("j"), listNames);//tests for most similar length if similarity score is the same
        // (ie if we search for a, with a list of andrew, an, it should return an)
        assertEquals("Jan", name5[0]);
        assertEquals(1, name5[1]);

        TradableItem i11 = new TradableItem("comuter", "test");
        TradableItem i12 = new TradableItem("computww", "test");
        tradableItemDatabase.update(i11);
        tradableItemDatabase.update(i12);
        listNames.add(i11.getId());
        listNames.add(i12.getId());
        Object[] name6 =  tradingInfoManager.similarSearch(("computer"), listNames);//tests for missing char
        //assertEquals( "comuter",name6[0]);
        //assertEquals(7, name6[1]);

        TradableItem i13 = new TradableItem("hat", "test");
        TradableItem i14 = new TradableItem("hwat", "test");
        tradableItemDatabase.update(i13);
        tradableItemDatabase.update(i14);
        listNames.add(i13.getId());
        listNames.add(i14.getId());
        Object[] name7 =  tradingInfoManager.similarSearch(("red hat"), listNames);//tests for when the string we are searching for, is bigger then the name of the item
        assertEquals( "hat",name7[0]);
        assertEquals(3, name7[1]);



        /*
            problem with similarSearch rn is when a string is missing a letter
         */
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

    private Trade trade(String id1, String id2, Date date1, Date date2, String location, String item1, String item2, int edits, String message) {
        return new Trade(id1, id2, date1, date2, location,
                item1, item2, edits, message);
    }

}
