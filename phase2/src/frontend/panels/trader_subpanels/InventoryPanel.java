package frontend.panels.trader_subpanels;

import javax.swing.JPanel;

import java.awt.*;

import backend.models.users.Trader;

public class InventoryPanel extends JPanel {
    public InventoryPanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) {
        this.setSize(1000, 900);
        this.setBackground(Color.BLUE);
    }
}