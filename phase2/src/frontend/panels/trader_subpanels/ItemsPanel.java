package frontend.panels.trader_subpanels;

import javax.swing.*;

import backend.models.users.Trader;
import backend.tradesystem.managers.TradingManager;

import java.awt.*;
import java.util.ArrayList;

public class ItemsPanel extends JPanel {

    private Trader trader;
    private Font regular, bold, italic, boldItalic;

    private JScrollPane inventoryItemsScrollPane, wishlistItemsScrollPane;
    private JPanel inventoryTitleContainer, wishlistTitleContainer, inventoryItemsContainer, wishlistItemsContainer;
    private JButton addInventoryItemButton, addWishlistItemButton;
    private JLabel inventoryTitle, wishlistTitle;

    private Color bg = new Color(51, 51, 51);
    private Color blue = new Color(0, 240, 239);
    private Color red = new Color(219, 58, 52);
    private Color gray = new Color(196, 196, 196);

    private TradingManager tradeManager;

    public ItemsPanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) {

        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setSize(1000, 900);
        this.setBackground(bg);

        inventoryTitleContainer = new JPanel(new GridLayout(1,2));
        inventoryTitleContainer.setPreferredSize(new Dimension(1200, 75));
        inventoryTitleContainer.setBackground(bg);

        inventoryTitle = new JLabel("Inventory");
        inventoryTitle.setForeground(Color.WHITE);
        inventoryTitle.setFont(regular.deriveFont(30f));

        addInventoryItemButton = new JButton("Request Item to Inventory");
        addInventoryItemButton.setForeground(Color.CYAN);
        addInventoryItemButton.setBackground(bg);
        addInventoryItemButton.setFont(boldItalic.deriveFont(25f));
        addInventoryItemButton.setHorizontalAlignment(JButton.RIGHT);
        addInventoryItemButton.setOpaque(true);
        addInventoryItemButton.setBorderPainted(false);

        inventoryItemsScrollPane = new JScrollPane();
        inventoryItemsScrollPane.setBorder(null);
        inventoryItemsScrollPane.setPreferredSize(new Dimension(1200, 325));

        wishlistTitleContainer = new JPanel(new GridLayout(1,2));
        wishlistTitleContainer.setPreferredSize(new Dimension(1200, 75));
        wishlistTitleContainer.setBackground(bg);

        wishlistTitle = new JLabel("Wishlist");
        wishlistTitle.setForeground(Color.WHITE);
        wishlistTitle.setFont(regular.deriveFont(30f));

        addWishlistItemButton = new JButton("Add Item to Wishlist");
        addWishlistItemButton.setForeground(Color.CYAN);
        addWishlistItemButton.setBackground(bg);
        addWishlistItemButton.setFont(boldItalic.deriveFont(25f));
        addWishlistItemButton.setHorizontalAlignment(JButton.RIGHT);
        addWishlistItemButton.setOpaque(true);
        addWishlistItemButton.setBorderPainted(false);

        wishlistItemsScrollPane = new JScrollPane();
        wishlistItemsScrollPane.setBorder(null);
        wishlistItemsScrollPane.setPreferredSize(new Dimension(1200, 325));

        inventoryTitleContainer.add(inventoryTitle);
        inventoryTitleContainer.add(addInventoryItemButton);

        wishlistTitleContainer.add(wishlistTitle);
        wishlistTitleContainer.add(addWishlistItemButton);

        this.add(inventoryTitleContainer);
        this.add(inventoryItemsScrollPane);
        this.add(wishlistTitleContainer);
        this.add(wishlistItemsScrollPane);
    }

    private void getInventory() {
        // trader.getAvailableItems();
    }
    
    private void getWishlist() {
        // trader.getWishlist();

    }
}
