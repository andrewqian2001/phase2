package frontend.panels;

import javax.swing.JPanel;

import java.awt.*;

import backend.models.users.Trader;
import backend.models.users.User;

public class SearchPanel extends JPanel {
    public SearchPanel(User user, Font regular, Font bold, Font italic, Font boldItalic) {
        this.setSize(1000, 900);
        this.setBackground(Color.CYAN);
    }
}