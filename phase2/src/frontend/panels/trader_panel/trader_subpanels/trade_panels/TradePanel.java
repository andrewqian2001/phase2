package frontend.panels.trader_panel.trader_subpanels.trade_panels;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import backend.exceptions.AuthorizationException;
import backend.exceptions.TradableItemNotFoundException;
import backend.exceptions.TradeNotFoundException;
import backend.exceptions.UserNotFoundException;

/**
 * Represents the panel where trading occurs
 */
public class TradePanel extends JPanel  {

    private final Color bg = new Color(51, 51, 51);

    /**
     * Makes a trade panel
     *
     * @param trader     the trader id
     * @param regular    the regular font
     * @param bold       the bold font
     * @param italic     the italics font
     * @param boldItalic the bold italics font
     * @throws IOException                   issues with getting database files
     * @throws UserNotFoundException         trader is is bad
     * @throws AuthorizationException        user id isn't a trader
     * @throws TradeNotFoundException
     * @throws TradableItemNotFoundException
     */
    public TradePanel(String trader, Font regular, Font bold, Font italic, Font boldItalic) throws IOException,
            UserNotFoundException, AuthorizationException, TradeNotFoundException, TradableItemNotFoundException {

        this.setBorder(BorderFactory.createEmptyBorder(25, 0, 100, 25));
        this.setBackground(bg);

        this.add(new OngoingTradesPanel(trader, regular, bold, italic, boldItalic));
        this.add(new TradeRequestsPanel(trader, regular, bold, italic, boldItalic));
    }
}