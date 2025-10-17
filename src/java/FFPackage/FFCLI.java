package FFPackage;
import DBHelper.PCharacters;

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
        boolean adding = true;
        while (adding) {
            System.out.println("\n--- Add character manually ---\n");
            System.out.println("Enter character name: ");
            String name = sc.nextLine();

            String job = getValidJob(sc);
            int level =  getValidLevel(sc);
            double hp = getValidHP(sc);
            boolean active = getValidActive(sc);

            ff.addCharacter(new PCharacter("", name, job, level, hp, active));
            System.out.println(name + " added successfully!");

            System.out.print("Enter another character? (yes/no): ");
            if (!sc.nextLine().equals("yes")) {
                adding = false;
            }
        }
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

    private static double getValidHP(Scanner sc) {
        while (true) {
            System.out.print("HP: ");
            try {
                double hp = Double.parseDouble(sc.nextLine().trim());
                if(hp > 0 ) return hp;
                else System.out.println("Invalid HP!");
            }catch (NumberFormatException e) {
                System.out.println("Invalid HP!");}
        }
    }

    private static boolean getValidActive(Scanner sc) {
        while (true) {
            System.out.print("Is the character in your party? (yes/no): ");
            String response = sc.nextLine().trim().toLowerCase();

            if (response.equals("yes")) return true;
            if (response.equals("no")) return false;

            System.out.println("Please enter yes or no.");
        }
    }

    public static void debugCharacter(String id) {
        PCharacters db = new PCharacters();
        db.printById(id);
    }
}