package frontend;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.UIManager;

import backend.tradesystem.DetectDatabaseChange;

/**
 * This is where the program starts running
 * Code partially taken from
 * https://stackoverflow.com/questions/54815226/how-can-i-detect-if-a-file-has-been-modified-using-lastmodified
 */
public class Main {
    public static void main(String[] args) {
        try {
            //new TemporarySetup();
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            WindowManager windowManager = new WindowManager();
            windowManager.run();
            TimerTask task = new DetectDatabaseChange() {
                protected void onChange() {
                    // If backend files change, this gets called
                    try {
                        if (!windowManager.getUserId().equals("")) {
                            windowManager.logout();
                            windowManager.login(windowManager.getUserId());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, new Date(), 500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
