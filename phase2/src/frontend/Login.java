package frontend;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Login {
    public void initialize() throws IOException, FontFormatException {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(100, 50, 0, 50));
        panel.setLayout(new GridLayout(8, 1));

        Font font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("./fonts/IBMPlexSans-BoldItalic.ttf"));

        JLabel title = new JLabel("tRaDeMaStEr 9000");
        title.setFont(font.deriveFont(48f));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(JLabel.CENTER);
        panel.add(title);

        panel.setBackground(new Color(50,50,50));
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("tRaDeMaStEr 9000 login");
        frame.pack();
        frame.setVisible(true);
    }
}
