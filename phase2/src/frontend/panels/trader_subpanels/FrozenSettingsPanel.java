package frontend.panels.trader_subpanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;

public class FrozenSettingsPanel extends SettingsPanel {
    
    public FrozenSettingsPanel(String traderId, Font regular, Font bold, Font italic, Font boldItalic) throws IOException, UserNotFoundException, AuthorizationException {
        super(traderId, regular, bold, italic, boldItalic);
        super.remove(0);
        super.remove(3);

        JLabel settingsTitleLabel = new JLabel("Frozen Trader Settings");
        settingsTitleLabel.setFont(regular.deriveFont(35f));
        settingsTitleLabel.setPreferredSize(new Dimension(1200, 75));
        settingsTitleLabel.setForeground(Color.WHITE);
        settingsTitleLabel.setOpaque(false);


        JTextArea prefaceText = getPrefaceText();
        JPanel requestUnfreezePanel = getRequestUnFreezePanel();

        this.add(settingsTitleLabel, 0);
        this.add(prefaceText, 1);
        this.add(requestUnfreezePanel, 2);
    }

    private JPanel getRequestUnFreezePanel() {
        return new JPanel();
    }

    private JTextArea getPrefaceText() {
        JTextArea preface = new JTextArea("You are currently unable to trade or manage items. This may be caused due to trading beyond the ongoing trades limit.\nIn order to be un-frozen, you must request an un-freeze and an admin will process your request as soon as possible.");
        preface.setFont(super.italic.deriveFont(20f));
        preface.setPreferredSize(new Dimension(1200, 75));
        preface.setLineWrap(true);
        preface.setEditable(false);
        preface.setForeground(gray);
        preface.setOpaque(false);

        return preface;
    }
}