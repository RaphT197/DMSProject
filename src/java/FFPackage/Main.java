package FFPackage;

public class Main {
    public static void main(String[] args) {
        System.out.println("Choose mode: 1 = CLI, 2 = GUI");
        java.util.Scanner sc = new java.util.Scanner(System.in);
        String choice = sc.nextLine();
        if (choice.equals("1")) {
            FFCLI.main(new String[]{}); // runs CLI
        } else {
            FFGUI.main(new String[]{}); // runs GUI
        }
    }
}

