package DBHelper;
import FFPackage.PCharacter;
import java.sql.*;
import java.util.ArrayList;

public class PCharacters {
    private final String DATABASE_PATH = "C:\\Users\\phael\\IdeaProjects\\DMSProject\\ffgame.db";
    private final String CONNECTION_STRING = "jdbc:sqlite:" + DATABASE_PATH;

    public PCharacters() {
        // Create table if not exists
        String sql = "CREATE TABLE IF NOT EXISTS characters (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "job TEXT NOT NULL, " +
                "level INTEGER NOT NULL, " +
                "hp REAL NOT NULL, " +
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

    public void insert(String id, String name, String job, int level, double hp, boolean isActive) {
        String sql = "INSERT INTO characters (id,name,job,level,hp,isActive) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, job);
            pstmt.setInt(4, level);
            pstmt.setDouble(5, hp);
            pstmt.setInt(6, isActive ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
                            rs.getDouble("hp"),
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
                        rs.getDouble("hp"),
                        rs.getInt("isActive") == 1
                );
                list.add(pc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
