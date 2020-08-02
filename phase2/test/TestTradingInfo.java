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
    private Database<User> userDatabase;
    private Database<Trade> tradeDatabase;
    private Database<TradableItem> tradableItemDatabase;

    private Trader[] traders;

    private Trader tHasWishlist;
    private TradableItem crack;
    private Trader storeItems;
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
                traders[i] = getTrader(traders[i].getId());
                handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(0), true);
                handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(1), true);
                handleRequestsManager.processItemRequest(traders[i].getId(), traders[i].getRequestedItems().get(2), true);
                traders[i] = getTrader(traders[i].getId());
            }
            for (int i = 1; i < traders.length-1; i++) {
                traderManager.addToWishList(traders[i].getId(), traders[i + 1].getAvailableItems().get(0));
//                traderManager.addToWishList(traders[i].getId(), traders[i - 1].getAvailableItems().get(0));
                traders[i] = getTrader(traders[i].getId());
            }
            admin = (Admin) getUser(loginManager.registerUser("admin", "PASDASDFDSAFpadsf1", UserTypes.ADMIN));


            //Trader to hold all the items in tHasWishlists wishlist (no trade should be made with this trader)
            storeItems = getTrader(loginManager.registerUser("storeItems", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            TradableItem watch = new TradableItem("watch", "desc");
            TradableItem phone = new TradableItem("phone", "desc");
            TradableItem laptop = new TradableItem("laptop", "desc");
            TradableItem barbell = new TradableItem("barbell", "desc");
            TradableItem wallet = new TradableItem("wallet", "desc");
            tradableItemDatabase.update(watch);
            tradableItemDatabase.update(phone);
            tradableItemDatabase.update(barbell);
            tradableItemDatabase.update(laptop);
            tradableItemDatabase.update(wallet);
            storeItems.getAvailableItems().add(watch.getId());
            storeItems.getAvailableItems().add(phone.getId());
            storeItems.getAvailableItems().add(barbell.getId());
            storeItems.getAvailableItems().add(laptop.getId());
            storeItems.getAvailableItems().add(wallet.getId());
            userDatabase.update(storeItems);
            storeItems = getTrader(storeItems.getId());

            tHasWishlist = getTrader(loginManager.registerUser("tHasWishlist", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            crack = new TradableItem("crack", "crack");
            TradableItem crack2 = new TradableItem("crack2", "crack");
            tradableItemDatabase.update(crack);
            tradableItemDatabase.update(crack2);
            tHasWishlist.getAvailableItems().add(crack.getId());
            tHasWishlist.getWishlist().add(watch.getId());
            tHasWishlist.getWishlist().add(phone.getId());
            tHasWishlist.getWishlist().add(laptop.getId());
            tHasWishlist.getWishlist().add((barbell.getId()));
            tHasWishlist.getWishlist().add(wallet.getId());
            userDatabase.update(tHasWishlist);
            tHasWishlist = getTrader(tHasWishlist.getId());

            //items that should not be in trades
            TradableItem wackWatch = new TradableItem("wack watct", "desc");
            TradableItem wackPhone = new TradableItem("wack phonw", "desc");
            TradableItem wackLaptop = new TradableItem("wack waptop", "desc");
            TradableItem wackBarbell = new TradableItem("wack warbell", "desc");
            TradableItem wackWallet = new TradableItem("wack ballet", "desc");
            tradableItemDatabase.update(wackWatch);
            tradableItemDatabase.update(wackPhone);
            tradableItemDatabase.update(wackLaptop);
            tradableItemDatabase.update(wackBarbell);
            tradableItemDatabase.update(wackWallet);

            tHasInventory1 = getTrader(loginManager.registerUser("tHasInventory1", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            TradableItem watch1 = new TradableItem("apple watch", "desc");
            tradableItemDatabase.update(watch1);
            tHasInventory1.getAvailableItems().add(watch1.getId());
            tHasInventory1.getAvailableItems().add(wackPhone.getId());
            tHasInventory1.getAvailableItems().add(wackBarbell.getId());
            tHasInventory1.getAvailableItems().add(wackLaptop.getId());
            tHasInventory1.getAvailableItems().add(wackWallet.getId());
            tHasInventory1.getWishlist().add(crack2.getId());
            userDatabase.update(tHasInventory1);
            tHasInventory1 = getTrader(tHasInventory1.getId());

            tHasInventory2 = getTrader(loginManager.registerUser("tHasInventory2", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            TradableItem phone2 = new TradableItem("samsung phone", "desc");
            tradableItemDatabase.update(phone2);
            tHasInventory2.getAvailableItems().add(wackWatch.getId());
            tHasInventory2.getAvailableItems().add(phone2.getId());
            tHasInventory2.getAvailableItems().add(wackBarbell.getId());
            tHasInventory2.getAvailableItems().add(wackLaptop.getId());
            tHasInventory2.getAvailableItems().add(wackWallet.getId());
            tHasInventory2.getWishlist().add(crack2.getId());
            userDatabase.update(tHasInventory2);
            tHasInventory2 = getTrader(tHasInventory2.getId());

            tHasInventory3 = getTrader(loginManager.registerUser("tHasInventory3", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            TradableItem laptop3 = new TradableItem("acer LAPTOP", "desc");
            tradableItemDatabase.update(laptop3);
            tHasInventory3.getAvailableItems().add(wackWatch.getId());
            tHasInventory3.getAvailableItems().add(wackPhone.getId());
            tHasInventory3.getAvailableItems().add(wackBarbell.getId());
            tHasInventory3.getAvailableItems().add(laptop3.getId());
            tHasInventory3.getAvailableItems().add(wackWallet.getId());
            tHasInventory3.getWishlist().add(crack2.getId());
            userDatabase.update(tHasInventory3);
            tHasInventory3 = getTrader(tHasInventory3.getId());

            tHasInventory4 = getTrader(loginManager.registerUser("tHasInventory4", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            TradableItem barbell4 = new TradableItem("45kg barbell", "desc");
            tradableItemDatabase.update(barbell4);
            tHasInventory4.getAvailableItems().add(wackWatch.getId());
            tHasInventory4.getAvailableItems().add(wackPhone.getId());
            tHasInventory4.getAvailableItems().add(barbell4.getId());
            tHasInventory4.getAvailableItems().add(wackLaptop.getId());
            tHasInventory4.getAvailableItems().add(wackWallet.getId());
            tHasInventory4.getWishlist().add(crack2.getId());
            userDatabase.update(tHasInventory4);
            tHasInventory4 = getTrader(tHasInventory4.getId());

            tHasInventory5 = getTrader(loginManager.registerUser("tHasInventory5", "Passssssssssssssssssssssssssssssssssssssssss1", UserTypes.TRADER));
            TradableItem wallet5 = new TradableItem("leather wallet", "desc");
            tradableItemDatabase.update(wallet5);
            tHasInventory5.getAvailableItems().add(wackWatch.getId());
            tHasInventory5.getAvailableItems().add(wackPhone.getId());
            tHasInventory5.getAvailableItems().add(wackBarbell.getId());
            tHasInventory5.getAvailableItems().add(wackLaptop.getId());
            tHasInventory5.getAvailableItems().add(wallet5.getId());
            tHasInventory5.getWishlist().add(crack2.getId());
            userDatabase.update(tHasInventory5);
            tHasInventory5 = getTrader(tHasInventory5.getId());



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
                ArrayList<String[]> suggested = tradingInfoManager.suggestLend(traders[i].getId());
                assertEquals(suggested.size(), 1);
                assertEquals(suggested.get(0)[0], traders[i].getId());
                assertEquals(suggested.get(0)[1], traders[i - 1].getId());
                assertEquals(suggested.get(0)[2], traders[i - 1].getWishlist().get(0));

            }
        } catch (Exception e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }


    @Test
    public void testSuggestTrade(){
        try {
            for (int i = 1; i < traders.length-1; i++) {
            traderManager.addToWishList(traders[i].getId(), traders[i - 1].getAvailableItems().get(0));
            traders[i] = getTrader(traders[i].getId());
            }
            for (int i = 2; i < traders.length - 2; i++) {
                ArrayList<String[]> suggested = tradingInfoManager.suggestTrade(traders[i].getId());
                assertEquals(suggested.size(), 2);
                //[thisTraderId, toTraderId, itemIdToGive, itemIdToReceive]
                assertEquals(suggested.get(0)[0], traders[i].getId());
                assertEquals(suggested.get(0)[0], traders[i].getId());
                if (suggested.get(0)[1].equals(traders[i+1].getId())){
                    assertEquals(suggested.get(0)[2], traders[i].getAvailableItems().get(0));
                    assertEquals(suggested.get(0)[3], traders[i+1].getAvailableItems().get(0));
                    assertEquals(suggested.get(1)[2], traders[i].getAvailableItems().get(0));
                    assertEquals(suggested.get(1)[3], traders[i-1].getAvailableItems().get(0));
                }
                else if (suggested.get(0)[1].equals(traders[i-1].getId())){
                    assertEquals(suggested.get(1)[2], traders[i].getAvailableItems().get(0));
                    assertEquals(suggested.get(1)[3], traders[i+1].getAvailableItems().get(0));
                    assertEquals(suggested.get(0)[2], traders[i].getAvailableItems().get(0));
                    assertEquals(suggested.get(0)[3], traders[i-1].getAvailableItems().get(0));
                }
                else{
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
        Date date = new Date();
        String location = "Toronto";
        String message = "This is a trade";
        int allowedEdits = 3;
        ArrayList<Trade> automatedTrades = tradingInfoManager.automatedTradeSuggestion(tHasWishlist.getId(),crack.getId(), date, date, location,allowedEdits, message);
        Trade t1 = automatedTrades.get(0);
        assertEquals("crack", t1.getFirstUserOffer());
        assertEquals("apple watch", t1.getSecondUserOffer());
        assertEquals("tHasWishlist", getTrader(t1.getFirstUserId()).getUsername());
        assertEquals("tHasInventory1", getTrader(t1.getSecondUserId()).getUsername());

        Trade t2 = automatedTrades.get(1);
        assertEquals("crack", t2.getFirstUserOffer());
        assertEquals("samsung phone", t2.getSecondUserOffer());
        assertEquals("tHasWishlist", getTrader(t2.getFirstUserId()).getUsername());
        assertEquals("tHasInventory2", getTrader(t2.getSecondUserId()).getUsername());

        Trade t3 = automatedTrades.get(2);
        assertEquals("crack", t3.getFirstUserOffer());
        assertEquals("acer LAPTOP", t3.getSecondUserOffer());
        assertEquals("tHasWishlist", getTrader(t3.getFirstUserId()).getUsername());
        assertEquals("tHasInventory3", getTrader(t3.getSecondUserId()).getUsername());

        Trade t4 = automatedTrades.get(3);
        assertEquals("crack", t4.getFirstUserOffer());
        assertEquals("45kg barbell", t4.getSecondUserOffer());
        assertEquals("tHasWishlist", getTrader(t4.getFirstUserId()).getUsername());
        assertEquals("tHasInventory4", getTrader(t4.getSecondUserId()).getUsername());

        Trade t5 = automatedTrades.get(4);
        assertEquals("crack", t5.getFirstUserOffer());
        assertEquals("leather wallet", t5.getSecondUserOffer());
        assertEquals("tHasWishlist", getTrader(t5.getFirstUserId()).getUsername());
        assertEquals("tHasInventory5", getTrader(t5.getSecondUserId()).getUsername());


    }

    @Test
    public void testSimilarSearch() throws TradableItemNotFoundException, AuthorizationException, UserNotFoundException {
        //need to change similarSearch to private after finished testing

        ArrayList<String> listNames = new ArrayList<>();

        //similar search rn still needs a way to deal with strings with missing chars

        TradableItem i1 = new TradableItem("andrer", "test");
        TradableItem i2 = new TradableItem("ANDREW", "test");
        TradableItem search = new TradableItem("andrew", "test");
        tradableItemDatabase.update(search);
        tradableItemDatabase.update(i1);
        tradableItemDatabase.update(i2);
        listNames.add(i1.getId());
        listNames.add(i2.getId());
        Object[] name = tradingInfoManager.similarSearch(search.getId(), listNames); //tests for ignoring capital case
        assertEquals("ANDREW", name[0]);
        assertEquals(6, name[1]);

        TradableItem i3 = new TradableItem("plastic water bottle", "test");
        TradableItem i4 = new TradableItem("bowtle", "test");
        TradableItem search2 = new TradableItem("bottle", "test");
        tradableItemDatabase.update(search2);
        tradableItemDatabase.update(i3);
        tradableItemDatabase.update(i4);
        listNames.add(i3.getId());
        listNames.add(i4.getId());
        Object[] name2 = tradingInfoManager.similarSearch(search2.getId(), listNames); //tests for multiple words case
        assertEquals("plastic water bottle", name2[0]);
        assertEquals(6, name2[1]);

        TradableItem i5 = new TradableItem("123456", "test");
        TradableItem i6 = new TradableItem("1234567", "test");
        TradableItem search3 = new TradableItem("1234567", "test");
        tradableItemDatabase.update(search3);
        tradableItemDatabase.update(i5);
        tradableItemDatabase.update(i6);
        listNames.add(i5.getId());
        listNames.add(i6.getId());
        Object[] name3 = tradingInfoManager.similarSearch(search3.getId(), listNames); //tests for most accurate word with words with diff length
        assertEquals("1234567", name3[0]);
        assertEquals(7,name3[1]);

        TradableItem i7 = new TradableItem("55554", "test");
        TradableItem i8 = new TradableItem("55544", "test");
        TradableItem search4 = new TradableItem("55555", "test");
        tradableItemDatabase.update(search4);
        tradableItemDatabase.update(i7);
        tradableItemDatabase.update(i8);
        listNames.add(i7.getId());
        listNames.add(i8.getId());
        Object[] name4 = tradingInfoManager.similarSearch(search4.getId(), listNames); //tests for replaced char strings (not the same as strings missing chars which is a problem rn)
        assertEquals("55554", name4[0]);
        assertEquals(4, name4[1]);

        TradableItem i9 = new TradableItem("Jan", "test");
        TradableItem i10 = new TradableItem("January", "test");
        TradableItem search5 = new TradableItem("j", "test");
        tradableItemDatabase.update(search5);
        tradableItemDatabase.update(i9);
        tradableItemDatabase.update(i10);
        listNames.add(i9.getId());
        listNames.add(i10.getId());
        Object[] name5 =  tradingInfoManager.similarSearch(search5.getId(), listNames);//tests for most similar length if similarity score is the same
        // (ie if we search for a, with a list of andrew, an, it should return an)
        assertEquals("Jan", name5[0]);
        assertEquals(1, name5[1]);



        TradableItem i11 = new TradableItem("comuter", "test");
        TradableItem i12 = new TradableItem("compuwww", "test");
        TradableItem search6 = new TradableItem("computer", "test");
        tradableItemDatabase.update(search6);
        tradableItemDatabase.update(i11);
        tradableItemDatabase.update(i12);
        listNames.add(i11.getId());
        listNames.add(i12.getId());
        Object[] name6 =  tradingInfoManager.similarSearch(search6.getId(), listNames);//tests for missing char
        assertEquals( "comuter",name6[0]);
        assertEquals(6, name6[1]);

        TradableItem i13 = new TradableItem("Chrisstmas", "test");
        TradableItem i14 = new TradableItem("Christwww", "test");
        TradableItem search7 = new TradableItem("Christmas", "test");
        tradableItemDatabase.update(search7);
        tradableItemDatabase.update(i13);
        tradableItemDatabase.update(i14);
        listNames.add(i13.getId());
        listNames.add(i14.getId());
        Object[] name7 =  tradingInfoManager.similarSearch(search7.getId(), listNames);//tests for extra char
        assertEquals( "Chrisstmas",name7[0]);
        assertEquals(8, name7[1]);


        TradableItem i15 = new TradableItem("hat", "test");
        TradableItem i16 = new TradableItem("hwat", "test");
        TradableItem search8 = new TradableItem("red hat", "test");
        tradableItemDatabase.update(search8);
        tradableItemDatabase.update(i15);
        tradableItemDatabase.update(i16);
        listNames.add(i15.getId());
        listNames.add(i16.getId());
        Object[] name8 =  tradingInfoManager.similarSearch(search8.getId(), listNames);//tests for when the string we are searching for, is bigger then the name of the item
        assertEquals( "hat",name8[0]);
        assertEquals(3, name8[1]);


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
