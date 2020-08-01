package frontend.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Used to show a panel with background as an image
 */
public class ImagePanel extends JComponent {
    private final Image image;

    /**
     * For making a panel with a background image
     *
     * @param image the image to show
     */
    public ImagePanel(Image image) {
        this.image = image;
    }

    /**
     * For painting the image
     *
     * @param g graphics used for painting
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}