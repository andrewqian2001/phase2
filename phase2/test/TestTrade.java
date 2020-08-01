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

import backend.tradesystem.queries.ItemQuery;
import backend.tradesystem.queries.TradeQuery;
import backend.tradesystem.queries.UserQuery;
import org.junit.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.Assert.*;

public class TestTrade extends TestManager{
    // Test branch set up
    private TraderManager traderManager;
    private LoginManager loginManager;
    private HandleItemRequestsManager handleRequestsManager;
    private TradingManager tradingManager;
    private Database<User>  userDatabase;
    private Database<Trade> tradeDatabase;
    private Database<TradableItem> tradableItemDatabase;
    private UserQuery userQuery;
    private ItemQuery itemQuery;
    private TradeQuery tradeQuery;

    private Trader trader1;
    private Trader trader2;
    private Admin admin;
    private final String USER_PATH = "./test/testUsers.ser";
    private final String TRADABLE_ITEM_PATH = "./test/testTradableItems.ser";
    private final String TRADE_PATH = "./test/testTrades.ser";
    private final String TRADER_PROPERTY_FILE_PATH = "./test/trader.properties";
    private Date goodDate = new Date(System.currentTimeMillis() + 99999999);
    private Date goodDate2 = new Date(System.currentTimeMillis() + 999999999);

    public TestTrade() throws IOException {
        super();
    }

