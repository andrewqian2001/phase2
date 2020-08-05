package frontend.panels.trader_panel.trader_subpanels.trade_panels.trade_modals;

import java.awt.Font;

import javax.swing.JDialog;

public class EditTradeModal extends JDialog {

	public EditTradeModal(String tradeID, String trader, boolean isTraderFirstUser, Font regular, Font bold,
			Font italic, Font boldItalic) {
	}
    
}

/*
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