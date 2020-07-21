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

        tradeRequestsContainer = new JScrollPane();
        getTradeRequestPanels();

        ongoingTradesContainer = new JScrollPane();
        getOngoingTradesPanel();

        ongoingTradesTitle = new JLabel("Ongoing Trades");
        ongoingTradesTitle.setFont(regular.deriveFont(20f));
        ongoingTradesTitle.setForeground(Color.BLACK);
        ongoingTradesTitle.setHorizontalAlignment(JLabel.CENTER);

        tradeRequestsTitle = new JLabel("Trade Requests");

        addTradeButton = new JButton("Add Trade");

        // this.add(tradeRequestsTitle);
        // this.add(tradeRequestsContainer);
        this.add(ongoingTradesTitle, BorderLayout.NORTH);
        this.add(ongoingTradesContainer, BorderLayout.CENTER);
    }

    private void getTradeRequestPanels() {
    }
    
    private void getOngoingTradesPanel() {
        for(String tradeID : trader.getAcceptedTrades()) {
            try {
                Trade ongoingTrade = tradeManager.getTrade(tradeID);
                JPanel ongoingTradePanel = new JPanel(new GridLayout(1,3)); //FIX
                JLabel otherTraderName = new JLabel(tradeManager.getUser(ongoingTrade.getFirstUserId()).getUsername());
                JLabel tradeLocation = new JLabel(ongoingTrade.getMeetingLocation());
                JLabel tradeMeetingTime = new JLabel(ongoingTrade.getMeetingTime().toString());
                ongoingTradePanel.add(otherTraderName);
                ongoingTradePanel.add(tradeLocation);
                ongoingTradePanel.add(tradeMeetingTime);
                ongoingTradesContainer.add(ongoingTradePanel);
            } catch(TradeNotFoundException | UserNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
        }


    }
}