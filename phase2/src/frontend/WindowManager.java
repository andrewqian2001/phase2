package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class WindowManager {

    public void start() throws IOException, FontFormatException {
        Login l = new Login();
        l.initialize();
    }
}
