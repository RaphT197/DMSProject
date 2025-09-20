package FFPackage;

import DBHelper.PCharacters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class FF {
    private ArrayList<PCharacter> characters = new ArrayList<>();
    private PCharacters db = new PCharacters();
    private final double MAX_LEVEL = 99;

    public void addCharacter(PCharacter pc) {
        characters.add(pc);
        db.insert(pc.getId(), pc.getName(), pc.getJob(), pc.getLevel(), pc.getHp(), pc.isActive());
    }

    public ArrayList<PCharacter> getCharacters() { return characters; }

    public void levelUpById(String id, int increment) {
        for (PCharacter c : characters) {
            if (c.getId().equals(id)) {
                int newLevel = Math.min((int)MAX_LEVEL, c.getLevel() + increment);
                c.setLevel(newLevel);
                c.setHp(c.getHp() + new Random().nextInt(1000));
                db.update("level", String.valueOf(c.getLevel()), "id", id);
                db.update("hp", String.valueOf(c.getHp()), "id", id);
            }
        }
    }

    public void removeCharacterById(String id) {
        characters.removeIf(c -> c.getId().equals(id));
        db.delete("id", id);
    }

    public void addCharactersFromFile(String filename) {
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                String[] parts = fileScanner.nextLine().split(",");
                if (parts.length != 5) continue;
                try {
                    String name = parts[0].trim();
                    String job = parts[1].trim();
                    int level = Integer.parseInt(parts[2].trim());
                    double hp = Double.parseDouble(parts[3].trim());
                    boolean isActive = Boolean.parseBoolean(parts[4].trim());

                    PCharacter pc = new PCharacter("", name, job, level, hp, isActive);
                    addCharacter(pc);
                } catch (Exception ignored) { }
            }
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }
}
