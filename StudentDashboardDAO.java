
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudentDashboardDAO {

    public static void showDashboard(Student student) {

        String requestSql = """
                SELECT
                    COUNT(*) AS total,
                    SUM(CASE WHEN status = 'OPEN'
                        THEN 1 ELSE 0 END) AS open_count,
                    SUM(CASE WHEN status = 'ASSIGNED'
                        THEN 1 ELSE 0 END) AS assigned_count,
                    SUM(CASE WHEN status = 'RESOLVED'
                        THEN 1 ELSE 0 END) AS resolved_count
                FROM help_requests
                WHERE roll = ?
                """;

        String helperSql = """
                SELECT
                    COUNT(*) AS helper_total,
                    SUM(CASE WHEN status = 'RESOLVED'
                        THEN 1 ELSE 0 END) AS helper_resolved
                FROM help_requests
                WHERE assigned_helper = ?
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement requestStmt = con.prepareStatement(requestSql);
                PreparedStatement helperStmt = con.prepareStatement(helperSql)) {

            requestStmt.setString(1, student.getRoll());
            helperStmt.setString(1, student.getRoll());

            System.out.println("\n===== STUDENT DASHBOARD =====");
            System.out.println("Student Name: " + student.getName());
            System.out.println("Roll Number: " + student.getRoll());

            System.out.println("\n----- MY HELP REQUESTS -----");

            try (ResultSet rs = requestStmt.executeQuery()) {

                if (rs.next()) {
                    System.out.println(
                            "Total Requests: " + rs.getInt("total"));

                    System.out.println(
                            "Open Requests: " + rs.getInt("open_count"));

                    System.out.println(
                            "Assigned Requests: " + rs.getInt("assigned_count"));

                    System.out.println(
                            "Resolved Requests: " + rs.getInt("resolved_count"));
                }
            }

            System.out.println("\n----- MY HELPER ACTIVITY -----");

            try (ResultSet rs = helperStmt.executeQuery()) {

                if (rs.next()) {
                    System.out.println(
                            "Assigned as Helper: " +
                                    rs.getInt("helper_total"));

                    System.out.println(
                            "Resolved as Helper: " +
                                    rs.getInt("helper_resolved"));
                }
            }

            ArrayList<String> skills = SkillDAO.getSkills(student.getRoll());

            System.out.println("\n----- MY SKILLS -----");
            System.out.println("Total Skills: " + skills.size());
            System.out.println("Skills: " + skills);

            System.out.println("--------------------------");

        } catch (SQLException e) {
            System.out.println(
                    "Dashboard Error: " + e.getMessage());
        }
    }
}
