package frontend.panels.general_panels.search_panels;

import frontend.WindowManager;

import javax.swing.*;
import java.awt.event.WindowAdapter;

public class TraderDetailsWindowAdapter extends WindowAdapter {

    private WindowManager frame;

    public TraderDetailsWindowAdapter(WindowManager frame) {
        super();
        this.frame = frame;
    }

    @Override
    public void windowClosing(java.awt.event.WindowEvent e) {
        frame.setVisible(true);
        e.getWindow().dispose();
    }

}
