package DBHelper;

import FFPackage.PCharacter;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class PCharacters {
    private final String CONNECTION_STRING;

    // Default constructor - uses database in project root
    public PCharacters() {
        String projectRoot = System.getProperty("user.dir");
        String dbPath = projectRoot + File.separator + "ffgame.db";
        this.CONNECTION_STRING = "jdbc:sqlite:" + dbPath;
        initializeDatabase();

        // Optional: Print for debugging
        System.out.println("Using database: " + dbPath);
    }

    // Constructor for custom database path (used in tests)
    public PCharacters(String databasePath) {
        this.CONNECTION_STRING = "jdbc:sqlite:" + databasePath;
        initializeDatabase();
    }

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

    private void execute(String sql) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
            throw new RuntimeException("Failed to insert character: " + e.getMessage());
        }
    }

    public void update(String field, String value, String whereField, String whereValue) {
        String sql = "UPDATE characters SET " + field + "=? WHERE " + whereField + "=?";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            pstmt.setString(2, whereValue);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public boolean idExists(String id) {
        String sql = "SELECT id FROM characters WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public PCharacter selectById(String id) {
        String sql = "SELECT * FROM characters WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
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
        return null;
    }

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

    public void printById(String id) {
        String sql = "SELECT * FROM characters WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
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