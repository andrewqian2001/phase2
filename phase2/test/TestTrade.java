import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserNotFoundException;
import backend.models.TradableItem;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.tradesystem.UserTypes;
import backend.tradesystem.managers.HandleItemRequestsManager;
import backend.tradesystem.managers.LoginManager;
import backend.tradesystem.managers.TraderManager;
import org.junit.*;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestTrade {
    private TraderManager traderManager;
    private LoginManager loginManager;
    private HandleItemRequestsManager handleRequestsManager;
    private Trader trader1;
    private Trader trader2;
    private Admin admin;
    private final String USER_PATH = "./phase1/test/testUsers.ser";
    private final String TRADABLE_ITEM_PATH = "./phase1/test/testTradableItems.ser";
    private final String TRADE_PATH = "./phase1/test/testTrades.ser";

    @Before
    public void beforeEach() {
        try {
            traderManager = new TraderManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            loginManager = new LoginManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);
            handleRequestsManager = new HandleItemRequestsManager(USER_PATH, TRADABLE_ITEM_PATH, TRADE_PATH);

            trader1 = (Trader) loginManager.registerUser("user", "pass", UserTypes.TRADER);
            trader2 = (Trader) loginManager.registerUser("user1", "pass", UserTypes.TRADER);
            admin = (Admin) loginManager.registerUser("admin", "pass", UserTypes.ADMIN);
            TradableItem item1 = traderManager.addRequestItem(trader1.getId(), "apple", "sweet");
            TradableItem item2 = traderManager.addRequestItem(trader1.getId(), "apple1", "sweet1");
            TradableItem item3 = traderManager.addRequestItem(trader1.getId(), "apple2", "sweet2");
            TradableItem item4 = traderManager.addRequestItem(trader2.getId(), "pear1", "disgusting");
            TradableItem item5 = traderManager.addRequestItem(trader2.getId(), "pear2", "disgusting2");
            TradableItem item6 = traderManager.addRequestItem(trader2.getId(), "pear3", "disgusting3");
            handleRequestsManager.processItemRequest(trader1.getId(), item1.getId(), true);
            handleRequestsManager.processItemRequest(trader1.getId(), item2.getId(), true);
            handleRequestsManager.processItemRequest(trader1.getId(), item3.getId(), true);
            handleRequestsManager.processItemRequest(trader2.getId(), item4.getId(), true);
            handleRequestsManager.processItemRequest(trader2.getId(), item5.getId(), true);
            handleRequestsManager.processItemRequest(trader2.getId(), item6.getId(), true);
        } catch (IOException ignored) {
            System.err.println("ERRORS WITH SETTING UP DATABASE FILES");
        } catch (UserAlreadyExistsException ignored) {
            System.err.println("REGISTERING USER ERROR");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (AuthorizationException e) {
            e.printStackTrace();
        } catch (TradableItemNotFoundException e) {
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

}
