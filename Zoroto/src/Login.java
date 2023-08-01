import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/zoroto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root123";

    private static final String SELECT_QUERY = "SELECT id FROM customers WHERE phone = ? AND password = ?";

    public static void main(String[] args) {
        // Sample user login data
        String phone = "1234567890";
        String password = "password123";

        // Call the login method to validate user credentials
        int userId = login(phone, password);

        if (userId != -1) {
            System.out.println("Login successful! User ID: " + userId);
        } else {
            System.out.println("Invalid login credentials.");
        }
    }

    public static int login(String phone, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(SELECT_QUERY)) {

            pstmt.setString(1, phone);
            pstmt.setString(2, password);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id"); // User ID found, login successful
                } else {
                    return -1; // User not found or invalid credentials
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions appropriately
            return -1;
        }
    }
}
