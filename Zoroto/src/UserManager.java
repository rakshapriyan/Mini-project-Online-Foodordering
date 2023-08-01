import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public abstract class UserManager {
    protected static final String DB_URL = "jdbc:mysql://localhost:3306/zoroto";
    protected static final String DB_USER = "root";
    protected static final String DB_PASSWORD = "root123";

    protected static final String SELECT_USER_QUERY = "SELECT * FROM customers WHERE id = ?";

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    protected Customer findUserById(Connection conn, int userId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SELECT_USER_QUERY)) {
            pstmt.setInt(1, userId);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                return new Customer(
                    resultSet.getString("name"),
                    resultSet.getString("phone"),
                    resultSet.getString("password")
                );
            }
        }
        return null;
    }

    protected boolean userExists(Connection conn, int userId) throws SQLException {
        return findUserById(conn, userId) != null;
    }

    // Abstract methods that must be implemented by subclasses
    public abstract void performAction();
}
