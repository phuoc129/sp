package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/grocery_store";
    private static final String USER = "root";
    private static final String PASSWORD = "123456"; // Thay password của bạn vào đây
    
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Kết nối database thành công!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy MySQL JDBC Driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database!");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đóng kết nối database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tạo bảng tự động nếu chưa có
    public static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            String createTableSQL = "CREATE TABLE IF NOT EXISTS products (" +
                    "id VARCHAR(50) PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "category VARCHAR(100) NOT NULL," +
                    "price DOUBLE NOT NULL," +
                    "quantity INT NOT NULL" +
                    ")";
            
            stmt.executeUpdate(createTableSQL);
            System.out.println("Khởi tạo bảng products thành công!");
            
        } catch (SQLException e) {
            System.err.println("Lỗi khởi tạo database!");
            e.printStackTrace();
        }
    }
}