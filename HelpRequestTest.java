
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class HelpRequestTest {

    @Test
    void testNewRequestIsOpen() {
        HelpRequest request = new HelpRequest(
                100, "103", "Java Basics",
                "Need help with Java");

        assertEquals("OPEN", request.getStatus());
    }

    @Test
    void testAcceptRequest() {
        HelpRequest request = new HelpRequest(
                100, "103", "Java Basics",
                "Need help with Java",
                "ASSIGNED", "104");

        request.acceptRequest();

        assertEquals("IN_PROGRESS", request.getStatus());
        assertEquals("104", request.getAssignedHelper());
    }

    @Test
    void testRejectRequest() {
        HelpRequest request = new HelpRequest(
                100, "103", "Java Basics",
                "Need help with Java",
                "ASSIGNED", "104");

        request.rejectRequest();

        assertEquals("OPEN", request.getStatus());
        assertNull(request.getAssignedHelper());
    }

    @Test
    void testResolveRequest() {
        HelpRequest request = new HelpRequest(
                100, "103", "Java Basics",
                "Need help with Java",
                "IN_PROGRESS", "104");

        request.resolveRequest();

        assertEquals("RESOLVED", request.getStatus());
    }

    @Test
    void testCannotAcceptResolvedRequest() {

        HelpRequest request = new HelpRequest(
                100, "103", "Java Basics",
                "Need help with Java",
                "RESOLVED", "104");

        request.acceptRequest();

        assertEquals("RESOLVED", request.getStatus());
    }

}
