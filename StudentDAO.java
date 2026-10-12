
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class StudentDAO {

    public static boolean addStudent(Student student) {

        String sql = "INSERT INTO students (roll, name, course) VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, student.getRoll());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getCourse());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    public static ArrayList<Student> getAllStudents() {

        ArrayList<Student> students = new ArrayList<>();

        String sql = "SELECT roll, name, course FROM students";

        try (Connection con = DatabaseConnection.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                String roll = rs.getString("roll");
                String name = rs.getString("name");
                String course = rs.getString("course");

                Student student = new Student(name, roll, course);
                students.add(student);
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }

        return students;
    }

    public static boolean registerStudent(
            Student student, String password) {

        String sql = """
                INSERT INTO students
                (roll, name, course, password_hash, password_salt)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            String salt = PasswordHasher.generateSalt();
            String hash = PasswordHasher.hashPassword(password, salt);

            pstmt.setString(1, student.getRoll());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getCourse());
            pstmt.setString(4, hash);
            pstmt.setString(5, salt);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

}
