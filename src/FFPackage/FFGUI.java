package FFPackage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class FFGUI {
    private JFrame frame;
    private JTextArea outputArea;
    private FF ff;

    public FFGUI() {
        ff = new FF();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("FF Character Manager");
        frame.setBounds(100, 100, 600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(2,3));
        JButton addManual = new JButton("Add Character");
        JButton addFile = new JButton("Add From File");
        JButton display = new JButton("Display All");
        JButton levelUp = new JButton("Level Up");
        JButton remove = new JButton("Remove");
        JButton exit = new JButton("Exit");

        panel.add(addManual); panel.add(addFile); panel.add(display);
        panel.add(levelUp); panel.add(remove); panel.add(exit);
        frame.add(panel, BorderLayout.SOUTH);

        addManual.addActionListener(e -> addCharacterManual());
        addFile.addActionListener(e -> addCharacterFromFile());
        display.addActionListener(e -> displayCharacters());
        levelUp.addActionListener(e -> levelUpCharacter());
        remove.addActionListener(e -> removeCharacter());
        exit.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    private void addCharacterManual() {
        try {
            String name = JOptionPane.showInputDialog(frame, "Name:").trim();
            String job = JOptionPane.showInputDialog(frame, "Job:").trim();
            if (!PCharacter.isValidJob(job)) { JOptionPane.showMessageDialog(frame, "Invalid Job"); return; }
            int level = Integer.parseInt(JOptionPane.showInputDialog(frame, "Level:").trim());
            double hp = Double.parseDouble(JOptionPane.showInputDialog(frame, "HP:").trim());
            boolean isActive = Boolean.parseBoolean(JOptionPane.showInputDialog(frame, "Active (true/false):").trim());

            PCharacter pc = new PCharacter("", name, job, level, hp, isActive);
            ff.addCharacter(pc);
            outputArea.append("Added: " + pc + "\n");
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
        outputArea.append("\n--- Characters ---\n");
        for (PCharacter c : chars) outputArea.append(c + "\n");
        outputArea.append("-----------------\n");
    }

    private void levelUpCharacter() {
        String id = JOptionPane.showInputDialog(frame, "Character ID:");
        int inc = Integer.parseInt(JOptionPane.showInputDialog(frame, "Level Increase:"));
        ff.levelUpById(id, inc);
        outputArea.append("Leveled up: " + id + "\n");
    }

    private void removeCharacter() {
        String id = JOptionPane.showInputDialog(frame, "Character ID to remove:");
        ff.removeCharacterById(id);
        outputArea.append("Removed: " + id + "\n");
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(FFGUI::new); }
}
