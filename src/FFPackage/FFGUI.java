package FFPackage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class FFGUI {
    private JFrame frame;
    private JTextArea outputArea;
    private FF ff;

    public FFGUI() {
        ff = new FF();
        initialize();
    }

    private JLabel jobImageLabel;

    private void initialize() {

        frame = new JFrame("FF Character Manager");
        frame.setIconImage(new ImageIcon("src/images/app_icon.png").getImage());
        frame.setBounds(100, 100, 600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());


        outputArea = new JTextArea();
        outputArea.setEditable(false);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(4,4));
        JButton addManual = new JButton("Add Character");
        JButton addFile = new JButton("Add From File");
        JButton display = new JButton("Display All");
        JButton levelUp = new JButton("Level Up");
        JButton update = new JButton("Update");
        JButton remove = new JButton("Remove");
        JButton exit = new JButton("Exit");

        panel.add(addManual); panel.add(addFile); panel.add(display);
        panel.add(levelUp); panel.add(remove); panel.add(exit);
        panel.add(update);


        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(panel, BorderLayout.CENTER);

        jobImageLabel = new JLabel();
        southPanel.add(jobImageLabel, BorderLayout.EAST);

        ImageIcon randomIcon = getRandomJobIcon();
        if (randomIcon != null) {
            Image img = randomIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            jobImageLabel.setIcon(new ImageIcon(img));
        }




        frame.add(southPanel, BorderLayout.SOUTH);

        addManual.addActionListener(e -> addCharacterManual());
        addFile.addActionListener(e -> addCharacterFromFile());
        display.addActionListener(e -> displayCharacters());
        levelUp.addActionListener(e -> levelUpCharacter());
        remove.addActionListener(e -> removeCharacter());
        update.addActionListener(e -> updateCharacter());
        exit.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    private File[] getAllJobImages () {
        File folder = new File("src/images");
        return folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
    }

    private ImageIcon getRandomJobIcon(){
        File[] images = getAllJobImages();
        if (images == null || images.length == 0) return null;
        File randomImage = images[new Random(). nextInt(images.length)];
        return new ImageIcon(randomImage.getAbsolutePath());
    }
    private void addCharacterManual() {
        try {
            String name = JOptionPane.showInputDialog(frame, "Name:").trim();
            String job = JOptionPane.showInputDialog(frame, "Job:").trim();
            if (!PCharacter.isValidJob(job)) { JOptionPane.showMessageDialog(frame, "Invalid Job"); return; }
            int level = Integer.parseInt(JOptionPane.showInputDialog(frame, "Level:").trim());
            double hp = Double.parseDouble(JOptionPane.showInputDialog(frame, "HP:").trim());
            String activeInput = JOptionPane.showInputDialog(frame,"Is the character in your party? (yes/no):");
            if (!activeInput.equalsIgnoreCase("yes") && !activeInput.equalsIgnoreCase("no")) {
                throw new IllegalArgumentException("Active must be 'yes' or 'no'");
            }
            boolean isActive = Boolean.parseBoolean(activeInput);


            PCharacter pc = new PCharacter("", name, job, level, hp, isActive);
            ff.addCharacter(pc);
            outputArea.append("Added: " + pc + "\n");

            ImageIcon randomIcon = getRandomJobIcon();
            if (randomIcon != null) {
                Image img = randomIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                jobImageLabel.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage()); }
    }

    private void addCharacterFromFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            ff.addCharactersFromFile(f.getAbsolutePath());
            displayCharacters();
        }
    }

    private void displayCharacters() {
        ArrayList<PCharacter> chars = ff.getCharacters();
        if (chars == null || chars.size() == 0) {
            JOptionPane.showMessageDialog(frame, "Nothing to Display");
        } else {
            outputArea.append("\n--- Characters ---\n");
            for (PCharacter c : chars) outputArea.append(c + "\n");
            outputArea.append("-----------------\n");
            ImageIcon randomIcon = getRandomJobIcon();
            if (randomIcon != null) {
                Image img = randomIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                jobImageLabel.setIcon(new ImageIcon(img));
            }
        }

    }

    private void levelUpCharacter() {
        ArrayList<PCharacter> chars = ff.getCharacters();
        if (chars == null || chars.size() == 0) {
            JOptionPane.showMessageDialog(frame, "Nothing to LevelUp");
        } else{
            String id = JOptionPane.showInputDialog(frame, "Character ID:");
            int inc = Integer.parseInt(JOptionPane.showInputDialog(frame, "Level Increase:"));
            ff.levelUpById(id, inc);
            outputArea.append("Leveled up: " + id + "\n");
        }

    }

    private void updateCharacter() {
        String id = JOptionPane.showInputDialog(frame, "Character ID:");
        ff.updateCharacterById(id);
        outputArea.append("Updated: " + id + "\n");
    }

    private void removeCharacter() {
        if (ff.getCharacters().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No characters to remove!");
        }
        else {
            String id = JOptionPane.showInputDialog(frame, "Character ID to remove:");
            ff.removeCharacterById(id);
            outputArea.append("Removed: " + id + "\n");
        }

    }

    public static void main(String[] args) { SwingUtilities.invokeLater(FFGUI::new); }
}
