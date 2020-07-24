package frontend.panels.admin_subpanels;

import backend.models.users.Admin;

import javax.swing.*;
import java.awt.*;

public class OverviewPanel extends JPanel {

    private JPanel itemRequestsTitleContainer, itemRequestsContainer, frozenTraderTitleContainer, unFreezeRequestsContainer, freezeTradersContainer, bottomSplitContainer;
    private JScrollPane itemRequestsScrollPane, unFreezeRequestsScrollPane, freezeTradersScrollPane;
    private JButton acceptAllItemRequestsButton, unFreezeAllTradersButton, freezeAllTradersButton;
    private JLabel itemRequestsTitle, unFreezeRequestsTitle, freezeTraderTitle;
    private Font regular, bold, italic, boldItalic;

    private Admin admin;

    private Color bg = new Color(51, 51, 51);
    private Color blue = new Color(0, 240, 239);
    private Color detailsButton = new Color(142, 142, 142);
    private Color confirmButton = new Color(27, 158, 36);
    private Color red = new Color(219, 58, 52);

    public OverviewPanel(Admin admin, Font regular, Font bold, Font italic, Font boldItalic) {

        this.admin = admin;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setPreferredSize(new Dimension(1000, 900)); // fix this later
        this.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 25));
        this.setBackground(Color.BLACK);

        itemRequestsTitleContainer = new JPanel(new GridLayout(1,2));
        itemRequestsTitleContainer.setOpaque(false);
        itemRequestsTitleContainer.setPreferredSize(new Dimension(1300, 75));

        itemRequestsTitle = new JLabel("Item Requests");
        itemRequestsTitle.setFont(this.regular.deriveFont(30f));
        itemRequestsTitle.setForeground(Color.WHITE);
        itemRequestsTitle.setHorizontalAlignment(JLabel.LEFT);
        itemRequestsTitleContainer.add(itemRequestsTitle);


        acceptAllItemRequestsButton = new JButton("Accept All");
        acceptAllItemRequestsButton.setFont(this.boldItalic.deriveFont(20f));
        acceptAllItemRequestsButton.setHorizontalAlignment(JButton.RIGHT);
        acceptAllItemRequestsButton.setForeground(blue);
        acceptAllItemRequestsButton.setBackground(Color.BLACK);
        acceptAllItemRequestsButton.setOpaque(true);
        acceptAllItemRequestsButton.setBorderPainted(false);
        itemRequestsTitleContainer.add(acceptAllItemRequestsButton);

        getAllItemRequests();
        getAllUnFreezeRequests();
        getAllToBeFrozenUsers();

        frozenTraderTitleContainer = new JPanel(new GridLayout(1,4, 50, 0));
        frozenTraderTitleContainer.setOpaque(false);
        frozenTraderTitleContainer.setPreferredSize(new Dimension(1300, 75));

        unFreezeRequestsTitle = new JLabel("Un-freeze Requests");
        unFreezeRequestsTitle.setFont(this.regular.deriveFont(30f));
        unFreezeRequestsTitle.setForeground(Color.WHITE);
        unFreezeRequestsTitle.setHorizontalAlignment(JLabel.LEFT);
        frozenTraderTitleContainer.add(unFreezeRequestsTitle);


        unFreezeAllTradersButton = new JButton("Un-freeze All");
        unFreezeAllTradersButton.setFont(this.boldItalic.deriveFont(20f));
        unFreezeAllTradersButton.setHorizontalAlignment(JButton.RIGHT);
        unFreezeAllTradersButton.setForeground(blue);
        unFreezeAllTradersButton.setBackground(Color.BLACK);
        unFreezeAllTradersButton.setOpaque(true);
        unFreezeAllTradersButton.setBorderPainted(false);
        frozenTraderTitleContainer.add(unFreezeAllTradersButton);

        freezeTraderTitle = new JLabel("To-be-frozen Users");
        freezeTraderTitle.setFont(this.regular.deriveFont(30f));
        freezeTraderTitle.setForeground(Color.WHITE);
        freezeTraderTitle.setHorizontalAlignment(JLabel.LEFT);
        frozenTraderTitleContainer.add(freezeTraderTitle);


        freezeAllTradersButton = new JButton("Freeze All");
        freezeAllTradersButton.setFont(this.boldItalic.deriveFont(20f));
        freezeAllTradersButton.setHorizontalAlignment(JButton.RIGHT);
        freezeAllTradersButton.setForeground(blue);
        freezeAllTradersButton.setBackground(Color.BLACK);
        freezeAllTradersButton.setOpaque(true);
        freezeAllTradersButton.setBorderPainted(false);
        frozenTraderTitleContainer.add(freezeAllTradersButton);


        itemRequestsScrollPane = new JScrollPane(itemRequestsContainer);
        itemRequestsScrollPane.setPreferredSize(new Dimension(1300, 300));
        itemRequestsScrollPane.setBorder(null);

        unFreezeRequestsScrollPane = new JScrollPane(unFreezeRequestsContainer);
        unFreezeRequestsScrollPane.setPreferredSize(new Dimension(600, 300));

        freezeTradersScrollPane = new JScrollPane(freezeTradersContainer);
        freezeTradersScrollPane.setPreferredSize(new Dimension(600, 300));
        
        bottomSplitContainer = new JPanel(new GridLayout(1, 2, 50, 0));
        bottomSplitContainer.setPreferredSize(new Dimension(1300, 300));
        bottomSplitContainer.setBackground(Color.BLACK);
        bottomSplitContainer.add(unFreezeRequestsScrollPane);
        bottomSplitContainer.add(freezeTradersScrollPane);

        this.add(itemRequestsTitleContainer);
        this.add(itemRequestsScrollPane);
        this.add(frozenTraderTitleContainer);
        this.add(bottomSplitContainer);
        
    }

    private void getAllItemRequests() {
        itemRequestsContainer = new JPanel(new GridLayout(10,1));
        itemRequestsContainer.setBackground(bg);
        itemRequestsContainer.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        for (int i = 0; i < 10; i++) {
            try {
                JPanel itemRequestPanel = new JPanel(new GridLayout(1, 6, 10, 0));
                itemRequestPanel.setPreferredSize(new Dimension(1000, 75));
                itemRequestPanel.setBackground(bg);
                
                JLabel traderName = new JLabel("TraderName #" + (i + 1));
                traderName.setFont(regular.deriveFont(20f));
                traderName.setForeground(Color.WHITE);
                traderName.setHorizontalAlignment(JLabel.LEFT);

                JLabel traderItemName = new JLabel("Item Name #" + (i + 1));
                traderItemName.setFont(regular.deriveFont(20f));
                traderItemName.setForeground(Color.WHITE);
                traderItemName.setHorizontalAlignment(JLabel.CENTER);

                JLabel traderItemDesc = new JLabel("Item Desc #" + (i + 1));
                traderItemDesc.setFont(regular.deriveFont(20f));
                traderItemDesc.setForeground(Color.WHITE);
                traderItemDesc.setHorizontalAlignment(JLabel.CENTER);


                JButton acceptItemRequestButton = new JButton("Accept");
                acceptItemRequestButton.setFont(bold.deriveFont(20f));
                acceptItemRequestButton.setForeground(Color.WHITE);
                acceptItemRequestButton.setBackground(confirmButton);
                acceptItemRequestButton.setOpaque(true);
                acceptItemRequestButton.setBorderPainted(false);

                JButton rejectItemRequestButton = new JButton("Reject");
                rejectItemRequestButton.setFont(bold.deriveFont(20f));
                rejectItemRequestButton.setForeground(Color.WHITE);
                rejectItemRequestButton.setBackground(red);
                rejectItemRequestButton.setOpaque(true);
                rejectItemRequestButton.setBorderPainted(false);

                itemRequestPanel.add(traderName);
                itemRequestPanel.add(traderItemName);
                itemRequestPanel.add(traderItemDesc);
                itemRequestPanel.add(acceptItemRequestButton);
                itemRequestPanel.add(rejectItemRequestButton);
                itemRequestsContainer.add(itemRequestPanel);
                // } catch(TradeNotFoundException | UserNotFoundException exception) {
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
    
    private void getAllUnFreezeRequests() {
        unFreezeRequestsContainer = new JPanel(new GridLayout(10,1));
        unFreezeRequestsContainer.setBackground(bg);
        
        
    }
    
    private void getAllToBeFrozenUsers() {
        freezeTradersContainer = new JPanel(new GridLayout(10,1));
        freezeTradersContainer.setBackground(bg);

    }
}
