package frontend.panels.admin_subpanels;

import backend.models.users.User;

import javax.swing.*;
import java.awt.*;

public class OverviewPanel extends JPanel {
    public OverviewPanel(User user, Font regular, Font bold, Font italic, Font boldItalic) {
        this.setSize(1000, 900);
        this.setBackground(Color.BLACK);
    }
}
