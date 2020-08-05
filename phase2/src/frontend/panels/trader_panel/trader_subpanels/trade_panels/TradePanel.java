package frontend.panels.trader_panel.trader_subpanels.trade_panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import backend.exceptions.AuthorizationException;
import backend.exceptions.CannotTradeException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.ItemQuery;
import backend.tradesystem.queries.TradeQuery;
import backend.tradesystem.queries.UserQuery;
import backend.tradesystem.trader_managers.TradingInfoManager;
import backend.tradesystem.trader_managers.TradingManager;

/**
 * Represents the panel where trading occurs
 */
public class TradePanel extends JPanel  {

    private JPanel tradeRequestsContainer;
    private final JScrollPane tradeRequestsScrollPane;
    private final JPanel tradeRequestsHeader;
    private final Font regular, bold, italic, boldItalic;
    private final String trader;

    private final Color bg = new Color(51, 51, 51);
    private final Color gray = new Color(196, 196, 196);
    private final Color gray2 = new Color(142, 142, 142);
    private final Color green = new Color(27, 158, 36);
    private final Color red = new Color(219, 58, 52);
    private final TradeQuery tradeQuery = new TradeQuery();
    private final UserQuery userQuery = new UserQuery();
    private final ItemQuery itemQuery = new ItemQuery();
    private final TradingManager tradeManager = new TradingManager();
    private final TradingInfoManager infoManager = new TradingInfoManager();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm", new Locale("en", "US"));

