package com.zetcode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:users.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            // Đăng ký driver SQLite
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
            System.out.println("Kết nối thành công!");
            createUsersTable(conn);
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Driver không tìm thấy: " + e.getMessage());
        }
        return conn;
    }

    private static void createUsersTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " username TEXT NOT NULL,\n"
                + " password TEXT NOT NULL,\n"
                + " highscore INTEGER DEFAULT 0\n"
                + ");";
    
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Bảng users đã được tạo thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi tạo bảng: " + e.getMessage());
        }
    }
    public static int getHighScore(String username) {
        String sql = "SELECT highscore FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("highscore");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy điểm cao nhất: " + e.getMessage());
        }
        return 0;
    }

    public static void updateHighScore(String username, int score) {
        String sql = "UPDATE users SET highscore = ? WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, score);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật điểm cao nhất: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        connect(); // Test kết nối
    }
}
