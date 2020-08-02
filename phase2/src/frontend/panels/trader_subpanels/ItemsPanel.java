package frontend.panels.trader_subpanels;

import javax.swing.*;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.ItemQuery;
import backend.tradesystem.queries.UserQuery;
import backend.tradesystem.trader_managers.TraderManager;
import backend.tradesystem.trader_managers.TradingInfoManager;
import backend.tradesystem.trader_managers.TradingManager;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.IOException;

public class ItemsPanel extends JPanel {

    private String traderId;
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
    private TradingInfoManager infoManager;
    private final ItemQuery itemQuery = new ItemQuery();
    private final UserQuery userQuery = new UserQuery();

    public ItemsPanel(String traderId, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {

        this.traderId = traderId;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setSize(1000, 900);
        this.setBackground(bg);

        tradeManager = new TradingManager();
        traderManager = new TraderManager();
        infoManager = new TradingInfoManager();

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
                        traderManager.addRequestItem(traderId, itemNameInput.getText().trim(), itemDescInput.getText().trim());
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
        try {
            getInventory();
        } catch (UserNotFoundException | AuthorizationException e) {
            e.printStackTrace();
        }
        inventoryItemsScrollPane.setViewportView(inventoryItemsContainer);

        JPanel topInventoryItemsScrollHeaderPane = new JPanel(new GridLayout(1, 3));
        topInventoryItemsScrollHeaderPane.setPreferredSize(new Dimension(1200, 325));

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
        addWishlistItemButton.addActionListener(event -> {
            JDialog addNewItemModal = new JDialog();
            addNewItemModal.setTitle("Add Item to Wishlist");
            addNewItemModal.setSize(500, 500);
            addNewItemModal.setResizable(false);
            addNewItemModal.setLocationRelativeTo(null);

            JPanel addNewItemPanel = new JPanel();
            addNewItemPanel.setPreferredSize(new Dimension(500, 500));
            addNewItemPanel.setBackground(bg);

            JLabel itemNameTitle = new JLabel("Trader Username:");
            itemNameTitle.setFont(italic.deriveFont(20f));
            itemNameTitle.setPreferredSize(new Dimension(450, 50));
            itemNameTitle.setOpaque(false);
            itemNameTitle.setForeground(Color.WHITE);

            JComboBox<TraderComboBoxItem> traders = new JComboBox<>();
            traders.setPreferredSize(new Dimension(450, 50));
            traders.setFont(regular.deriveFont(20f));
            traders.setBackground(gray2);
            traders.setForeground(Color.BLACK);
            traders.setOpaque(true);
            infoManager.getAllTraders().forEach(id -> {
                if (!id.equals(this.traderId))
                    traders.addItem(new TraderComboBoxItem(id));
            });

            JLabel inventoryItemTitle = new JLabel("Item from their Inventory:");
            inventoryItemTitle.setFont(italic.deriveFont(20f));
            inventoryItemTitle.setPreferredSize(new Dimension(450, 50));
            inventoryItemTitle.setOpaque(false);
            inventoryItemTitle.setForeground(Color.WHITE);

            JComboBox<InventoryComboBoxItem> inventoryItems = new JComboBox<>();
            inventoryItems.setPreferredSize(new Dimension(450, 50));
            inventoryItems.setFont(regular.deriveFont(20f));
            inventoryItems.setBackground(gray2);
            inventoryItems.setForeground(Color.BLACK);
            inventoryItems.setOpaque(true);

            JButton itemSubmitButton = new JButton("Submit Request");
            itemSubmitButton.setFont(bold.deriveFont(25f));
            itemSubmitButton.setBackground(green);
            itemSubmitButton.setOpaque(true);
            itemSubmitButton.setForeground(Color.WHITE);
            itemSubmitButton.setPreferredSize(new Dimension(225, 75));
            itemSubmitButton.setBorder(BorderFactory.createLineBorder(bg, 15));
            itemSubmitButton.addActionListener(e -> {
                if (inventoryItems.getSelectedItem() != null) {
                    try {
                        traderManager.addToWishList(traderId, ((InventoryComboBoxItem) inventoryItems.getSelectedItem()).getId());
                        addNewItemModal.dispose();
                    } catch (UserNotFoundException | TradableItemNotFoundException | AuthorizationException e1) {
                        System.out.println(e1.getMessage());
                    }
                }
            });

            traders.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    inventoryItems.setVisible(false);
                    inventoryItems.removeAllItems();
                    try {
                        for (String itemId : userQuery.getAvailableItems(((TraderComboBoxItem) e.getItem()).getId())) {
                            inventoryItems.addItem(new InventoryComboBoxItem(itemId));
                        }
                    } catch (UserNotFoundException | AuthorizationException ex) {
                        ex.printStackTrace();
                    }
                    inventoryItems.setVisible(true);
                }
            });

            addNewItemPanel.add(itemNameTitle);
            addNewItemPanel.add(traders);
            addNewItemPanel.add(inventoryItemTitle);
            addNewItemPanel.add(inventoryItems);
            addNewItemModal.add(addNewItemPanel);
            addNewItemModal.add(itemSubmitButton, BorderLayout.SOUTH);
            addNewItemModal.setModal(true);
            addNewItemModal.setVisible(true);
        });

        wishlistItemsScrollPane = new JScrollPane();
        wishlistItemsScrollPane.setBorder(null);
        wishlistItemsScrollPane.setPreferredSize(new Dimension(1200, 325));
        try {
            getWishlist();
        } catch (UserNotFoundException | AuthorizationException e) {
            e.printStackTrace();
        }
        wishlistItemsScrollPane.setViewportView(wishlistItemsContainer);

        JPanel topWishlistItemsScrollHeaderPane = new JPanel(new GridLayout(1, 3));
        topWishlistItemsScrollHeaderPane.setPreferredSize(new Dimension(1200, 325));

        //JPanel topWishlistItemsScrollHeaderPane = new JPanel(new GridLayout(1, 3));
        //topWishlistItemsScrollHeaderPane.setPreferredSize(new Dimension(1200, 75));

        inventoryTitleContainer.add(inventoryTitle);
        inventoryTitleContainer.add(addInventoryItemButton);

        wishlistTitleContainer.add(wishlistTitle);
        wishlistTitleContainer.add(addWishlistItemButton);

        topInventoryItemsScrollHeaderPane.add(inventoryItemsScrollPane);
        topWishlistItemsScrollHeaderPane.add(wishlistItemsScrollPane);

        this.add(inventoryTitleContainer);
        this.add(inventoryItemsScrollPane);
        this.add(topInventoryItemsScrollHeaderPane);
        //this.add(wishlistTitleContainer);
        this.add(topWishlistItemsScrollHeaderPane);
        //this.add(wishlistItemsScrollPane);
    }

    private void getInventory() throws UserNotFoundException, AuthorizationException {
        int numRows = userQuery.getAvailableItems(traderId).size();
        if (numRows < 4) numRows = 4;
        inventoryItemsContainer = new JPanel(new GridLayout(numRows, 1));
        inventoryItemsContainer.setBackground(gray2);
        inventoryItemsContainer.setBorder(null);

        for (String itemId : userQuery.getAvailableItems(traderId)) {
            try {
                JPanel itemPanel = new JPanel(new GridLayout(1, 4, 10, 0));
                itemPanel.setPreferredSize(new Dimension(1000, 75));
                itemPanel.setBackground(gray);
                itemPanel.setBorder(BorderFactory.createLineBorder(bg));

                JLabel itemName = new JLabel(itemQuery.getName(itemId));
                itemName.setFont(regular.deriveFont(20f));
                itemName.setForeground(Color.BLACK);
                itemName.setHorizontalAlignment(JLabel.LEFT);
                itemName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                JLabel itemDesc = new JLabel(itemQuery.getDesc(itemId));
                itemDesc.setFont(regular.deriveFont(20f));
                itemDesc.setForeground(Color.BLACK);
                itemDesc.setHorizontalAlignment(JLabel.LEFT);

                JLabel itemIdTitle = new JLabel("<html><pre>#" + itemId.substring(itemId.length() - 12) + "</pre></html>");
                itemIdTitle.setFont(regular.deriveFont(20f));
                itemIdTitle.setForeground(Color.BLACK);
                itemIdTitle.setHorizontalAlignment(JLabel.LEFT);

                JButton removeItemButton = new JButton("Remove");
                removeItemButton.setFont(bold.deriveFont(20f));
                removeItemButton.setForeground(Color.WHITE);
                removeItemButton.setBackground(red);
                removeItemButton.setOpaque(true);
                removeItemButton.setBorder(BorderFactory.createLineBorder(gray, 15));

                itemPanel.add(itemName);
                itemPanel.add(itemDesc);
                itemPanel.add(itemIdTitle);
                itemPanel.add(removeItemButton);
                inventoryItemsContainer.add(itemPanel);

                removeItemButton.addActionListener(event -> {
                    try {
                        traderManager.removeFromInventory(traderId, itemId);
                        inventoryItemsContainer.remove(itemPanel);
                        inventoryItemsContainer.revalidate();
                        inventoryItemsContainer.repaint();
                    } catch (UserNotFoundException | AuthorizationException e) {
                        System.out.println(e.getMessage());
                    }
                });

            } catch (TradableItemNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private void getWishlist() throws UserNotFoundException, AuthorizationException {
        int numRows = userQuery.getWishlist(traderId).size();
        if (numRows < 4) numRows = 4;
        wishlistItemsContainer = new JPanel(new GridLayout(numRows, 1));
        wishlistItemsContainer.setBackground(gray2);
        wishlistItemsContainer.setBorder(null);

        for (String itemId : userQuery.getWishlist(traderId)) {
            try {
                JPanel itemPanel = new JPanel(new GridLayout(1, 5, 10, 0));
                itemPanel.setPreferredSize(new Dimension(1000, 75));
                itemPanel.setBackground(gray);
                itemPanel.setBorder(BorderFactory.createLineBorder(bg));

                JLabel itemName = new JLabel(itemQuery.getName(itemId));
                itemName.setFont(regular.deriveFont(20f));
                itemName.setForeground(Color.BLACK);
                itemName.setHorizontalAlignment(JLabel.LEFT);
                itemName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                JLabel itemDesc = new JLabel(itemQuery.getDesc(itemId));
                itemDesc.setFont(regular.deriveFont(20f));
                itemDesc.setForeground(Color.BLACK);
                itemDesc.setHorizontalAlignment(JLabel.LEFT);

                JLabel itemIdTitle = new JLabel("<html><pre>#" + itemId.substring(itemId.length() - 12) + "</pre></html>");
                itemIdTitle.setFont(regular.deriveFont(20f));
                itemIdTitle.setForeground(Color.BLACK);
                itemIdTitle.setHorizontalAlignment(JLabel.LEFT);

                JLabel itemOwnerName = new JLabel(userQuery.getUsername(infoManager.getTraderThatHasTradableItemId(itemId)));
                itemOwnerName.setFont(regular.deriveFont(20f));
                itemOwnerName.setForeground(Color.BLACK);
                itemOwnerName.setHorizontalAlignment(JLabel.CENTER);

                JButton removeItemButton = new JButton("Remove");
                removeItemButton.setFont(bold.deriveFont(20f));
                removeItemButton.setForeground(Color.WHITE);
                removeItemButton.setBackground(red);
                removeItemButton.setOpaque(true);
                removeItemButton.setBorder(BorderFactory.createLineBorder(gray, 15));

                itemPanel.add(itemName);
                itemPanel.add(itemDesc);
                itemPanel.add(itemIdTitle);
                itemPanel.add(itemOwnerName);
                itemPanel.add(removeItemButton);
                wishlistItemsContainer.add(itemPanel);

                removeItemButton.addActionListener(event -> {
                    try {
                        traderManager.removeFromWishList(traderId, itemId);
                        wishlistItemsContainer.remove(itemPanel);
                        wishlistItemsContainer.revalidate();
                        wishlistItemsContainer.repaint();
                    } catch (UserNotFoundException | AuthorizationException e) {
                        System.out.println(e.getMessage());
                    }
                });

            } catch (TradableItemNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private class TraderComboBoxItem {
        final String id;

        public TraderComboBoxItem(String id) {
            this.id = id;
        }

        public String toString() {
            try {
                return userQuery.getUsername(id);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
            return "";
        }

        public String getId() {
            return id;
        }
    }

    private class InventoryComboBoxItem {
        final String id;

        public InventoryComboBoxItem(String id) {
            this.id = id;
        }

        public String toString() {
            try {
                return itemQuery.getName(id);
            } catch (TradableItemNotFoundException e) {
                e.printStackTrace();
            }
            return "";
        }

        public String getId() {
            return id;
        }

    }
}
