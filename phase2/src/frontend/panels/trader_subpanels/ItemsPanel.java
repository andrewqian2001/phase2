package frontend.panels.trader_subpanels;

import javax.swing.*;

import backend.exceptions.TradableItemNotFoundException;
import backend.models.TradableItem;
import backend.models.users.Trader;
import backend.tradesystem.managers.TradingManager;

import java.awt.*;
import java.io.IOException;
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

    public ItemsPanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {

        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setSize(1000, 900);
        this.setBackground(bg);

        tradeManager = new TradingManager();

        inventoryTitleContainer = new JPanel(new GridLayout(1, 2));
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
        getInventory();
        inventoryItemsScrollPane.setViewportView(inventoryItemsContainer);

        wishlistTitleContainer = new JPanel(new GridLayout(1, 2));
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
        getWishlist();
        wishlistItemsScrollPane.setViewportView(wishlistItemsContainer);

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
        inventoryItemsContainer = new JPanel(new GridLayout(trader.getAvailableItems().size(), 1));
        inventoryItemsContainer.setBackground(gray);
        inventoryItemsContainer.setBorder(null);

        for (String itemId : trader.getAvailableItems()) {
            try {
                TradableItem item = tradeManager.getTradableItem(itemId);
                JPanel itemPanel = new JPanel(new GridLayout(1, 5, 10, 0));
                itemPanel.setPreferredSize(new Dimension(1000, 75));
                itemPanel.setBackground(gray);
                itemPanel.setBorder(BorderFactory.createLineBorder(bg));

                JLabel itemName = new JLabel(item.getName());
                itemName.setFont(regular.deriveFont(20f));
                itemName.setForeground(Color.BLACK);
                itemName.setHorizontalAlignment(JLabel.LEFT);
                itemName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                JLabel itemDesc = new JLabel(item.getDesc());
                itemDesc.setFont(regular.deriveFont(20f));
                itemDesc.setForeground(Color.BLACK);
                itemDesc.setHorizontalAlignment(JLabel.LEFT);

                JButton removeitemButton = new JButton("Remove");
                removeitemButton.setFont(bold.deriveFont(20f));
                removeitemButton.setForeground(Color.WHITE);
                removeitemButton.setBackground(red);
                removeitemButton.setOpaque(true);
                removeitemButton.setBorder(BorderFactory.createLineBorder(gray, 15));

                itemPanel.add(itemName);
                itemPanel.add(itemDesc);
                itemPanel.add(removeitemButton);
                inventoryItemsContainer.add(itemPanel);
            } catch (TradableItemNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
    
    private void getWishlist() {
        // trader.getWishlist();
        wishlistItemsContainer = new JPanel(new GridLayout(trader.getWishlist().size(), 1));
        wishlistItemsContainer.setBackground(gray);
        wishlistItemsContainer.setBorder(null);
        
        for (String itemId : trader.getWishlist()) {
            try {
                TradableItem item = tradeManager.getTradableItem(itemId);
                JPanel itemPanel = new JPanel(new GridLayout(1, 5, 10, 0));
                itemPanel.setPreferredSize(new Dimension(1000, 75));
                itemPanel.setBackground(gray);
                itemPanel.setBorder(BorderFactory.createLineBorder(bg));

                JLabel itemName = new JLabel(item.getName());
                itemName.setFont(regular.deriveFont(20f));
                itemName.setForeground(Color.BLACK);
                itemName.setHorizontalAlignment(JLabel.LEFT);
                itemName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                JLabel itemDesc = new JLabel(item.getDesc());
                itemDesc.setFont(regular.deriveFont(20f));
                itemDesc.setForeground(Color.BLACK);
                itemDesc.setHorizontalAlignment(JLabel.LEFT);

                JButton removeitemButton = new JButton("Remove");
                removeitemButton.setFont(bold.deriveFont(20f));
                removeitemButton.setForeground(Color.WHITE);
                removeitemButton.setBackground(red);
                removeitemButton.setOpaque(true);
                removeitemButton.setBorder(BorderFactory.createLineBorder(gray, 15));

                itemPanel.add(itemName);
                itemPanel.add(itemDesc);
                itemPanel.add(removeitemButton);
                wishlistItemsContainer.add(itemPanel);

            } catch (TradableItemNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}
