import backend.exceptions.*;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.UserTypes;
import backend.tradesystem.managers.*;
import backend.Database;
import java.util.Date;
import org.junit.*;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestTrade {
    private TraderManager traderManager;
    private LoginManager loginManager;
    private HandleItemRequestsManager handleRequestsManager;
    private TradingManager tradingManager;
    private Database<User>  userDatabase;
    private Database<Trade> tradeDatabase;
    private Database<TradableItem> tradableItemDatabase;

    private Trader trader1;
    private Trader trader2;
    private Admin admin;
    private final String USER_PATH = "./phase2/test/testUsers.ser";
    private final String TRADABLE_ITEM_PATH = "./phase2/test/testTradableItems.ser";
    private final String TRADE_PATH = "./phase2/test/testTrades.ser";
    private final String TRADER_PROPERTY_FILE_PATH = "./phase2/test/trader.properties";

    @Before
    public void beforeEach() {
        try {
            traderManager = new TraderManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            loginManager = new LoginManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH, TRADER_PROPERTY_FILE_PATH);
            tradingManager = new TradingManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            handleRequestsManager = new HandleItemRequestsManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            userDatabase = new Database<>(USER_PATH);
            tradeDatabase = new Database<>(TRADE_PATH);
            tradableItemDatabase = new Database<>(TRADABLE_ITEM_PATH);
            trader1 = (Trader) loginManager.registerUser("user", "pass", UserTypes.TRADER);
            trader2 = (Trader) loginManager.registerUser("user1", "pass", UserTypes.TRADER);
            admin = (Admin) loginManager.registerUser("admin", "pass", UserTypes.ADMIN);
            trader1 = traderManager.addRequestItem(trader1.getId(), "apple", "sweet");
            trader1 = traderManager.addRequestItem(trader1.getId(), "apple1", "sweet1");
            trader1 = traderManager.addRequestItem(trader1.getId(), "apple2", "sweet2");
            trader2 = traderManager.addRequestItem(trader2.getId(), "pear1", "disgusting");
            trader2 = traderManager.addRequestItem(trader2.getId(), "pear2", "disgusting2");
            trader2 = traderManager.addRequestItem(trader2.getId(), "pear3", "disgusting3");
            trader1 = handleRequestsManager.processItemRequest(trader1.getId(), trader1.getRequestedItems().get(0), true);
            trader1 = handleRequestsManager.processItemRequest(trader1.getId(), trader1.getRequestedItems().get(0), true);
            trader1 = handleRequestsManager.processItemRequest(trader1.getId(), trader1.getRequestedItems().get(0), true);
            trader2 = handleRequestsManager.processItemRequest(trader2.getId(), trader2.getRequestedItems().get(0), true);
            trader2 = handleRequestsManager.processItemRequest(trader2.getId(), trader2.getRequestedItems().get(0), true);
            trader2 = handleRequestsManager.processItemRequest(trader2.getId(), trader2.getRequestedItems().get(0), true);
        } catch (IOException ignored) {
            System.err.println("ERRORS WITH SETTING UP DATABASE FILES");
        } catch (UserAlreadyExistsException ignored) {
            System.err.println("REGISTERING USER ERROR");
        } catch (UserNotFoundException | AuthorizationException | TradableItemNotFoundException e) {
            e.printStackTrace();
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
    }

    @Test
    public void testTemporaryTrade() {
        assertEquals("Should return the previous seed value\n", 1, 2);
    }

    @Test
    public void testPermanentTrade(){
        try {
            String item1 = trader1.getAvailableItems().get(0);
            String item2 = trader2.getAvailableItems().get(0);
            Trade trade = tradingManager.requestTrade(trader1.getId(), trader2.getId(), new Date(), null, "home",
                    item1, item2, 3);
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
            } catch (TradeNotFoundException e) {
                fail("Trade was not found for some reason");
            } catch (CannotTradeException e){
                //GOOD!
            }
            trader1.getAvailableItems().add(item1);
            userDatabase.update(trader1);
            //make sure trader1 can accept the trade
            try{
                tradingManager.acceptRequest(trader1.getId(), trade.getId());
                // Once trader2 accepts ...
                assertTrue(tradingManager.acceptRequest(trader2.getId(), trade.getId()));
            } catch (TradeNotFoundException e) {
                fail("Trade was not found for some reason");
            }
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
            tradingManager.confirmFirstMeeting(trader1.getId(), trade.getId(), true);
            tradingManager.confirmFirstMeeting(trader2.getId(), trade.getId(), true);

            //check the trade is now in the completed trades
            update();
            assertEquals(trader1.getCompletedTrades().get(0), trade.getId());
            assertEquals(trader2.getCompletedTrades().get(0), trade.getId());
            // check that the trade is no longer in the accepted trades
            assertTrue(trader1.getAcceptedTrades().size() == 0);
            assertTrue(trader2.getAcceptedTrades().size() == 0);
            // check that the items are in the correct pos
            assertEquals(trader1.getAvailableItems().get(trader1.getAvailableItems().size()-1), item2);
            assertEquals(trader2.getAvailableItems().get(trader2.getAvailableItems().size()-1), item1);


        } catch (UserNotFoundException | AuthorizationException | CannotTradeException | TradeNotFoundException e) {
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

            Trade trade1 = tradingManager.requestTrade(trader1.getId(), trader2.getId(), new Date(), null, "home",
                    item1, item2, 1);

            ///Trade is requested
            update();
            assertEquals(trader1.getRequestedTrades().get(0), trader2.getRequestedTrades().get(0));
            assertEquals(trader1.getRequestedTrades().get(0), trade1.getId());

            try {
                tradingManager.counterTradeOffer(trader1.getId(), trade1.getId(), new Date(), null, "...",
                        item3, item4);
                fail("This user should not be able to send an edited trade offer");
            } catch (CannotTradeException e) {
                assertEquals("A previous trade offer has already been sent", e.getMessage());
            }

            try {
                tradingManager.counterTradeOffer(trader2.getId(), trade1.getId(), new Date(), null, "...",
                        item3, item4);
                fail("This user is giving items they don't have");
            } catch (CannotTradeException e) {
                assertEquals("One of the traders does not have the required item!", e.getMessage());
            }

            tradingManager.counterTradeOffer(trader2.getId(), trade1.getId(), new Date(123), new Date(456), "Home",
                    item4, item3);
            //Trade should have same ID and location in trades
            update();
            assertEquals(trader1.getRequestedTrades().get(0), trader2.getRequestedTrades().get(0));
            assertEquals(trader1.getRequestedTrades().get(0), trade1.getId());

            Trade editedTrade = tradingManager.getTrade(trader1.getRequestedTrades().get(0));

            // Comparing edited trade to the parameters we passed to it
            update();
            assertEquals(editedTrade.getId(), trade1.getId());
            assertEquals(editedTrade.getFirstUserOffer(), item3);
            assertEquals(editedTrade.getSecondUserOffer(), item4);
            assertEquals(editedTrade.getMeetingTime(), new Date(123));
            assertEquals(editedTrade.getMeetingLocation(), "Home");
            assertEquals(editedTrade.getSecondMeetingTime(), new Date(456));

            try {
                tradingManager.counterTradeOffer(trader1.getId(), trade1.getId(), new Date(), null, "...",
                        item2, item1);
                fail("You cant send an offer for an item you dont have");
            } catch (CannotTradeException e) {
                assertEquals("One of the traders does not have the required item!", e.getMessage());
            }

            try {
                tradingManager.counterTradeOffer(trader2.getId(), trade1.getId(), new Date(), null, "...",
                        item2, item1);
                fail("This trader already sent an offer.");
            } catch (CannotTradeException e) {
                assertEquals("A previous trade offer has already been sent", e.getMessage());
            }

            tradingManager.counterTradeOffer(trader1.getId(), trade1.getId(), new Date(), null, "...",
                    item1, item2);

            //Trade should have same ID and location in trades
            update();
            assertEquals(trader1.getRequestedTrades().get(0), trader2.getRequestedTrades().get(0));
            assertEquals(trader1.getRequestedTrades().get(0), trade1.getId());

            editedTrade = tradingManager.getTrade(trader1.getRequestedTrades().get(0));

            // Comparing edited trade to the parameters we passed to it
            update();
            assertEquals(editedTrade.getId(), trade1.getId());
            assertEquals(editedTrade.getFirstUserOffer(), item1);
            assertEquals(editedTrade.getSecondUserOffer(), item2);

            try{
                tradingManager.counterTradeOffer(trader2.getId(), trade1.getId(), new Date(), null, "...",
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

    private void update(){
        try {
            trader1 = traderManager.getTrader(trader1.getId());
            trader2 = traderManager.getTrader(trader2.getId());
            admin = (Admin) traderManager.getUser(admin.getId());

        } catch (UserNotFoundException | AuthorizationException e) {
            e.printStackTrace();
        }

    }

}
