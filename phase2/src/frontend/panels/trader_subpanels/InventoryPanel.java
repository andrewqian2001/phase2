package frontend.panels.trader_subpanels;

import javax.swing.*;

import java.awt.*;

import backend.models.users.Trader;

public class InventoryPanel extends JPanel {
    private Trader trader;
    private Font regular, bold, italic, boldItalic;
    private JScrollPane wishlistScrollPane;
    private JPanel wishlistContainer, wishlistTitleContainer;
    private JButton addWishlistButton;
    private JLabel wishlistTitle;

    private Color bg = new Color(51, 51, 51);
    private Color blue = new Color(0, 240, 239);
    private Color red = new Color(219, 58, 52);

    public InventoryPanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) {
        this.setSize(1000, 900);
        this.setBackground(Color.BLUE);
    }
}