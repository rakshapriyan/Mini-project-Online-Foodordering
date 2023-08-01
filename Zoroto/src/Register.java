import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Register {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/zoroto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root123";

    private static final String INSERT_QUERY = "INSERT INTO customers (name, phone, password) VALUES (?, ?, ?)";

    public static void main(String[] args) {
        // Sample customer data
        Customer customer = new Customer("John Doe", "1234567890", "password123");

        // Call the signUpCustomer method to insert the customer data into the database
        signUpCustomer(customer);
    }

    public static void signUpCustomer(Customer customer) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(INSERT_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getPassword());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Customer signed up successfully!");

                // Retrieve the generated auto-incremented ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int customerId = generatedKeys.getInt(1);
                        System.out.println("Customer ID: " + customerId);
                    }
                }

            } else {
                System.out.println("Failed to sign up customer.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any exceptions appropriately
        }
    }
}
