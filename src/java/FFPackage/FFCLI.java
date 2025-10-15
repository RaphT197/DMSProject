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

        int maxDigit = 4;
        boolean active = true;
        ArrayList<PCharacter> characters = ff.getCharacters();

        while (active) {
            if (characters.isEmpty()) {
                System.out.println("No characters in the database!");
                active = false;

            }
            else {
                System.out.print("Enter your Character's ID: ");
                System.out.print("or press E to return to main menu ");
                String id = sc.nextLine().trim();

                if (id.equalsIgnoreCase("e")){
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


    //allows users to update a specific character by entering the ID and what they want to update
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

            if(id.equalsIgnoreCase("e")){
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

    //removes character by ID
    private static void remove() {
        boolean searching = true;
        if (ff.getCharacters().isEmpty()) {
            System.out.println("\nNo characters to remove!");
            return;

        }

        while(searching){
            System.out.print("Enter Character ID to remove or press E to exit: "); String id = sc.nextLine().trim();

            if(id.equalsIgnoreCase("e")){
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

}