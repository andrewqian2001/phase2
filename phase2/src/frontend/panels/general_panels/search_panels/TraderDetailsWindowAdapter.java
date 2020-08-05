package frontend.panels.general_panels.search_panels;

import frontend.WindowManager;

import java.awt.event.WindowAdapter;

/**
 * For showing JFrame of showing details of trader
 */
public class TraderDetailsWindowAdapter extends WindowAdapter {

    private final WindowManager frame;

    /**
     * Makes new JFrame
     * @param frame the frame
     */
    public TraderDetailsWindowAdapter(WindowManager frame) {
        super();
        this.frame = frame;
    }

    /**
     * Handling when window is closing
     * @param e the event
     */
    @Override
    public void windowClosing(java.awt.event.WindowEvent e) {
        frame.setVisible(true);
        e.getWindow().dispose();
    }

}
