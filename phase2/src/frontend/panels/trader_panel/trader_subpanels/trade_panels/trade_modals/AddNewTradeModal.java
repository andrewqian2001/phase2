package frontend.panels.trader_panel.trader_subpanels.trade_panels.trade_modals;

import java.awt.Font;

import javax.swing.JDialog;

public class AddNewTradeModal extends JDialog {

	public AddNewTradeModal(String trader, String[] suggested, Font regular, Font bold, Font italic, Font boldItalic) {
	}
    
}
/*
    @Override
    public void actionPerformed(ActionEvent e) {
        if (trader.equals(""))
            return;

        ArrayList<String> allTraders = infoManager.getAllTraders();

        boolean isSuggestedTrade = e.getActionCommand().equals("<html><b><i><u>Suggest Trade</u></i></b></html>");
        boolean isSuggestedLend = e.getActionCommand().equals("<html><b><i><u>Suggest Lend</u></i></b></html>");

        String[] suggested = new String[0];
        try {
            if (isSuggestedLend)
                suggested = infoManager.suggestLend(trader, false);
            else if (isSuggestedTrade) {
                suggested = infoManager.suggestTrade(trader, false);
                if(suggested.length == 0) {
                    suggested = infoManager.automatedTradeSuggestion(trader, true);
                }
            }
        } catch (UserNotFoundException | AuthorizationException e3) {
            e3.printStackTrace();
        }

        if (suggested.length == 0 && (isSuggestedLend || isSuggestedTrade)) {
            isSuggestedTrade = false;
            isSuggestedLend = false;

            JDialog noSuggestionsFound = new JDialog();
            noSuggestionsFound.setTitle("No Suggestions Found");
            noSuggestionsFound.setSize(500, 200);
            noSuggestionsFound.setResizable(false);
            noSuggestionsFound.setLocationRelativeTo(null);

            JTextArea noSuggestionsTitle = new JTextArea("Unfortunately, we are not able to find a trade suggestion for you.\n\nClosing this pop-up will take you to the\n'Add New Trade' menu.");
            noSuggestionsTitle.setFont(regular.deriveFont(22f));
            noSuggestionsTitle.setBackground(bg);
            noSuggestionsTitle.setForeground(Color.WHITE);
            noSuggestionsTitle.setPreferredSize(new Dimension(500,200));
            noSuggestionsTitle.setOpaque(true);
            noSuggestionsTitle.setEditable(false);
            noSuggestionsTitle.setLineWrap(true);
            noSuggestionsTitle.setWrapStyleWord(true);
            noSuggestionsTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            noSuggestionsFound.add(noSuggestionsTitle);
            noSuggestionsFound.setModal(true);
            noSuggestionsFound.setVisible(true);
        }

        JDialog addNewTradeModal = new JDialog();
        addNewTradeModal.setTitle("Add New Trade");
        addNewTradeModal.setSize(500, 1050);
        addNewTradeModal.setResizable(false);
        addNewTradeModal.setLocationRelativeTo(null);

        JPanel addNewTradePanel = new JPanel();
        addNewTradePanel.setPreferredSize(new Dimension(500, 1050));
        addNewTradePanel.setBackground(bg);

        JLabel tradeWithinCityTitle = new JLabel("Trade Within City?");
        tradeWithinCityTitle.setFont(italic.deriveFont(20f));
        tradeWithinCityTitle.setPreferredSize(new Dimension(425, 50));
        tradeWithinCityTitle.setOpaque(false);
        tradeWithinCityTitle.setForeground(Color.WHITE);

        JCheckBox tradeWithinCityButton = new JCheckBox();
        tradeWithinCityButton.setPreferredSize(new Dimension(25, 25));
        tradeWithinCityButton.setSelected(false);
        tradeWithinCityButton.setForeground(Color.WHITE);
        tradeWithinCityButton.setBackground(bg);

        JLabel otherTraderNameTitle = new JLabel("Trader Username:");
        otherTraderNameTitle.setFont(italic.deriveFont(20f));
        otherTraderNameTitle.setPreferredSize(new Dimension(450, 50));
        otherTraderNameTitle.setOpaque(false);
        otherTraderNameTitle.setForeground(Color.WHITE);

        JComboBox<String> traders = new JComboBox<>();
        traders.setPreferredSize(new Dimension(450, 50));
        traders.setFont(regular.deriveFont(20f));
        traders.setBackground(gray2);
        traders.setForeground(Color.BLACK);
        traders.setOpaque(true);
        traders.addItem(null);
        allTraders.forEach(traderId -> {
            if (!traderId.equals(trader)) {
                try {
                    traders.addItem(userQuery.getUsername(traderId));
                } catch (UserNotFoundException e2) {
                    e2.printStackTrace();
                }
            }
        });

        JButton tradeSubmitButton = new JButton("Submit");
        tradeSubmitButton.setFont(bold.deriveFont(25f));
        tradeSubmitButton.setBackground(green);
        tradeSubmitButton.setOpaque(true);
        tradeSubmitButton.setForeground(Color.WHITE);
        tradeSubmitButton.setPreferredSize(new Dimension(225, 75));
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
        traderItems.setPreferredSize(new Dimension(450, 50));
        try {
            for (String itemId : userQuery.getAvailableItems(trader)) {
                traderItems.addItem(itemQuery.getName(itemId));
            }
        } catch (AuthorizationException | UserNotFoundException | TradableItemNotFoundException exception) {
            exception.printStackTrace();
        }
        traderItems.addItem(null);

        JLabel otherTraderItemTitle = new JLabel("Item from their Inventory:");
        otherTraderItemTitle.setFont(italic.deriveFont(20f));
        otherTraderItemTitle.setPreferredSize(new Dimension(450, 50));
        otherTraderItemTitle.setOpaque(false);
        otherTraderItemTitle.setForeground(Color.WHITE);

        JComboBox<String> otherTraderItems = new JComboBox<>();
        otherTraderItems.setPreferredSize(new Dimension(450, 50));
        otherTraderItems.setFont(regular.deriveFont(20f));
        otherTraderItems.setBackground(gray2);
        otherTraderItems.setForeground(Color.BLACK);
        otherTraderItems.setOpaque(true);
        otherTraderItems.setEnabled(false);

        traders.addItemListener(ev -> {
            try {
                if (ev.getStateChange() == ItemEvent.SELECTED) {
                    if(traders.getItemAt(0) == null) traders.removeItemAt(0);
                    otherTraderItems.setEnabled(false);
                    otherTraderItems.setVisible(false);
                    otherTraderItems.removeAllItems();
                    for (String itemId : userQuery
                            .getAvailableItems(userQuery.getUserByUsername((String) traders.getSelectedItem()))) {
                        otherTraderItems.addItem(itemQuery.getName(itemId));
                    }
                    otherTraderItems.addItem(null);
                    otherTraderItems.setVisible(true);
                    otherTraderItems.setEnabled(true);
                }
            } catch (TradableItemNotFoundException | UserNotFoundException | AuthorizationException e1) {
                e1.printStackTrace();
            }
        });

        if (isSuggestedLend || isSuggestedTrade) {
            try {
                traders.setSelectedItem(userQuery.getUsername(suggested[1]));
                traderItems.setSelectedItem(itemQuery.getName(suggested[2]));
                if (isSuggestedTrade) {
                    for (String itemId : userQuery.getAvailableItems(userQuery.getUserByUsername((String) traders.getSelectedItem()))) {
                        otherTraderItems.addItem(itemQuery.getName(itemId));
                    }
                    otherTraderItems.addItem(null);
                    otherTraderItems.setSelectedItem(itemQuery.getName(suggested[3]));
                    otherTraderItems.setEnabled(true);
                }
            } catch (UserNotFoundException | TradableItemNotFoundException | AuthorizationException e1) {
                e1.printStackTrace();
            }

        }

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
        isTemporaryButton.setPreferredSize(new Dimension(25, 25));
        isTemporaryButton.setSelected(true);
        isTemporaryButton.setForeground(Color.WHITE);
        isTemporaryButton.setBackground(bg);

        JLabel firstMeetingDateTitle = new JLabel("First Meeting Date:");
        firstMeetingDateTitle.setPreferredSize(new Dimension(450, 50));
        firstMeetingDateTitle.setFont(italic.deriveFont(20f));
        firstMeetingDateTitle.setOpaque(false);
        firstMeetingDateTitle.setForeground(Color.WHITE);

        JPanel firstMeetingDate = dateInput();

        JLabel secondMeetingDateTitle = new JLabel("Second Meeting Date:");
        secondMeetingDateTitle.setPreferredSize(new Dimension(450, 50));
        secondMeetingDateTitle.setFont(italic.deriveFont(20f));
        secondMeetingDateTitle.setOpaque(false);
        secondMeetingDateTitle.setForeground(Color.WHITE);

        JPanel secondMeetingDate = dateInput();

        isTemporaryButton.addItemListener(ex -> {
            if (isTemporaryButton.isSelected()) {
                secondMeetingDateTitle.setVisible(true);
                secondMeetingDate.setVisible(true);
            } else {
                secondMeetingDateTitle.setVisible(false);
                secondMeetingDate.setVisible(false);
            }
        });

        tradeWithinCityButton.addItemListener(ex -> {
            traders.setVisible(false);
            traders.removeAllItems();
            allTraders.clear();
            if (tradeWithinCityButton.isSelected()) {
                try {
                    allTraders.addAll(infoManager.getAllTradersInCity(userQuery.getCity(trader)));
                } catch (UserNotFoundException | AuthorizationException e2) {
                    e2.printStackTrace();
                }
            } else {
                allTraders.addAll(infoManager.getAllTraders());
            }
            traders.addItem(null);
            allTraders.forEach(traderId -> {
                if (!traderId.equals(trader)) {
                    try {
                        traders.addItem(userQuery.getUsername(traderId));
                    } catch (UserNotFoundException e2) {
                        e2.printStackTrace();
                    }
                }
            });
            otherTraderItems.removeAllItems();
            otherTraderItems.setEnabled(false);
            traders.setVisible(true);
            traders.revalidate();
            traders.repaint();
        });

        JLabel messageTitle = new JLabel("Attach a message with this trade: (Optional)");
        messageTitle.setFont(italic.deriveFont(20f));
        messageTitle.setPreferredSize(new Dimension(450, 50));
        messageTitle.setOpaque(false);
        messageTitle.setForeground(Color.WHITE);

        JTextField messageInput = new JTextField();
        messageInput.setFont(regular.deriveFont(20f));
        messageInput.setBackground(gray2);
        messageInput.setForeground(Color.BLACK);
        messageInput.setPreferredSize(new Dimension(450, 50));
        messageInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel error = new JLabel();
        error.setPreferredSize(new Dimension(500, 50));
        error.setForeground(red);
        error.setFont(boldItalic.deriveFont(20f));
        error.setHorizontalAlignment(JLabel.CENTER);
        error.setVisible(false);

        tradeSubmitButton.addActionListener(e1 -> {
            if (otherTraderItems.isEnabled() && (!meetingLocationInput.getText().trim().equals(""))
                    && ((traderItems.getSelectedItem() != null ^ otherTraderItems.getSelectedItem() != null)
                            || (traderItems.getSelectedItem() != null && otherTraderItems.getSelectedItem() != null))) {
                // meetingInput.add(months); 0
                // meetingInput.add(days); 1
                // meetingInput.add(years); 2
                // meetingInput.add(hours); 3
                // meetingInput.add(minutes); 4
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
                            if (((String) ((JComboBox<?>) c).getSelectedItem().toString()).length() == 1) {
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
                    Date secondMeeting = secondMeetingString.equals("") ? null : dateFormat.parse(secondMeetingString);
                    String firstTraderOffer = "";
                    String otherTraderOffer = "";

                    if (traderItems.getSelectedItem() != null) {
                        firstTraderOffer = userQuery.getAvailableItems(trader).get(traderItems.getSelectedIndex());
                    }

                    if (otherTraderItems.getSelectedItem() != null) {
                        otherTraderOffer = userQuery
                                .getAvailableItems(userQuery.getUserByUsername((String) traders.getSelectedItem()))
                                .get(otherTraderItems.getSelectedIndex());
                    }
                    String message = messageInput.getText();

                    tradeManager.requestTrade(trader, userQuery.getUserByUsername((String) traders.getSelectedItem()),
                            firstMeeting, secondMeeting, meetingLocationInput.getText(), firstTraderOffer,
                            otherTraderOffer, 3, message);
                    addNewTradeModal.dispose();
                } catch (ParseException | UserNotFoundException | AuthorizationException | CannotTradeException e2) {
                    error.setText(e2.getMessage());
                    error.setVisible(true);
                }
                try {
                    getTradeRequestPanels();
                    tradeRequestsContainer.revalidate();
                    tradeRequestsContainer.repaint();
                    tradeRequestsScrollPane.setViewportView(tradeRequestsContainer);
                } catch (UserNotFoundException | AuthorizationException e2) {
                    e2.printStackTrace();
                }
            }
        });

        if (!isSuggestedLend && !isSuggestedTrade) {
            addNewTradePanel.add(tradeWithinCityTitle);
            addNewTradePanel.add(tradeWithinCityButton);
        }
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
        addNewTradePanel.add(firstMeetingDate);
        addNewTradePanel.add(secondMeetingDateTitle);
        addNewTradePanel.add(secondMeetingDate);
        addNewTradePanel.add(messageTitle);
        addNewTradePanel.add(messageInput);
        addNewTradePanel.add(error);

        addNewTradeModal.add(addNewTradePanel);
        addNewTradeModal.add(tradeSubmitButton, BorderLayout.SOUTH);
        addNewTradeModal.setModal(true);
        addNewTradeModal.setVisible(true);
    }

    private JPanel dateInput() {
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

        meetingInput.add(months);
        meetingInput.add(days);
        meetingInput.add(years);
        meetingInput.add(hours);
        meetingInput.add(minutes);
        return meetingInput;
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
*/