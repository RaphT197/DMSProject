package FFPackage;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

// Custom JScrollPane with background image
class BackgroundScrollPane extends JScrollPane {
    private Image backgroundImage;

    public BackgroundScrollPane(JTable table) {
        super(table);

        // Load the background image
        URL bgImageURL = getClass().getResource("/images/carbuncles.png");
        if (bgImageURL != null) {
            backgroundImage = new ImageIcon(bgImageURL).getImage();
        } else {
            System.out.println("Background image not found!");
        }

        setOpaque(false);
        getViewport().setOpaque(false);

        // Fix the corner issue - make corners transparent
        JPanel corner = new JPanel();
        corner.setOpaque(true);
        setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);
        setCorner(JScrollPane.LOWER_RIGHT_CORNER, corner);
        setCorner(JScrollPane.LOWER_LEFT_CORNER, corner);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Get background color based on current theme
        boolean isDark = UIManager.getLookAndFeel() instanceof FlatDarkLaf;
        Color bgColor = isDark ? UIManager.getColor("Panel.background") : Color.WHITE;

        // Paint theme-appropriate background first
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Paint the background image with opacity
        if (backgroundImage != null) {
            float opacity = 0.3f;  // Adjust this for visibility (0.2-0.5 works well)
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        g2d.dispose();
        super.paintComponent(g);
    }
}