import org.junit.*;
import static org.junit.Assert.*;

public class TradeTest {
    @Before
    public void setUp() {

    }


    // test hashing(int)
    @Test(timeout=50)
    public void testHashing1() {
        MyHashing h = new MyHashing();
        assertEquals("Should return the previous seed value\n", 100, h.hash(40));
    }

}
