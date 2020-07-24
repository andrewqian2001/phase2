package frontend.panels.trader_subpanels;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

import backend.models.users.Trader;

public class ItemPanel extends JPanel {

    private Trader trader;
    private Font regular, bold, italic, boldItalic;
    private JScrollPane itemScrollPane;
    private JPanel itemContainer, itemTitleContainer;
    private JButton additemButton;
    private JLabel itemTitle;
    private ArrayList<String> items;
    private String type;

    private Color bg = new Color(51, 51, 51);
    private Color blue = new Color(0, 240, 239);
    private Color red = new Color(219, 58, 52);

    public ItemPanel(Trader trader, ArrayList<String> items, String type, Font regular, Font bold, Font italic, Font boldItalic) {
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

        itemTitleContainer = new JPanel(new GridLayout(1, 2));
        itemTitleContainer.setOpaque(false);
        itemTitleContainer.setPreferredSize(new Dimension(1300, 75));

        itemTitle = new JLabel(type);
        itemTitle.setFont(this.regular.deriveFont(30f));
        itemTitle.setForeground(Color.WHITE);
        itemTitle.setHorizontalAlignment(JLabel.LEFT);

        itemScrollPane = new JScrollPane();
        itemScrollPane.setPreferredSize(new Dimension(1300, 675));

        additemButton = new JButton("Add Item to " + type);
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
        itemContainer = new JPanel(new GridLayout(10, 1));
        itemContainer.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        // itemContainer = new JPanel(new GridLayout(trader.getitem().size(),
        // 1));
        // for(String tradeID : trader.getitem()) {
        for (int i = 0; i < 10; i++) {
            try {
                // Trade item = tradeManager.getTrade(tradeID);
                JPanel itemPanel = new JPanel(new GridLayout(1, 5, 10, 0));
                itemPanel.setPreferredSize(new Dimension(1000, 75));

                JLabel itemName = new JLabel(type + "Item #"+(1+i));
                itemName.setFont(regular.deriveFont(20f));
                itemName.setForeground(Color.BLACK);
                itemName.setHorizontalAlignment(JLabel.LEFT);
                // JLabel itemDesc = new JLabel(item.getMeetingLocation());
                JLabel itemDesc = new JLabel(type + "ItemDesc #"+(i+1));
                itemDesc.setFont(regular.deriveFont(20f));
                itemDesc.setForeground(Color.BLACK);
                itemDesc.setHorizontalAlignment(JLabel.LEFT);

                JButton removeitemButton = new JButton("Remove");
                removeitemButton.setFont(bold.deriveFont(20f));
                removeitemButton.setForeground(Color.WHITE);
                removeitemButton.setBackground(red);
                removeitemButton.setOpaque(true);
                removeitemButton.setBorderPainted(false);

                itemPanel.add(itemName);
                itemPanel.add(itemDesc);
                itemPanel.add(removeitemButton);
                itemContainer.add(itemPanel);
                // } catch(TradeNotFoundException | UserNotFoundException exception) {
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}