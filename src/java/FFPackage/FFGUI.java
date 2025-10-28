package FFPackage;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;


public class FFGUI {
    private JFrame frame;
    private BackgroundTextArea outputArea;
    private FF ff;
    private JLabel jobImageLabel;

    public FFGUI() {
        ff = new FF();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("FF Character Manager");

        // load icon using getResource
        URL appIconURL = getClass().getResource("/images/app_icon.png");
        if (appIconURL != null) {
            frame.setIconImage(new ImageIcon(appIconURL).getImage());
        } else {
            System.out.println("App icon not found!");
        }

        frame.setBounds(100, 100, 600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create the BackgroundTextArea
        outputArea = new BackgroundTextArea();
        outputArea.setEditable(false);

        // Try to load the background image
        java.net.URL bgImageURL = getClass().getResource("/images/carbuncles.png");
        if (bgImageURL != null) {
            outputArea.setBackgroundImage(new ImageIcon(bgImageURL).getImage(), 0.2f);
        } else {
            System.out.println("Background image not found!");
        }

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Add the scroll pane to the frame
        frame.add(scrollPane, BorderLayout.CENTER);


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
        boolean adding = true;
        while (adding) {
            try {
                String name = JOptionPane.showInputDialog(frame, "Name:");
                if (name == null) {
                    adding = false;
                    continue;
                }

                String job = validJob();
                if (job == null) {
                    adding = false;
                    continue;
                }

                Integer level = validLevel();
                if (level == null) {
                    adding = false;
                    continue;
                }

                Double hp = validHP();
                if (hp == null) {
                    adding = false;
                    continue;
                }

                Boolean activeInput = validActive();
                if (activeInput == null) {
                    adding = false;
                    continue;
                }

                PCharacter pc = new PCharacter("", name, job, level, hp, activeInput);
                ff.addCharacter(pc);
                outputArea.append("Added: " + pc + "\n");

                JOptionPane.showMessageDialog(frame, "Character added successfully!");

                int option = JOptionPane.showConfirmDialog(frame, "Would you like to add another character?");

                if (option == JOptionPane.NO_OPTION || option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                    adding = false;
                }

                updateRandomJobIcon();
            } catch (HeadlessException e) {
                JOptionPane.showMessageDialog(frame, "Error, please try again! ");
            }
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

        if (ff.getCharacters().isEmpty()) {
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
        private void updateCharacter() {

        boolean updating = true;
        String id = "";
        String[] options = {"Name", "Job", "Level", "HP", "Cancel"};

            if(ff.getCharacters().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Nothing to update.");
            } else {
                id = JOptionPane.showInputDialog(frame, "Character ID:");

                while (updating) {
                    int choice = JOptionPane.showOptionDialog(
                            frame,  // ← Parent component
                            "What would you like to update?",
                            "Update Character",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );

                    switch (choice) {
                        case 0 -> {
                            // Name
                            String newName = JOptionPane.showInputDialog(frame, "New name:");
                            if (newName != null && !newName.trim().isEmpty()) {
                                ff.updateCharacterName(id, newName);
                                outputArea.append("\n--- Character Name Updated! ---\n");
                                updateRandomJobIcon();
                            }
                        }

                        case 1 -> {
                            // Job
                            String newJob = JOptionPane.showInputDialog(frame, "New job:");
                            if (newJob != null && !newJob.trim().isEmpty()) {
                                try {
                                    ff.updateCharacterJob(id, newJob);
                                    outputArea.append("\n--- Character Job Updated! ---\n");
                                    updateRandomJobIcon();
                                } catch (IllegalArgumentException e) {
                                    JOptionPane.showMessageDialog(frame, "Invalid Job: " + e.getMessage());
                                }
                            }
                        }

                        case 2 -> {
                            // Level
                            String levelStr = JOptionPane.showInputDialog(frame, "New level (1-99):");
                            if (levelStr != null) {
                                try {
                                    int newLevel = Integer.parseInt(levelStr.trim());
                                    ff.updateCharacterLevel(id, newLevel);
                                    outputArea.append("\n--- Character Level Updated! ---\n");
                                    updateRandomJobIcon();
                                } catch (NumberFormatException e) {
                                    JOptionPane.showMessageDialog(frame, "Invalid number!");
                                }
                            }
                        }

                        case 3 -> {
                            // HP
                            String hpStr = JOptionPane.showInputDialog(frame, "New HP:");
                            if (hpStr != null) {
                                try {
                                    double newHp = Double.parseDouble(hpStr.trim());
                                    ff.updateCharacterHp(id, newHp);
                                    outputArea.append("\n--- Character HP Updated! ---\n");
                                    updateRandomJobIcon();
                                } catch (NumberFormatException e) {
                                    JOptionPane.showMessageDialog(frame, "Invalid number!");
                                }
                            }
                        }

                        case 4, JOptionPane.CLOSED_OPTION -> // Cancel
                        {
                            // User closed dialog
                            updating = false;
                            updateRandomJobIcon();
                        }
                    }
                    // Cancel
                                    }

            }


            if (id == null || id.trim().isEmpty()) {
                return;  // User cancelled
            }

            if (!ff.characterExists(id)) {
                JOptionPane.showMessageDialog(frame, "Character does not exist.");
                updateRandomJobIcon();
}





        }

    //remove characters by ID
    private void removeCharacter() {
        boolean searching = true;

        if (ff.getCharacters().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No characters to remove.");
            return;
        }

        while(searching) {
            String id = JOptionPane.showInputDialog(frame, "Character ID to remove:");

            if(id == null) {
                searching = false;
                continue;
            }

            if (!ff.characterExists(id)) {
                JOptionPane.showMessageDialog(frame, "Character does not exist");
                continue;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to remove character ID" + id + "?",
                    "Confirm deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if(confirm == JOptionPane.YES_OPTION) {
                ff.removeCharacterById(id);
                outputArea.append("Removed: " + id + "\n");
                displayCharacters();
                updateRandomJobIcon();
            }

            searching = false;

        }
    }

    private Boolean validActive() {
        while(true) {
            try {
                String active = JOptionPane.showInputDialog(frame, "In party? (yes/no):");
                if (active == null) { // User cancelled
                    return null;
                }
                if (active.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Invalid input!");
                    continue;
                }
                if (active.equalsIgnoreCase("yes")) {
                    return true;
                }
                if (active.equalsIgnoreCase("no")) {
                    return false;
                }
                JOptionPane.showMessageDialog(frame, "Please type 'yes' or 'no'.");
            } catch (InputMismatchException e) {
                JOptionPane.showMessageDialog(frame, "Invalid input! Please type yes or no ");
            }
        }
    }

    private Double validHP() {
        while(true) {
            try {
                String input = JOptionPane.showInputDialog(frame, "HP:");
                if (input == null) { // User cancelled
                    return null;
                }
                double hp = Double.parseDouble(input);
                if (hp <= 0) {
                    JOptionPane.showMessageDialog(frame, "HP must be greater than 0.");
                } else return hp;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid number!");
            }
        }
    }

    private String validJob() {
        while(true) {
            try {
                String input = JOptionPane.showInputDialog(frame, "Job:");
                if (input == null) { // User cancelled
                    return null;
                }
                String job = input.trim();
                if (PCharacter.isValidJob(job)) return job;
                else JOptionPane.showMessageDialog(frame, "Invalid Job: " + job);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(frame, "Invalid Job: " + e.getMessage());
            }
        }
    }

    private Integer validLevel() {
        while(true) {
            try {
                String input = JOptionPane.showInputDialog(frame, "Level:");
                if (input == null) { // User cancelled
                    return null;
                }
                int level = Integer.parseInt(input.trim());
                if (level > 0 && level < 100) return level;
                JOptionPane.showMessageDialog(frame, "Levels must be between 1 and 99 " + level);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(frame, "Invalid entry, please use numbers between 1 and 99. ");
            }
        }
    }

    //starts the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FFGUI::new);
    }
}