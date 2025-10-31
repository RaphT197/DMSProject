package FFPackage;
import java.util.*;

public class PCharacter  {
    private String id;
    private String name;
    private String job;
    private int level;
    private int hp;
    private int mp;
    private boolean isActive;

    private static final Set<String> validJobs = new HashSet<>(Arrays.asList(
            "Warrior","Thief","Monk","Red Mage","White Mage","Black Mage",
            "Knight","Ninja","Master","Red Wizard","White Wizard","Black Wizard",
            "Archer","Bard","Beastmaster","Berserker","Blue Mage","Chemist",
            "Dancer","Dark Knight","Dragoon","Machinist","Geomancer","Samurai",
            "Summoner","Scholar","Astrologian","Time Mage"
    ));

    public static boolean isValidJob(String job) {
        return validJobs.stream().anyMatch(v -> v.equalsIgnoreCase(job.trim()));
    }

    public static Set<String> getValidJobs() { return validJobs; }

    public static String generateId() {
        Random random = new Random();
        return String.valueOf(1000 + random.nextInt(9000)); // Generates 1000-9999
    }


    public PCharacter(String id, String name, String job, int level, int hp, int mp, boolean isActive) {
        // If empty, generate random ID
        if (id == null || id.isEmpty()) {
            this.id = generateId();
        } else {
            this.id = id;
        }

        this.name = name != null ? name.trim() : "";

        if (!isValidJob(job)) throw new IllegalArgumentException("Invalid job: " + job);
        this.job = normalizeJob(job);

        if (level < 1 || level > 99) throw new IllegalArgumentException("Level must be 1-99");
        this.level = level;

        if (hp < 0) throw new IllegalArgumentException("HP cannot be negative");
        this.hp =  hp;

        if (mp < 0) throw new IllegalArgumentException("MP cannot be negative");
        this.mp = mp;

        this.isActive = isActive;
    }

    private String normalizeJob(String job) {
        String[] parts = job.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getJob() { return job; }
    public int getLevel() { return level; }
    public int getMp() { return mp; }
    public int getHp() { return hp; }
    public boolean isActive() { return isActive; }

    public void setName(String name) { this.name = name != null ? name.trim() : ""; }
    public void setJob(String job) { this.job = normalizeJob(job); }
    public void setLevel(int level) { this.level = level; }
    public void setHp(int hp) { this.hp = hp; }
    public void setMp(int mp) { this.mp = mp; }
    public void setActive(boolean active) { this.isActive = active; }


    @Override
    public String toString() {
        return String.format("%s (%s) - Job: %s, Lv: %d, HP: %d, MP: %d, Active: %b",
                id, name, job, level, hp, mp, isActive);
    }
}