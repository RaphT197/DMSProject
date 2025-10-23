package FFPackage;

import javax.swing.*;
import java.awt.*;

class BackgroundTextArea extends JTextArea {
    private Image backgroundImage;
    private float opacity = 0.3f;

    public BackgroundTextArea() {
        setOpaque(false);
    }

    public void setBackgroundImage(Image image, float opacity) {
        this.backgroundImage = image;
        this.opacity = opacity;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Paint white background first so text is readable
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Paint the image with opacity
        if (backgroundImage != null) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Reset
        }

        g2d.dispose();

        // Paint the text on top
        super.paintComponent(g);
    }
}