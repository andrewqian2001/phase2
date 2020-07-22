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

    private JScrollPane ongoingTradesContainer, tradeRequestsContainer;
    private JButton addTradeButton;
    private JLabel ongoingTradesTitle, tradeRequestsTitle;
    private Font regular, bold, italic, boldItalic;
    private TradingManager tradeManager;
    private Trader trader;

    private Color bg = new Color(51, 51, 51);

    public TradePanel(Trader trader, Font regular, Font bold, Font italic, Font boldItalic) throws IOException {
        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;

        tradeManager = new TradingManager();

        this.setSize(1000, 900); // fix this later
        this.setBackground(bg);
        this.setLayout(new GridLayout(5,1)); //FIX

        tradeRequestsContainer = new JScrollPane();
        
        ongoingTradesContainer = new JScrollPane();
        
        ongoingTradesTitle = new JLabel("Ongoing Trades");
        ongoingTradesTitle.setFont(regular.deriveFont(20f));
        ongoingTradesTitle.setForeground(Color.WHITE);
        ongoingTradesTitle.setHorizontalAlignment(JLabel.LEFT);

        tradeRequestsTitle = new JLabel("Trade Requests");
        tradeRequestsTitle.setFont(regular.deriveFont(20f));
        tradeRequestsTitle.setForeground(Color.WHITE);
        tradeRequestsTitle.setHorizontalAlignment(JLabel.LEFT);
        
        
        addTradeButton = new JButton("Add Trade");
        
        this.add(ongoingTradesTitle);
        getOngoingTradesPanel();
        this.add(tradeRequestsTitle);
        getTradeRequestPanels();
        this.add(tradeRequestsContainer);
    }

    private void getTradeRequestPanels() {
    }
    
    private void getOngoingTradesPanel() {
        for(String tradeID : trader.getAcceptedTrades()) {
        // for(int i = 0; i < 3; i++) {
            try {
                Trade ongoingTrade = tradeManager.getTrade(tradeID);
                JPanel ongoingTradePanel = new JPanel(new GridLayout(1,3)); //FIX
                JLabel otherTraderName = new JLabel(tradeManager.getUser(ongoingTrade.getFirstUserId()).getUsername());
                // JLabel otherTraderName = new JLabel("otherTraderName");
                otherTraderName.setFont(regular.deriveFont(20f));
                otherTraderName.setForeground(Color.BLACK);
                otherTraderName.setHorizontalAlignment(JLabel.LEFT);
                JLabel tradeLocation = new JLabel(ongoingTrade.getMeetingLocation());
                // JLabel tradeLocation = new JLabel("McDonalds");
                tradeLocation.setFont(regular.deriveFont(20f));
                tradeLocation.setForeground(Color.BLACK);
                tradeLocation.setHorizontalAlignment(JLabel.LEFT);
                JLabel tradeMeetingTime = new JLabel(ongoingTrade.getMeetingTime().toString());
                // JLabel tradeMeetingTime = new JLabel("4:20:69AM");
                tradeMeetingTime.setFont(regular.deriveFont(20f));
                tradeMeetingTime.setForeground(Color.BLACK);
                tradeMeetingTime.setHorizontalAlignment(JLabel.LEFT);

                ongoingTradePanel.add(otherTraderName);
                ongoingTradePanel.add(tradeLocation);
                ongoingTradePanel.add(tradeMeetingTime);
                ongoingTradesContainer.add(ongoingTradePanel);
            } catch(TradeNotFoundException | UserNotFoundException exception) {
            // } catch(Exception exception) {
                System.out.println(exception.getMessage());
            }
        }


    }
}