import java.sql.*;

public class CheckSkillsTable {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");

        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlite:campusconnect.db");
             Statement statement = connection.createStatement();
             ResultSet tables = statement.executeQuery(
                "SELECT sql FROM sqlite_master WHERE type='table' AND name='student_skills'")) {

            if (tables.next()) {
                System.out.println("TABLE FOUND:");
                System.out.println(tables.getString("sql"));
            } else {
                System.out.println("TABLE NOT FOUND");
            }
        }
    }
}
