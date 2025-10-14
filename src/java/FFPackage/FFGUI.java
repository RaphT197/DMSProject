package FFPackage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class FFGUI {
    private JFrame frame;
    private JTextArea outputArea;
    private FF ff;
    private JLabel jobImageLabel;

    public FFGUI() {
        ff = new FF();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("FF Character Manager");

        // load icon using getResource
        java.net.URL appIconURL = getClass().getResource("/images/app_icon.png");
        if (appIconURL != null) {
            frame.setIconImage(new ImageIcon(appIconURL).getImage());
        } else {
            System.out.println("App icon not found!");
        }

        frame.setBounds(100, 100, 600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(4, 4));
        JButton addManual = new JButton("Add Character");
        JButton addFile = new JButton("Add From File");
        JButton display = new JButton("Display All");
        JButton levelUp = new JButton("Level Up");
        JButton update = new JButton("Update");
        JButton remove = new JButton("Remove");
        JButton exit = new JButton("Exit");

        //giving function to every button instantiated
        panel.add(addManual);
        panel.add(addFile);
        panel.add(display);
        panel.add(levelUp);
        panel.add(remove);
        panel.add(exit);
        panel.add(update);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(panel, BorderLayout.CENTER);

        //creates a space for the random job images
        jobImageLabel = new JLabel();
        southPanel.add(jobImageLabel, BorderLayout.EAST);
        updateRandomJobIcon();

        frame.add(southPanel, BorderLayout.SOUTH);

        // event listeners
        addManual.addActionListener(e -> addCharacterManual());
        addFile.addActionListener(e -> addCharacterFromFile());
        display.addActionListener(e -> displayCharacters());
        levelUp.addActionListener(e -> levelUpCharacter());
        remove.addActionListener(e -> removeCharacter());
        update.addActionListener(e -> updateCharacter());
        exit.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    //function to rotate the job images from the images folder
    private void updateRandomJobIcon() {
        ImageIcon randomIcon = getRandomJobIcon();
        if (randomIcon != null) {
            Image img = randomIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            jobImageLabel.setIcon(new ImageIcon(img));
        }
    }

    // random job icon method
    private ImageIcon getRandomJobIcon() {
        String[] jobImageNames = {
                "astrologian.png", "bard.png", "black_mage.png", "dark_knight.png",
                "dragoon.png", "monk.png", "paladin.png", "scholar.png",
                "summoner.png", "warrior.png", "white_mage.png"
        };

        String name = jobImageNames[new Random().nextInt(jobImageNames.length)];


        java.net.URL url = getClass().getResource("/images/" + name);

        if (url == null) {
            System.out.println("Image not found: /images/" + name);
            return null;
        }

        return new ImageIcon(url);
    }

    //manually adds characters from the users input
    private void addCharacterManual() {
        try {
            String name = JOptionPane.showInputDialog(frame, "Name:").trim();
            String job = JOptionPane.showInputDialog(frame, "Job:").trim();
            if (!PCharacter.isValidJob(job)) {
                JOptionPane.showMessageDialog(frame, "Invalid Job");
                return;
            }
            int level = Integer.parseInt(JOptionPane.showInputDialog(frame, "Level:").trim());
            double hp = Double.parseDouble(JOptionPane.showInputDialog(frame, "HP:").trim());
            String activeInput = JOptionPane.showInputDialog(frame, "In party? (yes/no):");
            boolean isActive = activeInput.equalsIgnoreCase("yes");

            PCharacter pc = new PCharacter("", name, job, level, hp, isActive);
            ff.addCharacter(pc);
            outputArea.append("Added: " + pc + "\n");

            updateRandomJobIcon();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

        // add characters from a .txt file
    private void addCharacterFromFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            ff.addCharactersFromFile(chooser.getSelectedFile().getAbsolutePath());
            displayCharacters();
        }
    }

    //display all DB characters
    private void displayCharacters() {
        ArrayList<PCharacter> chars = ff.getCharacters();
        if (chars.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nothing to display.");
        } else {
            outputArea.append("\n--- Characters ---\n");
            for (PCharacter c : chars) outputArea.append(c + "\n");
            outputArea.append("-----------------\n");
            updateRandomJobIcon();
        }
    }

    //lvlup characters by their ID
    private void levelUpCharacter() {
        ArrayList<PCharacter> chars = ff.getCharacters();
        if (chars.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nothing to level up.");
        } else {
            String id = JOptionPane.showInputDialog(frame, "Character ID:");
            int inc = Integer.parseInt(JOptionPane.showInputDialog(frame, "Level increase:"));
            ff.levelUpById(id, inc);
            outputArea.append("\n--- Character Leveled Up! ---\n");
            outputArea.append("Character Level Up! "  + id);
        }
    }

        //update a character by ID
    // TODO THE BUTTONS WONT ADD THEMSELVES ALONE :)
    //
    private void updateCharacter() {
        ArrayList<PCharacter> chars = ff.getCharacters();
        if (chars.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nothing to update.");
        }
        else {
            String id = JOptionPane.showInputDialog(frame, "Character ID:");
            ff.updateCharacterById(id);
            outputArea.append("Updated: " + id + " \n");
        }
    }

    //remove characters by ID
    private void removeCharacter() {
        if (ff.getCharacters().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No characters to remove.");
        } else {
            String id = JOptionPane.showInputDialog(frame, "Character ID to remove:");
            ff.removeCharacterById(id);
            outputArea.append("Removed: " + id + "\n");
        }
    }

    //starts the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FFGUI::new);
    }
}