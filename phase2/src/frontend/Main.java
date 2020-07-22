package frontend;

import java.awt.*;
import java.io.*;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This is where the program starts running
 */
public class Main {
    public static void main(String[] args) {
        try {
            //THE NEXT LINE SHOULD BE REMOVED AFTER WE'RE DONE (sets up two traders)
            new TemporarySetup();

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            WindowManager windowManager = new WindowManager();
            windowManager.run();
        } catch (IOException | FontFormatException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println(e.getMessage());
        }
    }
}
