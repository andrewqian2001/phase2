package frontend.panels.trader_subpanels;

import javax.swing.JPanel;

import java.awt.*;

import backend.models.users.Trader;

public class TradePanel extends JPanel {

    private Color bg = new Color(51, 51, 51);

    public TradePanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) {
        this.setSize(1000, 900);
        this.setBackground(bg);
    }
}