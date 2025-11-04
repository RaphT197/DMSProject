package java.FFPackage;

import FFPackage.FF;
import FFPackage.PCharacter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PCharacterTest {

    FF ff = new FF();

    @Test
    public void testValidCharacterCreation() {
        // Arrange & Act
        PCharacter character = new PCharacter("1234", "Cloud", "Warrior", 50, 9999,9999, true);

        // Assert
        assertEquals("1234", character.getId());
        assertEquals("Cloud", character.getName());
        assertEquals("Warrior", character.getJob());
        assertEquals(50, character.getLevel());
        assertEquals(9999, character.getHp());
        assertTrue(character.isActive());
        ff.singleCharacterDisplay("1234");
    }

    @Test
    public void testInvalidJobThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new PCharacter("3333", "Cloud", "InvalidJob", 50, 9999, 999,true);
        });
    }

    @Test
    public void testJobNormalization() {
        // Test case-insensitive job matching
        PCharacter character = new PCharacter("5555", "Cloud", "warrior", 50, 9999, 999,true);
        assertEquals("Warrior", character.getJob()); // Should be capitalized
        ff.singleCharacterDisplay("5555");
    }

    @Test
    public void testLevelBoundaries() {
        // Test level too low
        assertThrows(IllegalArgumentException.class, () -> {
            new PCharacter("1234", "Cloud", "Warrior", 0, 9999,9999, true);
        });

        // Test level too high
        assertThrows(IllegalArgumentException.class, () -> {
            new PCharacter("1234", "Cloud", "Warrior", 100, 9999,9999, true);
        });

        // Test valid boundaries
        PCharacter min = new PCharacter("3214", "Cloud", "Warrior", 1, 9999,9999, true);
        assertEquals(1, min.getLevel());
        ff.singleCharacterDisplay("3214");

        PCharacter max = new PCharacter("5678", "Tifa", "Monk", 99, 9999, 999,true);
        assertEquals(99, max.getLevel());
    }

    @Test
    public void testNegativeHpThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PCharacter("9889", "Cloud", "Warrior", 50, -100, -1000,true);
        });
    }

    @Test
    public void testIdGeneration() {
        // Test auto-generated ID when empty string provided
        PCharacter character = new PCharacter("", "Cloud", "Warrior", 50, 9999, 9999,true);

        assertNotNull(character.getId());
        assertEquals(4, character.getId().length()); // Should be 4 digits
        assertTrue(character.getId().matches("\\d{4}")); // Should be all digits
    }

    @Test
    public void testIsValidJob() {
        assertTrue(PCharacter.isValidJob("Warrior"));
        assertTrue(PCharacter.isValidJob("warrior")); // Case insensitive
        assertTrue(PCharacter.isValidJob("White Mage"));
        assertFalse(PCharacter.isValidJob("InvalidJob"));
        assertFalse(PCharacter.isValidJob(""));

    }
}