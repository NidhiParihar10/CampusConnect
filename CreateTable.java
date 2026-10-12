
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class CreateTable {

    public static void main(String[] args) {

        String studentsSql = """
                CREATE TABLE IF NOT EXISTS students (
                    roll TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    course TEXT NOT NULL
                )
                """;

        String requestsSql = """
                CREATE TABLE IF NOT EXISTS help_requests (
                    request_id INTEGER PRIMARY KEY,
                    roll TEXT NOT NULL,
                    topic TEXT NOT NULL,
                    description TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'OPEN',
                    FOREIGN KEY (roll) REFERENCES students(roll)
                )
                """;

        String skillsSql = """
                CREATE TABLE IF NOT EXISTS student_skills (
                    roll TEXT NOT NULL,
                    skill TEXT NOT NULL COLLATE NOCASE,
                    PRIMARY KEY (roll, skill),
                    FOREIGN KEY (roll) REFERENCES students(roll)
                )
                """;

        try (Connection con = DatabaseConnection.getConnection();
                Statement stmt = con.createStatement()) {

            stmt.executeUpdate(studentsSql);
            stmt.executeUpdate(requestsSql);
            stmt.executeUpdate(skillsSql);

            System.out.println("All Three Tables Created Successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
