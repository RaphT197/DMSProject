package DBHelper;

import FFPackage.PCharacter;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;

/**
 * Data access object (DAO) for {@link PCharacter} records stored in a SQLite database.
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Creating the {@code characters} table (if it doesn't exist)</li>
 *     <li>Inserting, updating, deleting character rows</li>
 *     <li>Querying characters by ID or retrieving all characters</li>
 * </ul>
 */
public class PCharacters {

    /**
     * JDBC connection string pointing to the SQLite database file.
     * <p>
     * Example: {@code jdbc:sqlite:C:\path\to\project\ffgame.db}
     */
    private final String CONNECTION_STRING;

    /**
     * Default constructor – uses a SQLite database named {@code ffgame.db}
     * in the project root directory.
     */
    public PCharacters() {
        String projectRoot = System.getProperty("user.dir");
        String dbPath = projectRoot + File.separator + "ffgame.db";
        this.CONNECTION_STRING = "jdbc:sqlite:" + dbPath;
        initializeDatabase();

        // Optional debug output so it's clear which DB file is being used
        System.out.println("Using database: " + dbPath);
    }

    /**
     * Constructor that allows passing a custom database path.
     * <p>
     * This is useful for tests or when you want to use a different DB file
     * than the default one in the project root.
     *
     * @param databasePath full path to the SQLite database file
     */
    public PCharacters(String databasePath) {
        this.CONNECTION_STRING = "jdbc:sqlite:" + databasePath;
        initializeDatabase();
    }

    /**
     * Ensures that the {@code characters} table exists in the database.
     * <p>
     * If the table does not exist, it will be created. If it already exists,
     * this method has no effect.
     */
    public void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS characters (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "job TEXT NOT NULL, " +
                "level INTEGER NOT NULL, " +
                "hp INTEGER NOT NULL, " +
                "mp INTEGER NOT NULL, " +
                "isActive INTEGER NOT NULL)";
        execute(sql);
    }

    /**
     * Executes a SQL statement that does not return a result set
     * (e.g. CREATE TABLE, ALTER TABLE).
     *
     * @param sql the SQL string to execute
     */
    private void execute(String sql) {
        // Try-with-resources so Connection and Statement are automatically closed
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            // For infrastructure-style methods, logging the stack trace is usually enough
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new character row into the {@code characters} table.
     *
     * @param id       unique identifier for the character
     * @param name     character name
     * @param job      character job (e.g. "Warrior", "Mage")
     * @param level    character level
     * @param hp       hit points
     * @param mp       magic points
     * @param isActive whether this character is currently active
     * @throws RuntimeException if the insert fails for any reason
     */
    public void insert(String id, String name, String job, int level, int hp, int mp, boolean isActive) {
        String sql = "INSERT INTO characters (id,name,job,level,hp,mp,isActive) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, job);
            pstmt.setInt(4, level);
            pstmt.setInt(5, hp);
            pstmt.setInt(6, mp);
            pstmt.setInt(7, isActive ? 1 : 0);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Bubble up as unchecked so callers can choose whether to catch it
            throw new RuntimeException("Failed to insert character: " + e.getMessage());
        }
    }

    /**
     * Updates a single column in the {@code characters} table for rows matching the given condition.
     * <p>
     * Note: {@code field} and {@code whereField} are interpolated directly into the SQL,
     * so they should only come from trusted sources (e.g. constants, not user input).
     * Values are still parameterized.
     *
     * @param field      name of the column to update
     * @param value      new value to set
     * @param whereField column used in the WHERE clause
     * @param whereValue value used in the WHERE clause
     */
    public void update(String field, String value, String whereField, String whereValue) {
        String sql = "UPDATE characters SET " + field + "=? WHERE " + whereField + "=?";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, value);
            pstmt.setString(2, whereValue);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // In a real app you might want to log and/or propagate this
        }
    }

    /**
     * Deletes rows from the {@code characters} table where the given column
     * matches the specified value.
     * <p>
     * As with {@link #update(String, String, String, String)}, {@code whereField}
     * should come from a trusted source to avoid SQL injection.
     *
     * @param whereField the column name to use in the WHERE clause
     * @param whereValue the value to match for deletion
     */
    public void delete(String whereField, String whereValue) {
        String sql = "DELETE FROM characters WHERE " + whereField + "=?";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, whereValue);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether a character with the given ID exists in the database.
     *
     * @param id the character ID to look for
     * @return {@code true} if a row with that ID exists, {@code false} otherwise
     */
    public boolean idExists(String id) {
        String sql = "SELECT id FROM characters WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                // rs.next() will be true if at least one row is returned
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Retrieves a single {@link PCharacter} from the database by ID.
     *
     * @param id the ID of the character to fetch
     * @return a {@link PCharacter} instance if found, or {@code null} if not found
     */
    public PCharacter selectById(String id) {
        String sql = "SELECT * FROM characters WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Build the domain object directly from the result set
                    return new PCharacter(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("job"),
                            rs.getInt("level"),
                            rs.getInt("hp"),
                            rs.getInt("mp"),
                            rs.getInt("isActive") == 1
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Null here means "not found" or "error" – caller should handle accordingly
        return null;
    }

    /**
     * Retrieves all characters from the {@code characters} table.
     *
     * @return a list of all {@link PCharacter} instances; never {@code null}
     */
    public ArrayList<PCharacter> selectAll() {
        ArrayList<PCharacter> list = new ArrayList<>();
        String sql = "SELECT * FROM characters";

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PCharacter pc = new PCharacter(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("job"),
                        rs.getInt("level"),
                        rs.getInt("hp"),
                        rs.getInt("mp"),
                        rs.getInt("isActive") == 1
                );
                list.add(pc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Convenience method that prints a character (or a "not found" message)
     * to {@link System#out} based on its ID.
     * <p>
     * This is mainly helpful for quick debugging or CLI-style tools.
     *
     * @param id the ID of the character to print
     */
    public void printById(String id) {
        String sql = "SELECT * FROM characters WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Relies on PCharacter.toString() for human-readable output
                    System.out.println(new PCharacter(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("job"),
                            rs.getInt("level"),
                            rs.getInt("hp"),
                            rs.getInt("mp"),
                            rs.getInt("isActive") == 1
                    ));
                } else {
                    System.out.println("No character found with ID: " + id);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
