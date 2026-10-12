
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class InputValidatorTest {

    @Test
    void testValidName() {
        assertTrue(InputValidator.isValidName("Aman"));
    }

    @Test
    void testEmptyName() {
        assertFalse(InputValidator.isValidName(""));
    }

    @Test
    void testSpacesOnlyName() {
        assertFalse(InputValidator.isValidName("   "));
    }

    @Test
    void testNullName() {
        assertFalse(InputValidator.isValidName(null));
    }

    @Test
    void testValidRoll() {
        assertTrue(InputValidator.isValidRoll("104"));
    }

    @Test
    void testEmptyRoll() {
        assertFalse(InputValidator.isValidRoll(""));
    }

    @Test
    void testValidStudent() {
        assertTrue(InputValidator.isValidStudent(
                "Rohan", "104", "MCA"));
    }

    @Test
    void testInvalidStudent() {
        assertFalse(InputValidator.isValidStudent(
                "", "104", "MCA"));
    }

    @Test
    void testEmptyTopic() {
        assertFalse(InputValidator.isValidTopic(""));
    }

    @Test
    void testEmptyDescription() {
        assertFalse(InputValidator.isValidDescription("   "));
    }

}
