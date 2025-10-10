package FFPackage;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class FFCLI {
    private static final Scanner sc = new Scanner(System.in);
    private static final FF ff = new FF();

    //menu for the CLI
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

    // user can manually enter a character
    private static void addManual() {
        try {
            System.out.print("Name: "); String name = sc.nextLine().trim();
            System.out.print("Job: "); String job = sc.nextLine().trim();
            if (!PCharacter.isValidJob(job)) {
                System.out.println("Invalid job! Allowed: " + PCharacter.getValidJobs());
                return;
            }
            System.out.print("Level: "); int level = Integer.parseInt(sc.nextLine().trim());
            System.out.print("HP: "); double hp = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Is the character in your party? (yes/no):"); String isActive = sc.nextLine();
            if (!isActive.equalsIgnoreCase("yes") && !isActive.equalsIgnoreCase("no")) {
                throw new IllegalArgumentException("\nActive must be 'yes' or 'no'");
            }
            boolean active = isActive.equalsIgnoreCase("yes");
            ff.addCharacter(new PCharacter("", name, job, level, hp, active));
            System.out.println("Character added!");
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    //function to read and add characters from the .txt file
    private static void addFromFile() {
        System.out.print("Enter filename (absolute path): ");
        String path = sc.nextLine().trim();
        File f = new File(path);
        if (!f.exists()) { System.out.println("File does not exist."); return; }
        ff.addCharactersFromFile(path);
        System.out.println("Characters imported from file.");
    }

    //display all characters in the database
    private static void display() {
        ArrayList<PCharacter> characters = ff.getCharacters();
        if  (characters.isEmpty()) { System.out.println("No characters added!");  return; }
        else {
            for (PCharacter c : ff.getCharacters()) System.out.println(c);
        }
    }

    //allows users to levelup a character by their character ID
    private static void levelUp() {
        ArrayList<PCharacter> characters = ff.getCharacters();
        if (characters.isEmpty()) {
            System.out.println("No characters in the file!");
            return;
        }
        else {
            System.out.print("Enter Character ID: ");
            String id = sc.nextLine().trim();
            System.out.print("Enter levels to increase: ");
            int inc = Integer.parseInt(sc.nextLine().trim());
            ff.levelUpById(id, inc);
        }
    }

    //allows users to update a specific character by entering the ID and what they want to update
    public static void update() {
        ArrayList<PCharacter> characters = ff.getCharacters();
        if (characters.isEmpty()) { System.out.println("No characters to update!"); return;}
        else {
            System.out.print("Enter Character ID: ");
            String id = sc.nextLine().trim();
            ff.updateCharacterById(id);
        }
    }

    //removes character by ID
    private static void remove() {
        if (ff.getCharacters().isEmpty()) {
            System.out.println("\nNo characters to remove!");
        }
        else {
            System.out.print("Enter Character ID to remove: "); String id = sc.nextLine().trim();
            ff.removeCharacterById(id);
            System.out.println("Character removed.");
        }
    }
}