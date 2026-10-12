
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateDatabase {

    public static void main(String[] args) {

        try (Connection con = DatabaseConnection.getConnection();
                Statement stmt = con.createStatement()) {

            boolean columnExists = false;

            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(help_requests)")) {

                while (rs.next()) {
                    if (rs.getString("name")
                            .equalsIgnoreCase("assigned_helper")) {
                        columnExists = true;
                        break;
                    }
                }
            }

            if (!columnExists) {
                stmt.executeUpdate(
                        "ALTER TABLE help_requests " +
                                "ADD COLUMN assigned_helper TEXT " +
                                "REFERENCES students(roll)");

                System.out.println(
                        "Assigned Helper Column Added Successfully!");
            } else {
                System.out.println(
                        "Assigned Helper Column Already Exists!");
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
}
