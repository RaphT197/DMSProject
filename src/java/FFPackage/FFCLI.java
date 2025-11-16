package FFPackage;

import DBHelper.PCharacters;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Simple command-line interface (CLI) for managing {@link PCharacter} data.
 * <p>
 * This class provides a text menu for:
 * <ul>
 *     <li>Adding characters manually</li>
 *     <li>Importing characters from a file</li>
 *     <li>Listing all characters</li>
 *     <li>Leveling up characters</li>
 *     <li>Updating and removing characters</li>
 * </ul>
 * It delegates all business logic to {@link FF} and persistence to {@link PCharacters}.
 */
public class FFCLI {

    /**
     * Single shared {@link Scanner} instance for reading console input.
     */
    private static final Scanner sc = new Scanner(System.in);

    /**
     * Service layer that encapsulates character-related operations.
     */
    private static final FF ff = new FF();

    /**
     * Entry point for the CLI application.
     * <p>
     * Displays a looped menu and routes user input to the corresponding
     * handler methods until the user chooses to exit.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Final Fantasy Manager (CLI) ---");
            System.out.println("1. Add character manually");
            System.out.println("2. Add characters from file");
            System.out.println("3. Display all characters");
            System.out.println("4. Level up character");
            System.out.println("5. Remove character");
            System.out.println("6. Update character");
            System.out.println("7. Exit");
            System.out.print("Choose: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1": addManual(); break;
                case "2": addFromFile(); break;
                case "3": display(); break;
                case "4": levelUp(); break;
                case "5": remove(); break;
                case "6": update(); break;
                case "7": System.exit(0);
                default: System.out.println("Invalid choice!");
            }
        }
    }

    /**
     * Interactive flow to manually add characters one by one.
     * <p>
     * Prompts the user for all fields (name, job, level, HP, MP, active)
     * and uses validation helper methods to ensure correct input.
     * Continues until the user answers something other than {@code "yes"}.
     */
    private static void addManual() {
        boolean adding = true;
        while (adding) {
            System.out.println("\n--- Add character manually ---\n");
            System.out.println("Enter character name: ");
            String name = sc.nextLine();

            String job = getValidJob(sc);
            int level = getValidLevel(sc);
            int hp = getValidHP(sc);
            int mp = getValidMP(sc);
            boolean active = getValidActive(sc);

            ff.addCharacter(new PCharacter("", name, job, level, hp, mp, active));
            System.out.println(name + " added successfully!");

            System.out.print("Enter another character? (yes/no): ");
            if (!sc.nextLine().equals("yes")) {
                adding = false;
            }
        }
    }

    /**
     * Asks the user for a file path and imports characters from that file.
     * <p>
     * The actual parsing and validation is delegated to
     * {@link FF#addCharactersFromFile(String)}.
     * The path must point to an existing file or an error is shown.
     */
    private static void addFromFile() {
        System.out.print("Enter filename (absolute path): ");
        String path = sc.nextLine().trim();
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        ff.addCharactersFromFile(path);
        System.out.println("Characters imported from file.");
    }

    /**
     * Displays all characters currently stored in the database.
     * <p>
     * If there are no characters, a friendly message is shown instead.
     */
    private static void display() {
        ArrayList<PCharacter> characters = ff.getCharacters();
        if (characters.isEmpty()) {
            System.out.println("No characters added!");
            return;
        } else {
            for (PCharacter c : ff.getCharacters()) {
                System.out.println(c);
            }
        }
    }

    /**
     * Allows the user to level up a specific character by ID.
     * <p>
     * Includes:
     * <ul>
     *     <li>Early exit with "E"</li>
     *     <li>Basic ID length validation</li>
     *     <li>Existence check before leveling up</li>
     * </ul>
     */
    private static void levelUp() {

        int maxDigit = 4;
        boolean active = true;
        ArrayList<PCharacter> characters = ff.getCharacters();

        while (active) {
            if (characters.isEmpty()) {
                System.out.println("No characters in the database!");
                active = false;

            } else {
                System.out.print("Enter your Character's ID: ");
                System.out.print("or press E to return to main menu ");
                String id = sc.nextLine().trim();

                if (id.equalsIgnoreCase("e")) {
                    break;
                }

                if (id.length() > maxDigit || id.isEmpty()) {
                    System.out.println("Invalid character id!");
                    continue;
                }

                if (!ff.characterExists(id)) {
                    System.out.println("Character does not exist!");
                    continue;
                }

                try {
                    System.out.println("Enter levels to level up! ");
                    int level = Integer.parseInt(sc.nextLine().trim());
                    ff.levelUpById(id, level);
                    active = false;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid level!");
                }
            }
        }
    }

