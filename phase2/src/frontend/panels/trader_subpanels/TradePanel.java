package frontend.panels.trader_subpanels;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;

import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.tradesystem.managers.TradingManager;

public class TradePanel extends JPanel {

    private JPanel ongoingTradesContainer, tradeRequestsContainer, ongoingTradesTitleContainer;
    private JScrollPane tradeRequestsScrollPane, ongoingTradesScrollPane;
    private JButton addTradeButton;
    private JLabel ongoingTradesTitle, tradeRequestsTitle;
    private Font regular, bold, italic, boldItalic;
    private TradingManager tradeManager;
    private Trader trader;

    private Color bg = new Color(51, 51, 51);
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

        this.setPreferredSize(new Dimension(1000, 900)); // fix this later
        this.setBackground(bg);

        tradeRequestsScrollPane = new JScrollPane();
        tradeRequestsScrollPane.setPreferredSize(new Dimension(1200, 300));
        
        ongoingTradesScrollPane = new JScrollPane();
        ongoingTradesScrollPane.setPreferredSize(new Dimension(1200, 300));
        
        ongoingTradesContainer = new JPanel(new GridLayout(1,2));
        ongoingTradesContainer.setOpaque(false);
        ongoingTradesContainer.setPreferredSize(new Dimension(1200,50));
        
        ongoingTradesTitle = new JLabel("Ongoing Trades");
        ongoingTradesTitle.setFont(this.regular.deriveFont(30f));
        ongoingTradesTitle.setForeground(Color.WHITE);
        ongoingTradesTitle.setHorizontalAlignment(JLabel.LEFT);
        ongoingTradesContainer.add(ongoingTradesTitle);
        
        addTradeButton = new JButton("Add new trade");
        addTradeButton.setFont(this.boldItalic.deriveFont(20f));
        addTradeButton.setHorizontalAlignment(JButton.RIGHT);
        addTradeButton.setForeground(Color.WHITE);
        addTradeButton.setBackground(bg);
        addTradeButton.setOpaque(true);
        addTradeButton.setBorderPainted(false);
        ongoingTradesContainer.add(addTradeButton);
        

        tradeRequestsTitle = new JLabel("Trade Requests");
        tradeRequestsTitle.setFont(this.regular.deriveFont(30f));
        tradeRequestsTitle.setForeground(Color.WHITE);
        tradeRequestsTitle.setHorizontalAlignment(JLabel.LEFT);
        tradeRequestsTitle.setPreferredSize(new Dimension(1200, 50));
        
        this.add(ongoingTradesContainer);
        getOngoingTradesPanel();
        ongoingTradesScrollPane.setViewportView(ongoingTradesContainer);
        this.add(ongoingTradesScrollPane);
        
