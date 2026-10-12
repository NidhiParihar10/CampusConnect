
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class HelpRequestDAO {

    // 1. Create a new Help Request
    public static int addRequest(
            String roll, String topic, String description) {

        String sql = """
                INSERT INTO help_requests
                (roll, topic, description, status)
                VALUES (?, ?, ?, 'OPEN')
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, roll);
            pstmt.setString(2, topic);
            pstmt.setString(3, description);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                try (Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(
                                "SELECT last_insert_rowid()")) {

                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }

        return -1;
    }

    // 2. Retrieve all saved requests
    public static ArrayList<HelpRequest> getAllRequests() {

        ArrayList<HelpRequest> requests = new ArrayList<>();

        String sql = """
                SELECT request_id, roll, topic, description,
                       status, assigned_helper
                FROM help_requests
                ORDER BY request_id
                """;

        try (Connection con = DatabaseConnection.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                int id = rs.getInt("request_id");
                String roll = rs.getString("roll");
                String topic = rs.getString("topic");
                String description = rs.getString("description");
                String status = rs.getString("status");
                String helperRoll = rs.getString("assigned_helper");

                HelpRequest request = new HelpRequest(
                        id, roll, topic, description,
                        status, helperRoll);

                requests.add(request);
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }

        return requests;
    }

    // 3. Resolve an OPEN or ASSIGNED request
    public static boolean resolveRequest(int requestId) {

        String sql = """
                UPDATE help_requests
                SET status = 'RESOLVED'
                WHERE request_id = ?
                 AND status IN ('OPEN', 'IN_PROGRESS')
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, requestId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    // 4. Update an OPEN request
    public static boolean updateRequest(
            int requestId, String newTopic,
            String newDescription) {

        String sql = """
                UPDATE help_requests
                SET topic = ?, description = ?
                WHERE request_id = ?
                  AND status = 'OPEN'
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, newTopic);
            pstmt.setString(2, newDescription);
            pstmt.setInt(3, requestId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    // 5. Delete a request
    public static boolean deleteRequest(
            int requestId, String roll) {

        String sql = """
                DELETE FROM help_requests
                WHERE request_id = ? AND roll = ?
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, requestId);
            pstmt.setString(2, roll);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    // 6. Assign a helper to an OPEN request
    public static boolean assignHelper(
            int requestId, String helperRoll) {

        String sql = """
                UPDATE help_requests
                SET assigned_helper = ?,
                    status = 'ASSIGNED'
                WHERE request_id = ?
                  AND status = 'OPEN'
                  AND roll <> ?
                  AND EXISTS (
                      SELECT 1 FROM students
                      WHERE roll = ?
                  )
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, helperRoll);
            pstmt.setInt(2, requestId);
            pstmt.setString(3, helperRoll);
            pstmt.setString(4, helperRoll);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean acceptRequest(int requestId, String helperRoll) {

        String sql = """
                UPDATE help_requests
                SET status = 'IN_PROGRESS',
                    accepted_at = CURRENT_TIMESTAMP
                WHERE request_id = ?
                  AND assigned_helper = ?
                  AND status = 'ASSIGNED'
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, requestId);
            pstmt.setString(2, helperRoll);

            int rows = pstmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean rejectRequest(int requestId, String helperRoll) {

        String sql = """
                UPDATE help_requests
                SET status = 'OPEN',
                    assigned_helper = NULL,
                    accepted_at = NULL
                WHERE request_id = ?
                  AND assigned_helper = ?
                  AND status = 'ASSIGNED'
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, requestId);
            pstmt.setString(2, helperRoll);

            int rows = pstmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    public static java.util.ArrayList<HelpRequest> searchByTopic(
            String keyword) {

        java.util.ArrayList<HelpRequest> results = new java.util.ArrayList<>();

        String sql = """
                SELECT *
                FROM help_requests
                WHERE LOWER(topic) LIKE LOWER(?)
                ORDER BY request_id DESC
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    HelpRequest request = new HelpRequest(
                            rs.getInt("request_id"),
                            rs.getString("roll"),
                            rs.getString("topic"),
                            rs.getString("description"),
                            rs.getString("status"),
                            rs.getString("assigned_helper"));

                    results.add(request);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Search Error: " + e.getMessage());
        }

        return results;
    }

    public static java.util.ArrayList<HelpRequest> filterByStatus(
            String status) {

        java.util.ArrayList<HelpRequest> results = new java.util.ArrayList<>();

        String sql = """
                SELECT *
                FROM help_requests
                WHERE status = ?
                ORDER BY request_id DESC
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, status.toUpperCase());

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    HelpRequest request = new HelpRequest(
                            rs.getInt("request_id"),
                            rs.getString("roll"),
                            rs.getString("topic"),
                            rs.getString("description"),
                            rs.getString("status"),
                            rs.getString("assigned_helper"));

                    results.add(request);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Filter Error: " + e.getMessage());
        }

        return results;
    }

    public static java.util.ArrayList<HelpRequest> searchByTopicAndStatus(
            String keyword, String status) {

        java.util.ArrayList<HelpRequest> results = new java.util.ArrayList<>();

        String sql = """
                SELECT *
                FROM help_requests
                WHERE LOWER(topic) LIKE LOWER(?)
                  AND status = ?
                ORDER BY request_id DESC
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, status.trim().toUpperCase());

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    HelpRequest request = new HelpRequest(
                            rs.getInt("request_id"),
                            rs.getString("roll"),
                            rs.getString("topic"),
                            rs.getString("description"),
                            rs.getString("status"),
                            rs.getString("assigned_helper"));

                    results.add(request);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Combined Search Error: " + e.getMessage());
        }

        return results;
    }

}
