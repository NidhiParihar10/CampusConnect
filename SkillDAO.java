
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SkillDAO {

    // Save student skill in SQLite
    public static boolean addSkill(String roll, String skill) {

        skill = skill.trim().toLowerCase();

        if (skill.isEmpty()) {
            return false;
        }

        String sql = """
                INSERT INTO student_skills (roll, skill)
                VALUES (?, ?)
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, roll);
            pstmt.setString(2, skill);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                System.out.println("Skill already exists or violates database constraints!");
            } else {
                System.out.println("Database Error: " + e.getMessage());
            }
            return false;
        }

    }

    // Load saved skills of a student
    public static ArrayList<String> getSkills(String roll) {

        ArrayList<String> skills = new ArrayList<>();

        String sql = """
                SELECT skill FROM student_skills
                WHERE roll = ?
                ORDER BY skill
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, roll);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    skills.add(rs.getString("skill"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }

        return skills;
    }
}