        this.add(tradeRequestsTitle);
        getTradeRequestPanels();
        tradeRequestsScrollPane.setViewportView(tradeRequestsContainer);
        this.add(tradeRequestsScrollPane);
    }

    private void getTradeRequestPanels() {
        tradeRequestsContainer = new JPanel(new GridLayout(5, 1));
        // tradeRequestsContainer = new JPanel(new GridLayout(trader.getRequestedTrades().size(), 1));
        // for(String tradeID : trader.getRequestedTrades()) {
        for (int i = 0; i < 5; i++) {
            try {
                // Trade ongoingTrade = tradeManager.getTrade(tradeID);
                JPanel tradeRequestPanel = new JPanel(new GridLayout(1, 5, 10, 0));
                tradeRequestPanel.setSize(900, 10);
                // JLabel otherTraderName = new JLabel((tradeManager.getUser(ongoingTrade.getFirstUserId()).getUsername().equals(trader.getUsername()) ? tradeManager.getUser(ongoingTrade.getFirstUserId()).getUsername() : tradeManager.getUser(ongoingTrade.getSecondUserId()).getUsername()));
                JLabel otherTraderName = new JLabel("otherTraderName");
                otherTraderName.setFont(regular.deriveFont(20f));
                otherTraderName.setForeground(Color.BLACK);
                otherTraderName.setHorizontalAlignment(JLabel.CENTER);

                JLabel tradeLocation = new JLabel("Mom's Basement");
                tradeLocation.setFont(regular.deriveFont(20f));
                tradeLocation.setForeground(Color.BLACK);
                tradeLocation.setHorizontalAlignment(JLabel.CENTER);
                
                JLabel otherTraderItemName = new JLabel("Guitar");
                otherTraderItemName.setFont(regular.deriveFont(20f));
                otherTraderItemName.setForeground(Color.BLACK);
                otherTraderItemName.setHorizontalAlignment(JLabel.CENTER);

                JLabel traderItemName = new JLabel("Fried Chicken");
                traderItemName.setFont(regular.deriveFont(20f));
                traderItemName.setForeground(Color.BLACK);
                traderItemName.setHorizontalAlignment(JLabel.CENTER);

                JButton tradeDetailsButton = new JButton("Details");
                tradeDetailsButton.setFont(bold.deriveFont(20f));
                tradeDetailsButton.setForeground(Color.WHITE);
                tradeDetailsButton.setBackground(detailsButton);
                tradeDetailsButton.setOpaque(true);
                tradeDetailsButton.setBorderPainted(false);

                JButton tradeConfirmButton = new JButton("Confirm");
                tradeConfirmButton.setFont(bold.deriveFont(20f));
                tradeConfirmButton.setForeground(Color.WHITE);
                tradeConfirmButton.setBackground(confirmButton);
                tradeConfirmButton.setOpaque(true);
                tradeConfirmButton.setBorderPainted(false);

                JButton tradeRejectButton = new JButton("Reject");
                tradeRejectButton.setFont(bold.deriveFont(20f));
                tradeRejectButton.setForeground(Color.WHITE);
                tradeRejectButton.setBackground(red);
                tradeRejectButton.setOpaque(true);
                tradeRejectButton.setBorderPainted(false);

                tradeRequestPanel.add(otherTraderName);
                tradeRequestPanel.add(tradeLocation);
                tradeRequestPanel.add(otherTraderItemName);
                tradeRequestPanel.add(tradeDetailsButton);
                tradeRequestPanel.add(tradeConfirmButton);
                tradeRequestPanel.add(tradeRejectButton);
                tradeRequestsContainer.add(tradeRequestPanel);
                // } catch(TradeNotFoundException | UserNotFoundException exception) {
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }

    }
    
    private void getOngoingTradesPanel() {
        ongoingTradesContainer = new JPanel(new GridLayout(5, 1));
        // ongoingTradesContainer = new JPanel(new GridLayout(trader.getAcceptedTrades().size(), 1));
        // for(String tradeID : trader.getAcceptedTrades()) {
        for(int i = 0; i < 5; i++) {
            try {
                // Trade ongoingTrade = tradeManager.getTrade(tradeID);
                JPanel ongoingTradePanel = new JPanel(new GridLayout(1,5)); 
                ongoingTradePanel.setSize(900,10);
                // JLabel otherTraderName = new JLabel((tradeManager.getUser(ongoingTrade.getFirstUserId()).getUsername().equals(trader.getUsername()) ? tradeManager.getUser(ongoingTrade.getFirstUserId()).getUsername() : tradeManager.getUser(ongoingTrade.getSecondUserId()).getUsername()));
                JLabel otherTraderName = new JLabel("otherTraderName");
                otherTraderName.setFont(regular.deriveFont(20f));
                otherTraderName.setForeground(Color.BLACK);
                otherTraderName.setHorizontalAlignment(JLabel.CENTER);
                // JLabel tradeLocation = new JLabel(ongoingTrade.getMeetingLocation());
                JLabel tradeLocation = new JLabel("McDonalds");
                tradeLocation.setFont(regular.deriveFont(20f));
                tradeLocation.setForeground(Color.BLACK);
                tradeLocation.setHorizontalAlignment(JLabel.CENTER);
                // JLabel tradeMeetingTime = new JLabel(ongoingTrade.getMeetingTime().toString()); // fix to get current
                JLabel tradeMeetingTime = new JLabel("2020/07/30@14:20");
                tradeMeetingTime.setFont(regular.deriveFont(20f));
                tradeMeetingTime.setForeground(Color.BLACK);
                tradeMeetingTime.setHorizontalAlignment(JLabel.CENTER);

                JButton tradeDetailsButton = new JButton("Details");
                tradeDetailsButton.setFont(bold.deriveFont(20f));
                tradeDetailsButton.setForeground(Color.WHITE);
                tradeDetailsButton.setBackground(detailsButton);
                tradeDetailsButton.setOpaque(true);
                tradeDetailsButton.setBorderPainted(false);

                JButton tradeConfirmButton = new JButton("Confirm");
                tradeConfirmButton.setFont(bold.deriveFont(20f));
                tradeConfirmButton.setForeground(Color.WHITE);
                tradeConfirmButton.setBackground(confirmButton);
                tradeConfirmButton.setOpaque(true);
                tradeConfirmButton.setBorderPainted(false);

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