
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class DeleteUser extends UserManager {
    private static final String DELETE_QUERY = "DELETE FROM customers WHERE id = ?";

    @Override
    public void performAction() {
        try (Scanner sc = new Scanner(System.in);
             Connection conn = getConnection()) {

            System.out.println("Enter the user ID to delete: ");
            int userId = sc.nextInt();

            if (!userExists(conn, userId)) {
                System.out.println("User with ID " + userId + " does not exist.");
                return;
            }

            deleteUser(conn, userId);
            System.out.println("User deleted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteUser(Connection conn, int userId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(DELETE_QUERY)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public static void execute() {
        DeleteUser deleteUser = new DeleteUser();
        deleteUser.performAction();
    }
}
