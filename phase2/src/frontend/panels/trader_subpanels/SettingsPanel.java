package frontend.panels.trader_subpanels;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;

import backend.models.users.Trader;

public class SettingsPanel extends JPanel {
    private Font regular, bold, italic, boldItalic;

    private Color bg = new Color(51, 51, 51);
    private Color gray = new Color(196, 196, 196);
    private Color gray2 = new Color(142, 142, 142);
    private Color green = new Color(27, 158, 36);
    private Color red = new Color(219, 58, 52);

    public SettingsPanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) {
        this.setBackground(bg);
    }
}