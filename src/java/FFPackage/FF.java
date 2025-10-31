package FFPackage;

import DBHelper.PCharacters;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class FF {
    private final PCharacters db = new PCharacters();
    private final int MAX_LEVEL = 99;
    private final int MIN_LEVEL = 1;



    // add a character manually
    public String addCharacter(PCharacter pc) {
        String id = pc.getId();
        int attempts = 0;
        int maxAttempts = 100;

        // Keep generating new IDs until we find a unique one
        while (db.idExists(id)) {
            id = PCharacter.generateId();
            attempts++;

            if (attempts >= maxAttempts) {
                throw new IllegalStateException("Could not generate unique ID after " + maxAttempts + " attempts");
            }
        }

        // Insert with the unique ID
        db.insert(id, pc.getName(), pc.getJob(), pc.getLevel(), pc.getHp(), pc.getMp() , pc.isActive());
        return id;
    }

    public ArrayList<PCharacter> getCharacters() { return db.selectAll(); }

    // level up by ID
    public void levelUpById(String id, int increment) {

        PCharacter c = db.selectById(id);

        if (c == null) {
            System.out.println("Character not found!");
            return;
        }

        int newLevel = c.getLevel() + increment;
        if (newLevel > MAX_LEVEL) newLevel = MAX_LEVEL;
        if (newLevel < MIN_LEVEL) newLevel = MIN_LEVEL;

        int newHp = c.getHp() + new Random().nextInt(500);

        db.update("level", String.valueOf(newLevel), "id", id);
        db.update("hp", String.valueOf(newHp), "id", id);

        System.out.println("Character leveled up! " + c.getName() + " is level " + newLevel);

    }

    // remove by ID
    public void removeCharacterById(String id) {
        db.delete("id", id);
    }

    public void updateCharacterById(String id) {

            Scanner sc = new Scanner(System.in);  // create scanner once
            String options = """
                    1. Change name
                    2. Change job
                    3. Change level
                    4. Change hp
                    5. Change mp
                    5. Exit
                    """;


            PCharacter c = db.selectById(id);


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
                    System.out.println("Character's name has been updated to: " + c.getName());
                    break;
                case "2":
                    System.out.print("Enter new job: ");
                    String newJob = sc.nextLine();
                    if (!PCharacter.isValidJob(newJob)) {
                        System.out.println("Invalid job!");
                        continue;
                    }
                    c.setJob(newJob);
                    db.update("job", newJob, "id", id);
                    System.out.println("Character's job has been updated to: " + c.getJob());
                    break;
                case "3":
                    System.out.print("Enter new level: ");
                    try {
                        int newLevel = Integer.parseInt(sc.nextLine());
                        if (newLevel < MIN_LEVEL) newLevel = MIN_LEVEL;
                        if (newLevel > MAX_LEVEL) newLevel = MAX_LEVEL;
                        c.setLevel(newLevel);
                        db.update("level", String.valueOf(newLevel), "id", id);
                        System.out.println("Character's level has been updated to: " + c.getLevel());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number!");
                        continue;
                    }
                    break;
                case "4":
                    System.out.print("Enter new hp: ");
                    try {
                        int newHp = Integer.parseInt(sc.nextLine());
                        c.setHp(newHp);
                        db.update("hp", String.valueOf(newHp), "id", id);
                        System.out.println("Character's hp has been updated to: " + c.getHp());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number!");
                        continue;
                    }
                    break;
                case "5":
                    System.out.println("Exiting update menu.");
                    updating = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
                    continue;
            }
        }
    }


    // Add characters from a txt file
    public void addCharactersFromFile(String filename) {
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                try {
                    String name = parts[0].trim();
                    String job = parts[1].trim();
                    int level = Integer.parseInt(parts[2].trim());
                    int hp = Integer.parseInt(parts[3].trim());
                    int mp = Integer.parseInt(parts[4].trim());
                    boolean isActive = Boolean.parseBoolean(parts[5].trim());

                    // Validate job
                    if (!PCharacter.isValidJob(job)) {
                        System.out.println("Invalid job in file, skipping: " + job);
                        continue;
                    }

                    // Clamp level
                    if (level < MIN_LEVEL) level = MIN_LEVEL;
                    if (level > MAX_LEVEL) level = MAX_LEVEL;

                    PCharacter newChar = new PCharacter("", name, job, level, hp, mp, isActive);
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

    //check characters existence within the db
    public boolean characterExists(String id) {
        return db.idExists(id); // checks db
    }

    public void updateCharacterName(String id, String newName) {
        db.update("name", newName, "id", id);  // SET name=newName WHERE id=id
    }

    public void updateCharacterJob(String id, String newJob) {
        if (!PCharacter.isValidJob(newJob)) {
            throw new IllegalArgumentException("Invalid job: " + newJob);
        }
        db.update("job", newJob, "id", id);  // SET job=newJob WHERE id=id
    }

    public void updateCharacterLevel(String id, int newLevel) {
        // Clamp level
        if (newLevel < MIN_LEVEL) newLevel = MIN_LEVEL;
        if (newLevel > MAX_LEVEL) newLevel = MAX_LEVEL;

        db.update("level", String.valueOf(newLevel), "id", id);
    }

    public void updateCharacterHp(String id, double newHp) {
        db.update("hp", String.valueOf(newHp), "id", id);
    }

    public void updateIsActive(String id, boolean isActive) {
        db.update("isActive", String.valueOf(isActive), "id", id);
    }

    public void singleCharacterDisplay(String id) {

        db.printById(id);
    }

}