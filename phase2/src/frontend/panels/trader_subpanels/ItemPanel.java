package frontend.panels.trader_subpanels;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import backend.models.TradableItem;
import backend.models.users.Trader;
import backend.tradesystem.managers.TradingManager;

public class ItemPanel extends JPanel {

    private Trader trader;
    private Font regular, bold, italic, boldItalic;
    private JScrollPane itemScrollPane;
    private JPanel itemContainer, itemTitleContainer;
    private JButton additemButton;
    private JLabel itemTitle;
    private ArrayList<String> items;
    private String type;

    private TradingManager tradeManager;

    private Color bg = new Color(51, 51, 51);
    private Color blue = new Color(0, 240, 239);
    private Color red = new Color(219, 58, 52);
    private Color gray = new Color(196, 196, 196);

    public ItemPanel(Trader trader, ArrayList<String> items, String type, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        this.items = items;
        this.type = type;
        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setSize(1000, 900);
        this.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 25));
        this.setBackground(bg);

        tradeManager = new TradingManager();

        itemTitleContainer = new JPanel(new GridLayout(1, 2));
        itemTitleContainer.setOpaque(false);
        itemTitleContainer.setPreferredSize(new Dimension(1300, 75));

        itemTitle = new JLabel(this.type);
        itemTitle.setFont(this.regular.deriveFont(30f));
        itemTitle.setForeground(Color.WHITE);
        itemTitle.setHorizontalAlignment(JLabel.LEFT);

        itemScrollPane = new JScrollPane();
        itemScrollPane.setPreferredSize(new Dimension(1300, 675));

        additemButton = new JButton("Add Item to " + this.type);
        additemButton.setFont(this.boldItalic.deriveFont(20f));
        additemButton.setHorizontalAlignment(JButton.RIGHT);
        additemButton.setForeground(blue);
        additemButton.setBackground(bg);
        additemButton.setOpaque(true);
        additemButton.setBorderPainted(false);

        itemTitleContainer.add(itemTitle);
        itemTitleContainer.add(additemButton);

        this.add(itemTitleContainer);
        getItems();
        itemScrollPane.setViewportView(itemContainer);
        this.add(itemScrollPane);
    }

    private void getItems() {
        // itemContainer = new JPanel(new GridLayout(10, 1));
        itemContainer = new JPanel(new GridLayout(this.items.size(), 1));
        itemContainer.setBackground(gray);
        itemContainer.setBorder(null);
        for(String tradeID : this.items) {
        // for (int i = 0; i < 10; i++) {
            try {
                TradableItem item = tradeManager.getTradableItem(tradeID);
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
                itemContainer.add(itemPanel);

            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}