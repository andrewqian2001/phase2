package frontend;

import javax.swing.UIManager;

/**
 * This is where the program starts running
 */
    public class Main {
    public static void main(String[] args) {
        try {
                // new TemporarySetup();
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            WindowManager windowManager = new WindowManager();
            windowManager.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
