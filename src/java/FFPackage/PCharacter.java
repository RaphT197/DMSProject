package FFPackage;

import java.util.*;

/**
 * Domain model representing a Final Fantasy character in the system.
 * <p>
 * A {@code PCharacter} has:
 * <ul>
 *     <li>A unique ID (4-digit string, usually 1000–9999)</li>
 *     <li>Name</li>
 *     <li>Job (validated against a known set of jobs)</li>
 *     <li>Level (1–99)</li>
 *     <li>HP / MP (non-negative)</li>
 *     <li>Active flag (in party or not)</li>
 * </ul>
 * The constructor enforces basic invariants such as valid job, level range,
 * and non-negative HP/MP.
 */
public class PCharacter  {

    /** Immutable unique identifier for this character. */
    private final String id;

    /** Character name. */
    private String name;

    /** Character job (normalized to title case). */
    private String job;

    /** Character level (intended to be 1–99). */
    private int level;

    /** Hit points (must be non-negative). */
    private int hp;

    /** Magic points (must be non-negative). */
    private int mp;

    /** Whether the character is currently in the party. */
    private boolean isActive;

    /**
     * Set of valid job names for characters.
     * <p>
     * Validation is case-insensitive, but jobs are stored in normalized title case.
     */
    private static final Set<String> validJobs = new HashSet<>(Arrays.asList(
            "Warrior","Thief","Monk","Red Mage","White Mage","Black Mage",
            "Knight","Ninja","Master","Red Wizard","White Wizard","Black Wizard",
            "Archer","Bard","Beastmaster","Berserker","Blue Mage","Chemist",
            "Dancer","Dark Knight","Dragoon","Machinist","Geomancer","Samurai",
            "Summoner","Scholar","Astrologian","Time Mage"
    ));

    /**
     * Checks if the given job name is allowed.
     * <p>
     * Comparison is case-insensitive, and leading/trailing whitespace is ignored.
     *
     * @param job the job name to validate
     * @return {@code true} if the job is in the {@link #validJobs} set, {@code false} otherwise
     */
    public static boolean isValidJob(String job) {
        return validJobs.stream().anyMatch(v -> v.equalsIgnoreCase(job.trim()));
    }

    /**
     * Returns the set of valid jobs.
     * <p>
     * Note: the returned set is the backing set itself, so callers should not modify it
     * in normal use. If you want to prevent accidental modification, you can wrap this
     * in {@code Collections.unmodifiableSet(validJobs)} instead.
     *
     * @return a set of valid job names
     */
    public static Set<String> getValidJobs() {
        return validJobs;
    }

    /**
     * Generates a pseudo-random 4-digit ID string between 1000 and 9999 (inclusive).
     *
     * @return a 4-digit ID string
     */
    public static String generateId() {
        Random random = new Random();
        return String.valueOf(1000 + random.nextInt(9000)); // Generates 1000-9999
    }

    /**
     * Constructs a new {@code PCharacter} with the given properties.
     * <p>
     * Validation rules:
     * <ul>
     *     <li>If {@code id} is {@code null} or empty, a random ID is generated</li>
     *     <li>{@code job} must be valid according to {@link #isValidJob(String)}</li>
     *     <li>{@code level} must be between 1 and 99 (inclusive)</li>
     *     <li>{@code hp} and {@code mp} must be non-negative</li>
     * </ul>
     *
     * @param id       character ID; if null/empty, one is generated
     * @param name     character name (trimmed; {@code null} becomes empty string)
     * @param job      character job; must be valid
     * @param level    character level (1–99)
     * @param hp       hit points (must be ≥ 0)
     * @param mp       magic points (must be ≥ 0)
     * @param isActive whether this character is in the party
     * @throws IllegalArgumentException if any of the validation rules are violated
     */
    public PCharacter(String id, String name, String job, int level, int hp, int mp, boolean isActive) {
        // If empty, generate random ID
        if (id == null || id.isEmpty()) {
            this.id = generateId();
        } else {
            this.id = id;
        }

        this.name = name != null ? name.trim() : "";

        if (!isValidJob(job)) {
            throw new IllegalArgumentException("Invalid job: " + job);
        }
        this.job = normalizeJob(job);

        if (level < 1 || level > 99) {
            throw new IllegalArgumentException("Level must be 1-99");
        }
        this.level = level;

        if (hp < 0) {
            throw new IllegalArgumentException("HP cannot be negative");
        }
        this.hp =  hp;

        if (mp < 0) {
            throw new IllegalArgumentException("MP cannot be negative");
        }
        this.mp = mp;

        this.isActive = isActive;
    }

    /**
     * Normalizes a job string to title case for each word.
     * <p>
     * Example: {@code "dark knight"} → {@code "Dark Knight"}.
     *
     * @param job the raw job string
     * @return normalized, title-cased job string
     */
    private String normalizeJob(String job) {
        String[] parts = job.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0)))
                    .append(p.substring(1))
                    .append(" ");
        }
        return sb.toString().trim();
    }

    // Getters

    /**
     * Returns this character's ID.
     *
     * @return the character ID
     */
    public String getId() { return id; }

    /**
     * Returns this character's name.
     *
     * @return the character name
     */
    public String getName() { return name; }

    /**
     * Returns this character's job (normalized form).
     *
     * @return the character job
     */
    public String getJob() { return job; }

    /**
     * Returns this character's level.
     *
     * @return the character level
     */
    public int getLevel() { return level; }

    /**
     * Returns this character's magic points (MP).
     *
     * @return the character MP
     */
    public int getMp() { return mp; }

    /**
     * Returns this character's hit points (HP).
     *
     * @return the character HP
     */
    public int getHp() { return hp; }

    /**
     * Indicates whether this character is currently in the party.
     *
     * @return {@code true} if active, {@code false} otherwise
     */
    public boolean isActive() { return isActive; }

    // Setters

    /**
     * Updates the character's name.
     * <p>
     * The name is trimmed; {@code null} becomes an empty string.
     *
     * @param name new name value
     */
    public void setName(String name) { this.name = name != null ? name.trim() : ""; }

    /**
     * Updates the character's job without validation.
     * <p>
     * The value is normalized to title case via {@link #normalizeJob(String)}.
     * Callers are expected to use {@link #isValidJob(String)} before assigning.
     *
     * @param job new job value
     */
    public void setJob(String job) { this.job = normalizeJob(job); }

    /**
     * Updates the character's level.
     * <p>
     * No range validation is performed here; callers should enforce 1–99 as needed.
     *
     * @param level new level value
     */
    public void setLevel(int level) { this.level = level; }

    /**
     * Updates the character's HP.
     * <p>
     * No validation is done here; callers should ensure non-negative values.
     *
     * @param hp new HP value
     */
    public void setHp(int hp) { this.hp = hp; }

    /**
     * Updates the character's MP.
     * <p>
     * No validation is done here; callers should ensure non-negative values.
     *
     * @param mp new MP value
     */
    public void setMp(int mp) { this.mp = mp; }

    /**
     * Updates the character's active flag.
     *
     * @param active {@code true} if the character is in the party
     */
    public void setActive(boolean active) { this.isActive = active; }

    /**
     * Returns a human-readable representation of the character including
     * ID, name, job, level, HP, MP and active flag.
     */
    @Override
    public String toString() {
        return String.format("%s (%s) - Job: %s, Lv: %d, HP: %d, MP: %d, Active: %b",
                id, name, job, level, hp, mp, isActive);
    }
}
