
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class StudentTest {

    @Test
    void testStudentName() {
        Student student = new Student("Rohan", "104", "MCA");
        assertEquals("Rohan", student.getName());
    }

    @Test
    void testStudentRollNumber() {
        Student student = new Student("Rohan", "104", "MCA");
        assertEquals("104", student.getRoll());
    }

    @Test
    void testStudentCourse() {
        Student student = new Student("Rohan", "104", "MCA");
        assertEquals("MCA", student.getCourse());
    }
}
