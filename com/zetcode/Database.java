package com.zetcode;

import java.sql.*;

/**
 * Quản lý cơ sở dữ liệu SQLite cho Tetris Game.
 * Hỗ trợ lưu trữ điểm riêng biệt cho các chế độ chơi khác nhau.
 */
public class Database {
    private static final String URL = "jdbc:sqlite:users.db";
    private static boolean initialized = false;
    
    // Các hằng số cho chế độ chơi
    public static final String SINGLE_MODE = "single";
    public static final String SPEED_MODE = "speed";

    /**
     * Kết nối đến database SQLite và khởi tạo nếu cần.
     */
    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(URL);
            
            if (!initialized) {
                createUsersTable(conn);
                migrateScoreData(conn); // Chuyển đổi dữ liệu điểm nếu cần
                initialized = true;
            }
            
            return conn;
        } catch (Exception e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
            return null;
        }
    }

    /**
     * Tạo bảng users nếu chưa tồn tại
     */
    private static void createUsersTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "email TEXT," +
                "highscore INTEGER DEFAULT 0," +
                "single_mode_score INTEGER DEFAULT 0," +
                "speed_mode_score INTEGER DEFAULT 0);";
    
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Lỗi tạo bảng: " + e.getMessage());
        }
    }
    
    /**
     * Chuyển dữ liệu highscore hiện có sang single_mode_score
     */
    private static void migrateScoreData(Connection conn) {
        try {
            // Kiểm tra xem cột single_mode_score đã tồn tại chưa
            DatabaseMetaData meta = conn.getMetaData();
            boolean hasNewColumns = false;
            
            try (ResultSet rs = meta.getColumns(null, null, "users", "single_mode_score")) {
                hasNewColumns = rs.next(); // Nếu có kết quả, nghĩa là cột đã tồn tại
            }
            
            if (!hasNewColumns) {
                // Nếu các cột mới chưa tồn tại, thêm chúng vào
                String alterTableSql1 = "ALTER TABLE users ADD COLUMN single_mode_score INTEGER DEFAULT 0";
                String alterTableSql2 = "ALTER TABLE users ADD COLUMN speed_mode_score INTEGER DEFAULT 0";
                
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(alterTableSql1);
                    stmt.execute(alterTableSql2);
                    
                    // Chuyển dữ liệu từ highscore sang single_mode_score
                    String migrateSql = "UPDATE users SET single_mode_score = highscore WHERE single_mode_score = 0";
                    stmt.execute(migrateSql);
                    
                    System.out.println("Đã di chuyển dữ liệu điểm số sang cấu trúc mới.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi di chuyển dữ liệu điểm số: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra xem username đã tồn tại chưa
     */
    public static boolean usernameExists(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra username: " + e.getMessage());
            return false;
        }
    }

    /**
     * Thêm người dùng mới
     */
    public static boolean addUser(String username, String password, String email) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }
        
        if (usernameExists(username)) return false;
        
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy mật khẩu hiện tại (cho chức năng quên mật khẩu)
     */
    public static String getCurrentPassword(String username) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy mật khẩu: " + e.getMessage());
        }
        return null;
    }

    /**
     * Kiểm tra email có khớp với username
     */
    public static boolean checkEmailByUsername(String username, String email) {
        String sql = "SELECT email FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String savedEmail = rs.getString("email");
                    return savedEmail != null && savedEmail.equalsIgnoreCase(email);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra email: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cập nhật email
     */
    public static boolean updateUserEmail(String username, String email) {
        String sql = "UPDATE users SET email = ? WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, username);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy điểm cao theo chế độ chơi
     * 
     * @param username Tên người dùng
     * @param mode Chế độ chơi (SINGLE_MODE hoặc SPEED_MODE)
     * @return Điểm cao nhất của người dùng trong chế độ chơi đó
     */
    public static int getHighScore(String username, String mode) {
        String columnName;
        
        switch (mode) {
            case SINGLE_MODE:
                columnName = "single_mode_score";
                break;
            case SPEED_MODE:
                columnName = "speed_mode_score";
                break;
            default:
                columnName = "highscore"; // Fallback to original column
        }
        
        String sql = "SELECT " + columnName + " FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(columnName);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy điểm cao (" + mode + "): " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Lấy điểm cao từ cột highscore cũ (tương thích ngược)
     */
    public static int getHighScore(String username) {
        // Giữ lại phương thức cũ để tương thích ngược
        return getHighScore(username, SINGLE_MODE);
    }

    /**
     * Cập nhật điểm cao theo chế độ chơi
     * 
     * @param username Tên người dùng
     * @param score Điểm số mới
     * @param mode Chế độ chơi (SINGLE_MODE hoặc SPEED_MODE)
     * @return true nếu cập nhật thành công
     */
    public static boolean updateHighScore(String username, int score, String mode) {
        String columnName;
        
        switch (mode) {
            case SINGLE_MODE:
                columnName = "single_mode_score";
                break;
            case SPEED_MODE:
                columnName = "speed_mode_score";
                break;
            default:
                columnName = "highscore"; // Fallback to original column
        }
        
        String sql = "UPDATE users SET " + columnName + " = ?, highscore = CASE WHEN ? > highscore THEN ? ELSE highscore END WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, score);
            pstmt.setInt(2, score);
            pstmt.setInt(3, score);
            pstmt.setString(4, username);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật điểm cao (" + mode + "): " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cập nhật điểm cao vào cột highscore cũ (tương thích ngược)
     */
    public static boolean updateHighScore(String username, int score) {
        // Giữ lại phương thức cũ để tương thích ngược
        return updateHighScore(username, score, SINGLE_MODE);
    }
}