    @Before
    public void beforeEach() {
        try {
            userQuery = new UserQuery(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            tradeQuery = new TradeQuery(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            itemQuery = new ItemQuery(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            traderManager = new TraderManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            loginManager = new LoginManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH, TRADER_PROPERTY_FILE_PATH);
            tradingManager = new TradingManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            handleRequestsManager = new HandleItemRequestsManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            userDatabase = new Database<>(USER_PATH);
            tradeDatabase = new Database<>(TRADE_PATH);
            tradableItemDatabase = new Database<>(TRADABLE_ITEM_PATH);
            trader1 = getTrader(loginManager.registerUser("user", "passssssssS11", UserTypes.TRADER));
            trader2 = getTrader(loginManager.registerUser("user1", "passssssssS11", UserTypes.TRADER));
            admin = (Admin) getUser(loginManager.registerUser("admin", "passssssssS11", UserTypes.ADMIN));
            traderManager.addRequestItem(trader1.getId(), "apple", "sweet");
            traderManager.addRequestItem(trader1.getId(), "apple1", "sweet1");
            traderManager.addRequestItem(trader1.getId(), "apple2", "sweet2");
            traderManager.addRequestItem(trader2.getId(), "pear1", "disgusting");
            traderManager.addRequestItem(trader2.getId(), "pear2", "disgusting2");
            traderManager.addRequestItem(trader2.getId(), "pear3", "disgusting3");
            handleRequestsManager.processItemRequest(trader1.getId(), userQuery.getRequestedItems(trader1.getId()).get(0), true);
            handleRequestsManager.processItemRequest(trader1.getId(), userQuery.getRequestedItems(trader1.getId()).get(0), true);
            handleRequestsManager.processItemRequest(trader1.getId(),userQuery.getRequestedItems(trader1.getId()).get(0), true);
            handleRequestsManager.processItemRequest(trader2.getId(), userQuery.getRequestedItems(trader2.getId()).get(0), true);
            handleRequestsManager.processItemRequest(trader2.getId(), userQuery.getRequestedItems(trader2.getId()).get(0), true);
            handleRequestsManager.processItemRequest(trader2.getId(), userQuery.getRequestedItems(trader2.getId()).get(0), true);
            update();

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
    public void testTemporaryTrade() {
        try{
            String item1 = userQuery.getAvailableItems(trader1.getId()).get(0);
            String item2 = userQuery.getAvailableItems(trader2.getId()).get(0);
            Trade trade = getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, goodDate2, "home",
                    item1, item2, 3, "This is a trade"));
            // make sure that the trades are requested
            update();
            assertEquals(userQuery.getRequestedTrades(trader1.getId()).get(0), userQuery.getRequestedTrades(trader2.getId()).get(0));
            //makes sure that the items are still in each person's inventory
            assertEquals(userQuery.getAvailableItems(trader1.getId()).get(0), item1);
            assertEquals(userQuery.getAvailableItems(trader2.getId()).get(0), item2);


            trader1.getAvailableItems().remove(item1);
            getUserDatabase().update(trader1);
            // make sure trader1 can no longer accept the trade
            try {
                tradingManager.acceptRequest(trader1.getId(), trade.getId());
                fail("Accept request should not work");
            } catch (CannotTradeException e){
                //GOOD!
            }
            trader1.getAvailableItems().add(item1);
            getUserDatabase().update(trader1);
            // Once trader2 accepts ...
            assertTrue(tradingManager.acceptRequest(trader2.getId(), trade.getId()));
            update();
            // make sure the trades are correctly in their respective lists
            assertEquals(trader1.getAcceptedTrades().get(0), trader2.getAcceptedTrades().get(0));
            // and that they're no longer requested
            assertTrue(trader1.getRequestedTrades().size() == 0);
            assertTrue(trader2.getRequestedTrades().size() == 0);
            // and that the items are no longer in the trader's inventories
            assertFalse(trader1.getAvailableItems().contains(item1));
            assertFalse(trader2.getAvailableItems().contains(item2));

            //confirm trade
            tradingManager.confirmMeetingGeneral(trader1.getId(), trade.getId(), true);
            // Make sure nothing changed after first confirmation
            assertNotEquals(trader1.getAvailableItems().get(trader1.getAvailableItems().size()-1), item2);
            assertNotEquals(trader2.getAvailableItems().get(trader2.getAvailableItems().size()-1), item1);
            assertEquals(0, trader1.getTradeCount());
            assertEquals(0, trader2.getTradeCount());
            assertEquals(0, trader1.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsBorrowed());
            assertEquals(0, trader1.getTotalItemsBorrowed());

            // Make sure confirming first meeting again does nothing
            tradingManager.confirmMeetingGeneral(trader1.getId(), trade.getId(), true);
            assertNotEquals(trader1.getAvailableItems().get(trader1.getAvailableItems().size()-1), item2);
            assertNotEquals(trader2.getAvailableItems().get(trader2.getAvailableItems().size()-1), item1);
            assertEquals(0, trader1.getTradeCount());
            assertEquals(0, trader2.getTradeCount());
            assertEquals(0, trader1.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsBorrowed());
            assertEquals(0, trader1.getTotalItemsBorrowed());

            tradingManager.confirmMeetingGeneral(trader2.getId(), trade.getId(), true);

            //check the trade is now in the completed trades
            update();
            // check that the trade is still accepted
            assertTrue(trader1.getAcceptedTrades().size() == 1);
            assertTrue(trader2.getAcceptedTrades().size() == 1);
            // check that the items are in the correct pos
            assertEquals(trader1.getOngoingItems().get(trader1.getOngoingItems().size()-1), item2);
            assertEquals(trader2.getOngoingItems().get(trader2.getOngoingItems().size()-1), item1);
            assertEquals(0, trader1.getTradeCount());
            assertEquals(0, trader2.getTradeCount());
            assertEquals(0, trader1.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsBorrowed());
            assertEquals(0, trader1.getTotalItemsBorrowed());



            /*
            * THIS POINT MARKS WHERE THE SECOND MEETING IS BEING TESTED
            * */

//            trader1.getAvailableItems().remove(item2);
//            userDatabase.update(trader1);
//            try {
//                tradingManager.confirmMeetingGeneral(trader2.getId(), trade.getId(), true);
//                fail();
//            }catch (CannotTradeException e){
//
//            }
//            try {
//                tradingManager.confirmMeetingGeneral(trader1.getId(), trade.getId(), true);
//                fail();
//            }catch (CannotTradeException e){
//
//            }
//            trader1.getAvailableItems().add(item2);
//            userDatabase.update(trader1);

            // This part makes sure that the incomplete trade count does not affect trade confirmation
            trader1.getAcceptedTrades().add("a");
            trader1.getAcceptedTrades().add("b");
            trader1.getAcceptedTrades().add("c");
            userDatabase.update(trader1);


            tradingManager.confirmMeetingGeneral(trader2.getId(), trade.getId(), true);
            // Make sure nothing has changed after first confirmation.
            update();
            // check that the trade is still accepted
            assertTrue(trader1.getAcceptedTrades().size() == 4);
            assertTrue(trader2.getAcceptedTrades().size() == 1);
            // check that the items are in the correct pos
            assertEquals(trader1.getOngoingItems().get(trader1.getOngoingItems().size()-1), item2);
            assertEquals(trader2.getOngoingItems().get(trader2.getOngoingItems().size()-1), item1);
            assertEquals(0, trader1.getTradeCount());
            assertEquals(0, trader2.getTradeCount());
            assertEquals(0, trader1.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsBorrowed());
            assertEquals(0, trader1.getTotalItemsBorrowed());

            // Make sure confirming again will not do anything
            tradingManager.confirmMeetingGeneral(trader2.getId(), trade.getId(), true);
            update();
            // check that the trade is still accepted
            assertTrue(trader1.getAcceptedTrades().size() == 4);
            assertTrue(trader2.getAcceptedTrades().size() == 1);
            // check that the items are in the correct pos
            assertEquals(trader1.getOngoingItems().get(trader1.getOngoingItems().size()-1), item2);
            assertEquals(trader2.getOngoingItems().get(trader2.getOngoingItems().size()-1), item1);
            assertEquals(0, trader1.getTradeCount());
            assertEquals(0, trader2.getTradeCount());
            assertEquals(0, trader1.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsBorrowed());
            assertEquals(0, trader1.getTotalItemsBorrowed());

            tradingManager.confirmMeetingGeneral(trader1.getId(), trade.getId(), true);
            //check the trade is now in the completed trades
            update();
            assertEquals(trader1.getCompletedTrades().get(0), trade.getId());
            assertEquals(trader2.getCompletedTrades().get(0), trade.getId());
            // check that the trade is no longer in the accepted trades
            assertTrue(trader1.getAcceptedTrades().size() == 3);
            assertTrue(trader2.getAcceptedTrades().size() == 0);
            // check that the items are in the correct pos
            assertEquals(trader1.getAvailableItems().get(trader1.getAvailableItems().size()-1), item1);
            assertEquals(trader2.getAvailableItems().get(trader2.getAvailableItems().size()-1), item2);
            assertEquals(1, trader1.getTradeCount());
            assertEquals(1, trader2.getTradeCount());
            assertEquals(0, trader1.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsBorrowed());
            assertEquals(0, trader1.getTotalItemsBorrowed());
            assertEquals(0, trader1.getOngoingItems().size());
            assertEquals(0, trader2.getOngoingItems().size());
            assertFalse(trader1.getAvailableItems().contains(item2));
            assertFalse(trader2.getAvailableItems().contains(item1));


    } catch (UserNotFoundException | AuthorizationException | CannotTradeException | TradeNotFoundException e) {
        fail(e.getMessage());
        e.printStackTrace();
    }
    }

    @Test
    public void testPermanentTrade(){
        try {
            String item1 = trader1.getAvailableItems().get(0);
            String item2 = trader2.getAvailableItems().get(0);
            Trade trade = getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, null, "home",
                    item1, item2, 3, "This is a trade"));
            // make sure that the trades are requested
            update();
            assertEquals(trader1.getRequestedTrades().get(0), trader2.getRequestedTrades().get(0));
            //makes sure that the items are still in each person's inventory
            assertEquals(trader1.getAvailableItems().get(0), item1);
            assertEquals(trader2.getAvailableItems().get(0), item2);
            trader1.getAvailableItems().remove(item1);
            userDatabase.update(trader1);
            // make sure trader1 can no longer accept the trade
            try {
                tradingManager.acceptRequest(trader1.getId(), trade.getId());
                fail("Accept request should not work");
            } catch (CannotTradeException e){
                //GOOD!
            }
            trader1.getAvailableItems().add(item1);
            userDatabase.update(trader1);
            //make sure trader1 can accept the trade
            tradingManager.acceptRequest(trader1.getId(), trade.getId());
            // Once trader2 accepts ...
            assertTrue(tradingManager.acceptRequest(trader2.getId(), trade.getId()));
            update();
            // make sure the trades are correctly in their respective lists
            assertEquals(trader1.getAcceptedTrades().get(0), trader2.getAcceptedTrades().get(0));
            // and that they're no longer requested
            assertTrue(trader1.getRequestedTrades().size() == 0);
            assertTrue(trader2.getRequestedTrades().size() == 0);
            // and that the items are no longer in the trader's inventories
            assertFalse(trader1.getAvailableItems().contains(item1));
            assertFalse(trader2.getAvailableItems().contains(item2));

            // This part makes sure that the incomplete trade count does not affect trade confirmation
            trader1.getAcceptedTrades().add("a");
            trader1.getAcceptedTrades().add("b");
            trader1.getAcceptedTrades().add("c");
            userDatabase.update(trader1);

            //confirm trade
            tradingManager.confirmMeetingGeneral(trader1.getId(), trade.getId(), true);
            tradingManager.confirmMeetingGeneral(trader2.getId(), trade.getId(), true);

            //check the trade is now in the completed trades
            update();
            assertEquals(trader1.getCompletedTrades().get(0), trade.getId());
            assertEquals(trader2.getCompletedTrades().get(0), trade.getId());
            // check that the trade is no longer in the accepted trades
            assertTrue(trader1.getAcceptedTrades().size() == 3);
            assertTrue(trader2.getAcceptedTrades().size() == 0);
            // check that the items are in the correct pos
            assertEquals(trader1.getAvailableItems().get(trader1.getAvailableItems().size()-1), item2);
            assertEquals(trader2.getAvailableItems().get(trader2.getAvailableItems().size()-1), item1);
            assertEquals(1, trader1.getTradeCount());
            assertEquals(1, trader2.getTradeCount());
            assertEquals(0, trader1.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsBorrowed());
            assertEquals(0, trader1.getTotalItemsBorrowed());


        } catch (UserNotFoundException | AuthorizationException | CannotTradeException | TradeNotFoundException e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testEditTrade(){
        try {
            String item1 = trader1.getAvailableItems().get(0);
            String item2 = trader2.getAvailableItems().get(0);
            String item3 = trader1.getAvailableItems().get(1);
            String item4 = trader2.getAvailableItems().get(1);

            Trade trade1 = getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, null, "home",
                    item1, item2, 1, "This is a trade"));

            ///Trade is requested
            update();
            assertEquals(trader1.getRequestedTrades().get(0), trader2.getRequestedTrades().get(0));
            assertEquals(trader1.getRequestedTrades().get(0), trade1.getId());

            try {
                tradingManager.counterTradeOffer(trader1.getId(), trade1.getId(), goodDate, null, "...",
                        item3, item4);
                fail("This user should not be able to send an edited trade offer");
            } catch (CannotTradeException e) {
                assertEquals("A previous trade offer has already been sent", e.getMessage());
            }

            try {
                tradingManager.counterTradeOffer(trader2.getId(), trade1.getId(), goodDate, null, "...",
                        item3, item4);
                fail("This user is giving items they don't have");
            } catch (CannotTradeException e) {
                assertEquals("One of the traders does not have the required item!", e.getMessage());
            }

            tradingManager.counterTradeOffer(trader2.getId(), trade1.getId(), goodDate, goodDate2, "Home",
                    item4, item3);
            //Trade should have same ID and location in trades
            update();
            assertEquals(trader1.getRequestedTrades().get(0), trader2.getRequestedTrades().get(0));
            assertEquals(trader1.getRequestedTrades().get(0), trade1.getId());

            Trade editedTrade = getTrade(trader1.getRequestedTrades().get(0));

            // Comparing edited trade to the parameters we passed to it
            update();
            assertEquals(editedTrade.getId(), trade1.getId());
            assertEquals(editedTrade.getFirstUserOffer(), item3);
            assertEquals(editedTrade.getSecondUserOffer(), item4);
            assertEquals(editedTrade.getMeetingTime(), goodDate);
            assertEquals(editedTrade.getMeetingLocation(), "Home");
            assertEquals(editedTrade.getSecondMeetingTime(), goodDate2);

            try {
                tradingManager.counterTradeOffer(trader1.getId(), trade1.getId(), goodDate, null, "...",
                        item2, item1);
                fail("You cant send an offer for an item you dont have");
            } catch (CannotTradeException e) {
                assertEquals("One of the traders does not have the required item!", e.getMessage());
            }

            try {
                tradingManager.counterTradeOffer(trader2.getId(), trade1.getId(), goodDate, null, "...",
                        item2, item1);
                fail("This trader already sent an offer.");
            } catch (CannotTradeException e) {
                assertEquals("A previous trade offer has already been sent", e.getMessage());
            }

            tradingManager.counterTradeOffer(trader1.getId(), trade1.getId(), goodDate, null, "...",
                    item1, item2);

            //Trade should have same ID and location in trades
            update();
            assertEquals(trader1.getRequestedTrades().get(0), trader2.getRequestedTrades().get(0));
            assertEquals(trader1.getRequestedTrades().get(0), trade1.getId());

            editedTrade = getTrade(trader1.getRequestedTrades().get(0));

            // Comparing edited trade to the parameters we passed to it
            update();
            assertEquals(editedTrade.getId(), trade1.getId());
            assertEquals(editedTrade.getFirstUserOffer(), item1);
            assertEquals(editedTrade.getSecondUserOffer(), item2);

            try{
                tradingManager.counterTradeOffer(trader2.getId(), trade1.getId(), goodDate, null, "...",
                        item4, item3);
                fail("Trade limit should be exceeded");
            }
            catch(CannotTradeException e){
                assertEquals("Too many edits. Trade is cancelled.", e.getMessage());
            }
        } catch (CannotTradeException | UserNotFoundException | AuthorizationException | TradeNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLendTrade(){
        try {
            String item1 = trader1.getAvailableItems().get(0);
            String item2 = trader2.getAvailableItems().get(0);


            trader1.setTradeCount(10);
            userDatabase.update(trader1);

            try {
                tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, null, "Home",
                        item1, "", 1, "This is a lend");
                fail("Trade limit is exceeded");
            }
            catch (CannotTradeException e){
                assertEquals("This user cannot trade due to trading restrictions", e.getMessage());
            }
            trader1.setTradeCount(0);
            userDatabase.update(trader1);
            Trade t = getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, null, "Home",
                        item1, "", 1, "This is a lend"));

            update();
            assertEquals(trader1.getRequestedTrades().get(0), trader2.getRequestedTrades().get(0));
            assertEquals(trader1.getAvailableItems().size(), 3);
            assertEquals(trader2.getAvailableItems().size(), 3);
            tradingManager.acceptRequest(trader1.getId(), t.getId());
            tradingManager.acceptRequest(trader2.getId(), t.getId());
            update();
            assertEquals(trader1.getAcceptedTrades().get(0), trader2.getAcceptedTrades().get(0));
            assertEquals(trader1.getRequestedTrades().size(), 0);
            assertEquals(0, trader2.getRequestedTrades().size());

            tradingManager.confirmMeetingGeneral(trader1.getId(), t.getId(), true);

            update();
            assertEquals(trader1.getAvailableItems().size(), 2);
            assertEquals(trader2.getAvailableItems().size(), 3);

            tradingManager.confirmMeetingGeneral(trader2.getId(), t.getId(), true);

            update();
            assertEquals(1, trader1.getCompletedTrades().size());
            assertEquals(0, trader1.getAcceptedTrades().size());
            assertEquals(2, trader1.getAvailableItems().size());

            assertEquals(1, trader2.getCompletedTrades().size());
            assertEquals(0, trader2.getAcceptedTrades().size());
            assertEquals(4, trader2.getAvailableItems().size());
            assertEquals(item1, trader2.getAvailableItems().get(trader2.getAvailableItems().size()-1));

            assertEquals(1, trader1.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsLent());
            assertEquals(0, trader1.getTotalItemsBorrowed());
            assertEquals(0, trader2.getTotalItemsBorrowed());
            assertEquals(1, trader1.getTradeCount());
            assertEquals(1, trader2.getTradeCount());

            String item3 = trader1.getAvailableItems().get(0);
            String item4 = trader2.getAvailableItems().get(0);
            try {
                tradingManager.requestTrade(trader2.getId(), trader1.getId(), goodDate, goodDate2, "home",
                        item3, "", 1, "This is a lend2");
                fail("Offering an item i dont' have");
            }
            catch (AuthorizationException e){
                assertEquals("The trade offer contains an item that the user does not have", e.getMessage());
            }
            t= getTrade(tradingManager.requestTrade(trader2.getId(), trader1.getId(), goodDate, goodDate2, "home",
                    item4, "", 1, "This is a lend2"));

            tradingManager.acceptRequest(trader1.getId(), t.getId());
            update();
            assertEquals(3, trader2.getAvailableItems().size());
            assertEquals(2, trader1.getAvailableItems().size());
            assertEquals(1, trader2.getOngoingItems().size());
            assertEquals(0, trader1.getOngoingItems().size());
            tradingManager.confirmMeetingGeneral(trader1.getId(), t.getId(), true);
            tradingManager.confirmMeetingGeneral(trader2.getId(), t.getId(), true);


            update();
            assertEquals(1, trader1.getTradeCount());
            assertEquals(1, trader2.getTradeCount());
            assertEquals(1, trader1.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsLent());
            assertEquals(3, trader2.getAvailableItems().size());
            assertEquals(2, trader1.getAvailableItems().size());
            assertEquals(0, trader2.getOngoingItems().size());
            assertEquals(1, trader1.getOngoingItems().size());
            tradingManager.confirmMeetingGeneral(trader1.getId(), t.getId(), true);
            tradingManager.confirmMeetingGeneral(trader2.getId(), t.getId(), true);
            update();
            assertEquals(2, trader1.getTradeCount());
            assertEquals(2, trader2.getTradeCount());
            assertEquals(1, trader1.getTotalItemsLent());
            assertEquals(1, trader2.getTotalItemsLent());
            assertEquals(4, trader2.getAvailableItems().size());
            assertEquals(2, trader1.getAvailableItems().size());
            assertEquals(0, trader2.getOngoingItems().size());
            assertEquals(0, trader1.getOngoingItems().size());

        } catch (UserNotFoundException | TradeNotFoundException | AuthorizationException | CannotTradeException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testBorrowTrade(){
        try{

            String item1 = trader1.getAvailableItems().get(0);
            String item2 = trader2.getAvailableItems().get(0);

            try {
                tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, null, "Home",
                        "", item1, 1, "This is a lend");
                fail("Did not lend first");
            }
            catch (CannotTradeException e){
                assertEquals("You have not lent enough to borrow", e.getMessage());
            }

            trader1.setTotalItemsLent(1);
            userDatabase.update(trader1);

            try {
                tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, null, "Home",
                        "", item1, 1, "This is a lend");
                fail("The item does not exist in the other person's inventory");
            }
            catch (AuthorizationException e){
                assertEquals("The trade offer contains an item that the user does not have", e.getMessage());
            }

            trader1.setTradeCount(10);
            userDatabase.update(trader1);

            try {
                tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, null, "Home",
                        "", item2, 1, "This is a lend");
                fail("Trade limit is exceeded");
            }
            catch (CannotTradeException e){
                assertEquals("This user cannot trade due to trading restrictions", e.getMessage());
            }
            trader1.setTradeCount(0);
            userDatabase.update(trader1);
            Trade t = getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, null, "Home",
                    "", item2, 1, "This is a lend"));

            update();
            assertEquals(trader1.getRequestedTrades().get(0), trader2.getRequestedTrades().get(0));
            assertEquals(trader1.getAvailableItems().size(), 3);
            assertEquals(trader2.getAvailableItems().size(), 3);
            tradingManager.acceptRequest(trader1.getId(), t.getId());
            tradingManager.acceptRequest(trader2.getId(), t.getId());
            update();
            assertEquals(trader1.getAcceptedTrades().get(0), trader2.getAcceptedTrades().get(0));
            assertEquals(trader1.getRequestedTrades().size(), 0);
            assertEquals(0, trader2.getRequestedTrades().size());

            tradingManager.confirmMeetingGeneral(trader1.getId(), t.getId(), true);

            update();
            assertEquals(trader1.getAvailableItems().size(), 3);
            assertEquals(trader2.getAvailableItems().size(), 2);

            tradingManager.confirmMeetingGeneral(trader2.getId(), t.getId(), true);

            update();
            assertEquals(1, trader1.getCompletedTrades().size());
            assertEquals(0, trader1.getAcceptedTrades().size());
            assertEquals(2, trader2.getAvailableItems().size());

            assertEquals(1, trader2.getCompletedTrades().size());
            assertEquals(0, trader2.getAcceptedTrades().size());
            assertEquals(4, trader1.getAvailableItems().size());
            assertEquals(item2, trader1.getAvailableItems().get(trader1.getAvailableItems().size()-1));

            assertEquals(1, trader1.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsLent());
            assertEquals(1, trader1.getTotalItemsBorrowed());
            assertEquals(0, trader2.getTotalItemsBorrowed());
            assertEquals(1, trader1.getTradeCount());
            assertEquals(1, trader2.getTradeCount());

            String item3 = trader1.getAvailableItems().get(0);
            String item4 = trader2.getAvailableItems().get(0);

            try {
                tradingManager.requestTrade(trader2.getId(), trader1.getId(), goodDate, goodDate2, "home",
                        "",item3, 1, "This is a lend2");
                fail("Have not borrowed");
            }
            catch (CannotTradeException e){
                assertEquals("You have not lent enough to borrow", e.getMessage());
            }

            trader2.setTotalItemsLent(1);
            userDatabase.update(trader2);

            try {
                tradingManager.requestTrade(trader2.getId(), trader1.getId(), goodDate, goodDate2, "home",
                        "",item4, 1, "This is a lend2");
                fail("Offering an item i dont' have");
            }
            catch (AuthorizationException e){
                assertEquals("The trade offer contains an item that the user does not have", e.getMessage());
            }
            t= getTrade(tradingManager.requestTrade(trader2.getId(), trader1.getId(), goodDate, goodDate2, "home",
                    "", item3, 1, "This is a lend2"));

            tradingManager.acceptRequest(trader1.getId(), t.getId());
            update();
            assertEquals(2, trader2.getAvailableItems().size());
            assertEquals(3, trader1.getAvailableItems().size());
            assertEquals(0, trader2.getOngoingItems().size());
            assertEquals(1, trader1.getOngoingItems().size());

            tradingManager.confirmMeetingGeneral(trader1.getId(), t.getId(), true);
            tradingManager.confirmMeetingGeneral(trader2.getId(), t.getId(), true);


            update();
            assertEquals(1, trader1.getTradeCount());
            assertEquals(1, trader2.getTradeCount());
            assertEquals(1, trader1.getTotalItemsLent());
            assertEquals(1, trader2.getTotalItemsLent());
            assertEquals(1, trader1.getTotalItemsBorrowed());
            assertEquals(0, trader2.getTotalItemsBorrowed());
            assertEquals(2, trader2.getAvailableItems().size());
            assertEquals(3, trader1.getAvailableItems().size());
            assertEquals(1, trader2.getOngoingItems().size());
            assertEquals(0, trader1.getOngoingItems().size());
            tradingManager.confirmMeetingGeneral(trader1.getId(), t.getId(), true);
            tradingManager.confirmMeetingGeneral(trader2.getId(), t.getId(), true);
            update();
            assertEquals(2, trader1.getTradeCount());
            assertEquals(2, trader2.getTradeCount());
            assertEquals(1, trader1.getTotalItemsLent());
            assertEquals(1, trader2.getTotalItemsLent());
            assertEquals(1, trader1.getTotalItemsBorrowed());
            assertEquals(1, trader2.getTotalItemsBorrowed());
            assertEquals(2, trader2.getAvailableItems().size());
            assertEquals(4, trader1.getAvailableItems().size());
            assertEquals(0, trader2.getOngoingItems().size());
            assertEquals(0, trader1.getOngoingItems().size());

        } catch (UserNotFoundException | TradeNotFoundException | AuthorizationException | CannotTradeException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testRescindTradeOffer(){
        try {
            String item1 = trader1.getAvailableItems().get(0);
            String item2 = trader1.getAvailableItems().get(1);
            String item4 = trader2.getAvailableItems().get(0);
            String item5 = trader2.getAvailableItems().get(1);
            trader1.setTotalItemsLent(trader1.getTotalItemsLent() + 1);
            userDatabase.update(trader1);
            Trade t1 = getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, goodDate2, "home",
                    item1, "", 1, "This is a lend2"));
            Trade t2 =getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, null, "home",
                    item2, item4, 1, "This is a lend2"));
            Trade t3 =getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, null, "home",
                    "", item5, 1, "This is a lend2"));

            update();
            assertTrue(trader1.getRequestedTrades().contains(t1.getId()));
            assertTrue(trader1.getRequestedTrades().contains(t2.getId()));
            assertTrue(trader1.getRequestedTrades().contains(t3.getId()));
            assertTrue(trader2.getRequestedTrades().contains(t1.getId()));
            assertTrue(trader2.getRequestedTrades().contains(t2.getId()));
            assertTrue(trader2.getRequestedTrades().contains(t3.getId()));
            // Only the sender, trader1, has accepted this trade

            tradingManager.rescindTradeRequest(t1.getId());
            update();
            assertFalse(trader1.getRequestedTrades().contains(t1.getId()));
            assertFalse(trader2.getRequestedTrades().contains(t1.getId()));

            // This trade will be accepted.
            tradingManager.acceptRequest(trader2.getId(), t2.getId());
            // Now it is accepted.
            try{
                tradingManager.rescindTradeRequest(t2.getId());
                fail();
            } catch (Exception e){

            }

            // This other trade will be confirmed
            tradingManager.acceptRequest(trader2.getId(), t3.getId());
            tradingManager.confirmMeetingGeneral(trader1.getId(), t3.getId(), true);
            tradingManager.confirmMeetingGeneral(trader2.getId(), t3.getId(), true);
            try{
                tradingManager.rescindTradeRequest(t3.getId());
                fail();
            } catch (Exception e){

            }

        }
        catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testRescindAcceptedTrade(){
        try {
            String item1 = trader1.getAvailableItems().get(0);
            String item2 = trader1.getAvailableItems().get(1);
            String item4 = trader2.getAvailableItems().get(0);
            String item5 = trader2.getAvailableItems().get(1);
            trader1.setTotalItemsLent(1);
            userDatabase.update(trader1);
            Trade t1 = getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, goodDate2, "home",
                    item1, "", 1, "This is a lend2"));
            Trade t2 =getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, null, "home",
                    item2, item4, 1, "This is a lend2"));
            Trade t3 =getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, null, "home",
                    "", item5, 1, "This is a lend2"));

            update();
            assertTrue(trader1.getRequestedTrades().contains(t1.getId()));
            assertTrue(trader1.getRequestedTrades().contains(t2.getId()));
            assertTrue(trader1.getRequestedTrades().contains(t3.getId()));
            assertTrue(trader2.getRequestedTrades().contains(t1.getId()));
            assertTrue(trader2.getRequestedTrades().contains(t2.getId()));
            assertTrue(trader2.getRequestedTrades().contains(t3.getId()));
            // Only the sender, trader1, has accepted this trade
            update();
            try{
                tradingManager.rescindOngoingTrade(t1.getId());
                fail();
            } catch (Exception e){

            }


            // This trade will be accepted.
            tradingManager.acceptRequest(trader2.getId(), t2.getId());
            // Now it is accepted.
            update();
            assertFalse(trader1.getRequestedTrades().contains(t2.getId()));
            assertFalse(trader2.getRequestedTrades().contains(t2.getId()));
            assertTrue(trader1.getAcceptedTrades().contains(t2.getId()));
            assertTrue(trader2.getAcceptedTrades().contains(t2.getId()));
            assertFalse(trader1.getAvailableItems().contains(item2));
            assertFalse(trader2.getAvailableItems().contains(item4));
            tradingManager.rescindOngoingTrade(t2.getId());
            update();
            assertFalse(trader1.getAcceptedTrades().contains(t2.getId()));
            assertFalse(trader1.getAcceptedTrades().contains(t2.getId()));
            assertTrue(trader1.getAvailableItems().contains(item2));
            assertTrue(trader2.getAvailableItems().contains(item4));
            // This other trade will be confirmed
            tradingManager.acceptRequest(trader2.getId(), t3.getId());
            tradingManager.confirmMeetingGeneral(trader1.getId(), t3.getId(), true);
            tradingManager.confirmMeetingGeneral(trader2.getId(), t3.getId(), true);
            tradingManager.confirmMeetingGeneral(trader1.getId(), t3.getId(), true);
            tradingManager.confirmMeetingGeneral(trader2.getId(), t3.getId(), true);
            try{
                tradingManager.rescindTradeRequest(t3.getId());
                fail();
            } catch (Exception e){

            }
            Trade lend = getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, goodDate2, "home",
                    item1, "", 1, "This is a lend2"));
            tradingManager.acceptRequest(trader2.getId(), lend.getId());
            tradingManager.rescindOngoingTrade(lend.getId());
            update();
            assertEquals(trader1.getAvailableItems().get(trader1.getAvailableItems().size() - 1), item1);
            assertFalse(trader1.getAvailableItems().contains(""));
            assertFalse(trader2.getAvailableItems().contains(""));
            assertEquals(1, trader1.getTradeCount());
            assertEquals(1, trader2.getTradeCount());
            assertEquals(1, trader1.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsBorrowed());
            assertEquals(1, trader1.getTotalItemsBorrowed());
            assertEquals(0, trader2.getTotalItemsLent());

            trader1.setTotalItemsLent(2);
            userDatabase.update(trader1);
            Trade borrow = getTrade(tradingManager.requestTrade(trader1.getId(), trader2.getId(), goodDate, goodDate2, "home",
                    "", item4, 1, "This is a lend2"));
            tradingManager.acceptRequest(trader2.getId(), borrow.getId());
            tradingManager.rescindOngoingTrade(borrow.getId());
            update();
            assertEquals(trader1.getAvailableItems().get(trader1.getAvailableItems().size() - 1), item1);
            assertFalse(trader1.getAvailableItems().contains(""));
            assertFalse(trader2.getAvailableItems().contains(""));
            assertEquals(1, trader1.getTradeCount());
            assertEquals(1, trader2.getTradeCount());
            assertEquals(2, trader1.getTotalItemsLent());
            assertEquals(0, trader2.getTotalItemsBorrowed());
            assertEquals(1, trader1.getTotalItemsBorrowed());
            assertEquals(0, trader2.getTotalItemsLent());
        }
        catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }

    private void update(){
        try {
            trader1 = getTrader(trader1.getId());
            trader2 = getTrader(trader2.getId());
            admin = (Admin) getUser(admin.getId());

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

    private Trade trade(String id1, String id2, Date date1, Date date2, String location, String item1, String item2, int edits, String message){
        Trade t = new Trade(id1, id2, date1, date2, location,
                item1, item2, edits, message);
        return t;
    }

}
