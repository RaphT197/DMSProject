package DBHelper;

import FFPackage.PCharacter;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PCharactersTest {

    private PCharacters db;

    // Real file path only — NOT a jdbc:sqlite: URL
    private static final String TEST_DB_PATH =
            System.getProperty("user.dir") + File.separator + "ffgame-test.db";

    @BeforeEach
    public void setUp() {
        db = new PCharacters(TEST_DB_PATH);
        cleanupTestData();
    }

    @AfterEach
    public void tearDown() {
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
                db.delete("id", id);
            } catch (Exception e) {
                // Ignore if the row does not exist
            }
        }
    }

    @Test
    public void testInsertAndSelect() {
        String id = "2222";
        String name = "Cloud";
        String job = "Warrior";
        int level = 50;
        int hp = 9999;
        int mp = 9999;
        boolean isActive = true;

        db.insert(id, name, job, level, hp, mp, isActive);
        PCharacter retrieved = db.selectById(id);

        assertNotNull(retrieved, "Character should be retrieved");
        assertEquals(id, retrieved.getId());
        assertEquals(name, retrieved.getName());
        assertEquals(job, retrieved.getJob());
        assertEquals(level, retrieved.getLevel());
        assertEquals(hp, retrieved.getHp());
        assertEquals(mp, retrieved.getMp());
        assertEquals(isActive, retrieved.isActive());

        System.out.println("Inserted and retrieved: " + retrieved);
    }

    @Test
    public void testIdExists() {
        String id = "1111";
        db.insert(id, "Cloud", "Warrior", 50, 9999, 9999, true);

        assertTrue(db.idExists(id), "ID should exist after insert");
        assertFalse(db.idExists("9999"), "Non-existent ID should return false");
    }

    @Test
    public void testUpdate() {
        String id = "1111";
        db.insert(id, "Cloud", "Warrior", 50, 9999, 9999, true);

        db.update("name", "Cloud Strife", "id", id);
        PCharacter updated = db.selectById(id);

        assertNotNull(updated, "Character should still exist after update");
        assertEquals("Cloud Strife", updated.getName(), "Name should be updated");
    }

    @Test
    public void testDelete() {
        String id = "1111";
        db.insert(id, "Cloud", "Warrior", 50, 9999, 9999, true);
        assertTrue(db.idExists(id), "Character should exist before delete");

        String id2 = "1234";
        db.insert(id2, "Tifa", "Monk", 48, 8800, 5500, true);
        assertTrue(db.idExists(id2), "Second character should exist before delete");

        db.delete("id", id);
        db.delete("id", id2);

        assertFalse(db.idExists(id), "Character should not exist after delete");
        assertNull(db.selectById(id), "selectById should return null after delete");

        assertFalse(db.idExists(id2), "Second character should not exist after delete");
        assertNull(db.selectById(id2), "selectById should return null after delete");
    }

    @Test
    public void testSelectAll() {
        db.insert("1234", "Cloud", "Warrior", 50, 9999, 9500, true);
        db.insert("3333", "Tifa", "Monk", 48, 8800, 5500, true);
        db.insert("5555", "Barrett", "Dragoon", 48, 8800, 5500, true);

        ArrayList<PCharacter> all = db.selectAll();

        assertTrue(all.size() >= 3, "Should have at least 3 characters");

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

        System.out.println("Total characters in test DB: " + all.size());
        db.printById("1234");
        db.printById("3333");
        db.printById("5555");
    }

    @Test
    public void testSelectByIdReturnsNullForNonExistent() {
        PCharacter result = db.selectById("9999");
        assertNull(result, "Should return null for non-existent ID");
    }

    @Test
    public void testPrintById() {
        String id = "5555";
        db.insert(id, "Barrett", "Dragoon", 99, 125421, 6000, false);

        System.out.println("\n=== Testing printById ===");
        db.printById(id);
        System.out.println("=========================\n");

        assertTrue(db.idExists(id), "Character should exist");
    }
}