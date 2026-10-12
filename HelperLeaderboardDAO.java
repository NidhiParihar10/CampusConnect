
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelperLeaderboardDAO {

    public static void showLeaderboard() {

        String sql = """
                SELECT
                    s.name,
                    s.roll,
                    COUNT(h.request_id) AS total_assigned,
                    SUM(CASE WHEN h.status = 'RESOLVED'
                        THEN 1 ELSE 0 END) AS total_resolved
                FROM students s
                LEFT JOIN help_requests h
                    ON s.roll = h.assigned_helper
                GROUP BY s.roll, s.name
                ORDER BY total_resolved DESC,
                         total_assigned DESC,
                         s.name ASC
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            System.out.println(
                    "\n===== TOP HELPERS LEADERBOARD =====");

            System.out.printf(
                    "%-6s %-15s %-8s %-10s %-10s%n",
                    "Rank", "Name", "Roll", "Assigned", "Resolved");

            int rank = 1;

            while (rs.next()) {

                System.out.printf(
                        "%-6d %-15s %-8s %-10d %-10d%n",
                        rank++,
                        rs.getString("name"),
                        rs.getString("roll"),
                        rs.getInt("total_assigned"),
                        rs.getInt("total_resolved"));
            }

        } catch (SQLException e) {
            System.out.println(
                    "Leaderboard Error: " + e.getMessage());
        }
    }
}
