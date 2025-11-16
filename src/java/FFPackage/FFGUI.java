package FFPackage;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Swing-based GUI for managing {@link PCharacter} instances.
 * <p>
 * This class provides a table view of characters and buttons for:
 * <ul>
 *     <li>Adding characters manually or from a file</li>
 *     <li>Refreshing the roster</li>
 *     <li>Leveling up, updating, or removing selected characters</li>
 * </ul>
 * It also supports light/dark themes and shows a random job icon for flavor.
 */
public class FFGUI {

    /** Main application window. */
    private JFrame frame;

    /** Service layer used for all character-related operations. */
    private final FF ff;

    /** Label used to display a random job icon and toggle light/dark theme on click. */
    private JLabel jobImageLabel;

    // JTable components
    /** Table displaying the character roster. */
    private JTable characterTable;

    /** Table model backing the character table. */
    private DefaultTableModel tableModel;

    /** Column titles for the character table. */
    private final String[] columnNames = {"ID", "Name", "Job", "Level", "HP", "MP", "In Party"};

    /**
     * Constructs the GUI and initializes all components.
     * <p>
     * Once constructed, the main frame is made visible automatically.
     */
    public FFGUI() {
        ff = new FF();
        initialize();
    }

    /**
     * Initializes the Swing UI: frame, table, buttons, layout and event handlers.
     */
    private void initialize() {
        frame = new JFrame("FF Character Manager");

        // Set initial look and feel (light theme by default)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load app icon if available
        URL appIconURL = getClass().getResource("/images/app_icon.png");
        if (appIconURL != null) {
            assert frame != null;
            frame.setIconImage(new ImageIcon(appIconURL).getImage());
        }

        assert frame != null;
        frame.setBounds(100, 100, 900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Table model: read-only cells
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Make table read-only
            }
        };

        // Table where the user interacts with the character roster
        characterTable = new JTable(tableModel);
        characterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        characterTable.setRowHeight(27);
        characterTable.setOpaque(false);  // Make table transparent so background can show through

