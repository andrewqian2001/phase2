package frontend;

import java.awt.*;
import java.io.IOException;

public class WindowManager {

    public void start() throws IOException, FontFormatException {
        Login l = new Login();
        l.initialize();
    }
}
