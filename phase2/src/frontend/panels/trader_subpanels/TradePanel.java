package frontend.panels.trader_subpanels;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import backend.exceptions.AuthorizationException;
import backend.exceptions.CannotTradeException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.tradesystem.managers.TradingInfoManager;
import backend.tradesystem.managers.TradingManager;

public class TradePanel extends JPanel implements ActionListener {

    private JPanel ongoingTradesContainer, tradeRequestsContainer, ongoingTradesTitleContainer,ongoingTradesHeader, tradeRequestsHeader;
    private JScrollPane tradeRequestsScrollPane, ongoingTradesScrollPane;
    private JButton addTradeButton;
    private JLabel ongoingTradesTitle, tradeRequestsTitle;
    private Font regular, bold, italic, boldItalic;
    private TradingManager tradeManager;
    private Trader trader;
    private GridBagConstraints gbc;
    private TradingInfoManager infoManager;

    private Color bg = new Color(51, 51, 51);
    private Color gray = new Color(196,196,196);
    private Color gray2 = new Color(142,142,142);
    private Color green = new Color(27,158,36);
    private Color red = new Color(219, 58, 52);

    public TradePanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        tradeManager = new TradingManager();
        infoManager = new TradingInfoManager();

        this.setBorder(BorderFactory.createEmptyBorder(25, 0, 100, 25));
        this.setBackground(bg);

        JPanel ongoingTrades = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();

        ongoingTradesTitleContainer = new JPanel(new GridLayout(1,2));
        ongoingTradesTitleContainer.setOpaque(false);
        ongoingTradesTitleContainer.setPreferredSize(new Dimension(1200,50));

        ongoingTradesTitle = new JLabel("Ongoing Trades");
        ongoingTradesTitle.setFont(this.regular.deriveFont(30f));
        ongoingTradesTitle.setForeground(Color.WHITE);
        ongoingTradesTitle.setBackground(bg);
        ongoingTradesTitle.setHorizontalAlignment(JLabel.LEFT);
        ongoingTradesTitle.setOpaque(true);
        ongoingTradesTitleContainer.add(ongoingTradesTitle);

        addTradeButton = new JButton("<html><b><i><u>Add new trade</u></i></b></html>");
        addTradeButton.setFont(addTradeButton.getFont().deriveFont(20f));
        addTradeButton.setHorizontalAlignment(JButton.RIGHT);
        addTradeButton.setForeground(Color.cyan);
        addTradeButton.setBackground(bg);
        addTradeButton.setOpaque(true);
        addTradeButton.setBorderPainted(false);
        addTradeButton.addActionListener(this);
        ongoingTradesTitleContainer.add(addTradeButton);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        ongoingTrades.add(ongoingTradesTitleContainer, gbc);

        ongoingTradesHeader = new JPanel(new GridLayout(1,5, 25, 0));
        ongoingTradesHeader.setPreferredSize(new Dimension(1200,25));
        ongoingTradesHeader.setBackground(gray);
        ongoingTradesHeader.setBorder(BorderFactory.createEmptyBorder(0,25, 0, 80));
        addOngoingTradesHeader();
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        ongoingTrades.add(ongoingTradesHeader, gbc);

        ongoingTradesScrollPane = new JScrollPane();
        ongoingTradesScrollPane.setPreferredSize(new Dimension(1200, 325));
        getOngoingTradesPanel();
        ongoingTradesScrollPane.setViewportView(ongoingTradesContainer);
        ongoingTradesScrollPane.setBackground(gray);
        ongoingTradesScrollPane.setBorder(null);
        gbc.gridy = 2;
        gbc.weighty = 0.8;
        ongoingTrades.add(ongoingTradesScrollPane, gbc);

        JPanel tradeRequests = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();

        tradeRequestsTitle = new JLabel("Trade Requests");
        tradeRequestsTitle.setFont(this.regular.deriveFont(30f));
        tradeRequestsTitle.setForeground(Color.WHITE);
        tradeRequestsTitle.setBackground(bg);
        tradeRequestsTitle.setOpaque(true);
        tradeRequestsTitle.setHorizontalAlignment(JLabel.LEFT);
        tradeRequestsTitle.setPreferredSize(new Dimension(1200, 50));
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        tradeRequests.add(tradeRequestsTitle, gbc);