    /**
     * Makes a trade panel
     *
     * @param trader     the trader id
     * @param regular    the regular font
     * @param bold       the bold font
     * @param italic     the italics font
     * @param boldItalic the bold italics font
     * @throws IOException            issues with getting database files
     * @throws UserNotFoundException  trader is is bad
     * @throws AuthorizationException user id isn't a trader
     * @throws TradeNotFoundException
     */
    public TradePanel(String trader, Font regular, Font bold, Font italic, Font boldItalic)
            throws IOException, UserNotFoundException, AuthorizationException, TradeNotFoundException {
        this.trader = trader;
        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;
        this.setBorder(BorderFactory.createEmptyBorder(25, 0, 100, 25));
        this.setBackground(bg);

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel tradeRequests = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();

        JLabel tradeRequestsTitle = new JLabel("Trade Requests");
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

        tradeRequestsHeader = new JPanel(new GridLayout(1, 8, 20, 0));
        tradeRequestsHeader.setPreferredSize(new Dimension(1200, 25));
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

        this.add(new OngoingTradesPanel(trader, regular, bold, italic, boldItalic));
        this.add(tradeRequests);
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

    private void getTradeRequestPanels() throws UserNotFoundException, AuthorizationException {
        ArrayList<String> requestedTrades = trader.equals("") ? new ArrayList<>()
                : userQuery.getRequestedTrades(trader);

        // tradeRequestsContainer = new JPanel(new GridLayout(10, 1));
        if (requestedTrades.size() == 0) {
            tradeRequestsContainer = new JPanel();
            tradeRequestsContainer.setBackground(gray2);
            JLabel noTradesFound = new JLabel("<html><pre>No Trade Requests Found</pre></html>");
            noTradesFound.setFont(bold.deriveFont(30f));
            noTradesFound.setForeground(Color.WHITE);
            tradeRequestsContainer.add(noTradesFound, BorderLayout.CENTER);
            return;
        }
        int numRows = requestedTrades.size();
        if (numRows < 4)
            numRows = 4;
        tradeRequestsContainer = new JPanel(new GridLayout(numRows, 1));
        tradeRequestsContainer.setBackground(gray2);
        tradeRequestsContainer.setBorder(null);
        for (String tradeID : requestedTrades) {
            try {
                if ((tradeQuery.getFirstUserId(tradeID).equals(trader)
                        && tradeQuery.getUserTurnToEdit(tradeID).equals(trader))
                        || !tradeQuery.getFirstUserId(tradeID).equals(trader)) {
                    JPanel tradeRequestPanel = new JPanel(new GridLayout(1, 7, 10, 0));
                    tradeRequestPanel.setPreferredSize(new Dimension(1000, 75));
                    tradeRequestPanel.setBackground(gray);
                    tradeRequestPanel.setBorder(BorderFactory.createLineBorder(bg));

                    JLabel otherTraderName = null;
                    JLabel otherTraderItemName = null;
                    JLabel traderItemName = null;
                    if (tradeQuery.getFirstUserId(tradeID).equals(trader)) {
                        otherTraderName = new JLabel(userQuery.getUsername(tradeQuery.getSecondUserId(tradeID)));

                        traderItemName = tradeQuery.getFirstUserOffer(tradeID).equals("") ? new JLabel("N/A")
                                : new JLabel(itemQuery.getName(tradeQuery.getFirstUserOffer(tradeID)));

                        otherTraderItemName = tradeQuery.getSecondUserOffer(tradeID).equals("") ? new JLabel("N/A")
                                : new JLabel(itemQuery.getName(tradeQuery.getSecondUserOffer(tradeID)));
                    } else {
                        otherTraderName = new JLabel(userQuery.getUsername(tradeQuery.getFirstUserId(tradeID)));

                        traderItemName = tradeQuery.getSecondUserOffer(tradeID).equals("") ? new JLabel("N/A")
                                : new JLabel(itemQuery.getName(tradeQuery.getSecondUserOffer(tradeID)));

                        otherTraderItemName = tradeQuery.getFirstUserOffer(tradeID).equals("") ? new JLabel("N/A")
                                : new JLabel(itemQuery.getName(tradeQuery.getFirstUserOffer(tradeID)));
                    }

                    // JLabel otherTraderName = new JLabel("otherTrader #" + (i + 1));
                    otherTraderName.setFont(regular.deriveFont(20f));
                    otherTraderName.setForeground(Color.BLACK);
                    otherTraderName.setHorizontalAlignment(JLabel.LEFT);
                    otherTraderName.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

                    JLabel tradeLocation = new JLabel(tradeQuery.getMeetingLocation(tradeID));
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

                    JLabel finalOtherTraderName = otherTraderName;
                    JLabel finalOtherTraderItemName = otherTraderItemName;
                    JLabel finalTraderItemName = traderItemName;

                    tradeDetailsButton.addActionListener(e -> {
                        JDialog tradeDetailsModal = new JDialog();
                        tradeDetailsModal.setTitle("Trade Details");
                        tradeDetailsModal.setSize(600, 600);
                        tradeDetailsModal.setResizable(false);
                        tradeDetailsModal.setLocationRelativeTo(null);

                        JPanel tradeDetailsPanel = new JPanel();
                        tradeDetailsPanel.setPreferredSize(new Dimension(600, 600));
                        tradeDetailsPanel.setBackground(bg);

                        JLabel otherTraderNameTitle = new JLabel("Trader Username:");
                        otherTraderNameTitle.setFont(italic.deriveFont(20f));
                        otherTraderNameTitle.setPreferredSize(new Dimension(290, 50));
                        otherTraderNameTitle.setOpaque(false);
                        otherTraderNameTitle.setForeground(Color.WHITE);

                        JLabel otherTraderDetailsName = new JLabel(
                                "<html><pre>" + finalOtherTraderName.getText() + "</pre></html>");
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

                        JLabel otherTraderItemRequestName = new JLabel(
                                "<html><pre>" + finalOtherTraderItemName.getText() + "</pre></html>");
                        JLabel traderItemRequestName = new JLabel(
                                "<html><pre>" + finalTraderItemName.getText() + "</pre></html>");

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

                        JLabel meetingLocationName = null;
                        try {
                            meetingLocationName = new JLabel(
                                    "<html><pre>" + tradeQuery.getMeetingLocation(tradeID) + "</pre></html>");
                        } catch (TradeNotFoundException tradeNotFoundException) {
                            tradeNotFoundException.printStackTrace();
                        }

                        meetingLocationName.setFont(italic.deriveFont(20f));
                        meetingLocationName.setPreferredSize(new Dimension(290, 50));
                        meetingLocationName.setOpaque(false);
                        meetingLocationName.setForeground(Color.WHITE);

                        JLabel firstMeetingDateTitle = new JLabel("First Meeting Date:");
                        firstMeetingDateTitle.setPreferredSize(new Dimension(290, 50));
                        firstMeetingDateTitle.setFont(italic.deriveFont(20f));
                        firstMeetingDateTitle.setOpaque(false);
                        firstMeetingDateTitle.setForeground(Color.WHITE);

                        JLabel firstMeetingDate = null;
                        try {
                            firstMeetingDate = new JLabel("<html><pre>"
                                    + dateFormat.format(tradeQuery.getMeetingTime(tradeID)) + "</pre></html>");
                        } catch (TradeNotFoundException tradeNotFoundException) {
                            tradeNotFoundException.printStackTrace();
                        }

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

                        try {
                            if (tradeQuery.getSecondMeetingTime(tradeID) != null) {
                                secondMeetingDate.setText(
                                        "<html><pre>" + dateFormat.format(tradeQuery.getSecondMeetingTime(tradeID))
                                                + "</pre></html>");
                            } else {
                                secondMeetingDate.setText("N/A");
                            }
                        } catch (TradeNotFoundException tradeNotFoundException) {
                            tradeNotFoundException.printStackTrace();
                        }

                        JLabel messageTitle = new JLabel("Optional Attached Messsage:");
                        messageTitle.setFont(italic.deriveFont(20f));
                        messageTitle.setPreferredSize(new Dimension(580, 50));
                        messageTitle.setOpaque(false);
                        messageTitle.setForeground(Color.WHITE);

                        JLabel messageBody = new JLabel();
                        messageBody.setFont(regular.deriveFont(20f));
                        messageBody.setPreferredSize(new Dimension(580, 50));
                        messageBody.setHorizontalAlignment(JLabel.CENTER);
                        messageBody.setOpaque(true);
                        messageBody.setBackground(gray);
                        messageBody.setForeground(Color.BLACK);

                        try {
                            messageBody.setText(tradeQuery.getMessage(tradeID));
                        } catch (TradeNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        JLabel availableEditsTitle = new JLabel("Available Edits Left:");
                        availableEditsTitle.setPreferredSize(new Dimension(290, 50));
                        availableEditsTitle.setFont(bold.deriveFont(20f));
                        availableEditsTitle.setOpaque(false);
                        availableEditsTitle.setForeground(Color.WHITE);

                        JLabel availableEdits = null;
                        try {
                            availableEdits = new JLabel("<html><pre>"
                                    + (tradeQuery.getMaxAllowedEdits(tradeID) / 2 - tradeQuery.getNumEdits(tradeID))
                                    + "</pre></html>");
                        } catch (TradeNotFoundException tradeNotFoundException) {
                            tradeNotFoundException.printStackTrace();
                        }

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
                        tradeDetailsPanel.add(messageTitle);
                        tradeDetailsPanel.add(messageBody);

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
                    editTradeButton.addActionListener(e -> {
                        JDialog tradeEditsModal = new JDialog();
                        tradeEditsModal.setTitle("Trade Edit");
                        tradeEditsModal.setSize(700, 700);
                        tradeEditsModal.setResizable(false);
                        tradeEditsModal.setLocationRelativeTo(null);

                        JPanel tradeEditsPanel = new JPanel();
                        tradeEditsPanel.setPreferredSize(new Dimension(700, 700));
                        tradeEditsPanel.setBackground(bg);

                        JLabel traderItemTitle = new JLabel("Item from your Inventory:");
                        traderItemTitle.setFont(italic.deriveFont(20f));
                        traderItemTitle.setPreferredSize(new Dimension(325, 50));
                        traderItemTitle.setOpaque(false);
                        traderItemTitle.setForeground(Color.WHITE);

                        JComboBox<String> traderItems = new JComboBox<>();
                        traderItems.setFont(regular.deriveFont(20f));
                        traderItems.setBackground(gray2);
                        traderItems.setForeground(Color.BLACK);
                        traderItems.setOpaque(true);
                        traderItems.setPreferredSize(new Dimension(325, 50));
                        try {
                            for (String itemId : userQuery.getAvailableItems(trader)) {
                                traderItems.addItem(itemQuery.getName(itemId));
                            }
                        } catch (UserNotFoundException | AuthorizationException | TradableItemNotFoundException e1) {
                            e1.printStackTrace();
                        }
                        traderItems.addItem(null);
                        // traderItems.setSelectedItem();

                        JLabel otherTraderItemTitle = new JLabel("Item from their Inventory:");
                        otherTraderItemTitle.setFont(italic.deriveFont(20f));
                        otherTraderItemTitle.setPreferredSize(new Dimension(325, 50));
                        otherTraderItemTitle.setOpaque(false);
                        otherTraderItemTitle.setForeground(Color.WHITE);

                        JComboBox<String> otherTraderItems = new JComboBox<>();
                        otherTraderItems.setFont(regular.deriveFont(20f));
                        otherTraderItems.setBackground(gray2);
                        otherTraderItems.setForeground(Color.BLACK);
                        otherTraderItems.setOpaque(true);
                        otherTraderItems.setPreferredSize(new Dimension(325, 50));
                        try {
                            for (String itemId : (userQuery.getAvailableItems(tradeQuery.getFirstUserId(tradeID)))) {
                                otherTraderItems.addItem(itemQuery.getName(itemId));
                            }
                        } catch (TradableItemNotFoundException | UserNotFoundException | TradeNotFoundException
                                | AuthorizationException e1) {
                            System.out.println(e1.getMessage());
                        }
                        otherTraderItems.addItem(null);
                        // otherTraderItems.setSelectedItem();

                        JLabel meetingLocationTitle = new JLabel("Meeting Location:");
                        meetingLocationTitle.setFont(italic.deriveFont(20f));
                        meetingLocationTitle.setPreferredSize(new Dimension(325, 50));
                        meetingLocationTitle.setOpaque(false);
                        meetingLocationTitle.setForeground(Color.WHITE);

                        JTextField meetingLocationInput = null;
                        try {
                            meetingLocationInput = new JTextField(tradeQuery.getMeetingLocation(tradeID));
                        } catch (TradeNotFoundException tradeNotFoundException) {
                            tradeNotFoundException.printStackTrace();
                        }

                        meetingLocationInput.setPreferredSize(new Dimension(325, 50));
                        meetingLocationInput.setFont(regular.deriveFont(20f));

                        JLabel firstMeetingDateTitle = new JLabel("First Meeting Date:");
                        firstMeetingDateTitle.setPreferredSize(new Dimension(200, 50));
                        firstMeetingDateTitle.setFont(italic.deriveFont(20f));
                        firstMeetingDateTitle.setOpaque(false);
                        firstMeetingDateTitle.setForeground(Color.WHITE);

                        String month = null;
                        int day = 0, year = 0, hour = 0, min = 0;
                        try {
                            month = tradeQuery.getMeetingTime(tradeID).toString().substring(4, 7);
                            day = Integer.parseInt(tradeQuery.getMeetingTime(tradeID).toString().substring(8, 10));
                            year = Integer.parseInt(tradeQuery.getMeetingTime(tradeID).toString().substring(24, 28));
                            hour = Integer.parseInt(tradeQuery.getMeetingTime(tradeID).toString().substring(11, 13));
                            min = Integer.parseInt(tradeQuery.getMeetingTime(tradeID).toString().substring(14, 16));
                        } catch (TradeNotFoundException tradeNotFoundException) {
                            tradeNotFoundException.printStackTrace();
                        }

                        JPanel firstMeetingDate = setDateInput(month, day, year, hour, min);

                        JLabel secondMeetingDateTitle = new JLabel("Second Meeting Date:");
                        secondMeetingDateTitle.setPreferredSize(new Dimension(200, 50));
                        secondMeetingDateTitle.setFont(italic.deriveFont(20f));
                        secondMeetingDateTitle.setOpaque(false);
                        secondMeetingDateTitle.setForeground(Color.WHITE);

                        String month2 = null;
                        int day2 = 0, year2 = 0, hour2 = 0, min2 = 0;
                        try {
                            month2 = tradeQuery.getSecondMeetingTime(tradeID).toString().substring(4, 7);
                            day2 = Integer
                                    .parseInt(tradeQuery.getSecondMeetingTime(tradeID).toString().substring(8, 10));
                            year2 = Integer
                                    .parseInt(tradeQuery.getSecondMeetingTime(tradeID).toString().substring(24, 28));
                            hour2 = Integer
                                    .parseInt(tradeQuery.getSecondMeetingTime(tradeID).toString().substring(11, 13));
                            min2 = Integer
                                    .parseInt(tradeQuery.getSecondMeetingTime(tradeID).toString().substring(14, 16));
                        } catch (TradeNotFoundException tradeNotFoundException) {
                            tradeNotFoundException.printStackTrace();
                        }
                        JPanel secondMeetingDate = setDateInput(month2, day2, year2, hour2, min2);

                        JLabel availableEditsTitle = new JLabel("Available Edits Left:");
                        availableEditsTitle.setPreferredSize(new Dimension(325, 50));
                        availableEditsTitle.setFont(bold.deriveFont(20f));
                        availableEditsTitle.setOpaque(false);
                        availableEditsTitle.setForeground(Color.WHITE);

                        JLabel availableEdits = null;
                        try {
                            availableEdits = new JLabel("<html><pre>" + (tradeQuery.getMaxAllowedEdits(tradeID) + 1) / 2
                                    + " Edit(s) Remaining</pre></html>");
                        } catch (TradeNotFoundException tradeNotFoundException) {
                            tradeNotFoundException.printStackTrace();
                        }
                        availableEdits.setFont(italic.deriveFont(20f));
                        availableEdits.setPreferredSize(new Dimension(325, 50));
                        availableEdits.setHorizontalAlignment(JLabel.CENTER);
                        availableEdits.setOpaque(false);
                        availableEdits.setForeground(Color.WHITE);

                        JLabel isTemporaryTitle = new JLabel("Is this trade temporary?");
                        isTemporaryTitle.setFont(italic.deriveFont(20f));
                        isTemporaryTitle.setPreferredSize(new Dimension(625, 50));
                        isTemporaryTitle.setOpaque(false);
                        isTemporaryTitle.setForeground(Color.WHITE);

                        JCheckBox isTemporaryButton = new JCheckBox();
                        isTemporaryButton.setPreferredSize(new Dimension(25, 25));
                        isTemporaryButton.setSelected(true);
                        isTemporaryButton.setForeground(Color.WHITE);
                        isTemporaryButton.setBackground(bg);

                        isTemporaryButton.addItemListener(ex -> {
                            if (isTemporaryButton.isSelected()) {
                                secondMeetingDateTitle.setVisible(true);
                                secondMeetingDate.setVisible(true);
                            } else {
                                secondMeetingDateTitle.setVisible(false);
                                secondMeetingDate.setVisible(false);
                            }
                        });

                        JLabel messageTitle = new JLabel("Attach a counter-message: (Optional)");
                        messageTitle.setFont(italic.deriveFont(20f));
                        messageTitle.setPreferredSize(new Dimension(650, 50));
                        messageTitle.setOpaque(false);
                        messageTitle.setForeground(Color.WHITE);

                        JTextField messageInput = new JTextField();
                        messageInput.setFont(regular.deriveFont(20f));
                        messageInput.setBackground(gray2);
                        messageInput.setForeground(Color.BLACK);
                        messageInput.setPreferredSize(new Dimension(650, 50));
                        messageInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                        JButton submitButton = new JButton("Submit");
                        submitButton.setFont(bold.deriveFont(20f));
                        submitButton.setBackground(green);
                        submitButton.setForeground(Color.WHITE);
                        submitButton.setPreferredSize(new Dimension(325, 50));

                        JLabel error = new JLabel();
                        error.setPreferredSize(new Dimension(650, 50));
                        error.setForeground(red);
                        error.setFont(boldItalic.deriveFont(20f));
                        error.setHorizontalAlignment(JLabel.CENTER);
                        error.setVisible(false);

                        tradeEditsPanel.add(traderItemTitle);
                        tradeEditsPanel.add(traderItems);
                        tradeEditsPanel.add(otherTraderItemTitle);
                        tradeEditsPanel.add(otherTraderItems);
                        tradeEditsPanel.add(meetingLocationTitle);
                        tradeEditsPanel.add(meetingLocationInput);
                        tradeEditsPanel.add(isTemporaryTitle);
                        tradeEditsPanel.add(isTemporaryButton);
                        tradeEditsPanel.add(firstMeetingDateTitle);
                        tradeEditsPanel.add(firstMeetingDate);
                        tradeEditsPanel.add(secondMeetingDateTitle);
                        tradeEditsPanel.add(secondMeetingDate);
                        tradeEditsPanel.add(messageTitle);
                        tradeEditsPanel.add(messageInput);
                        tradeEditsPanel.add(availableEditsTitle);
                        tradeEditsPanel.add(availableEdits);
                        tradeEditsPanel.add(error);

                        JTextField finalMeetingLocationInput = meetingLocationInput;

                        submitButton.addActionListener(f -> {
                            if (!finalMeetingLocationInput.getText().equals("")) {
                                String firstMeetingString = "";
                                String secondMeetingString = "";
                                for (int i = 0; i < 5; i++) {
                                    Component c = firstMeetingDate.getComponent(i);
                                    if (c instanceof JComboBox<?>) {
                                        if (((String) ((JComboBox<?>) c).getSelectedItem().toString()).length() == 1) {
                                            firstMeetingString += "0" + ((JComboBox<?>) c).getSelectedItem();
                                        } else {
                                            firstMeetingString += ((JComboBox<?>) c).getSelectedItem();
                                        }
                                        if (i == 3) {
                                            firstMeetingString += ":";
                                        } else {
                                            firstMeetingString += " ";
                                        }
                                    }
                                }
                                if (isTemporaryButton.isSelected()) {
                                    for (int i = 0; i < 5; i++) {
                                        Component c = secondMeetingDate.getComponent(i);
                                        if (c instanceof JComboBox<?>) {
                                            if (((String) ((JComboBox<?>) c).getSelectedItem().toString())
                                                    .length() == 1) {
                                                secondMeetingString += "0" + ((JComboBox<?>) c).getSelectedItem();
                                            } else {
                                                secondMeetingString += ((JComboBox<?>) c).getSelectedItem();
                                            }
                                            if (i == 3) {
                                                secondMeetingString += ":";
                                            } else {
                                                secondMeetingString += " ";
                                            }
                                        }
                                    }

                                }
                                try {
                                    Date firstMeeting = dateFormat.parse(firstMeetingString);
                                    Date secondMeeting = secondMeetingString.equals("") ? null
                                            : dateFormat.parse(secondMeetingString);

                                    String thisTraderOffer = "";

                                    if (traderItems.getSelectedItem() != null) {
                                        thisTraderOffer = userQuery.getAvailableItems(trader)
                                                .get(traderItems.getSelectedIndex());
                                    }

                                    String thatTraderOffer = "";
                                    if (otherTraderItems.getSelectedItem() != null) {
                                        thatTraderOffer = userQuery
                                                .getAvailableItems(tradeQuery.getFirstUserId(tradeID))
                                                .get(otherTraderItems.getSelectedIndex());
                                    }
                                    tradeManager.counterTradeOffer(trader, tradeID, firstMeeting, secondMeeting,
                                            finalMeetingLocationInput.getText(), thisTraderOffer, thatTraderOffer,
                                            messageInput.getText());
                                    tradeEditsModal.dispose();
                                } catch (ParseException | TradeNotFoundException | UserNotFoundException
                                        | CannotTradeException | AuthorizationException e2) {
                                    error.setText(e2.getMessage());
                                    error.setVisible(true);
                                }

                            }
                        });

                        tradeEditsModal.add(tradeEditsPanel);
                        tradeEditsModal.add(submitButton, BorderLayout.SOUTH);
                        tradeEditsModal.setModal(true);
                        tradeEditsModal.setVisible(true);
                    });

                    JButton tradeConfirmButton = new JButton("Accept");
                    tradeConfirmButton.setFont(bold.deriveFont(20f));
                    tradeConfirmButton.setForeground(Color.WHITE);
                    tradeConfirmButton.setBackground(green);
                    tradeConfirmButton.setOpaque(true);
                    tradeConfirmButton.setBorder(BorderFactory.createLineBorder(gray, 15));
                    tradeConfirmButton.addActionListener(e -> {
                        try {
                            tradeManager.acceptRequest(trader, tradeID);
                            tradeRequestsContainer.remove(tradeRequestPanel);
                            tradeRequestsContainer.revalidate();
                            tradeRequestsContainer.repaint();
                            // getOngoingTradesPanel();
                            // ongoingTradesContainer.revalidate();
                            // ongoingTradesContainer.repaint();
                            // ongoingTradesScrollPane.setViewportView(ongoingTradesContainer);
                        } catch (TradeNotFoundException | UserNotFoundException | AuthorizationException
                                | CannotTradeException e1) {
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
                            tradeManager.rescindTradeRequest(tradeID);
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
            } catch (TradeNotFoundException | UserNotFoundException | TradableItemNotFoundException exception) {
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

    private JPanel setDateInput(String month, int day, int year, int hour, int min) {
        JPanel meetingInput = new JPanel();
        meetingInput.setBackground(bg);
        meetingInput.setPreferredSize(new Dimension(450, 50));

        String[] monthList = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        JComboBox<String> months = new JComboBox<>(monthList);
        months.setPreferredSize(new Dimension(100, 50));
        months.setFont(regular.deriveFont(20f));

        JComboBox<Integer> days = new JComboBox<>();
        days.setFont(regular.deriveFont(20f));
        days.setPreferredSize(new Dimension(60, 50));
        for (int i = 1; i < 32; i++)
            days.addItem(i);

        months.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                int numDays = 0;
                if (e.getItem().equals("Apr") || e.getItem().equals("Jun") || e.getItem().equals("Sep")
                        || e.getItem().equals("Nov"))
                    numDays = 30;
                else if (e.getItem().equals("Feb"))
                    numDays = 28;
                else
                    numDays = 31;
                days.removeAllItems();
                for (int i = 1; i <= numDays; i++)
                    days.addItem(i);
            }
        });

        JComboBox<Integer> years = new JComboBox<>();
        years.setPreferredSize(new Dimension(100, 50));
        years.setFont(regular.deriveFont(20f));
        for (int i = 2020; i < 2026; i++)
            years.addItem(i);

        JComboBox<Integer> hours = new JComboBox<>();
        hours.setPreferredSize(new Dimension(50, 50));
        hours.setFont(regular.deriveFont(20f));
        for (int i = 0; i < 24; i++)
            hours.addItem(i);

        JComboBox<Integer> minutes = new JComboBox<>();
        minutes.setPreferredSize(new Dimension(50, 50));
        minutes.setFont(regular.deriveFont(20f));
        for (int i = 0; i < 60; i++)
            minutes.addItem(i);

        months.setSelectedItem(month);
        days.setSelectedItem(day);
        years.setSelectedItem(year);
        hours.setSelectedItem(hour);
        minutes.setSelectedItem(min);

        meetingInput.add(months);
        meetingInput.add(days);
        meetingInput.add(years);
        meetingInput.add(hours);
        meetingInput.add(minutes);
        return meetingInput;
    }
}