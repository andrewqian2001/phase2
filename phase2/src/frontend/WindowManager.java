package frontend;

import java.awt.FontFormatException;
import java.io.IOException;

public class WindowManager {
    public void start() {
        Login l = new Login();
        try {
            l.initialize();
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }
}
