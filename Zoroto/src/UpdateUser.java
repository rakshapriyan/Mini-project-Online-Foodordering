import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class UpdateUser extends UserManager {
    private static final String UPDATE_QUERY = "UPDATE customers SET name = ?, phone = ?, password = ? WHERE id = ?";

    @Override
    public void performAction() {
        try (Scanner sc = new Scanner(System.in);
             Connection conn = getConnection()) {

            System.out.println("Enter the user ID to update: ");
            int userId = sc.nextInt();

            if (!userExists(conn, userId)) {
                System.out.println("User with ID " + userId + " does not exist.");
                return;
            }

            System.out.println("Enter the new name: ");
            String name = sc.next();
            System.out.println("Enter the new phone number: ");
            String phone = sc.next();
            System.out.println("Enter the new password: ");
            String password = sc.next();

            updateUser(conn, name, phone, password, userId);
            System.out.println("User information updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUser(Connection conn, String name, String phone, String password, int userId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_QUERY)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, password);
            pstmt.setInt(4, userId);

            pstmt.executeUpdate();
        }
    }

    public static void execute() {
        UpdateUser updateUser = new UpdateUser();
        updateUser.performAction();
    }
}
