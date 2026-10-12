
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {

        String url = "jdbc:sqlite:campusconnect.db";

        Connection con = DriverManager.getConnection(url);

        try (Statement stmt = con.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            con.close();
            throw e;
        }

        return con;
    }
}
