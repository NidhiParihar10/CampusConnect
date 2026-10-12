
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAO {

    public static boolean registerPassword(
            String roll, String password) {

        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hashPassword(password, salt);

        String sql = """
                UPDATE students
                SET password_hash = ?, password_salt = ?
                WHERE roll = ?
                AND password_hash IS NULL
                AND password_salt IS NULL
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, hash);
            pstmt.setString(2, salt);
            pstmt.setString(3, roll);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean login(String roll, String password) {

        String sql = """
                SELECT password_hash, password_salt
                FROM students
                WHERE roll = ?
                """;

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, roll);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    String hash = rs.getString("password_hash");
                    String salt = rs.getString("password_salt");

                    if (hash == null || salt == null) {
                        return false;
                    }

                    return PasswordHasher.verifyPassword(
                            password, salt, hash);
                }
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }

        return false;
    }
}
