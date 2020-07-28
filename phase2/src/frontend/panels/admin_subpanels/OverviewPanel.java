package frontend.panels.admin_subpanels;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.TradableItem;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.managers.HandleFrozenManager;
import backend.tradesystem.managers.HandleItemRequestsManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class OverviewPanel extends JPanel {

    private JPanel itemRequestsTitleContainer, itemRequestsContainer, frozenTraderTitleContainer,
            unFreezeRequestsContainer, freezeTradersContainer, bottomSplitContainer;
    private JScrollPane itemRequestsScrollPane, unFreezeRequestsScrollPane, freezeTradersScrollPane;
    private JButton acceptAllItemRequestsButton, unFreezeAllTradersButton, freezeAllTradersButton;
    private JLabel itemRequestsTitle, unFreezeRequestsTitle, freezeTraderTitle;
    private Font regular, bold, italic, boldItalic;

    private HandleItemRequestsManager itemRequestManager;
    private HandleFrozenManager frozenManager;

    private Admin admin;

    private Color bg = new Color(51, 51, 51);
    private Color blue = new Color(0, 240, 239);
    private Color gray = new Color(142, 142, 142);
    private Color confirmButton = new Color(27, 158, 36);
    private Color current = new Color(32, 32, 32);
    private Color red = new Color(219, 58, 52);

    public OverviewPanel(Admin admin, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {

        this.admin = admin;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setPreferredSize(new Dimension(1000, 900)); // fix this later
        this.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 25));
        this.setBackground(Color.BLACK);

        itemRequestManager = new HandleItemRequestsManager();
        frozenManager = new HandleFrozenManager();

        itemRequestsTitleContainer = new JPanel(new GridLayout(1, 2));
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

        frozenTraderTitleContainer = new JPanel(new GridLayout(1, 4, 50, 0));
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
        unFreezeRequestsScrollPane.setBorder(null);

        freezeTradersScrollPane = new JScrollPane(freezeTradersContainer);
        freezeTradersScrollPane.setPreferredSize(new Dimension(600, 300));
        freezeTradersScrollPane.setBorder(null);

        bottomSplitContainer = new JPanel(new GridLayout(1, 2, 50, 0));
        bottomSplitContainer.setPreferredSize(new Dimension(1300, 300));
        bottomSplitContainer.setBackground(Color.BLACK);
        bottomSplitContainer.add(unFreezeRequestsScrollPane);
        bottomSplitContainer.add(freezeTradersScrollPane);

        this.add(itemRequestsTitleContainer);
        this.add(itemRequestsScrollPane);
        this.add(frozenTraderTitleContainer);
        this.add(bottomSplitContainer);

        acceptAllItemRequestsButton.addActionListener(e -> {
            for (Component itemRequest : itemRequestsContainer.getComponents()) {
                for (Component c : ((JPanel) itemRequest).getComponents()) {
                    if (c instanceof JButton && c.getBackground() == confirmButton) {
                        ((JButton) c).doClick();
                    }
                }
            }
        });

        unFreezeAllTradersButton.addActionListener(e -> {
            for (Component unFreezeRequest : unFreezeRequestsContainer.getComponents()) {
                for (Component c : ((JPanel) unFreezeRequest).getComponents()) {
                    if (c instanceof JButton && c.getBackground() == confirmButton) {
                        ((JButton) c).doClick();
                    }
                }
            }
        });

        freezeAllTradersButton.addActionListener(e -> {
            for (Component freezeComponent : freezeTradersContainer.getComponents()) {
                for (Component c : ((JPanel) freezeComponent).getComponents()) {
                    if (c instanceof JButton && c.getBackground() == blue) {
                        ((JButton) c).doClick();
                    }
                }
            }
        });

    }

    private void getAllItemRequests() {
        try {
            HashMap<Trader, ArrayList<TradableItem>> itemRequests = itemRequestManager.getAllItemRequests();
            int numRows = 0;
            for (Trader t : itemRequests.keySet())
                numRows += itemRequests.get(t).size();
            if (numRows < 4)
                numRows = 4;
            itemRequestsContainer = new JPanel(new GridLayout(numRows, 1));
            itemRequestsContainer.setBackground(bg);
            itemRequests.forEach((t, items) -> {
                items.forEach(item -> {
                    JPanel itemRequestPanel = new JPanel(new GridLayout(1, 6, 10, 0));
                    itemRequestPanel.setPreferredSize(new Dimension(1000, 75));
                    itemRequestPanel.setBackground(bg);
                    itemRequestPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, gray));

                    JLabel traderName = new JLabel(t.getUsername());
                    traderName.setFont(regular.deriveFont(20f));
                    traderName.setForeground(Color.WHITE);
                    traderName.setHorizontalAlignment(JLabel.LEFT);
                    traderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                    JLabel traderItemName = new JLabel(item.getName());
                    traderItemName.setFont(regular.deriveFont(20f));
                    traderItemName.setForeground(Color.WHITE);
                    traderItemName.setHorizontalAlignment(JLabel.LEFT);

                    JLabel traderItemDesc = new JLabel(item.getDesc());
                    traderItemDesc.setFont(regular.deriveFont(15f));
                    traderItemDesc.setForeground(Color.WHITE);
                    traderItemDesc.setHorizontalAlignment(JLabel.CENTER);

                    JButton acceptItemRequestButton = new JButton("Accept");
                    acceptItemRequestButton.setFont(bold.deriveFont(20f));
                    acceptItemRequestButton.setForeground(Color.WHITE);
                    acceptItemRequestButton.setBackground(confirmButton);
                    acceptItemRequestButton.setOpaque(true);
                    acceptItemRequestButton.setBorder(BorderFactory.createMatteBorder(15, 50, 15, 50, bg));

                    JButton rejectItemRequestButton = new JButton("Reject");
                    rejectItemRequestButton.setFont(bold.deriveFont(20f));
                    rejectItemRequestButton.setForeground(Color.WHITE);
                    rejectItemRequestButton.setBackground(red);
                    rejectItemRequestButton.setOpaque(true);
                    rejectItemRequestButton.setBorder(BorderFactory.createMatteBorder(15, 50, 15, 50, bg));

                    itemRequestPanel.add(traderName);
                    itemRequestPanel.add(traderItemName);
                    itemRequestPanel.add(traderItemDesc);
                    itemRequestPanel.add(acceptItemRequestButton);
                    itemRequestPanel.add(rejectItemRequestButton);
                    itemRequestsContainer.add(itemRequestPanel);

                    acceptItemRequestButton.addActionListener(e -> {
                        try {
                            itemRequestManager.processItemRequest(t.getId(), item.getId(), true);
                            itemRequestsContainer.remove(itemRequestPanel);
                            itemRequestsContainer.revalidate();
                            itemRequestsContainer.repaint();
                        } catch (TradableItemNotFoundException | UserNotFoundException | AuthorizationException e1) {
                            System.out.println(e1.getMessage());
                        }
                    });

                    rejectItemRequestButton.addActionListener(e -> {
                        try {
                            itemRequestManager.processItemRequest(t.getId(), item.getId(), false);
                            itemRequestsContainer.remove(itemRequestPanel);
                            itemRequestsContainer.revalidate();
                            itemRequestsContainer.repaint();
                        } catch (TradableItemNotFoundException | UserNotFoundException | AuthorizationException e1) {
                            System.out.println(e1.getMessage());
                        }
                    });
                });
            });
        } catch (TradableItemNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getAllUnFreezeRequests() {
        ArrayList<User> unFreezeRequests = frozenManager.getAllUnfreezeRequests();
        if(unFreezeRequests.size() == 0) {
            unFreezeRequestsContainer = new JPanel();
            unFreezeRequestsContainer.setBackground(bg);
            JLabel noTradersFound = new JLabel("<html><pre>No Requests Found</pre></html>");
            noTradersFound.setFont(regular.deriveFont(30f));
            noTradersFound.setPreferredSize(new Dimension(400, 275));
            noTradersFound.setHorizontalAlignment(JLabel.CENTER);
            noTradersFound.setVerticalAlignment(JLabel.CENTER);
            noTradersFound.setForeground(gray);
            unFreezeRequestsContainer.add(noTradersFound);
            return;
        }
        int numRows = unFreezeRequests.size();
        if (numRows < 4)
            numRows = 4;
        unFreezeRequestsContainer = new JPanel(new GridLayout(numRows, 1));
        unFreezeRequestsContainer.setBackground(bg);
        unFreezeRequests.forEach(user -> {
            JPanel unFreezeRequestsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
            unFreezeRequestsPanel.setPreferredSize(new Dimension(400, 75));
            unFreezeRequestsPanel.setBackground(bg);
            unFreezeRequestsPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, gray));

            JLabel traderName = new JLabel(user.getUsername());
            traderName.setFont(regular.deriveFont(20f));
            traderName.setForeground(Color.WHITE);
            traderName.setHorizontalAlignment(JLabel.LEFT);
            traderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

            JButton acceptUnFreezeRequestButton = new JButton("Un-Freeze");
            acceptUnFreezeRequestButton.setFont(bold.deriveFont(20f));
            acceptUnFreezeRequestButton.setForeground(Color.WHITE);
            acceptUnFreezeRequestButton.setBackground(confirmButton);
            acceptUnFreezeRequestButton.setOpaque(true);
            acceptUnFreezeRequestButton.setBorder(BorderFactory.createMatteBorder(15, 30, 15, 30, bg));

            JButton rejectUnFreezeRequestButton = new JButton("Reject");
            rejectUnFreezeRequestButton.setFont(bold.deriveFont(20f));
            rejectUnFreezeRequestButton.setForeground(Color.WHITE);
            rejectUnFreezeRequestButton.setBackground(red);
            rejectUnFreezeRequestButton.setOpaque(true);
            rejectUnFreezeRequestButton.setBorder(BorderFactory.createMatteBorder(15, 30, 15, 30, bg));

            unFreezeRequestsPanel.add(traderName);
            unFreezeRequestsPanel.add(acceptUnFreezeRequestButton);
            unFreezeRequestsPanel.add(rejectUnFreezeRequestButton);
            unFreezeRequestsContainer.add(unFreezeRequestsPanel);

            acceptUnFreezeRequestButton.addActionListener(e -> {
                try {
                    frozenManager.setFrozen(user.getId(), false);
                    unFreezeRequestsContainer.remove(unFreezeRequestsPanel);
                    unFreezeRequestsContainer.revalidate();
                    unFreezeRequestsContainer.repaint();
                } catch (UserNotFoundException e1) {
                    System.out.println(e1.getMessage());
                }
            });

            rejectUnFreezeRequestButton.addActionListener(e -> {
                unFreezeRequestsContainer.remove(unFreezeRequestsPanel);
                unFreezeRequestsContainer.revalidate();
                unFreezeRequestsContainer.repaint();

            });
        });
    }

    private void getAllToBeFrozenUsers() {
        ArrayList<Trader> tobeFrozenList = frozenManager.getShouldBeFrozen();
        int numRows = tobeFrozenList.size();
        if (numRows < 4)
            numRows = 4;
        freezeTradersContainer = new JPanel(new GridLayout(numRows, 1));
        freezeTradersContainer.setBackground(bg);
        tobeFrozenList.forEach(trader -> {
            JPanel freezeTraderPanel = new JPanel(new GridLayout(1, 2));
            freezeTraderPanel.setPreferredSize(new Dimension(400, 75));
            freezeTraderPanel.setBackground(bg);
            freezeTraderPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, gray));

            JLabel traderName = new JLabel(trader.getUsername());
            traderName.setFont(regular.deriveFont(20f));
            traderName.setForeground(Color.WHITE);
            traderName.setHorizontalAlignment(JLabel.LEFT);
            traderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

            JButton freezeTraderButton = new JButton("FREEZE");
            freezeTraderButton.setFont(boldItalic.deriveFont(20f));
            freezeTraderButton.setForeground(Color.WHITE);
            freezeTraderButton.setBackground(blue);
            freezeTraderButton.setOpaque(true);
            freezeTraderButton.setBorder(BorderFactory.createMatteBorder(15, 30, 15, 30, bg));

            freezeTraderPanel.add(traderName);
            freezeTraderPanel.add(freezeTraderButton);
            freezeTradersContainer.add(freezeTraderPanel);

            freezeTraderButton.addActionListener(e -> {
                try {
                    frozenManager.setFrozen(trader.getId(), true);
                    freezeTradersContainer.remove(freezeTraderPanel);
                    freezeTradersContainer.revalidate();
                    freezeTradersContainer.repaint();
                } catch (UserNotFoundException e1) {
                    System.out.println(e1.getMessage());
                }
            });
        });
    }
}
