package FFPackage;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * A custom {@link JScrollPane} that draws a themed background image behind
 * the contained {@link JTable}.
 * <p>
 * The background image is loaded from {@code /images/carbuncles.png} on the classpath
 * and is rendered with a configurable opacity so that table content stays readable.
 * The scroll pane and viewport are made non-opaque so the background can show through.
 */
class BackgroundScrollPane extends JScrollPane {

    /**
     * Background image drawn behind the table, or {@code null} if it failed to load.
     */
    private Image backgroundImage;

    /**
     * Creates a new scroll pane with a background image behind the given table.
     *
     * @param table the table to be wrapped in this scroll pane
     */
    public BackgroundScrollPane(JTable table) {
        super(table);

        // Load the background image from the resources folder
        URL bgImageURL = getClass().getResource("/images/carbuncles.png");
        if (bgImageURL != null) {
            backgroundImage = new ImageIcon(bgImageURL).getImage();
        } else {
            // Fallback: no image, just log so it's visible during development
            System.out.println("Background image not found!");
        }

        // Make the scroll pane and viewport transparent so the image can be seen
        setOpaque(false);
        getViewport().setOpaque(false);

        // ScrollPane corner components need to be explicitly handled so they
        // visually blend with the rest of the UI
        JPanel corner = new JPanel();
        corner.setOpaque(true);
        setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);
        setCorner(JScrollPane.LOWER_RIGHT_CORNER, corner);
        setCorner(JScrollPane.LOWER_LEFT_CORNER, corner);
    }

    /**
     * Paints the background of the scroll pane, including a theme-aware base color
     * and a semi-transparent background image (if available), then delegates to
     * the default painting logic.
     *
     * @param g the {@link Graphics} context in which to paint
     */
    @Override
    protected void paintComponent(Graphics g) {
        // Use a fresh Graphics instance to avoid leaking state (like alpha) to others
        Graphics2D g2d = (Graphics2D) g.create();

        // Determine a suitable base background color based on the current Look & Feel
        boolean isDark = UIManager.getLookAndFeel() instanceof FlatDarkLaf;
        Color bgColor = isDark ? UIManager.getColor("Panel.background") : Color.WHITE;

        // Paint theme-appropriate background first so we always have a clean base
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Paint the background image with some transparency so table content stays readable
        if (backgroundImage != null) {
            float opacity = 0.3f;  // Range ~0.2–0.5 usually looks good for overlays
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            // Reset composite to avoid affecting any subsequent painting
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        g2d.dispose();
        // Let the superclass handle borders, children, etc.
        super.paintComponent(g);
    }
}
