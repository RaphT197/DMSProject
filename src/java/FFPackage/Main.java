package FFPackage;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n*******************************************************");
        System.out.println("Welcome to the Final Fantasy Database Management System");
        System.out.println("*********************************************************");
        System.out.println("       *\n" +
                "      ***        ☆\n" +
                "       *\n" +
                "     **o o**\n" +
                "    *   ∧   *\n" +
                "    *  ~~~  *\n" +
                "     *-----*\n" +
                "    **     **\n" +
                "   *  *   *  *\n" +
                "     *  *  *\n" +
                "      *****\n" +
                "       * *\n" +
                "      *   *\n");
        System.out.println("Choose mode: 1 = CLI, 2 = GUI, 3 = QUIT");
        java.util.Scanner sc = new java.util.Scanner(System.in);
        String choice = sc.nextLine();
        boolean chosen = false;

        while (!chosen) {
            if (choice.equals("1")) { //runs CLI
                chosen = true;
                FFCLI.main(new String[]{});
            }
            else if (choice.equals("2")) { // runs GUI
                chosen = true;
                FFGUI.main(new String[]{});
        }
            else if (choice.equals("3")) {
                chosen = true;
                break;
            }
            else {
                System.out.println("Invalid choice");

            }
            choice = sc.nextLine();
        }

    }
}

