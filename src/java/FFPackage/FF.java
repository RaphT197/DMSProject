package FFPackage;

import DBHelper.PCharacters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class FF {
    private ArrayList<PCharacter> character = new ArrayList<>();
    private PCharacters db = new PCharacters();
    private final int MAX_LEVEL = 99;
    private final int MIN_LEVEL = 1;


    // Constructor: load characters from DB
    public FF() {
        ArrayList<PCharacter> rows = db.selectAll();
        for (PCharacter pc : rows) {
            character.add(pc);
        }
    }


    // Add a character manually or programmatically
    public void addCharacter(PCharacter pc) {
        character.add(pc);
        db.insert(pc.getId(), pc.getName(), pc.getJob(), pc.getLevel(), pc.getHp(), pc.isActive());
    }

    public ArrayList<PCharacter> getCharacters() { return character; }

    // Level up by ID
    public void levelUpById(String id, int increment) {

            for (PCharacter c : character) {
                if (c.getId().equals(id)) {
                    int newLevel = c.getLevel() + increment;
                    if (newLevel > MAX_LEVEL) newLevel = MAX_LEVEL;
                    if (newLevel < MIN_LEVEL) newLevel = MIN_LEVEL;
                    c.setLevel(newLevel);
                    System.out.println("Character leveled up! " + c.getName() + " is level " + c.getLevel());
                    // Increase HP randomly between 0–9999
                    c.setHp(c.getHp() + new Random().nextInt(10000));

                    // Update DB
                    db.update("level", String.valueOf(c.getLevel()), "id", id);
                    db.update("hp", String.valueOf(c.getHp()), "id", id);
                }
                else {
                    System.out.println("No Character Found");
                }
            }
    }

    // Remove by ID
    public void removeCharacterById(String id) {
        character.removeIf(c -> c.getId().equals(id));
        db.delete("id", id);
    }

    public void updateCharacterById(String id) {
        Scanner sc = new Scanner(System.in);  // create scanner once
        String options = """
            1. Change name
            2. Change job
            3. Change level
            4. Change hp
            5. Exit
            """;

        // Find the character first
        PCharacter c = null;
        for (PCharacter pc : character) {
            if (pc.getId().equals(id)) {
                c = pc;
                break;
            }
        }
        if (c == null) {
            System.out.println("Character not found with ID: " + id);
            return;
        }

        boolean updating = true;
        while (updating) {
            System.out.println("What would you like to update?");
            System.out.println(options);
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter new name: ");
                    String newName = sc.nextLine();
                    c.setName(newName);
                    db.update("name", newName, "id", id);
                    break;
                case "2":
                    System.out.print("Enter new job: ");
                    String newJob = sc.nextLine();
                    if (!PCharacter.isValidJob(newJob)) {
                        System.out.println("Invalid job!");
                        break;
                    }
                    c.setJob(newJob);
                    db.update("job", newJob, "id", id);
                    break;
                case "3":
                    System.out.print("Enter new level: ");
                    try {
                        int newLevel = Integer.parseInt(sc.nextLine());
                        if (newLevel < MIN_LEVEL) newLevel = MIN_LEVEL;
                        if (newLevel > MAX_LEVEL) newLevel = MAX_LEVEL;
                        c.setLevel(newLevel);
                        db.update("level", String.valueOf(newLevel), "id", id);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number!");
                    }
                    break;
                case "4":
                    System.out.print("Enter new hp: ");
                    try {
                        double newHp = Double.parseDouble(sc.nextLine());
                        c.setHp(newHp);
                        db.update("hp", String.valueOf(newHp), "id", id);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number!");
                    }
                    break;
                case "5":
                    System.out.println("Exiting update menu.");
                    updating = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }


    // Add characters from a CSV file
    public void addCharactersFromFile(String filename) {
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length != 5) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                try {
                    String name = parts[0].trim();
                    String job = parts[1].trim();
                    int level = Integer.parseInt(parts[2].trim());
                    double hp = Double.parseDouble(parts[3].trim());
                    boolean isActive = Boolean.parseBoolean(parts[4].trim());

                    // Validate job
                    if (!PCharacter.isValidJob(job)) {
                        System.out.println("Invalid job in file, skipping: " + job);
                        continue;
                    }

                    // Clamp level
                    if (level < MIN_LEVEL) level = MIN_LEVEL;
                    if (level > MAX_LEVEL) level = MAX_LEVEL;

                    PCharacter newChar = new PCharacter("", name, job, level, hp, isActive);
                    addCharacter(newChar);
                    System.out.println("Added: " + newChar);
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid number in line: " + line + " -> " + nfe.getMessage());
                } catch (IllegalArgumentException iae) {
                    System.out.println("Invalid character data: " + line + " -> " + iae.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        }
    }
}