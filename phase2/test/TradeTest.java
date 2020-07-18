import backend.tradesystem.managers.TraderManager;
import org.junit.*;
import static org.junit.Assert.*;

public class TradeTest {
    private TraderManager traderManager;
    @Before
    public void setUp() {
        traderManager = new TraderManager()
    }


    // test hashing(int)
    @Test(timeout=50)
    public void testHashing1() {
        MyHashing h = new MyHashing();
        assertEquals("Should return the previous seed value\n", 100, h.hash(40));
    }

}