    /**
     * Interactive flow to update a character by ID.
     * <p>
     * Validates that the character exists, then delegates the actual update
     * logic to {@link FF#updateCharacterById(String)}, which manages the
     * update sub-menu.
     */
    public static void update() {
        ArrayList<PCharacter> characters = ff.getCharacters();
        if (characters.isEmpty()) {
            System.out.println("No characters to update!");
            return;
        }

        boolean searching = true;

        while (searching) {
            System.out.print("Enter character ID or E to exit: ");
            String id = sc.nextLine().trim();

            if (id.equalsIgnoreCase("e")) {
                break;
            }
            if (!ff.characterExists(id)) {
                System.out.println("Character not found!");
                continue;
            }

            ff.updateCharacterById(id);
            searching = false;
        }

    }

    /**
     * Removes a character by ID after validating it exists.
     * <p>
     * The user can also press "E" to exit without removing anything.
     */
    private static void remove() {
        boolean searching = true;
        if (ff.getCharacters().isEmpty()) {
            System.out.println("\nNo characters to remove!");
            return;

        }

        while (searching) {
            System.out.print("Enter Character ID to remove or press E to exit: ");
            String id = sc.nextLine().trim();

            if (id.equalsIgnoreCase("e")) {
                searching = false;
            }
            if (!ff.characterExists(id)) {
                System.out.println("Character not found!");
                continue;
            }
            System.out.println("Character removed.");
            ff.removeCharacterById(id);
            searching = false;
        }
    }

    /**
     * Repeatedly prompts the user for a valid level value.
     * <p>
     * A valid level is an integer between 1 and 99 (inclusive).
     *
     * @param sc the scanner used for input
     * @return a valid level
     */
    private static int getValidLevel(Scanner sc) {
        while (true) {
            System.out.print("Level: ");
            try {
                int level = Integer.parseInt(sc.nextLine().trim());
                if (level > 0 && level < 100) return level;
                System.out.println("Level must be between 1 and 99!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid level!");
            }
        }
    }

    /**
     * Repeatedly prompts the user for a valid job value.
     * <p>
     * Valid jobs are defined by {@link PCharacter#getValidJobs()} and checked
     * using {@link PCharacter#isValidJob(String)}.
     *
     * @param sc the scanner used for input
     * @return a valid job string
     */
    private static String getValidJob(Scanner sc) {
        while (true) {
            System.out.print("Job: ");
            try {
                String job = sc.nextLine().trim();
                if (PCharacter.isValidJob(job)) return job;
                else System.out.println("Enter one of the following: " + PCharacter.getValidJobs());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid job!");
            }
        }
    }

    /**
     * Repeatedly prompts the user for a valid HP value.
     * <p>
     * HP must be a positive integer.
     *
     * @param sc the scanner used for input
     * @return a valid HP value
     */
    private static Integer getValidHP(Scanner sc) {
        while (true) {
            System.out.print("HP: ");
            try {
                int hp = Integer.parseInt(sc.nextLine().trim());
                if (hp > 0) return hp;
                else System.out.println("Invalid HP!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid HP!");
            }
        }
    }

    /**
     * Repeatedly prompts the user for a valid MP value.
     * <p>
     * MP must be a positive integer.
     *
     * @param sc the scanner used for input
     * @return a valid MP value
     */
    private static Integer getValidMP(Scanner sc) {
        while (true) {
            System.out.print("MP: ");
            try {
                int mp = Integer.parseInt(sc.nextLine().trim());
                if (mp > 0) return mp;
                else System.out.println("Invalid MP!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid MP!");
            }
        }
    }

    /**
     * Repeatedly prompts the user for an "active" flag.
     * <p>
     * Accepts only {@code "yes"} or {@code "no"} (case-insensitive).
     *
     * @param sc the scanner used for input
     * @return {@code true} if the user answers yes, {@code false} if no
     */
    private static boolean getValidActive(Scanner sc) {
        while (true) {
            System.out.print("Is the character in your party? (yes/no): ");
            String response = sc.nextLine().trim().toLowerCase();

            if (response.equals("yes")) return true;
            if (response.equals("no")) return false;

            System.out.println("Please enter yes or no.");
        }
    }

    /**
     * Convenience method for debugging a specific character by ID.
     * <p>
     * Creates its own {@link PCharacters} instance and prints the character data
     * directly, using {@link PCharacters#printById(String)}.
     *
     * @param id ID of the character to debug-print
     */
    public static void debugCharacter(String id) {
        PCharacters db = new PCharacters();
        db.printById(id);
    }
}
