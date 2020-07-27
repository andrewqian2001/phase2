package frontend.panels.trader_subpanels;

import javax.swing.*;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.TradableItem;
import backend.models.users.Trader;
import backend.tradesystem.managers.TraderManager;
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
    private Color gray = new Color(196, 196, 196);
    private Color gray2 = new Color(142, 142, 142);
    private Color green = new Color(27, 158, 36);
    private Color red = new Color(219, 58, 52);

    private TradingManager tradeManager;
    private TraderManager traderManager;

    public ItemsPanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {

        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setSize(1000, 900);
        this.setBackground(bg);

        tradeManager = new TradingManager();
        traderManager = new TraderManager();

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
        addInventoryItemButton.addActionListener(e -> {
            JDialog addNewItemModal = new JDialog();
            addNewItemModal.setTitle("Add New Item");
            addNewItemModal.setSize(500, 500);
            addNewItemModal.setResizable(false);
            addNewItemModal.setLocationRelativeTo(null);

            JPanel addNewItemPanel = new JPanel();
            addNewItemPanel.setPreferredSize(new Dimension(500, 500));
            addNewItemPanel.setBackground(bg);

            JLabel itemNameTitle = new JLabel("Item Name");
            itemNameTitle.setFont(italic.deriveFont(20f));
            itemNameTitle.setPreferredSize(new Dimension(450, 50));
            itemNameTitle.setOpaque(false);
            itemNameTitle.setForeground(Color.WHITE);

            JTextField itemNameInput = new JTextField();
            itemNameInput.setFont(regular.deriveFont(20f));
            itemNameInput.setBackground(gray2);
            itemNameInput.setForeground(Color.BLACK);
            itemNameInput.setPreferredSize(new Dimension(450, 50));
            itemNameInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel itemDescTitle = new JLabel("Short Description of Item:");
            itemDescTitle.setFont(italic.deriveFont(20f));
            itemDescTitle.setPreferredSize(new Dimension(450, 50));
            itemDescTitle.setOpaque(false);
            itemDescTitle.setForeground(Color.WHITE);

            JTextField itemDescInput = new JTextField();
            itemDescInput.setFont(regular.deriveFont(20f));
            itemDescInput.setBackground(gray2);
            itemDescInput.setForeground(Color.BLACK);
            itemDescInput.setPreferredSize(new Dimension(450, 50));
            itemDescInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JButton itemSubmitButton = new JButton("Submit Request");
            itemSubmitButton.setFont(bold.deriveFont(25f));
            itemSubmitButton.setBackground(green);
            itemSubmitButton.setOpaque(true);
            itemSubmitButton.setForeground(Color.WHITE);
            itemSubmitButton.setPreferredSize(new Dimension(225, 75));
            itemSubmitButton.setBorder(BorderFactory.createLineBorder(bg, 15));
            itemSubmitButton.addActionListener(event -> {
                if (itemNameInput.getText().trim().length() > 0 && itemDescInput.getText().trim().length() > 0) {
                    try {
                        traderManager.addRequestItem(trader.getId(), itemNameInput.getText().trim(), itemDescInput.getText().trim());
                        addNewItemModal.dispose();
                    } catch (UserNotFoundException | AuthorizationException e1) {
                        System.out.println(e1.getMessage());
                    }
                }
            });

            addNewItemPanel.add(itemNameTitle);
            addNewItemPanel.add(itemNameInput);
            addNewItemPanel.add(itemDescTitle);
            addNewItemPanel.add(itemDescInput);

            addNewItemModal.add(addNewItemPanel);
            addNewItemModal.add(itemSubmitButton, BorderLayout.SOUTH);
            addNewItemModal.setModal(true);
            addNewItemModal.setVisible(true);
        });

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
        int numRows = trader.getAvailableItems().size();
        if(numRows < 4) numRows = 4;
        inventoryItemsContainer = new JPanel(new GridLayout(numRows, 1));
        inventoryItemsContainer.setBackground(gray2);
        inventoryItemsContainer.setBorder(null);

        for (String itemId : trader.getAvailableItems()) {
            try {
                TradableItem item = tradeManager.getTradableItem(itemId);
                JPanel itemPanel = new JPanel(new GridLayout(1, 4, 10, 0));
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

                JLabel itemIdTitle = new JLabel("<html><pre>#" + item.getId().substring(item.getId().length() - 12) + "</pre></html>");
                itemIdTitle.setFont(regular.deriveFont(20f));
                itemIdTitle.setForeground(Color.BLACK);
                itemIdTitle.setHorizontalAlignment(JLabel.LEFT);

                JButton removeItemButton = new JButton("Remove");
                removeItemButton.setFont(bold.deriveFont(20f));
                removeItemButton.setForeground(Color.WHITE);
                removeItemButton.setBackground(red);
                removeItemButton.setOpaque(true);
                removeItemButton.setBorder(BorderFactory.createLineBorder(gray, 15));
                removeItemButton.addActionListener(event -> {
                    // TODO: Call removeItem() method
                    System.out.println("Removing #" + itemId);
                });

                itemPanel.add(itemName);
                itemPanel.add(itemDesc);
                itemPanel.add(itemIdTitle);
                itemPanel.add(removeItemButton);
                inventoryItemsContainer.add(itemPanel);
            } catch (TradableItemNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
    
    private void getWishlist() {
        int numRows = trader.getWishlist().size();
        if(numRows < 4) numRows = 4;
        wishlistItemsContainer = new JPanel(new GridLayout(numRows, 1));
        wishlistItemsContainer.setBackground(gray2);
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

                JLabel itemIdTitle = new JLabel("<html><pre>#" + item.getId().substring(item.getId().length() - 12) + "</pre></html>");
                itemIdTitle.setFont(regular.deriveFont(20f));
                itemIdTitle.setForeground(Color.BLACK);
                itemIdTitle.setHorizontalAlignment(JLabel.LEFT);

                //TODO: use getowner method
                JLabel itemOwnerName = new JLabel(trader.getUsername());
                itemOwnerName.setFont(regular.deriveFont(20f));
                itemOwnerName.setForeground(Color.BLACK);
                itemOwnerName.setHorizontalAlignment(JLabel.CENTER);

                JButton removeItemButton = new JButton("Remove");
                removeItemButton.setFont(bold.deriveFont(20f));
                removeItemButton.setForeground(Color.WHITE);
                removeItemButton.setBackground(red);
                removeItemButton.setOpaque(true);
                removeItemButton.setBorder(BorderFactory.createLineBorder(gray, 15));
                removeItemButton.addActionListener(event -> {
                    //TODO: Call removeItem() method
                    System.out.println("Removing #"+itemId);
                });

                itemPanel.add(itemName);
                itemPanel.add(itemDesc);
                itemPanel.add(itemIdTitle);
                itemPanel.add(itemOwnerName);
                itemPanel.add(removeItemButton);
                wishlistItemsContainer.add(itemPanel);

            } catch (TradableItemNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}
