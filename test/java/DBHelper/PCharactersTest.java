package DBHelper;

import FFPackage.PCharacter;
import org.junit.jupiter.api.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PCharactersTest {

    private static PCharacters db;
    private static final String TEST_DB_PATH = "test_ffgame.db";

    @BeforeEach
    public void setUp() {
        // Use the test database constructor!
        db = new PCharacters(TEST_DB_PATH);

        // Clean any existing test data before each test
        cleanupTestData();
    }

    @AfterEach
    public void tearDown() {
        //Delete the test database file after all tests
        File testDb = new File(TEST_DB_PATH);
        if (testDb.exists()) {
             testDb.delete();
         }
    }

    // Helper method to clean up known test IDs

    private void cleanupTestData() {
        String[] testIds = {"2222", "3333", "5555", "1234", "1111"};
        for (String id : testIds) {
            try {
//                System.out.println("Attempting to delete id " + id);
                db.delete("id", id);
                db.delete("id", "1111");
            }
            catch (Exception e) {
                //ignore might not exist
            }
        }
    }

    @Test
    public void testInsertAndSelect() {
        // Arrange
        String id = "2222";
        String name = "Cloud";
        String job = "Warrior";
        int level = 50;
        double hp = 9999;
        boolean isActive = true;

        // Act
        db.insert(id, name, job, level, hp, isActive);
        PCharacter retrieved = db.selectById(id);

        // Assert
        assertNotNull(retrieved, "Character should be retrieved");
        assertEquals(id, retrieved.getId());
        assertEquals(name, retrieved.getName());
        assertEquals(job, retrieved.getJob());
        assertEquals(level, retrieved.getLevel());
        assertEquals(hp, retrieved.getHp());
        assertEquals(isActive, retrieved.isActive());

        // Debug output
        System.out.println("Inserted and retrieved: " + retrieved);
    }

    @Test
    public void testIdExists() {
        // Arrange
        String id = "1111";
        db.insert(id, "Cloud", "Warrior", 50, 9999, true);

        // Act & Assert
        assertTrue(db.idExists(id), "ID should exist after insert");
        assertFalse(db.idExists("9999"), "Non-existent ID should return false");
    }

    @Test
    public void testUpdate() {
        // Arrange
        String id = "1111";
        db.insert(id, "Cloud", "Warrior", 50, 9999, true);

        // Act
        db.update("name", "Cloud Strife", "id", id);
        PCharacter updated = db.selectById(id);

        // Assert
        assertNotNull(updated, "Character should still exist after update");
        assertEquals("Cloud Strife", updated.getName(), "Name should be updated");
    }

    @Test
    public void testDelete() {
        // Arrange
        String id = "1111";
        db.insert(id, "Cloud", "Warrior", 50, 9999, true);
        assertTrue(db.idExists(id), "Character should exist before delete");

        String id2 = "1234";
        db.insert(id2, "Cloud", "Warrior", 50, 9999, true);
        assertTrue(db.idExists(id), "Character should exist before delete");

        // Act
        db.delete("id", id);
        db.delete("id", id2);

        // Assert
        assertFalse(db.idExists(id), "Character should not exist after delete");
        assertNull(db.selectById(id), "selectById should return null after delete");
        assertFalse(db.idExists(id2), "Character should not exist after delete");
        assertNull(db.selectById(id2), "selectById should return null after delete");
    }

    @Test
    public void testSelectAll() {
        // Arrange
        db.insert("1234", "Cloud", "Warrior", 50, 9999, true);
        db.insert("3333", "Tifa", "Monk", 48, 8800, true);
        db.insert("5555", "Barrett", "Dragoon", 48, 8800, true);

        // Act
        ArrayList<PCharacter> all = db.selectAll();

        // Assert
        assertTrue(all.size() >= 3, "Should have at least 2 characters");

        // Verify our test characters are in the list
        boolean foundCloud = false;
        boolean foundTifa = false;
        boolean foundBarrett = false;
        for (PCharacter c : all) {
            if (c.getId().equals("1234")) foundCloud = true;
            if (c.getId().equals("3333")) foundTifa = true;
            if (c.getId().equals("5555")) foundBarrett = true;
        }
        assertTrue(foundCloud, "Cloud should be in the list");
        assertTrue(foundTifa, "Tifa should be in the list");
        assertTrue(foundBarrett, "Barrett should be in the list");

        // Debug output
        int dbSize = all.size();
        System.out.println("Total characters in test DB: " + dbSize);
        db.printById("1234");
        db.printById("3333");
        db.printById("5555");
    }

    @Test
    public void testSelectByIdReturnsNullForNonExistent() {
        // Act
        PCharacter result = db.selectById("9999");

        // Assert
        assertNull(result, "Should return null for non-existent ID");
    }

    @Test
    public void testPrintById() {
        // Arrange
        String id = "5555";
        db.insert(id, "Barrett", "Dragoon", 99, 125421, false);

        //  this will print to console
        System.out.println("\n=== Testing printById ===");
        db.printById(id);
        System.out.println("=========================\n");

        // Just verify the character exists
        assertTrue(db.idExists(id), "Character should exist");
    }

    @AfterAll
    public static void testA() {
        db.delete("id", "1111");
    }
}