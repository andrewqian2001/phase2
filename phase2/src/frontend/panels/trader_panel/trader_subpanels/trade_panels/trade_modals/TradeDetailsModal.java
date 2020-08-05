package frontend.panels.trader_panel.trader_subpanels.trade_panels.trade_modals;

import java.awt.Font;

import javax.swing.JDialog;

public class TradeDetailsModal extends JDialog {

	public TradeDetailsModal(String tradeID, boolean showAvailableEdits, boolean isTraderFirstUser, Font regular, Font bold, Font italic,
			Font boldItalic) {
	}
    
}

/*
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

    JLabel otherTraderDetailsName = new JLabel(
            "<html><pre>" + otherTraderName.getText() + "</pre></html>");
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
    // TODO: Make this logic less ugly
    ;
    try {
        if ((tradeQuery.getFirstUserOffer(tradeID).equals("") && isTraderFirstUser)
                || (tradeQuery.getSecondUserOffer(tradeID).equals("") && !isTraderFirstUser)) {
            traderItemName.setText("N/A");
        } else if (!tradeQuery.getFirstUserOffer(tradeID).equals("") && isTraderFirstUser) {
            traderItemName.setText(itemQuery.getName(tradeQuery.getFirstUserOffer(tradeID)));
        } else if (!tradeQuery.getSecondUserOffer(tradeID).equals("") && !isTraderFirstUser) {
            traderItemName.setText(itemQuery.getName(tradeQuery.getSecondUserOffer(tradeID)));
        }
        if ((tradeQuery.getFirstUserOffer(tradeID).equals("") && !isTraderFirstUser)
                || (tradeQuery.getSecondUserOffer(tradeID).equals("") && isTraderFirstUser)) {
            otherTraderItemName.setText("N/A");
        } else if (!tradeQuery.getFirstUserOffer(tradeID).equals("") && !isTraderFirstUser) {
            otherTraderItemName.setText(itemQuery.getName(tradeQuery.getFirstUserOffer(tradeID)));
        } else if (!tradeQuery.getSecondUserOffer(tradeID).equals("") && isTraderFirstUser) {
            otherTraderItemName.setText(itemQuery.getName(tradeQuery.getSecondUserOffer(tradeID)));
        }
    } catch (TradableItemNotFoundException | TradeNotFoundException e1) {
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
            secondMeetingDate.setText("<html><pre>"
                    + dateFormat.format(tradeQuery.getSecondMeetingTime(tradeID)) + "</pre></html>");
        } else {
            secondMeetingDate.setText("N/A");
        }
    } catch (TradeNotFoundException tradeNotFoundException) {
        tradeNotFoundException.printStackTrace();
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

*/