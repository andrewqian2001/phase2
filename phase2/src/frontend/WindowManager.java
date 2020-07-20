package frontend;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class WindowManager extends JFrame {
    protected Font regular, bold, italic, boldItalic;
    private LoginPanel loginPanel;
    private JPanel userPanel; // either TraderPanel or AdminPanel

    public WindowManager() throws IOException, FontFormatException {
        regular = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Regular.ttf"));
        bold = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Bold.ttf"));
        italic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-Italic.ttf"));
        boldItalic = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-BoldItalic.ttf"));

        loginPanel = new LoginPanel(regular, bold, italic, boldItalic);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(loginPanel, BorderLayout.CENTER);
        this.setSize(loginPanel.getSize());
    }
    
    public void start() {
        this.setVisible(true);
    }
}
