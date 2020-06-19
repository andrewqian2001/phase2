package trades;

import main.Manager;

import java.io.IOException;
import java.io.Serializable;

public class TradeManager extends Manager<Trade> implements Serializable {
    public TradeManager(String filePath) throws IOException {
        super(filePath);
    }
}
