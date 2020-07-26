package frontend.panels.trader_subpanels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.io.IOException;

import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.tradesystem.managers.TradingManager;

public class TradePanel extends JPanel {

    private JPanel ongoingTradesContainer, tradeRequestsContainer, ongoingTradesTitleContainer,ongoingTradesHeader, tradeRequestsHeader;
    private JScrollPane tradeRequestsScrollPane, ongoingTradesScrollPane;
    private JButton addTradeButton;
    private JLabel ongoingTradesTitle, tradeRequestsTitle;
    private Font regular, bold, italic, boldItalic;
    private TradingManager tradeManager;
    private Trader trader;
    private GridBagConstraints gbc;

    private Color bg = new Color(51, 51, 51);
    private Color blue = new Color(0, 240, 239);
    private Color gray = new Color(196,196,196);
    private Color detailsButton = new Color(142,142,142);
    private Color confirmButton = new Color(27,158,36);
    private Color red = new Color(219, 58, 52);

    public TradePanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        tradeManager = new TradingManager();

        this.setBorder(BorderFactory.createEmptyBorder(25, 0, 100, 25));
        this.setBackground(bg);

        JPanel ongoingTrades = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();

        ongoingTradesTitleContainer = new JPanel(new GridLayout(1,2));
        ongoingTradesTitleContainer.setOpaque(false);
        ongoingTradesTitleContainer.setPreferredSize(new Dimension(1300,50));

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
        ongoingTradesTitleContainer.add(addTradeButton);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        ongoingTrades.add(ongoingTradesTitleContainer, gbc);