        // Custom scroll pane with background image
        BackgroundScrollPane tableScrollPane = new BackgroundScrollPane(characterTable);
        tableScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK),
                        "Character Roster",
                        TitledBorder.CENTER,
                        TitledBorder.TOP
                )
        );

        applyTableTheme();
        frame.add(tableScrollPane, BorderLayout.CENTER);

        // Button panel (bottom center)
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addManual = new JButton("Add Character");
        JButton addFile = new JButton("Add From File");
        JButton refresh = new JButton("Refresh");
        JButton levelUp = new JButton("Level Up Selected");
        JButton update = new JButton("Update Selected");
        JButton remove = new JButton("Remove Selected");
        JButton clear = new JButton("Clear Selection");
        JButton exit = new JButton("Exit");

        buttonPanel.add(addManual);
        buttonPanel.add(addFile);
        buttonPanel.add(refresh);
        buttonPanel.add(levelUp);
        buttonPanel.add(update);
        buttonPanel.add(remove);
        buttonPanel.add(clear);
        buttonPanel.add(exit);

        // South panel combines buttons with the job icon on the right
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);

        jobImageLabel = new JLabel();
        jobImageLabel.setPreferredSize(new Dimension(90, 90));
        southPanel.add(jobImageLabel, BorderLayout.EAST);

        // Use the job icon as a "theme toggle" as well
        jobImageLabel.setToolTipText("");
        jobImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        jobImageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    // Toggle between dark and light FlatLaf looks
                    if (UIManager.getLookAndFeel() instanceof FlatDarkLaf) {
                        UIManager.setLookAndFeel(new FlatLightLaf());
                    } else {
                        UIManager.setLookAndFeel(new FlatDarkLaf());
                    }

                    // Update UI for all components in the frame
                    SwingUtilities.updateComponentTreeUI(frame);

                    // Reapply table theming since colors may change with look & feel
                    applyTableTheme();

                    characterTable.setOpaque(false);
                    ((JComponent) characterTable.getParent()).setOpaque(false);

                    frame.repaint();

                } catch (UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }
            }
        });

        frame.add(southPanel, BorderLayout.SOUTH);

        // Wire up button actions
        addManual.addActionListener(_ -> addCharacterManual());
        addFile.addActionListener(_ -> addCharacterFromFile());
        refresh.addActionListener(_ -> refreshTable());
        levelUp.addActionListener(_ -> levelUpSelected());
        update.addActionListener(_ -> updateSelected());
        remove.addActionListener(_ -> removeSelected());
        clear.addActionListener(_ -> characterTable.clearSelection());
        exit.addActionListener(_ -> System.exit(0));

        // Load initial data into the table
        refreshTable();

        frame.setVisible(true);
    }

    /**
     * Custom {@link JScrollPane} that paints a semi-transparent background image
     * behind the character table.
     */
    private static class BackgroundScrollPane extends JScrollPane {

        /** Background image drawn under the table, or null if loading fails. */
        private Image backgroundImage;

        /**
         * Creates a scroll pane for the given table and loads the background asset.
         *
         * @param table the table to place inside the scroll pane
         */
        public BackgroundScrollPane(JTable table) {
            super(table);

            // Load the background image from resources
            URL bgImageURL = getClass().getResource("/images/carbuncles.png");
            if (bgImageURL != null) {
                backgroundImage = new ImageIcon(bgImageURL).getImage();
            } else {
                System.out.println("Background image not found!");
            }

            // Allow the image to show through
            setOpaque(false);
            getViewport().setOpaque(false);
        }

        /**
         * Paints a white background and then a translucent overlay image
         * before delegating to the superclass.
         *
         * @param g the Graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();

            // Paint white background first
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Paint the background image with opacity
            if (backgroundImage != null) {
                float opacity = 0.4f;
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            g2d.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Refreshes the table model with the latest data from the database
     * and updates the random job icon shown in the bottom-right corner.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);

        ArrayList<PCharacter> characters = ff.getCharacters();
        for (PCharacter c : characters) {
            Object[] row = {
                    c.getId(),
                    c.getName(),
                    c.getJob(),
                    c.getLevel(),
                    c.getHp(),
                    c.getMp(),
                    c.isActive() ? "Yes" : "No"
            };
            tableModel.addRow(row);
        }

        updateRandomJobIcon();
    }

    /**
     * Returns the ID of the currently selected character in the table.
     *
     * @return the selected character ID, or {@code null} if no row is selected
     */
    private String getSelectedId() {
        int selectedRow = characterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame,
                    "Please select a character from the table first!");
            return null;
        }
        return (String) tableModel.getValueAt(selectedRow, 0);
    }

    /**
     * Shows a form (via dialogs) to manually add a single character.
     * <p>
     * Uses validation helper methods to ensure valid input for all fields.
     * On success, the character is persisted and the table is refreshed.
     */
    private void addCharacterManual() {
        try {
            String name = JOptionPane.showInputDialog(frame, "Name:");
            if (name == null || name.trim().isEmpty()) return;

            String job = validJob();
            if (job == null) return;

            Integer level = validLevel();
            if (level == null) return;

            Integer hp = validHP();
            if (hp == null) return;

            Integer mp = validMp();
            if (mp == null) return;

            Boolean active = validActive();
            if (active == null) return;

            PCharacter pc = new PCharacter("", name, job, level, hp, mp, active);
            String assignedId = ff.addCharacter(pc);

            JOptionPane.showMessageDialog(frame,
                    "Character added successfully!\nAssigned ID: " + assignedId);

            refreshTable();
            updateRandomJobIcon();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    /**
     * Opens a file chooser and imports characters from the selected file.
     * <p>
     * Delegates actual file parsing and validation to
     * {@link FF#addCharactersFromFile(String)}.
     */
    private void addCharacterFromFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            ff.addCharactersFromFile(chooser.getSelectedFile().getAbsolutePath());
            refreshTable();
            JOptionPane.showMessageDialog(frame, "Characters imported successfully!");
        }
    }

    /**
     * Prompts for a level increase and applies it to the currently selected character.
     * <p>
     * Validates selection and numeric input before calling
     * {@link FF#levelUpById(String, int)}.
     */
    private void levelUpSelected() {
        String id = getSelectedId();
        if (id == null) return;

        try {
            String incStr = JOptionPane.showInputDialog(frame, "Level increase:");
            if (incStr == null) return;

            int inc = Integer.parseInt(incStr.trim());
            ff.levelUpById(id, inc);

            refreshTable();
            JOptionPane.showMessageDialog(frame, "Character leveled up!");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid number!");
        }
    }

    /**
     * Opens a small menu to update a specific field (name, job, level, HP)
     * for the currently selected character.
     */
    private void updateSelected() {
        String id = getSelectedId();
        if (id == null) return;

        String[] options = {"Name", "Job", "Level", "HP", "Cancel"};

        int choice = JOptionPane.showOptionDialog(
                frame,
                "What would you like to update?",
                "Update Character",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        try {
            switch (choice) {
                case 0 -> {  // Name
                    String newName = JOptionPane.showInputDialog(frame, "New name:");
                    if (newName != null && !newName.trim().isEmpty()) {
                        ff.updateCharacterName(id, newName);
                        refreshTable();
                        JOptionPane.showMessageDialog(frame, "Name updated!");
                    }
                }
                case 1 -> {  // Job
                    String newJob = validJob();
                    if (newJob != null) {
                        ff.updateCharacterJob(id, newJob);
                        refreshTable();
                        JOptionPane.showMessageDialog(frame, "Job updated!");
                    }
                }
                case 2 -> {  // Level
                    Integer newLevel = validLevel();
                    if (newLevel != null) {
                        ff.updateCharacterLevel(id, newLevel);
                        refreshTable();
                        JOptionPane.showMessageDialog(frame, "Level updated!");
                    }
                }
                case 3 -> {  // HP
                    Integer newHp = validHP();
                    if (newHp != null) {
                        ff.updateCharacterHp(id, newHp);
                        refreshTable();
                        JOptionPane.showMessageDialog(frame, "HP updated!");
                    }
                }
                // case 4 is "Cancel" -> do nothing
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    /**
     * Removes the currently selected character after a confirmation dialog.
     */
    private void removeSelected() {
        String id = getSelectedId();
        if (id == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to remove this character?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            ff.removeCharacterById(id);
            refreshTable();
            JOptionPane.showMessageDialog(frame, "Character removed!");
        }
    }

    // Validation methods

    /**
     * Asks the user (via dialog) whether the character is in the party.
     *
     * @return {@code true} for yes, {@code false} for no, or {@code null} if the user cancels
     */
    private Boolean validActive() {
        while (true) {
            String active = JOptionPane.showInputDialog(frame, "In party? (yes/no):");
            if (active == null) return null;
            if (active.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Invalid input!");
                continue;
            }
            if (active.equalsIgnoreCase("yes")) return true;
            if (active.equalsIgnoreCase("no")) return false;
            JOptionPane.showMessageDialog(frame, "Please type 'yes' or 'no'.");
        }
    }

    /**
     * Prompts the user for a valid HP value.
     * <p>
     * HP must be a positive integer.
     *
     * @return HP value or {@code null} if the user cancels
     */
    private Integer validHP() {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(frame, "HP:");
                if (input == null) return null;
                int hp = Integer.parseInt(input);
                if (hp <= 0) {
                    JOptionPane.showMessageDialog(frame, "HP must be greater than 0.");
                } else return hp;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid number!");
            }
        }
    }

    /**
     * Prompts the user for a valid MP value.
     * <p>
     * MP must be a positive integer.
     *
     * @return MP value or {@code null} if the user cancels
     */
    private Integer validMp() {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(frame, "MP:");
                if (input == null) return null;
                int mp = Integer.parseInt(input);
                if (mp <= 0) {
                    JOptionPane.showMessageDialog(frame, "MP must be greater than 0.");
                } else return mp;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid number!");
            }
        }
    }

    /**
     * Prompts the user for a valid job string.
     * <p>
     * Values are checked using {@link PCharacter#isValidJob(String)}.
     *
     * @return a valid job value or {@code null} if the user cancels
     */
    private String validJob() {
        while (true) {
            String input = JOptionPane.showInputDialog(frame, "Job:");
            if (input == null) return null;
            String job = input.trim();
            if (PCharacter.isValidJob(job)) return job;
            else JOptionPane.showMessageDialog(frame, "Invalid Job: " + job);
        }
    }

    /**
     * Prompts the user for a valid level (1–99).
     *
     * @return level value or {@code null} if the user cancels
     */
    private Integer validLevel() {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(frame, "Level (1-99):");
                if (input == null) return null;
                int level = Integer.parseInt(input.trim());
                if (level > 0 && level < 100) return level;
                JOptionPane.showMessageDialog(frame, "Level must be between 1 and 99!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid number!");
            }
        }
    }

    /**
     * Picks a random job icon from the resources folder and sets it on {@link #jobImageLabel}.
     * <p>
     * The image is scaled down for display.
     */
    private void updateRandomJobIcon() {
        ImageIcon randomIcon = getRandomJobIcon();
        if (randomIcon != null) {
            Image img = randomIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            jobImageLabel.setIcon(new ImageIcon(img));
        }
    }

    /**
     * Returns a random job icon from the {@code /images} folder.
     *
     * @return an {@link ImageIcon} if the resource exists, or {@code null} otherwise
     */
    private ImageIcon getRandomJobIcon() {
        String[] jobImageNames = {
                "astrologian.png", "bard.png", "black_mage.png", "dark_knight.png",
                "dragoon.png", "monk.png", "paladin.png", "scholar.png",
                "summoner.png", "warrior.png", "white_mage.png"
        };

        String name = jobImageNames[new Random().nextInt(jobImageNames.length)];
        URL url = getClass().getResource("/images/" + name);

        if (url == null) {
            System.out.println("Image not found: /images/" + name);
            return null;
        }

        return new ImageIcon(url);
    }

    /**
     * Applies theme-specific colors and transparency to the character table
     * based on the current Look & Feel (dark vs light).
     */
    private void applyTableTheme() {
        boolean isDark = UIManager.getLookAndFeel() instanceof FlatDarkLaf;

        if (isDark) {
            // Dark mode - use FlatLaf defaults
            characterTable.setBackground(UIManager.getColor("Table.background"));
            characterTable.setForeground(UIManager.getColor("Table.foreground"));
            characterTable.setGridColor(UIManager.getColor("Table.gridColor"));
            characterTable.setSelectionBackground(UIManager.getColor("Table.selectionBackground"));
            characterTable.setSelectionForeground(UIManager.getColor("Table.selectionForeground"));

            // Apply theme to table header
            characterTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background"));
            characterTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground"));

            // Make it translucent for background visibility
            Color bg = characterTable.getBackground();
            Color translucentBg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 200);
            characterTable.setBackground(translucentBg);
        } else {
            // Light mode - use greyish background
            Color lightGreyBg = new Color(220, 220, 220, 200); // Light grey with transparency
            characterTable.setBackground(lightGreyBg);
            characterTable.setForeground(Color.BLACK);
            characterTable.setGridColor(new Color(180, 180, 180));
            characterTable.setSelectionBackground(new Color(100, 150, 200, 220)); // Light blue selection
            characterTable.setSelectionForeground(Color.WHITE);

            // Apply theme to table header
            characterTable.getTableHeader().setBackground(new Color(200, 200, 200));
            characterTable.getTableHeader().setForeground(Color.BLACK);
        }

        // Keep the table non-opaque so background shows through
        characterTable.setOpaque(false);
        ((JComponent) characterTable.getParent()).setOpaque(false);
    }

    /**
     * GUI entry point. Launches the {@link FFGUI} on the Swing event dispatch thread.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FFGUI::new);
    }
}