        tradeRequestsHeader = new JPanel(new GridLayout(1,8,20, 0));
        tradeRequestsHeader.setPreferredSize(new Dimension(1200,25));
        tradeRequestsHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 120));
        tradeRequestsHeader.setBackground(gray);
        addTradeRequestsHeader();
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        tradeRequests.add(tradeRequestsHeader, gbc);

        tradeRequestsScrollPane = new JScrollPane();
        tradeRequestsScrollPane.setPreferredSize(new Dimension(1200, 325));
        getTradeRequestPanels();
        tradeRequestsScrollPane.setViewportView(tradeRequestsContainer);
        tradeRequestsScrollPane.setBorder(null);
        gbc.gridy = 2;
        gbc.weighty = 0.8;
        tradeRequests.add(tradeRequestsScrollPane, gbc);

        this.add(ongoingTrades);
        this.add(tradeRequests);
    }

    private void addOngoingTradesHeader() {
        JLabel name = new JLabel("Name");
        name.setFont(this.regular.deriveFont(20f));
        name.setForeground(Color.BLACK);
        name.setHorizontalAlignment(JLabel.LEFT);

        JLabel location = new JLabel("Location");
        location.setFont(this.regular.deriveFont(20f));
        location.setForeground(Color.BLACK);
        location.setHorizontalAlignment(JLabel.CENTER);

        JLabel meetingTime = new JLabel("           Meeting Time");
        meetingTime.setFont(this.regular.deriveFont(20f));
        meetingTime.setForeground(Color.BLACK);
        meetingTime.setHorizontalAlignment(JLabel.CENTER);

        JLabel empty2 = new JLabel("");
        JLabel empty1 = new JLabel("");

        ongoingTradesHeader.add(name);
        ongoingTradesHeader.add(location);
        ongoingTradesHeader.add(meetingTime);

        ongoingTradesHeader.add(empty1);
        ongoingTradesHeader.add(empty2);
    }

    private void addTradeRequestsHeader() {
        JLabel name = new JLabel("Name");
        name.setFont(this.regular.deriveFont(20f));
        name.setForeground(Color.BLACK);
        name.setHorizontalAlignment(JLabel.CENTER);

        JLabel location = new JLabel("Location");
        location.setFont(this.regular.deriveFont(20f));
        location.setForeground(Color.BLACK);
        location.setHorizontalAlignment(JLabel.CENTER);

        JLabel theirItem = new JLabel("Their Item");
        theirItem.setFont(this.regular.deriveFont(20f));
        theirItem.setForeground(Color.BLACK);
        theirItem.setHorizontalAlignment(JLabel.CENTER);

        JLabel yourItem = new JLabel("Your Item   ");
        yourItem.setFont(this.regular.deriveFont(20f));
        yourItem.setForeground(Color.BLACK);
        yourItem.setHorizontalAlignment(JLabel.CENTER);

        JLabel empty1 = new JLabel("");
        JLabel empty2 = new JLabel("");
        JLabel empty3 = new JLabel("");

        tradeRequestsHeader.add(name);
        tradeRequestsHeader.add(location);
        tradeRequestsHeader.add(theirItem);
        tradeRequestsHeader.add(yourItem);
        tradeRequestsHeader.add(empty1);
        tradeRequestsHeader.add(empty2);
        tradeRequestsHeader.add(empty3);
    }

    private void getTradeRequestPanels() {
        // tradeRequestsContainer = new JPanel(new GridLayout(10, 1));
        if(trader.getRequestedTrades().size() == 0) {
            tradeRequestsContainer = new JPanel();
            tradeRequestsContainer.setBackground(gray2);
            JLabel noTradesFound = new JLabel("<html><pre>No Trade Requests Found</pre></html>");
            noTradesFound.setFont(bold.deriveFont(30f));
            noTradesFound.setForeground(Color.WHITE);
            tradeRequestsContainer.add(noTradesFound, BorderLayout.CENTER);
            return;
        }
        int numRows = trader.getRequestedTrades().size();
        if(numRows < 4) numRows = 4;
        tradeRequestsContainer = new JPanel(new GridLayout(numRows, 1));
        tradeRequestsContainer.setBackground(gray2);
        tradeRequestsContainer.setBorder(null);
        for(String tradeID : trader.getRequestedTrades()) {
            try {
                Trade tradeRequest = tradeManager.getTrade(tradeID);
                if((tradeRequest.getFirstUserId().equals(trader.getId()) && tradeRequest.getUserTurnToEdit().equals(trader.getId())) || !tradeRequest.getFirstUserId().equals(trader.getId())) {
                    JPanel tradeRequestPanel = new JPanel(new GridLayout(1, 7, 10, 0));
                    tradeRequestPanel.setPreferredSize(new Dimension(1000, 75));
                    tradeRequestPanel.setBackground(gray);
                    tradeRequestPanel.setBorder(BorderFactory.createLineBorder(bg));

                    JLabel otherTraderName = new JLabel(tradeManager.getUser(tradeRequest.getFirstUserId()).getUsername());
                    JLabel otherTraderItemName = tradeRequest.getFirstUserOffer().equals("") ? new JLabel("N/A") : new JLabel(tradeManager.getTradableItem(tradeRequest.getFirstUserOffer()).getName());
                    JLabel traderItemName = tradeRequest.getSecondUserOffer().equals("") ? new JLabel("N/A") : new JLabel(tradeManager.getTradableItem(tradeRequest.getSecondUserOffer()).getName());

                    // JLabel otherTraderName = new JLabel("otherTrader #" + (i + 1));
                    otherTraderName.setFont(regular.deriveFont(20f));
                    otherTraderName.setForeground(Color.BLACK);
                    otherTraderName.setHorizontalAlignment(JLabel.LEFT);
                    otherTraderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                    JLabel tradeLocation = new JLabel(tradeRequest.getMeetingLocation());
                    // JLabel tradeLocation = new JLabel("Location #" + (i + 1));
                    tradeLocation.setFont(regular.deriveFont(20f));
                    tradeLocation.setForeground(Color.BLACK);
                    tradeLocation.setHorizontalAlignment(JLabel.CENTER);
                    
                    // JLabel otherTraderItemName = new JLabel("Their item #" + (i + 1));
                    otherTraderItemName.setFont(regular.deriveFont(20f));
                    otherTraderItemName.setForeground(Color.BLACK);
                    otherTraderItemName.setHorizontalAlignment(JLabel.CENTER);

                    // JLabel traderItemName = new JLabel("Your item #" + (i + 1));
                    traderItemName.setFont(regular.deriveFont(20f));
                    traderItemName.setForeground(Color.BLACK);
                    traderItemName.setHorizontalAlignment(JLabel.CENTER);

                    JButton tradeDetailsButton = new JButton("Details");
                    tradeDetailsButton.setFont(bold.deriveFont(20f));
                    tradeDetailsButton.setForeground(Color.WHITE);
                    tradeDetailsButton.setBackground(gray2);
                    tradeDetailsButton.setOpaque(true);
                    tradeDetailsButton.setBorder(BorderFactory.createLineBorder(gray, 15));
                    tradeDetailsButton.addActionListener(e -> {
                        JDialog tradeDetailsModal = new JDialog();
                        tradeDetailsModal.setTitle("Trade Details");
                        tradeDetailsModal.setSize(600, 450);
                        tradeDetailsModal.setResizable(false);
                        tradeDetailsModal.setLocationRelativeTo(null);

                        JPanel tradeDetailsPanel = new JPanel();
                        tradeDetailsPanel.setPreferredSize(new Dimension(600, 450));
                        tradeDetailsPanel.setBackground(bg);

                        JLabel otherTraderNameTitle = new JLabel("Trader Username:");
                        otherTraderNameTitle.setFont(italic.deriveFont(20f));
                        otherTraderNameTitle.setPreferredSize(new Dimension(290, 50));
                        otherTraderNameTitle.setOpaque(false);
                        otherTraderNameTitle.setForeground(Color.WHITE);

                        JLabel otherTraderDetailsName = new JLabel("<html><pre>" + otherTraderName.getText() + "</pre></html>");
                        otherTraderDetailsName.setFont(italic.deriveFont(20f));
                        otherTraderDetailsName.setPreferredSize(new Dimension(290, 50));
                        otherTraderDetailsName.setOpaque(false);
                        otherTraderDetailsName.setForeground(Color.WHITE);

                        JLabel traderItemTitle = new JLabel("Item from your Inventory:");
                        traderItemTitle.setFont(italic.deriveFont(20f));
                        traderItemTitle.setPreferredSize(new Dimension(290, 50));
                        traderItemTitle.setOpaque(false);
                        traderItemTitle.setForeground(Color.WHITE);

                        JLabel otherTraderItemTitle = new JLabel("Item from their Inventory:");
                        otherTraderItemTitle.setFont(italic.deriveFont(20f));
                        otherTraderItemTitle.setPreferredSize(new Dimension(290, 50));
                        otherTraderItemTitle.setOpaque(false);
                        otherTraderItemTitle.setForeground(Color.WHITE);

                        JLabel otherTraderItemRequestName = new JLabel("<html><pre>" + otherTraderItemName.getText() + "</pre></html>");
                        JLabel traderItemRequestName = new JLabel("<html><pre>" + traderItemName.getText() + "</pre></html>");
                        
                        otherTraderItemRequestName.setFont(regular.deriveFont(20f));
                        otherTraderItemRequestName.setPreferredSize(new Dimension(290, 50));
                        otherTraderItemRequestName.setOpaque(false);
                        otherTraderItemRequestName.setForeground(Color.WHITE);
                        
                        traderItemRequestName.setFont(regular.deriveFont(20f));
                        traderItemRequestName.setPreferredSize(new Dimension(290, 50));
                        traderItemRequestName.setOpaque(false);
                        traderItemRequestName.setForeground(Color.WHITE);

                        JLabel meetingLocationTitle = new JLabel("Meeting Location:");
                        meetingLocationTitle.setFont(italic.deriveFont(20f));
                        meetingLocationTitle.setPreferredSize(new Dimension(290, 50));
                        meetingLocationTitle.setOpaque(false);
                        meetingLocationTitle.setForeground(Color.WHITE);

                        JLabel meetingLocationName = new JLabel("<html><pre>" + tradeRequest.getMeetingLocation() + "</pre></html>");
                        meetingLocationName.setFont(italic.deriveFont(20f));
                        meetingLocationName.setPreferredSize(new Dimension(290, 50));
                        meetingLocationName.setOpaque(false);
                        meetingLocationName.setForeground(Color.WHITE);


                        JLabel firstMeetingDateTitle = new JLabel("First Meeting Date:");
                        firstMeetingDateTitle.setPreferredSize(new Dimension(290, 50));
                        firstMeetingDateTitle.setFont(italic.deriveFont(20f));
                        firstMeetingDateTitle.setOpaque(false);
                        firstMeetingDateTitle.setForeground(Color.WHITE);

                        JLabel firstMeetingDate = new JLabel("<html><pre>" + tradeRequest.getMeetingTime().toString().substring(0, tradeRequest.getMeetingTime().toString().length() - 12) + "</pre></html>");
                        firstMeetingDate.setFont(italic.deriveFont(20f));
                        firstMeetingDate.setPreferredSize(new Dimension(290, 50));
                        firstMeetingDate.setOpaque(false);
                        firstMeetingDate.setForeground(Color.WHITE);

                        JLabel secondMeetingDateTitle = new JLabel("Second Meeting Date:");
                        secondMeetingDateTitle.setPreferredSize(new Dimension(290, 50));
                        secondMeetingDateTitle.setFont(italic.deriveFont(20f));
                        secondMeetingDateTitle.setOpaque(false);
                        secondMeetingDateTitle.setForeground(Color.WHITE);

                        JLabel secondMeetingDate = new JLabel();
                        secondMeetingDate.setFont(bold.deriveFont(20f));
                        secondMeetingDate.setPreferredSize(new Dimension(290, 50));
                        secondMeetingDate.setOpaque(false);
                        secondMeetingDate.setForeground(Color.WHITE);

                        if(tradeRequest.getSecondMeetingTime() != null) {
                            secondMeetingDate.setText("<html><pre>" + tradeRequest.getSecondMeetingTime().toString().substring(0, tradeRequest.getSecondMeetingTime().toString().length() - 12) + "</pre></html>");
                        } else {
                            secondMeetingDate.setText("N/A");
                        }

                        JLabel availableEditsTitle = new JLabel("Available Edits Left:");
                        availableEditsTitle.setPreferredSize(new Dimension(290, 50));
                        availableEditsTitle.setFont(bold.deriveFont(20f));
                        availableEditsTitle.setOpaque(false);
                        availableEditsTitle.setForeground(Color.WHITE);

                        JLabel availableEdits = new JLabel("<html><pre>" + (tradeRequest.getMaxAllowedEdits()/2 - tradeRequest.getNumEdits())+ "</pre></html>");
                        availableEdits.setFont(italic.deriveFont(20f));
                        availableEdits.setPreferredSize(new Dimension(290, 50));
                        availableEdits.setOpaque(false);
                        availableEdits.setForeground(Color.WHITE);

                        tradeDetailsPanel.add(otherTraderNameTitle);
                        tradeDetailsPanel.add(otherTraderDetailsName);
                        tradeDetailsPanel.add(traderItemTitle);
                        tradeDetailsPanel.add(traderItemRequestName);
                        tradeDetailsPanel.add(otherTraderItemTitle);
                        tradeDetailsPanel.add(otherTraderItemRequestName);
                        tradeDetailsPanel.add(meetingLocationTitle);
                        tradeDetailsPanel.add(meetingLocationName);
                        tradeDetailsPanel.add(firstMeetingDateTitle);
                        tradeDetailsPanel.add(firstMeetingDate);
                        tradeDetailsPanel.add(secondMeetingDateTitle);
                        tradeDetailsPanel.add(secondMeetingDate);
                        tradeDetailsPanel.add(availableEditsTitle);
                        tradeDetailsPanel.add(availableEdits);

                        tradeDetailsModal.add(tradeDetailsPanel);
                        tradeDetailsModal.setModal(true);
                        tradeDetailsModal.setVisible(true);
                    });

                    JButton editTradeButton = new JButton("Edit");
                    editTradeButton.setFont(bold.deriveFont(20f));
                    editTradeButton.setForeground(Color.WHITE);
                    editTradeButton.setBackground(Color.CYAN);
                    editTradeButton.setOpaque(true);
                    editTradeButton.setBorder(BorderFactory.createLineBorder(gray, 15));

                    JButton tradeConfirmButton = new JButton("Accept");
                    tradeConfirmButton.setFont(bold.deriveFont(20f));
                    tradeConfirmButton.setForeground(Color.WHITE);
                    tradeConfirmButton.setBackground(green);
                    tradeConfirmButton.setOpaque(true);
                    tradeConfirmButton.setBorder(BorderFactory.createLineBorder(gray, 15));
                    tradeConfirmButton.addActionListener(e -> {
                        try {
                            tradeManager.acceptRequest(trader.getId(), tradeRequest.getId());
                            tradeRequestsContainer.remove(tradeRequestPanel);
                            tradeRequestsContainer.revalidate();
                            tradeRequestsContainer.repaint();
                        } catch (TradeNotFoundException | UserNotFoundException | AuthorizationException | CannotTradeException e1) {
                            System.out.println(e1.getMessage());
                        }
                        
                    });

                    JButton tradeRejectButton = new JButton("Reject");
                    tradeRejectButton.setFont(bold.deriveFont(20f));
                    tradeRejectButton.setForeground(Color.WHITE);
                    tradeRejectButton.setBackground(red);
                    tradeRejectButton.setOpaque(true);
                    tradeRejectButton.setBorder(BorderFactory.createLineBorder(gray, 15));
                    tradeRejectButton.addActionListener(e -> {
                        try {
                            tradeManager.rescindTradeRequest(tradeRequest.getId());
                            tradeRequestsContainer.remove(tradeRequestPanel);
                            tradeRequestsContainer.revalidate();
                            tradeRequestsContainer.repaint();
                        } catch (TradeNotFoundException | UserNotFoundException | AuthorizationException e1) {
                            System.out.println(e1.getMessage());
                        }
                        
                    });

                    tradeRequestPanel.add(otherTraderName);
                    tradeRequestPanel.add(tradeLocation);
                    tradeRequestPanel.add(otherTraderItemName);
                    tradeRequestPanel.add(traderItemName);
                    tradeRequestPanel.add(tradeDetailsButton);
                    tradeRequestPanel.add(editTradeButton);
                    tradeRequestPanel.add(tradeConfirmButton);
                    tradeRequestPanel.add(tradeRejectButton);
                    tradeRequestsContainer.add(tradeRequestPanel);
                }
            } catch(TradeNotFoundException | UserNotFoundException | TradableItemNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
        }

        if (tradeRequestsContainer.getComponents().length == 0) {
            tradeRequestsContainer = new JPanel();
            tradeRequestsContainer.setBackground(gray2);
            JLabel noTradesFound = new JLabel("<html><pre>No Trade Requests Found</pre></html>");
            noTradesFound.setFont(bold.deriveFont(30f));
            noTradesFound.setForeground(Color.WHITE);
            tradeRequestsContainer.add(noTradesFound, BorderLayout.CENTER);
            return;
        }

    }
    
    private void getOngoingTradesPanel() {
        // ongoingTradesContainer = new JPanel(new GridLayout(10, 1));
        if (trader.getAcceptedTrades().size() == 0) {
            ongoingTradesContainer = new JPanel();
            ongoingTradesContainer.setBackground(gray2);
            JLabel noTradesFound = new JLabel("<html><pre>No Ongoing Trades Found</pre></html>");
            noTradesFound.setFont(bold.deriveFont(30f));
            noTradesFound.setForeground(Color.WHITE);
            ongoingTradesContainer.add(noTradesFound, BorderLayout.CENTER);
            return;
        }
        int numRows = trader.getAcceptedTrades().size();
        if (numRows < 4) numRows = 4;
        ongoingTradesContainer = new JPanel(new GridLayout(numRows, 1));
        ongoingTradesContainer.setBackground(gray2);
        ongoingTradesContainer.setBorder(null);
        for(String tradeID : trader.getAcceptedTrades()) {
        // for(int i = 0; i < 10; i++) {
            try {
                Trade ongoingTrade = tradeManager.getTrade(tradeID);
                JPanel ongoingTradePanel = new JPanel(new GridLayout(1,5, 10, 0)); 
                ongoingTradePanel.setPreferredSize(new Dimension(1000,75));
                ongoingTradePanel.setBorder(BorderFactory.createLineBorder(bg));
                ongoingTradePanel.setBackground(gray);

                boolean isTraderFirstUser = tradeManager.getUser(ongoingTrade.getFirstUserId()).getUsername().equals(trader.getUsername());

                JLabel otherTraderName = new JLabel((!isTraderFirstUser ? tradeManager.getUser(ongoingTrade.getFirstUserId()).getUsername() : tradeManager.getUser(ongoingTrade.getSecondUserId()).getUsername()));
                // JLabel otherTraderName = new JLabel("otherTrader #"+ (i + 1));
                otherTraderName.setFont(regular.deriveFont(20f));
                otherTraderName.setForeground(Color.BLACK);
                otherTraderName.setHorizontalAlignment(JLabel.LEFT);
                otherTraderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                JLabel tradeLocation = new JLabel(ongoingTrade.getMeetingLocation());
                // JLabel tradeLocation = new JLabel("Meeting Location #" + (i+1));
                tradeLocation.setFont(regular.deriveFont(20f));
                tradeLocation.setForeground(Color.BLACK);
                tradeLocation.setHorizontalAlignment(JLabel.CENTER);

                JLabel tradeMeetingTime;
                if(ongoingTrade.isFirstUserConfirmed1() && ongoingTrade.isSecondUserConfirmed1()) {
                    tradeMeetingTime = new JLabel(ongoingTrade.getSecondMeetingTime().toString().substring(0, ongoingTrade.getSecondMeetingTime().toString().length() - 12));
                } else {
                    tradeMeetingTime = new JLabel(ongoingTrade.getMeetingTime().toString().substring(0, ongoingTrade.getMeetingTime().toString().length() - 12));
                }
                // JLabel tradeMeetingTime = new JLabel("2020/07/30@14:2"+i);
                tradeMeetingTime.setFont(regular.deriveFont(20f));
                tradeMeetingTime.setForeground(Color.BLACK);
                tradeMeetingTime.setHorizontalAlignment(JLabel.CENTER);

                JButton tradeDetailsButton = new JButton("Details");
                tradeDetailsButton.setFont(bold.deriveFont(20f));
                tradeDetailsButton.setForeground(Color.WHITE);
                tradeDetailsButton.setBackground(gray2);
                tradeDetailsButton.setOpaque(true);
                tradeDetailsButton.setBorder(BorderFactory.createLineBorder(gray, 15));
                tradeDetailsButton.addActionListener(e -> {
                    JDialog tradeDetailsModal = new JDialog();
                    tradeDetailsModal.setTitle("Trade Details");
                    tradeDetailsModal.setSize(600, 450);
                    tradeDetailsModal.setResizable(false);
                    tradeDetailsModal.setLocationRelativeTo(null);

                    JPanel tradeDetailsPanel = new JPanel();
                    tradeDetailsPanel.setPreferredSize(new Dimension(600, 450));
                    tradeDetailsPanel.setBackground(bg);

                    JLabel otherTraderNameTitle = new JLabel("Trader Username:");
                    otherTraderNameTitle.setFont(italic.deriveFont(20f));
                    otherTraderNameTitle.setPreferredSize(new Dimension(290, 50));
                    otherTraderNameTitle.setOpaque(false);
                    otherTraderNameTitle.setForeground(Color.WHITE);

                    JLabel otherTraderDetailsName = new JLabel("<html><pre>" + otherTraderName.getText() + "</pre></html>");
                    otherTraderDetailsName.setFont(italic.deriveFont(20f));
                    otherTraderDetailsName.setPreferredSize(new Dimension(290, 50));
                    otherTraderDetailsName.setOpaque(false);
                    otherTraderDetailsName.setForeground(Color.WHITE);

                    JLabel traderItemTitle = new JLabel("Item from your Inventory:");
                    traderItemTitle.setFont(italic.deriveFont(20f));
                    traderItemTitle.setPreferredSize(new Dimension(290, 50));
                    traderItemTitle.setOpaque(false);
                    traderItemTitle.setForeground(Color.WHITE);

                    JLabel otherTraderItemTitle = new JLabel("Item from their Inventory:");
                    otherTraderItemTitle.setFont(italic.deriveFont(20f));
                    otherTraderItemTitle.setPreferredSize(new Dimension(290, 50));
                    otherTraderItemTitle.setOpaque(false);
                    otherTraderItemTitle.setForeground(Color.WHITE);

                    JLabel otherTraderItemName = new JLabel();
                    JLabel traderItemName = new JLabel();
                    //TODO: Make this logic less ugly
                    try {
                        if((ongoingTrade.getFirstUserOffer().equals("") && isTraderFirstUser) || (ongoingTrade.getSecondUserOffer().equals("") && !isTraderFirstUser)) {
                                traderItemName.setText("N/A");
                        } else if(!ongoingTrade.getFirstUserOffer().equals("") && isTraderFirstUser) {
                            traderItemName.setText(tradeManager.getTradableItem(ongoingTrade.getFirstUserOffer()).getName());
                        } else if(!ongoingTrade.getSecondUserOffer().equals("") && !isTraderFirstUser) {
                            traderItemName.setText(tradeManager.getTradableItem(ongoingTrade.getSecondUserOffer()).getName());
                        }
                        if((ongoingTrade.getFirstUserOffer().equals("") && !isTraderFirstUser) || (ongoingTrade.getSecondUserOffer().equals("") && isTraderFirstUser)) {
                                otherTraderItemName.setText("N/A");
                        } else if(!ongoingTrade.getFirstUserOffer().equals("") && !isTraderFirstUser) {
                            otherTraderItemName.setText(tradeManager.getTradableItem(ongoingTrade.getFirstUserOffer()).getName());
                        } else if(!ongoingTrade.getSecondUserOffer().equals("") && isTraderFirstUser) {
                            otherTraderItemName.setText(tradeManager.getTradableItem(ongoingTrade.getSecondUserOffer()).getName());
                        }
                    } catch(TradableItemNotFoundException e1) {
                        System.out.println(e1.getMessage());
                    }

                    otherTraderItemName.setText("<html><pre>" + otherTraderItemName.getText() + "</pre></html>");
                    otherTraderItemName.setFont(regular.deriveFont(20f));
                    otherTraderItemName.setPreferredSize(new Dimension(290, 50));
                    otherTraderItemName.setOpaque(false);
                    otherTraderItemName.setForeground(Color.WHITE);
                    
                    traderItemName.setText("<html><pre>" + traderItemName.getText() + "</pre></html>");
                    traderItemName.setFont(regular.deriveFont(20f));
                    traderItemName.setPreferredSize(new Dimension(290, 50));
                    traderItemName.setOpaque(false);
                    traderItemName.setForeground(Color.WHITE);

                    JLabel meetingLocationTitle = new JLabel("Meeting Location:");
                    meetingLocationTitle.setFont(italic.deriveFont(20f));
                    meetingLocationTitle.setPreferredSize(new Dimension(290, 50));
                    meetingLocationTitle.setOpaque(false);
                    meetingLocationTitle.setForeground(Color.WHITE);

                    JLabel meetingLocationName = new JLabel("<html><pre>" + ongoingTrade.getMeetingLocation() + "</pre></html>");
                    meetingLocationName.setFont(italic.deriveFont(20f));
                    meetingLocationName.setPreferredSize(new Dimension(290, 50));
                    meetingLocationName.setOpaque(false);
                    meetingLocationName.setForeground(Color.WHITE);


                    JLabel firstMeetingDateTitle = new JLabel("First Meeting Date:");
                    firstMeetingDateTitle.setPreferredSize(new Dimension(290, 50));
                    firstMeetingDateTitle.setFont(italic.deriveFont(20f));
                    firstMeetingDateTitle.setOpaque(false);
                    firstMeetingDateTitle.setForeground(Color.WHITE);

                    JLabel firstMeetingDate = new JLabel("<html><pre>" + ongoingTrade.getMeetingTime().toString().substring(0, ongoingTrade.getMeetingTime().toString().length() - 12) + "</pre></html>");
                    firstMeetingDate.setFont(italic.deriveFont(20f));
                    firstMeetingDate.setPreferredSize(new Dimension(290, 50));
                    firstMeetingDate.setOpaque(false);
                    firstMeetingDate.setForeground(Color.WHITE);

                    JLabel secondMeetingDateTitle = new JLabel("Second Meeting Date:");
                    secondMeetingDateTitle.setPreferredSize(new Dimension(290, 50));
                    secondMeetingDateTitle.setFont(italic.deriveFont(20f));
                    secondMeetingDateTitle.setOpaque(false);
                    secondMeetingDateTitle.setForeground(Color.WHITE);

                    JLabel secondMeetingDate = new JLabel();
                    secondMeetingDate.setFont(bold.deriveFont(20f));
                    secondMeetingDate.setPreferredSize(new Dimension(290, 50));
                    secondMeetingDate.setOpaque(false);
                    secondMeetingDate.setForeground(Color.WHITE);

                    if(ongoingTrade.getSecondMeetingTime() != null) {
                        secondMeetingDate.setText("<html><pre>" + ongoingTrade.getSecondMeetingTime().toString().substring(0, ongoingTrade.getSecondMeetingTime().toString().length() - 12) + "</pre></html>");
                    } else {
                        secondMeetingDate.setText("N/A");
                    }

                    tradeDetailsPanel.add(otherTraderNameTitle);
                    tradeDetailsPanel.add(otherTraderDetailsName);
                    tradeDetailsPanel.add(traderItemTitle);
                    tradeDetailsPanel.add(traderItemName);
                    tradeDetailsPanel.add(otherTraderItemTitle);
                    tradeDetailsPanel.add(otherTraderItemName);
                    tradeDetailsPanel.add(meetingLocationTitle);
                    tradeDetailsPanel.add(meetingLocationName);
                    tradeDetailsPanel.add(firstMeetingDateTitle);
                    tradeDetailsPanel.add(firstMeetingDate);
                    tradeDetailsPanel.add(secondMeetingDateTitle);
                    tradeDetailsPanel.add(secondMeetingDate);

                    tradeDetailsModal.add(tradeDetailsPanel);
                    tradeDetailsModal.setModal(true);
                    tradeDetailsModal.setVisible(true);
                });

                JButton tradeConfirmButton = new JButton();

                if((isTraderFirstUser && (ongoingTrade.isFirstUserConfirmed1() || ongoingTrade.isFirstUserConfirmed2())) || (!isTraderFirstUser && (ongoingTrade.isSecondUserConfirmed1() || ongoingTrade.isSecondUserConfirmed2()))) {
                    tradeConfirmButton.setText("Confirmed");
                    tradeConfirmButton.setFont(boldItalic.deriveFont(20f));
                    tradeConfirmButton.setForeground(Color.WHITE);
                    tradeConfirmButton.setBackground(bg);
                    tradeConfirmButton.setEnabled(false);
                    tradeConfirmButton.setBorder(BorderFactory.createLineBorder(gray, 15));
                } else {
                    tradeConfirmButton.setText("Confirm");
                    tradeConfirmButton.setFont(bold.deriveFont(20f));
                    tradeConfirmButton.setForeground(Color.WHITE);
                    tradeConfirmButton.setBackground(green);
                    tradeConfirmButton.setOpaque(true);
                    tradeConfirmButton.setBorder(BorderFactory.createLineBorder(gray, 15));
                    tradeConfirmButton.addActionListener(e -> {
                        try {
                            tradeManager.confirmMeetingGeneral(trader.getId(), tradeID, true);
                            tradeConfirmButton.setBackground(bg);
                            tradeConfirmButton.setText("Confirmed");
                            tradeConfirmButton.setEnabled(false);
                            tradeConfirmButton.setFont(boldItalic.deriveFont(20f));
                            ongoingTradesContainer.revalidate();
                            ongoingTradesContainer.repaint();
                        } catch (TradeNotFoundException | UserNotFoundException | AuthorizationException
                                | CannotTradeException e1) {
                            System.out.println(e1.getMessage());
                        }
                    });
                }
                

                ongoingTradePanel.add(otherTraderName);
                ongoingTradePanel.add(tradeLocation);
                ongoingTradePanel.add(tradeMeetingTime);
                ongoingTradePanel.add(tradeDetailsButton);
                ongoingTradePanel.add(tradeConfirmButton);
                ongoingTradesContainer.add(ongoingTradePanel);
            } catch(TradeNotFoundException | UserNotFoundException exception) {
            // } catch(Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JDialog addNewTradeModal = new JDialog();
        addNewTradeModal.setTitle("Add New Trade");
        addNewTradeModal.setSize(500, 900);
        addNewTradeModal.setResizable(false);
        addNewTradeModal.setLocationRelativeTo(null);

        JPanel addNewTradePanel = new JPanel();
        addNewTradePanel.setPreferredSize(new Dimension(500,900));
        addNewTradePanel.setBackground(bg);

        JLabel otherTraderNameTitle = new JLabel("Trader Username:");
        otherTraderNameTitle.setFont(italic.deriveFont(20f));
        otherTraderNameTitle.setPreferredSize(new Dimension(450,50));
        otherTraderNameTitle.setOpaque(false);
        otherTraderNameTitle.setForeground(Color.WHITE);

        JComboBox<Trader> traders = new JComboBox<>();
        traders.setPreferredSize(new Dimension(450, 50));
        traders.setFont(regular.deriveFont(20f));
        traders.setBackground(gray2);
        traders.setForeground(Color.BLACK);
        traders.setOpaque(true);
        infoManager.getAllTraders().forEach(t -> {
            if (!t.getUsername().equals(trader.getUsername()))
                traders.addItem(t);
        });

        JButton tradeSubmitButton = new JButton("Submit");
        tradeSubmitButton.setFont(bold.deriveFont(25f));
        tradeSubmitButton.setBackground(green);
        tradeSubmitButton.setOpaque(true);
        tradeSubmitButton.setForeground(Color.WHITE);
        tradeSubmitButton.setPreferredSize(new Dimension(225,75));
        tradeSubmitButton.setBorder(BorderFactory.createLineBorder(bg, 15));

        JLabel traderItemTitle = new JLabel("Item from your Inventory:");
        traderItemTitle.setFont(italic.deriveFont(20f));
        traderItemTitle.setPreferredSize(new Dimension(450, 50));
        traderItemTitle.setOpaque(false);
        traderItemTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        traderItemTitle.setForeground(Color.WHITE);

        JComboBox<String> traderItems = new JComboBox<>();
        traderItems.setFont(regular.deriveFont(20f));
        traderItems.setBackground(gray2);
        traderItems.setForeground(Color.BLACK);
        traderItems.setOpaque(true);
        traderItems.setPreferredSize(new Dimension(450,50));
        for(String itemId : trader.getAvailableItems()) {
            try {
                traderItems.addItem(tradeManager.getTradableItem(itemId).getName());
            } catch (TradableItemNotFoundException e1) {
                System.out.println(e1.getMessage());
            }
        }

        JLabel otherTraderItemTitle = new JLabel("Item from their Inventory:");
        otherTraderItemTitle.setFont(italic.deriveFont(20f));
        otherTraderItemTitle.setPreferredSize(new Dimension(450, 50));
        otherTraderItemTitle.setOpaque(false);
        otherTraderItemTitle.setForeground(Color.WHITE);

        JComboBox<TradableItem> otherTraderItems = new JComboBox<>();
        otherTraderItems.setPreferredSize(new Dimension(450, 50));
        otherTraderItems.setFont(regular.deriveFont(20f));
        otherTraderItems.setBackground(gray2);
        otherTraderItems.setForeground(Color.BLACK);
        otherTraderItems.setOpaque(true);


        traders.addItemListener(ev -> {
            if (ev.getStateChange() == ItemEvent.SELECTED) {
                otherTraderItems.setVisible(false);
                otherTraderItems.removeAllItems();
                for (String itemId : ((Trader) ev.getItem()).getAvailableItems()) {
                    try {
                        otherTraderItems.addItem(tradeManager.getTradableItem(itemId));
                    } catch (TradableItemNotFoundException e1) {
                        System.out.println(e1.getMessage());
                    }
                }
                otherTraderItems.setVisible(true);
            }
        });


        JLabel meetingLocationTitle = new JLabel("Meeting Location");
        meetingLocationTitle.setFont(italic.deriveFont(20f));
        meetingLocationTitle.setPreferredSize(new Dimension(450, 50));
        meetingLocationTitle.setOpaque(false);
        meetingLocationTitle.setForeground(Color.WHITE);

        JTextField meetingLocationInput = new JTextField();
        meetingLocationInput.setFont(regular.deriveFont(20f));
        meetingLocationInput.setBackground(gray2);
        meetingLocationInput.setForeground(Color.BLACK);
        meetingLocationInput.setPreferredSize(new Dimension(450, 50));
        meetingLocationInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel isTemporaryTitle = new JLabel("Is this trade temporary?");
        isTemporaryTitle.setFont(italic.deriveFont(20f));
        isTemporaryTitle.setPreferredSize(new Dimension(425, 50));
        isTemporaryTitle.setOpaque(false);
        isTemporaryTitle.setForeground(Color.WHITE);

        JCheckBox isTemporaryButton = new JCheckBox();
        isTemporaryButton.setPreferredSize(new Dimension(25,25));
        isTemporaryButton.setSelected(true);
        isTemporaryButton.setForeground(Color.WHITE);
        isTemporaryButton.setBackground(bg);

        JLabel firstMeetingDateTitle = new JLabel("First Meeting Date:");
        firstMeetingDateTitle.setPreferredSize(new Dimension(450, 50));
        firstMeetingDateTitle.setFont(italic.deriveFont(20f));
        firstMeetingDateTitle.setOpaque(false);
        firstMeetingDateTitle.setForeground(Color.WHITE);

        JLabel secondMeetingDateTitle = new JLabel("Second Meeting Date:");
        secondMeetingDateTitle.setPreferredSize(new Dimension(450, 50));
        secondMeetingDateTitle.setFont(italic.deriveFont(20f));
        secondMeetingDateTitle.setOpaque(false);
        secondMeetingDateTitle.setForeground(Color.WHITE);

        isTemporaryButton.addItemListener(ex -> {
            if (isTemporaryButton.isSelected()) {
                secondMeetingDateTitle.setVisible(true);
            } else {
                secondMeetingDateTitle.setVisible(false);
            }
        });

        addNewTradePanel.add(otherTraderNameTitle);
        addNewTradePanel.add(traders);
        addNewTradePanel.add(traderItemTitle);
        addNewTradePanel.add(traderItems);
        addNewTradePanel.add(otherTraderItemTitle);
        addNewTradePanel.add(otherTraderItems);
        addNewTradePanel.add(meetingLocationTitle);
        addNewTradePanel.add(meetingLocationInput);
        addNewTradePanel.add(isTemporaryTitle);
        addNewTradePanel.add(isTemporaryButton);
        addNewTradePanel.add(firstMeetingDateTitle);
        addNewTradePanel.add(secondMeetingDateTitle);
        
        addNewTradeModal.add(addNewTradePanel);
        addNewTradeModal.add(tradeSubmitButton, BorderLayout.SOUTH);
        addNewTradeModal.setModal(true);
        addNewTradeModal.setVisible(true);
    }
}