        ongoingTradesHeader = new JPanel(new GridLayout(1,5));
        ongoingTradesHeader.setPreferredSize(new Dimension(1300,25));
        ongoingTradesHeader.setBackground(gray);
        ongoingTradesHeader.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 80));
        addOngoingTradesHeader();
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        ongoingTrades.add(ongoingTradesHeader, gbc);

        ongoingTradesScrollPane = new JScrollPane();
        ongoingTradesScrollPane.setPreferredSize(new Dimension(1300, 360));
        getOngoingTradesPanel();
        ongoingTradesScrollPane.setViewportView(ongoingTradesContainer);
        ongoingTradesScrollPane.setBackground(gray);
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
        tradeRequestsTitle.setPreferredSize(new Dimension(1300, 50));
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 0.1;
        tradeRequests.add(tradeRequestsTitle, gbc);

        tradeRequestsHeader = new JPanel(new GridLayout(1,7));
        tradeRequestsHeader.setPreferredSize(new Dimension(1300,25));
        tradeRequestsHeader.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 120));
        tradeRequestsHeader.setBackground(gray);
        addTradeRequestsHeader();
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        tradeRequests.add(tradeRequestsHeader, gbc);

        tradeRequestsScrollPane = new JScrollPane();
        tradeRequestsScrollPane.setPreferredSize(new Dimension(1300, 300));
        getTradeRequestPanels();
        tradeRequestsScrollPane.setViewportView(tradeRequestsContainer);
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

        JLabel meetingTime = new JLabel("Current Meeting Time");
        meetingTime.setFont(this.regular.deriveFont(20f));
        meetingTime.setForeground(Color.BLACK);
        meetingTime.setHorizontalAlignment(JLabel.RIGHT);

        JLabel empty1 = new JLabel("");
        JLabel empty2 = new JLabel("");

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
        name.setHorizontalAlignment(JLabel.LEFT);

        JLabel location = new JLabel("Location   ");
        location.setFont(this.regular.deriveFont(20f));
        location.setForeground(Color.BLACK);
        location.setHorizontalAlignment(JLabel.CENTER);

        JLabel theirItem = new JLabel("    Their Item");
        theirItem.setFont(this.regular.deriveFont(20f));
        theirItem.setForeground(Color.BLACK);
        theirItem.setHorizontalAlignment(JLabel.CENTER);

        JLabel yourItem = new JLabel("Your Item");
        yourItem.setFont(this.regular.deriveFont(20f));
        yourItem.setForeground(Color.BLACK);
        yourItem.setHorizontalAlignment(JLabel.RIGHT);

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
        tradeRequestsContainer = new JPanel(new GridLayout(trader.getRequestedTrades().size(), 1));
        tradeRequestsContainer.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        tradeRequestsContainer.setBackground(gray);
        for(String tradeID : trader.getRequestedTrades()) {
        // for (int i = 0; i < 10; i++) {
            try {

                Trade ongoingTrade = tradeManager.getTrade(tradeID);
                JPanel tradeRequestPanel = new JPanel(new GridLayout(1, 6, 10, 0));
                tradeRequestPanel.setPreferredSize(new Dimension(1000, 75));
                tradeRequestPanel.setBackground(gray);

                boolean isTraderFirstUser = tradeManager.getUser(ongoingTrade.getFirstUserId()).getUsername().equals(trader.getUsername());
                JLabel otherTraderName, otherTraderItemName, traderItemName;
                if(isTraderFirstUser) {
                    otherTraderName = new JLabel(tradeManager.getUser(ongoingTrade.getSecondUserId()).getUsername());
                    otherTraderItemName = new JLabel(tradeManager.getTradableItem(ongoingTrade.getSecondUserOffer()).toString());
                    traderItemName = new JLabel(tradeManager.getTradableItem(ongoingTrade.getFirstUserOffer()).toString());
                } else {
                    otherTraderName = new JLabel(tradeManager.getUser(ongoingTrade.getFirstUserId()).getUsername());
                    otherTraderItemName = new JLabel(tradeManager.getTradableItem(ongoingTrade.getFirstUserOffer()).toString());
                    traderItemName = new JLabel(tradeManager.getTradableItem(ongoingTrade.getSecondUserOffer()).toString());
                }

                // JLabel otherTraderName = new JLabel("otherTrader #" + (i + 1));
                otherTraderName.setFont(regular.deriveFont(20f));
                otherTraderName.setForeground(Color.BLACK);
                otherTraderName.setHorizontalAlignment(JLabel.LEFT);

                JLabel tradeLocation = new JLabel(ongoingTrade.getMeetingLocation());
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
                tradeDetailsButton.setBackground(detailsButton);
                tradeDetailsButton.setOpaque(true);
                tradeDetailsButton.setBorder(BorderFactory.createLineBorder(gray, 15));

                JButton tradeConfirmButton = new JButton("Confirm");
                tradeConfirmButton.setFont(bold.deriveFont(20f));
                tradeConfirmButton.setForeground(Color.WHITE);
                tradeConfirmButton.setBackground(confirmButton);
                tradeConfirmButton.setOpaque(true);
                tradeConfirmButton.setBorder(BorderFactory.createLineBorder(gray, 15));

                JButton tradeRejectButton = new JButton("Reject");
                tradeRejectButton.setFont(bold.deriveFont(20f));
                tradeRejectButton.setForeground(Color.WHITE);
                tradeRejectButton.setBackground(red);
                tradeRejectButton.setOpaque(true);
                tradeRejectButton.setBorder(BorderFactory.createLineBorder(gray, 15));

                tradeRequestPanel.add(otherTraderName);
                tradeRequestPanel.add(tradeLocation);
                tradeRequestPanel.add(otherTraderItemName);
                tradeRequestPanel.add(traderItemName);
                tradeRequestPanel.add(tradeDetailsButton);
                tradeRequestPanel.add(tradeConfirmButton);
                tradeRequestPanel.add(tradeRejectButton);
                tradeRequestsContainer.add(tradeRequestPanel);
                } catch(TradeNotFoundException | UserNotFoundException | TradableItemNotFoundException exception) {
            // } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }

    }
    
    private void getOngoingTradesPanel() {
        ongoingTradesContainer = new JPanel(new GridLayout(10, 1));
        ongoingTradesContainer.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        ongoingTradesContainer.setBackground(gray);
        // ongoingTradesContainer = new JPanel(new GridLayout(trader.getAcceptedTrades().size(), 1));
        // for(String tradeID : trader.getAcceptedTrades()) {
        for(int i = 0; i < 10; i++) {
            try {
                // Trade ongoingTrade = tradeManager.getTrade(tradeID);
                JPanel ongoingTradePanel = new JPanel(new GridLayout(1,5, 10, 0)); 
                ongoingTradePanel.setPreferredSize(new Dimension(1000,75));
                ongoingTradePanel.setBackground(gray);
                // JLabel otherTraderName = new JLabel((tradeManager.getUser(ongoingTrade.getFirstUserId()).getUsername().equals(trader.getUsername()) ? tradeManager.getUser(ongoingTrade.getFirstUserId()).getUsername() : tradeManager.getUser(ongoingTrade.getSecondUserId()).getUsername()));
                JLabel otherTraderName = new JLabel("otherTrader #"+ (i + 1));
                otherTraderName.setFont(regular.deriveFont(20f));
                otherTraderName.setForeground(Color.BLACK);
                otherTraderName.setHorizontalAlignment(JLabel.LEFT);
                // JLabel tradeLocation = new JLabel(ongoingTrade.getMeetingLocation());
                JLabel tradeLocation = new JLabel("Meeting Location #" + (i+1));
                tradeLocation.setFont(regular.deriveFont(20f));
                tradeLocation.setForeground(Color.BLACK);
                tradeLocation.setHorizontalAlignment(JLabel.CENTER);
                // JLabel tradeMeetingTime = new JLabel(ongoingTrade.getMeetingTime().toString()); // fix to get current
                JLabel tradeMeetingTime = new JLabel("2020/07/30@14:2"+i);
                tradeMeetingTime.setFont(regular.deriveFont(20f));
                tradeMeetingTime.setForeground(Color.BLACK);
                tradeMeetingTime.setHorizontalAlignment(JLabel.CENTER);

                JButton tradeDetailsButton = new JButton("Details");
                tradeDetailsButton.setFont(bold.deriveFont(20f));
                tradeDetailsButton.setForeground(Color.WHITE);
                tradeDetailsButton.setBackground(detailsButton);
                tradeDetailsButton.setOpaque(true);
                tradeDetailsButton.setBorder(BorderFactory.createLineBorder(gray, 15));

                JButton tradeConfirmButton = new JButton("Confirm");
                tradeConfirmButton.setFont(bold.deriveFont(20f));
                tradeConfirmButton.setForeground(Color.WHITE);
                tradeConfirmButton.setBackground(confirmButton);
                tradeConfirmButton.setOpaque(true);
                tradeConfirmButton.setBorder(BorderFactory.createLineBorder(gray, 15));

                ongoingTradePanel.add(otherTraderName);
                ongoingTradePanel.add(tradeLocation);
                ongoingTradePanel.add(tradeMeetingTime);
                ongoingTradePanel.add(tradeDetailsButton);
                ongoingTradePanel.add(tradeConfirmButton);
                ongoingTradesContainer.add(ongoingTradePanel);
            // } catch(TradeNotFoundException | UserNotFoundException exception) {
            } catch(Exception exception) {
                System.out.println(exception.getMessage());
            }
        }


    }
}