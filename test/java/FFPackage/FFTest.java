package FFPackage;

import DBHelper.PCharacters;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class FFTest {

    private FF ff;

    @BeforeEach
    public void setUp() {
        // You'll need to modify FF to accept a test database
        ff = new FF();

        // Add a test character
        PCharacter testChar = new PCharacter("1234", "Cloud", "Warrior", 50, 9999, true);
        PCharacter testChar2 = new PCharacter("9632", "Rukki", "Dragoon", 22, 100, true);
        PCharacter testChar3 = new PCharacter("3232", "Tifa", "Warrior", 50, 9999, true);
        ff.addCharacter(testChar);
        ff.addCharacter(testChar2);
        ff.addCharacter(testChar3);
    }

    @Test
    public void testCharacterExists() {
        assertTrue(ff.characterExists("1234"));
        assertTrue(ff.characterExists("3232"));
        assertFalse(ff.characterExists("9999"));
    }

    @Test
    public void testUpdateCharacterName() {
        // Act
        ff.singleCharacterDisplay("1234");
        ff.updateCharacterName("1234", "Cloud Strife");
        ff.singleCharacterDisplay("1234");

        // Assert
    }

    @Test
    public void testLevelUpClampsAtMax() {
        // Arrange

        PCharacter highLevel = new PCharacter("5678", "Tifa", "Monk", 98, 9999, true);
        ff.addCharacter(highLevel);

        // Act
        ff.levelUpById("5678", 5); // Try to go to 103
        ff.singleCharacterDisplay("5678");

        // Assert - level should be clamped at 99

    }

    @Test
    public void testRemoveCharacter() {
        // Act
        ff.removeCharacterById("1234");

        // Assert
        assertFalse(ff.characterExists("1234"));
    }
}