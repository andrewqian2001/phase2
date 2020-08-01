package frontend.panels.admin_subpanels;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.managers.HandleFrozenManager;
import backend.tradesystem.managers.HandleItemRequestsManager;
import backend.tradesystem.queries.ItemQuery;
import backend.tradesystem.queries.UserQuery;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class OverviewPanel extends JPanel {

    private JPanel itemRequestsTitleContainer, itemRequestsContainer, frozenTraderTitleContainer,
            unFreezeRequestsContainer, freezeTradersContainer, bottomSplitContainer, itemRequestsHeader, unFreezeRequestsHeader, freezeTradersHeader;
    private JScrollPane itemRequestsScrollPane, unFreezeRequestsScrollPane, freezeTradersScrollPane;
    private JButton acceptAllItemRequestsButton, unFreezeAllTradersButton, freezeAllTradersButton;
    private JLabel itemRequestsTitle, unFreezeRequestsTitle, freezeTraderTitle;
    private Font regular, bold, italic, boldItalic;
    private final UserQuery userQuery = new UserQuery();
    private final ItemQuery itemQuery = new ItemQuery();
    private HandleItemRequestsManager itemRequestManager;
    private HandleFrozenManager frozenManager;

    private Color bg = new Color(51, 51, 51);
    private Color blue = new Color(0, 240, 239);
    private Color gray = new Color(142, 142, 142);
    private Color confirmButton = new Color(27, 158, 36);
    private Color current = new Color(32, 32, 32);
    private Color red = new Color(219, 58, 52);
    private String adminId;

    public OverviewPanel(String adminId, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        this.adminId = adminId;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        this.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 25));
        this.setBackground(Color.BLACK);

        itemRequestManager = new HandleItemRequestsManager();
        frozenManager = new HandleFrozenManager();

        itemRequestsTitleContainer = new JPanel(new GridLayout(1, 2));
        itemRequestsTitleContainer.setOpaque(false);
        itemRequestsTitleContainer.setPreferredSize(new Dimension(1200, 50));

        itemRequestsTitle = new JLabel("Item Requests");
        itemRequestsTitle.setFont(this.regular.deriveFont(28f));
        itemRequestsTitle.setForeground(Color.WHITE);
        itemRequestsTitle.setHorizontalAlignment(JLabel.LEFT);
        itemRequestsTitle.setBackground(Color.black);
        itemRequestsTitle.setOpaque(true);
        itemRequestsTitleContainer.add(itemRequestsTitle);

        acceptAllItemRequestsButton = new JButton("Accept All");
        acceptAllItemRequestsButton.setFont(this.boldItalic.deriveFont(20f));
        acceptAllItemRequestsButton.setHorizontalAlignment(JButton.RIGHT);
        acceptAllItemRequestsButton.setForeground(blue);
        acceptAllItemRequestsButton.setBackground(Color.black);
        acceptAllItemRequestsButton.setOpaque(true);
        acceptAllItemRequestsButton.setBorderPainted(false);
        itemRequestsTitleContainer.add(acceptAllItemRequestsButton);

        JPanel itemRequests = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        itemRequests.add(itemRequestsTitleContainer, gbc);

        itemRequestsHeader = new JPanel(new GridLayout(1, 5, 25, 0));
        itemRequestsHeader.setPreferredSize(new Dimension(1200, 25));
        itemRequestsHeader.setBackground(bg);
        itemRequestsHeader.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 80));
        addItemRequestsHeader();
        gbc.insets = new Insets(0, 0, 1, 0);
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        itemRequests.add(itemRequestsHeader, gbc);

        getAllItemRequests();
        itemRequestsScrollPane = new JScrollPane(itemRequestsContainer);
        itemRequestsScrollPane.setPreferredSize(new Dimension(1200, 325));
        itemRequestsScrollPane.setBorder(null);
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridy = 2;
        gbc.weighty = 0.8;
        itemRequests.add(itemRequestsScrollPane, gbc);

        frozenTraderTitleContainer = new JPanel(new GridLayout(1, 4, 50, 0));
        frozenTraderTitleContainer.setOpaque(false);
        frozenTraderTitleContainer.setPreferredSize(new Dimension(1200, 75));

        unFreezeRequestsTitle = new JLabel("Un-freeze Requests");
        unFreezeRequestsTitle.setFont(this.regular.deriveFont(28f));
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
        freezeTraderTitle.setFont(this.regular.deriveFont(28f));
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

        gbc = new GridBagConstraints();
        JPanel unFreezeRequests = new JPanel(new GridBagLayout());

        unFreezeRequestsHeader = new JPanel(new GridLayout(1, 5, 25, 0));
        unFreezeRequestsHeader.setBackground(bg);
        unFreezeRequestsHeader.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 80));
        unFreezeRequestsHeader.setPreferredSize(new Dimension(575, 25));
        addUnFreezeRequestsHeader();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 1, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        unFreezeRequests.add(unFreezeRequestsHeader, gbc);

        getAllUnFreezeRequests();
        unFreezeRequestsScrollPane = new JScrollPane(unFreezeRequestsContainer);
        unFreezeRequestsScrollPane.setBorder(null);
        unFreezeRequestsScrollPane.setPreferredSize(new Dimension(575, 274));
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridy = 1;
        gbc.weighty = 0.9;
        unFreezeRequests.add(unFreezeRequestsScrollPane, gbc);

        JPanel freezeTraders = new JPanel(new GridBagLayout());

        freezeTradersHeader = new JPanel(new GridLayout(1, 5, 25, 0));
        freezeTradersHeader.setBackground(bg);
        freezeTradersHeader.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 80));
        freezeTradersHeader.setPreferredSize(new Dimension(575, 25));
        addFreezeTradersHeader();
        gbc.insets = new Insets(0, 0, 1, 0);
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        freezeTraders.add(freezeTradersHeader, gbc);

        getAllToBeFrozenUsers();
        freezeTradersScrollPane = new JScrollPane(freezeTradersContainer);
        freezeTradersScrollPane.setBorder(null);
        freezeTradersScrollPane.setPreferredSize(new Dimension(575, 274));
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridy = 1;
        gbc.weighty = 0.9;
        freezeTraders.add(freezeTradersScrollPane, gbc);

        bottomSplitContainer = new JPanel(new GridLayout(1, 2, 50, 0));
        bottomSplitContainer.setPreferredSize(new Dimension(1200, 300));
        bottomSplitContainer.setBackground(Color.BLACK);
        bottomSplitContainer.add(unFreezeRequests);
        bottomSplitContainer.add(freezeTraders);

        this.add(itemRequests);
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

    private void addFreezeTradersHeader() {
    }

    private void addUnFreezeRequestsHeader() {
    }

    private void addItemRequestsHeader() {
        JLabel name = new JLabel("Name");
        name.setFont(this.regular.deriveFont(20f));
        name.setForeground(Color.WHITE);
        name.setHorizontalAlignment(JLabel.LEFT);

        JLabel item = new JLabel("Item");
        item.setFont(this.regular.deriveFont(20f));
        item.setForeground(Color.WHITE);
        item.setHorizontalAlignment(JLabel.LEFT);

        JLabel itemDesc = new JLabel("    Item Description");
        itemDesc.setFont(this.regular.deriveFont(20f));
        itemDesc.setForeground(Color.WHITE);
        itemDesc.setHorizontalAlignment(JLabel.CENTER);

        JLabel empty2 = new JLabel("");
        JLabel empty3 = new JLabel("");

        itemRequestsHeader.add(name);
        itemRequestsHeader.add(item);
        itemRequestsHeader.add(itemDesc);
        itemRequestsHeader.add(empty2);
        itemRequestsHeader.add(empty3);
    }

    private void getAllItemRequests() {
        try {
            HashMap<String, ArrayList<String>> itemRequests = itemRequestManager.getAllItemRequests();
            if (itemRequests.size() == 0) {
                itemRequestsContainer = new JPanel();
                itemRequestsContainer.setBackground(bg);
                JLabel noItemsFound = new JLabel("<html><pre>No Item Requests Found</pre></html>");
                noItemsFound.setFont(regular.deriveFont(30f));
                noItemsFound.setPreferredSize(new Dimension(1000, 275));
                noItemsFound.setHorizontalAlignment(JLabel.CENTER);
                noItemsFound.setVerticalAlignment(JLabel.CENTER);
                noItemsFound.setForeground(gray);
                itemRequestsContainer.add(noItemsFound);
                return;
            }
            int numRows = 0;
            for (String t : itemRequests.keySet())
                numRows += itemRequests.get(t).size();
            if (numRows < 4)
                numRows = 4;
            itemRequestsContainer = new JPanel(new GridLayout(numRows, 1));
            itemRequestsContainer.setBackground(bg);
            itemRequests.forEach((t, items) -> {
                items.forEach(item -> {
                    try {
                        JPanel itemRequestPanel = new JPanel(new GridLayout(1, 6, 10, 0));
                        itemRequestPanel.setPreferredSize(new Dimension(1000, 75));
                        itemRequestPanel.setBackground(bg);
                        itemRequestPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, gray));

                        JLabel traderName = new JLabel(userQuery.getUsername(t));
                        traderName.setFont(regular.deriveFont(20f));
                        traderName.setForeground(Color.WHITE);
                        traderName.setHorizontalAlignment(JLabel.LEFT);
                        traderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                        JLabel traderItemName = new JLabel(itemQuery.getName(item));
                        traderItemName.setFont(regular.deriveFont(20f));
                        traderItemName.setForeground(Color.WHITE);
                        traderItemName.setHorizontalAlignment(JLabel.LEFT);

                        JLabel traderItemDesc = new JLabel(itemQuery.getName(item));
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
                                itemRequestManager.processItemRequest(t, item, true);
                                itemRequestsContainer.remove(itemRequestPanel);
                                itemRequestsContainer.revalidate();
                                itemRequestsContainer.repaint();
                            } catch (TradableItemNotFoundException | UserNotFoundException | AuthorizationException e1) {
                                System.out.println(e1.getMessage());
                            }
                        });

                        rejectItemRequestButton.addActionListener(e -> {
                            try {
                                itemRequestManager.processItemRequest(t, item, false);
                                itemRequestsContainer.remove(itemRequestPanel);
                                itemRequestsContainer.revalidate();
                                itemRequestsContainer.repaint();
                            } catch (TradableItemNotFoundException | UserNotFoundException | AuthorizationException e1) {
                                System.out.println(e1.getMessage());
                            }
                        });
                    } catch (TradableItemNotFoundException | UserNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                });
            });
        } catch (TradableItemNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getAllUnFreezeRequests() {
        ArrayList<String> unFreezeRequests = frozenManager.getAllUnfreezeRequests();
        if (unFreezeRequests.size() == 0) {
            unFreezeRequestsContainer = new JPanel();
            unFreezeRequestsContainer.setBackground(bg);
            JLabel noTradersFound = new JLabel("<html><pre>No Requests Found</pre></html>");
            noTradersFound.setFont(regular.deriveFont(30f));
            noTradersFound.setPreferredSize(new Dimension(400, 275));
            noTradersFound.setHorizontalAlignment(JLabel.CENTER);
            noTradersFound.setVerticalAlignment(JLabel.CENTER);
            noTradersFound.setForeground(gray);
            unFreezeRequestsContainer.add(noTradersFound);
            unFreezeRequestsHeader.setVisible(false);
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

            JLabel traderName = null;
            try {
                traderName = new JLabel(userQuery.getUsername(user));
                traderName.setFont(regular.deriveFont(20f));
                traderName.setForeground(Color.WHITE);
                traderName.setHorizontalAlignment(JLabel.LEFT);
                traderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
            } catch (UserNotFoundException e) {
                System.out.println(e.getMessage());
            }

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
                    frozenManager.setFrozen(user, false);
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
        ArrayList<String> tobeFrozenList = frozenManager.getShouldBeFrozen();
        if (tobeFrozenList.size() == 0) {
            freezeTradersContainer = new JPanel();
            freezeTradersContainer.setBackground(bg);
            JLabel noTradersFound = new JLabel("<html><pre>No Traders to freeze</pre></html>");
            noTradersFound.setFont(regular.deriveFont(30f));
            noTradersFound.setPreferredSize(new Dimension(400, 275));
            noTradersFound.setHorizontalAlignment(JLabel.CENTER);
            noTradersFound.setVerticalAlignment(JLabel.CENTER);
            noTradersFound.setForeground(gray);
            freezeTradersContainer.add(noTradersFound);
            freezeTradersHeader.setVisible(false);
            return;
        }
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

            JLabel traderName = null;
            try {
                traderName = new JLabel(userQuery.getUsername(trader));
                traderName.setFont(regular.deriveFont(20f));
                traderName.setForeground(Color.WHITE);
                traderName.setHorizontalAlignment(JLabel.LEFT);
                traderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
            } catch (UserNotFoundException e) {
                System.out.println(e.getMessage());
            }

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
                    frozenManager.setFrozen(trader, true);
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
