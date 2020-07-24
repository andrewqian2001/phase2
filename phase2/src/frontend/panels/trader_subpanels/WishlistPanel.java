package frontend.panels.trader_subpanels;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.*;

import backend.models.users.Trader;

public class WishlistPanel extends JPanel {

    private Trader trader;
    private Font regular, bold, italic, boldItalic;
    private JScrollPane wishlistScrollPane;
    private JPanel wishlistContainer, wishlistTitleContainer;
    private JButton addWishlistButton;
    private JLabel wishlistTitle;

    private Color bg = new Color(51, 51, 51);
    private Color blue = new Color(0,240,239);
    private Color red = new Color(219, 58, 52);

    public WishlistPanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) {

        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setSize(1000, 900);
        this.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 25));
        this.setBackground(bg);

        wishlistTitleContainer = new JPanel(new GridLayout(1, 2));
        wishlistTitleContainer.setOpaque(false);
        wishlistTitleContainer.setPreferredSize(new Dimension(1300, 75));

        wishlistTitle = new JLabel("Wishlist");
        wishlistTitle.setFont(this.regular.deriveFont(30f));
        wishlistTitle.setForeground(Color.WHITE);
        wishlistTitle.setHorizontalAlignment(JLabel.LEFT);

        wishlistScrollPane = new JScrollPane();
        wishlistScrollPane.setPreferredSize(new Dimension(1300, 675));

        addWishlistButton = new JButton("Add Item to Wishlist");
        addWishlistButton.setFont(this.boldItalic.deriveFont(20f));
        addWishlistButton.setHorizontalAlignment(JButton.RIGHT);
        addWishlistButton.setForeground(blue);
        addWishlistButton.setBackground(bg);
        addWishlistButton.setOpaque(true);
        addWishlistButton.setBorderPainted(false);

        wishlistTitleContainer.add(wishlistTitle);
        wishlistTitleContainer.add(addWishlistButton);


        this.add(wishlistTitleContainer);
        getWishlistItems();
        wishlistScrollPane.setViewportView(wishlistContainer);
        this.add(wishlistScrollPane);
    }

    private void getWishlistItems() {
        wishlistContainer = new JPanel(new GridLayout(10, 1));
        wishlistContainer.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        // wishlistContainer = new JPanel(new GridLayout(trader.getwishlist().size(),
        // 1));
        // for(String tradeID : trader.getWishlist()) {
        for (int i = 0; i < 10; i++) {
            try {
                // Trade wishlistItem = tradeManager.getTrade(tradeID);
                JPanel wishlistItemPanel = new JPanel(new GridLayout(1, 5, 10, 0));
                wishlistItemPanel.setPreferredSize(new Dimension(1000, 75));

                JLabel wishlistItemName = new JLabel("wishlistItem #"+(1+i));
                wishlistItemName.setFont(regular.deriveFont(20f));
                wishlistItemName.setForeground(Color.BLACK);
                wishlistItemName.setHorizontalAlignment(JLabel.LEFT);
                // JLabel wishlistItemDesc = new JLabel(wishlistItem.getMeetingLocation());
                JLabel wishlistItemDesc = new JLabel("wishlistItemDesc #"+(i+1));
                wishlistItemDesc.setFont(regular.deriveFont(20f));
                wishlistItemDesc.setForeground(Color.BLACK);
                wishlistItemDesc.setHorizontalAlignment(JLabel.LEFT);

                JButton removeWishlistItemButton = new JButton("Remove");
                removeWishlistItemButton.setFont(bold.deriveFont(20f));
                removeWishlistItemButton.setForeground(Color.WHITE);
                removeWishlistItemButton.setBackground(red);
                removeWishlistItemButton.setOpaque(true);
                removeWishlistItemButton.setBorderPainted(false);

                wishlistItemPanel.add(wishlistItemName);
                wishlistItemPanel.add(wishlistItemDesc);
                wishlistItemPanel.add(removeWishlistItemButton);
                wishlistContainer.add(wishlistItemPanel);
                // } catch(TradeNotFoundException | UserNotFoundException exception) {
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}