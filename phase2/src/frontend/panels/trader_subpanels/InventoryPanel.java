package frontend.panels.trader_subpanels;

import javax.swing.*;

import java.awt.*;

import backend.models.users.Trader;

public class InventoryPanel extends JPanel {
    private Trader trader;
    private Font regular, bold, italic, boldItalic;
    private JScrollPane inventoryScrollPane;
    private JPanel inventoryContainer, inventoryTitleContainer;
    private JButton addinventoryButton;
    private JLabel inventoryTitle;

    private Color bg = new Color(51, 51, 51);
    private Color blue = new Color(0, 240, 239);
    private Color red = new Color(219, 58, 52);

    public InventoryPanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) {

        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setSize(1000, 900);
        this.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 25));
        this.setBackground(bg);

        inventoryTitleContainer = new JPanel(new GridLayout(1, 2));
        inventoryTitleContainer.setOpaque(false);
        inventoryTitleContainer.setPreferredSize(new Dimension(1300, 75));

        inventoryTitle = new JLabel("Inventory");
        inventoryTitle.setFont(this.regular.deriveFont(30f));
        inventoryTitle.setForeground(Color.WHITE);
        inventoryTitle.setHorizontalAlignment(JLabel.LEFT);

        inventoryScrollPane = new JScrollPane();
        inventoryScrollPane.setPreferredSize(new Dimension(1300, 675));

        addinventoryButton = new JButton("Add Item to inventory");
        addinventoryButton.setFont(this.boldItalic.deriveFont(20f));
        addinventoryButton.setHorizontalAlignment(JButton.RIGHT);
        addinventoryButton.setForeground(blue);
        addinventoryButton.setBackground(bg);
        addinventoryButton.setOpaque(true);
        addinventoryButton.setBorderPainted(false);

        inventoryTitleContainer.add(inventoryTitle);
        inventoryTitleContainer.add(addinventoryButton);

        this.add(inventoryTitleContainer);
        getInventoryItems();
        inventoryScrollPane.setViewportView(inventoryContainer);
        this.add(inventoryScrollPane);
    }

    private void getInventoryItems() {
        inventoryContainer = new JPanel(new GridLayout(10, 1));
        inventoryContainer.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        // inventoryContainer = new JPanel(new GridLayout(trader.getinventory().size(),
        // 1));
        // for(String tradeID : trader.getinventory()) {
        for (int i = 0; i < 10; i++) {
            try {
                // Trade inventoryItem = tradeManager.getTrade(tradeID);
                JPanel inventoryItemPanel = new JPanel(new GridLayout(1, 5, 10, 0));
                inventoryItemPanel.setPreferredSize(new Dimension(1000, 75));

                JLabel inventoryItemName = new JLabel("inventoryItem #" + (1 + i));
                inventoryItemName.setFont(regular.deriveFont(20f));
                inventoryItemName.setForeground(Color.BLACK);
                inventoryItemName.setHorizontalAlignment(JLabel.LEFT);
                // JLabel inventoryItemDesc = new JLabel(inventoryItem.getMeetingLocation());
                JLabel inventoryItemDesc = new JLabel("inventoryItemDesc #" + (i + 1));
                inventoryItemDesc.setFont(regular.deriveFont(20f));
                inventoryItemDesc.setForeground(Color.BLACK);
                inventoryItemDesc.setHorizontalAlignment(JLabel.LEFT);

                JButton removeinventoryItemButton = new JButton("Remove");
                removeinventoryItemButton.setFont(bold.deriveFont(20f));
                removeinventoryItemButton.setForeground(Color.WHITE);
                removeinventoryItemButton.setBackground(red);
                removeinventoryItemButton.setOpaque(true);
                removeinventoryItemButton.setBorderPainted(false);

                inventoryItemPanel.add(inventoryItemName);
                inventoryItemPanel.add(inventoryItemDesc);
                inventoryItemPanel.add(removeinventoryItemButton);
                inventoryContainer.add(inventoryItemPanel);
                // } catch(TradeNotFoundException | UserNotFoundException exception) {
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}
