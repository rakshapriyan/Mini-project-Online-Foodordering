import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OrderFood {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/zoroto";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root123";

    private static final String SELECT_CATEGORIES_QUERY = "SELECT category_id, category_name FROM categories";
    private static final String SELECT_FOODS_BY_CATEGORY_FILTER_QUERY =
            "SELECT food_id, food_name, price, quantity FROM foods WHERE category_id = ? AND price BETWEEN ? AND ? AND quantity >= ?";
    private static final String INSERT_ORDER_QUERY = "INSERT INTO orders (food_id, quantity) VALUES (?, ?)";
    private static final String UPDATE_FOOD_QUANTITY_QUERY = "UPDATE foods SET quantity = quantity - ? WHERE food_id = ?";

    private static final double DISCOUNT_THRESHOLD = 1000.0;
    private static final double DISCOUNT_PERCENTAGE = 0.03;

    public static void Order() {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            displayCategories(conn);

            Map<Integer, Integer> ordersMap = new HashMap<>();

            while (true) {
                System.out.print("Enter the category ID to view its foods (0 to finish): ");
                int categoryId = scanner.nextInt();

                if (categoryId == 0) {
                    break;
                }

                System.out.print("Enter the minimum price (INR) to filter: ");
                double minPrice = scanner.nextDouble();

                System.out.print("Enter the maximum price (INR) to filter: ");
                double maxPrice = scanner.nextDouble();

                System.out.print("Enter the minimum quantity to filter: ");
                int minQuantity = scanner.nextInt();

                displayFoodsByCategoryWithFilter(conn, categoryId, minPrice, maxPrice, minQuantity);

                System.out.print("Enter the food ID to order: ");
                int foodId = scanner.nextInt();

                System.out.print("Enter the quantity to order: ");
                int quantity = scanner.nextInt();

                ordersMap.put(foodId, quantity);
            }

            if (orderFoods(conn, ordersMap)) {
                System.out.println("All orders placed successfully!");

                // Calculate and display the total price
                double totalPrice = calculateTotalPrice(conn, ordersMap);
                System.out.println("Total Price: INR " + totalPrice);

                // Apply discount if the order price is more than 1000
                if (totalPrice > DISCOUNT_THRESHOLD) {
                    double discountAmount = totalPrice * DISCOUNT_PERCENTAGE;
                    totalPrice -= discountAmount;
                    System.out.println("Discount Applied: INR " + discountAmount);
                }

                System.out.println("Final Price: INR " + totalPrice);
            } else {
                System.out.println("Failed to place some orders.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayCategories(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SELECT_CATEGORIES_QUERY);
             ResultSet resultSet = pstmt.executeQuery()) {

            System.out.println("Available Categories:");
            System.out.println("--------------------");
            while (resultSet.next()) {
                int categoryId = resultSet.getInt("category_id");
                String categoryName = resultSet.getString("category_name");
                System.out.println(categoryId + ". " + categoryName);
            }
            System.out.println("--------------------");
        }
    }

    private static void displayFoodsByCategoryWithFilter(Connection conn, int categoryId, double minPrice, double maxPrice, int minQuantity) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SELECT_FOODS_BY_CATEGORY_FILTER_QUERY)) {
            pstmt.setInt(1, categoryId);
            pstmt.setDouble(2, minPrice);
            pstmt.setDouble(3, maxPrice);
            pstmt.setInt(4, minQuantity);
            try (ResultSet resultSet = pstmt.executeQuery()) {

                System.out.println("Foods in the selected category with filters:");
                System.out.println("--------------------");
                while (resultSet.next()) {
                    int foodId = resultSet.getInt("food_id");
                    String foodName = resultSet.getString("food_name");
                    double price = resultSet.getDouble("price");
                    int quantity = resultSet.getInt("quantity");
                    System.out.println("Food ID: " + foodId + ", Food: " + foodName + ", Price: " + price + ", Quantity: " + quantity);
                }
                System.out.println("--------------------");
            }
        }
    }

    private static boolean orderFoods(Connection conn, Map<Integer, Integer> ordersMap) throws SQLException {
        try {
            conn.setAutoCommit(false);

            for (Map.Entry<Integer, Integer> entry : ordersMap.entrySet()) {
                int foodId = entry.getKey();
                int quantity = entry.getValue();

                // Check if the food exists and has enough quantity
                int availableQuantity = getAvailableFoodQuantity(conn, foodId);
                if (availableQuantity < quantity) {
                    System.out.println("Insufficient quantity available for the selected food (ID: " + foodId + ").");
                    return false;
                }

                // Update the food quantity and insert the order details
                try (PreparedStatement updatePstmt = conn.prepareStatement(UPDATE_FOOD_QUANTITY_QUERY);
                     PreparedStatement insertPstmt = conn.prepareStatement(INSERT_ORDER_QUERY)) {

                    updatePstmt.setInt(1, quantity);
                    updatePstmt.setInt(2, foodId);
                    updatePstmt.executeUpdate();

                    insertPstmt.setInt(1, foodId);
                    insertPstmt.setInt(2, quantity);
                    insertPstmt.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private static double calculateTotalPrice(Connection conn, Map<Integer, Integer> ordersMap) throws SQLException {
        double totalPrice = 0.0;

        for (Map.Entry<Integer, Integer> entry : ordersMap.entrySet()) {
            int foodId = entry.getKey();
            int quantity = entry.getValue();

            // Fetch the price of the food item from the database
            double price = getFoodPrice(conn, foodId);

            // Calculate the subtotal for the current food item and add it to the total price
            double subtotal = price * quantity;
            totalPrice += subtotal;
        }

        return totalPrice;
    }

    private static double getFoodPrice(Connection conn, int foodId) throws SQLException {
        String selectPriceQuery = "SELECT price FROM foods WHERE food_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(selectPriceQuery)) {
            pstmt.setInt(1, foodId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("price");
                } else {
                    throw new SQLException("Food not found.");
                }
            }
        }
    }
    
    private static int getAvailableFoodQuantity(Connection conn, int foodId) throws SQLException {
        String selectQuantityQuery = "SELECT quantity FROM foods WHERE food_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(selectQuantityQuery)) {
            pstmt.setInt(1, foodId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("quantity");
                } else {
                    throw new SQLException("Food not found.");
                }
            }
        }
    }
}
