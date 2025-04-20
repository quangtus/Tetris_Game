package com.zetcode;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JButton registerButton;
    private JButton backButton;

    // Màu sắc
    private final Color LIGHT_BLUE = new Color(173, 216, 230);
    private final Color DARK_BLUE = new Color(0, 102, 153);
    private final Color BUTTON_BLUE = new Color(0, 153, 204);
    private final Color BUTTON_BLUE_HOVER = new Color(0, 180, 240);
    private final Color BUTTON_LIGHT_BLUE = new Color(51, 204, 255);
    private final Color BUTTON_LIGHT_BLUE_HOVER = new Color(60, 220, 255);

    public RegisterFrame() {
        initUI();
    }

    private void initUI() {
        // === THIẾT LẬP CƠ BẢN FRAME ===
        setTitle("Tetris - Register");
        setSize(450, 500); // Cao hơn để chứa thêm trường
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Đặt màu nền chính
        getContentPane().setBackground(LIGHT_BLUE);
        
        // === TẠO PANEL CHÍNH ===
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(LIGHT_BLUE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // === TẠO PANEL TIÊU ĐỀ ===
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(LIGHT_BLUE);
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Đăng Ký");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        titlePanel.add(titleLabel);
        
        // === TẠO PANEL FORM ĐĂNG KÝ ===
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 20));
        formPanel.setBackground(LIGHT_BLUE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Username
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(250, 40));
        usernameField.setMargin(new Insets(5, 7, 5, 7));
        formPanel.add(usernameField);
        
        // Password
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(250, 40));
        passwordField.setMargin(new Insets(5, 7, 5, 7));
        formPanel.add(passwordField);
        
        // Confirm password
        JLabel confirmPasswordLabel = new JLabel("Xác nhận mật khẩu:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(confirmPasswordLabel);
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 16));
        confirmPasswordField.setPreferredSize(new Dimension(250, 40));
        confirmPasswordField.setMargin(new Insets(5, 7, 5, 7));
        formPanel.add(confirmPasswordField);
        
        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(emailLabel);
        
        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        emailField.setPreferredSize(new Dimension(250, 40));
        emailField.setMargin(new Insets(5, 7, 5, 7));
        formPanel.add(emailField);
        
        // === TẠO PANEL NÚT ===
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        buttonPanel.setBackground(LIGHT_BLUE);
        buttonPanel.setMaximumSize(new Dimension(600, 50));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Nút Back
        backButton = createStyledButton("Quay lại", BUTTON_LIGHT_BLUE, BUTTON_LIGHT_BLUE_HOVER, Color.WHITE);
        backButton.addActionListener(e -> returnToLogin());
        buttonPanel.add(backButton);
        
        // Nút Register
        registerButton = createStyledButton("Đăng ký", BUTTON_BLUE, BUTTON_BLUE_HOVER, Color.WHITE);
        registerButton.addActionListener(e -> handleRegister());
        buttonPanel.add(registerButton);
        
        // === THÊM PANEL VÀO PANEL CHÍNH ===
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(formPanel);
        mainPanel.add(buttonPanel);
        
        // === THÊM PANEL CHÍNH VÀO FRAME ===
        add(mainPanel);
        
        // Đặt nút Register là mặc định khi nhấn Enter
        getRootPane().setDefaultButton(registerButton);
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color hoverColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 17));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(170, 45));
        
        // Thiết lập border với viền tròn nhẹ
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Thêm hiệu ứng hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.contains(e.getPoint())) {
                    button.setBackground(hoverColor);
                } else {
                    button.setBackground(bgColor);
                }
            }
        });
        
        return button;
    }
    
    private void returnToLogin() {
        dispose();
        EventQueue.invokeLater(() -> {
            var loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
    
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String email = emailField.getText().trim();
        
        // Kiểm tra thông tin nhập
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.", 
                "Thông tin thiếu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Kiểm tra mật khẩu trùng khớp
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Mật khẩu xác nhận không khớp.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Kiểm tra email hợp lệ (nếu đã nhập)
        if (!email.isEmpty() && !isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, 
                "Email không hợp lệ.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Kiểm tra tài khoản đã tồn tại
        if (Database.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, 
                "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.", 
                "Đăng ký thất bại", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Đăng ký tài khoản mới
        if (registerUser(username, password, email)) {
            JOptionPane.showMessageDialog(this, 
                "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.", 
                "Đăng ký thành công", JOptionPane.INFORMATION_MESSAGE);
            returnToLogin();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Đăng ký thất bại. Vui lòng thử lại sau.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean registerUser(String username, String password, String email) {
    try {
        // Thêm debug để xác định vị trí database
        File dbFile = new File("users.db");
        System.out.println("Database file location: " + dbFile.getAbsolutePath());
        System.out.println("File exists: " + dbFile.exists());
        System.out.println("Can write: " + dbFile.canWrite());
        
        // Sử dụng phương thức addUser từ Database
        boolean result = Database.addUser(username, password, email);
        if (!result) {
            JOptionPane.showMessageDialog(this,
                "Chi tiết lỗi: Không thể thêm người dùng vào database. Vui lòng kiểm tra console.",
                "Đăng ký thất bại", JOptionPane.ERROR_MESSAGE);
        }
        return result;
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Lỗi ngoại lệ: " + e.getMessage(), 
            "Đăng ký thất bại", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}