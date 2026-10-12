
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UpdateAuthDatabase {

    public static void main(String[] args) {

        try (Connection con = DatabaseConnection.getConnection();
                Statement stmt = con.createStatement()) {

            boolean hashExists = false;
            boolean saltExists = false;

            // Check existing columns
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(students)")) {

                while (rs.next()) {
                    String column = rs.getString("name");

                    if (column.equalsIgnoreCase("password_hash")) {
                        hashExists = true;
                    }

                    if (column.equalsIgnoreCase("password_salt")) {
                        saltExists = true;
                    }
                }
            }

            // Add password hash column
            if (!hashExists) {
                stmt.executeUpdate(
                        "ALTER TABLE students ADD COLUMN password_hash TEXT");
            }

            // Add password salt column
            if (!saltExists) {
                stmt.executeUpdate(
                        "ALTER TABLE students ADD COLUMN password_salt TEXT");
            }

            System.out.println(
                    "Authentication Columns Ready Successfully!");

        } catch (SQLException e) {
            System.out.println(
                    "Database Error: " + e.getMessage());
        }
    }
}
