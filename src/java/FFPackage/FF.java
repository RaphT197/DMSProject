package FFPackage;

import DBHelper.PCharacters;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Core service layer for managing {@link PCharacter} instances in the game.
 * <p>
 * This class acts as a higher-level API over the {@link PCharacters} DAO:
 * it handles business rules such as:
 * <ul>
 *     <li>Generating unique IDs for new characters</li>
 *     <li>Clamping levels to allowed ranges</li>
 *     <li>Bulk-adding characters from a text file</li>
 *     <li>Convenience update and display operations</li>
 * </ul>
 */
public class FF {

    /**
     * Database helper for persisting and querying characters.
     */
    private final PCharacters db = new PCharacters();

    /**
     * Maximum allowed character level (inclusive).
     */
    private final int MAX_LEVEL = 99;

    /**
     * Minimum allowed character level (inclusive).
     */
    private final int MIN_LEVEL = 1;

    /**
     * Adds a single character to the database.
     * <p>
     * If the provided {@link PCharacter} already has an ID that exists in the DB,
     * a new ID is generated until a unique one is found (or a maximum number
     * of attempts is reached).
     *
     * @param pc the character to add (its name/job/level/etc. are used)
     * @return the final unique ID assigned to this character
     * @throws IllegalStateException if a unique ID cannot be generated after
     *                               a fixed number of attempts
     */
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
        db.insert(id, pc.getName(), pc.getJob(), pc.getLevel(), pc.getHp(), pc.getMp(), pc.isActive());
        return id;
    }

    /**
     * Retrieves all characters currently stored in the database.
     *
     * @return a list of {@link PCharacter} instances; never {@code null}
     */
    public ArrayList<PCharacter> getCharacters() {
        return db.selectAll();
    }

    /**
     * Levels up a character by its ID, adjusting both level and HP.
     * <p>
     * The new level is clamped between {@link #MIN_LEVEL} and {@link #MAX_LEVEL}.
     * HP is increased by a random amount up to 500.
     *
     * @param id        ID of the character to level up
     * @param increment how many levels to add (can be negative to level down)
     */
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

    /**
     * Removes a character from the database by ID.
     *
     * @param id ID of the character to delete
     */
    public void removeCharacterById(String id) {
        db.delete("id", id);
    }

    /**
     * Interactive CLI-based update for a single character.
     * <p>
     * This method repeatedly prompts the user on the console to choose which field
     * to update (name, job, level, hp, etc.) and applies the changes until the user exits.
     *
     * @param id ID of the character to update
     */
    public void updateCharacterById(String id) {

        Scanner sc = new Scanner(System.in);  // created once for this update session
        String options = """
                1. Change name
                2. Change job
                3. Change level
                4. Change hp
                5. Change mp
                5. Exit
                """;
        // NOTE: the menu text lists "Change mp" but there is no case handling it yet.

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
            }
        }
    }

    /**
     * Reads a list of characters from a text file and adds them to the database.
     * <p>
     * Each line is expected to have the format:
     * <pre>
     * name,job,level,hp,mp,isActive
     * </pre>
     * Lines that are malformed or contain invalid data are skipped with a message.
     *
     * @param filename path to the input file
     */
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

                    // Validate job against allowed values
                    if (!PCharacter.isValidJob(job)) {
                        System.out.println("Invalid job in file, skipping: " + job);
                        continue;
                    }

                    // Clamp level into allowed range
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

    /**
     * Checks if a character with the given ID exists in the database.
     *
     * @param id the character ID to check
     * @return {@code true} if a character with that ID exists, {@code false} otherwise
     */
    // check characters existence within the db
    public boolean characterExists(String id) {
        return db.idExists(id); // checks db
    }

    /**
     * Updates a character's name in the database.
     *
     * @param id      ID of the character to update
     * @param newName new name to set
     */
    public void updateCharacterName(String id, String newName) {
        db.update("name", newName, "id", id);  // SET name=newName WHERE id=id
    }

    /**
     * Updates a character's job in the database.
     * <p>
     * The job is validated via {@link PCharacter#isValidJob(String)} before being applied.
     *
     * @param id     ID of the character to update
     * @param newJob new job name
     * @throws IllegalArgumentException if the job is not valid
     */
    public void updateCharacterJob(String id, String newJob) {
        if (!PCharacter.isValidJob(newJob)) {
            throw new IllegalArgumentException("Invalid job: " + newJob);
        }
        db.update("job", newJob, "id", id);  // SET job=newJob WHERE id=id
    }

    /**
     * Updates a character's level in the database.
     * <p>
     * The level is clamped to the valid range between {@link #MIN_LEVEL}
     * and {@link #MAX_LEVEL}.
     *
     * @param id       ID of the character to update
     * @param newLevel new level value to set
     */
    public void updateCharacterLevel(String id, int newLevel) {
        // Clamp level
        if (newLevel < MIN_LEVEL) newLevel = MIN_LEVEL;
        if (newLevel > MAX_LEVEL) newLevel = MAX_LEVEL;

        db.update("level", String.valueOf(newLevel), "id", id);
    }

    /**
     * Updates a character's HP in the database.
     * <p>
     * Note: the database column is integer-based; the {@code double}
     * value will be stringified and stored as such. If the DB expects
     * an integer, you may want to change this method to accept an {@code int}.
     *
     * @param id    ID of the character to update
     * @param newHp new HP value to set
     */
    public void updateCharacterHp(String id, double newHp) {
        db.update("hp", String.valueOf(newHp), "id", id);
    }

    /**
     * Prints a single character (or a message if not found) to the console.
     * <p>
     * This is a small wrapper around {@link PCharacters#printById(String)}.
     *
     * @param id ID of the character to display
     */
    public void singleCharacterDisplay(String id) {
        db.printById(id);
    }

}
