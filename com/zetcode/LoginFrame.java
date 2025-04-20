package com.zetcode;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // Màu sắc
    private final Color LIGHT_BLUE = new Color(173, 216, 230);
    private final Color DARK_BLUE = new Color(0, 102, 153);
    private final Color BUTTON_BLUE = new Color(0, 153, 204);
    private final Color BUTTON_BLUE_HOVER = new Color(0, 180, 240);

    public LoginFrame() {
        initUI();
    }

    private void initUI() {
        // === THIẾT LẬP CƠ BẢN FRAME ===
        setTitle("Tetris - Login");
        setSize(450, 420);
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
        
        JLabel titleLabel = new JLabel("Welcome to Tetris");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        titlePanel.add(titleLabel);
        
        // === TẠO PANEL FORM ĐĂNG NHẬP ===
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 15, 20));
        formPanel.setBackground(LIGHT_BLUE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        formPanel.setMaximumSize(new Dimension(600, 120));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(250, 40));
        usernameField.setMargin(new Insets(5, 7, 5, 7));
        formPanel.add(usernameField);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(250, 40));
        passwordField.setMargin(new Insets(5, 7, 5, 7));
        formPanel.add(passwordField);
        
        // === TẠO PANEL NÚT ĐĂNG NHẬP (Chỉ còn 1 nút) ===
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(LIGHT_BLUE);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(200, 50));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        // Nút login ở giữa
        loginButton = createStyledButton("Login", BUTTON_BLUE, BUTTON_BLUE_HOVER, Color.WHITE);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Container để căn giữa nút
        JPanel loginButtonContainer = new JPanel();
        loginButtonContainer.setBackground(LIGHT_BLUE);
        loginButtonContainer.add(loginButton);
        
        // === TẠO PANEL LIÊN KẾT (Đăng ký & Quên mật khẩu) ===
        JPanel linkPanel = new JPanel();
        linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.Y_AXIS));
        linkPanel.setBackground(LIGHT_BLUE);
        linkPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Liên kết đăng ký
        JPanel registerLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerLinkPanel.setBackground(LIGHT_BLUE);
        
        JButton registerLink = new JButton("Chưa có tài khoản? Đăng ký");
        styleAsLink(registerLink);
        registerLink.addActionListener(e -> openRegisterPage());
        registerLinkPanel.add(registerLink);
        
        // Liên kết quên mật khẩu
        JPanel forgotPasswordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        forgotPasswordPanel.setBackground(LIGHT_BLUE);
        
        JButton forgotPasswordButton = new JButton("Quên mật khẩu?");
        styleAsLink(forgotPasswordButton);
        forgotPasswordButton.addActionListener(e -> openForgotPasswordPage());
        forgotPasswordPanel.add(forgotPasswordButton);
        
        // Thêm vào panel liên kết
        linkPanel.add(registerLinkPanel);
        linkPanel.add(Box.createVerticalStrut(5)); // Khoảng cách giữa hai liên kết
        linkPanel.add(forgotPasswordPanel);
        
        // === THÊM PANEL VÀO PANEL CHÍNH ===
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(formPanel);
        mainPanel.add(loginButtonContainer);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(linkPanel);
        
        // === THÊM PANEL CHÍNH VÀO FRAME ===
        add(mainPanel);
        
        // === THÊM XỬ LÝ SỰ KIỆN ===
        loginButton.addActionListener(e -> handleLogin());
        
        // Đặt nút Login là mặc định khi nhấn Enter
        getRootPane().setDefaultButton(loginButton);
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
    
    // Style cho các liên kết (hiển thị như text)
    private void styleAsLink(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(DARK_BLUE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.ITALIC, 14));
        button.setFocusPainted(false);
    }
    
    // Mở trang đăng ký
    private void openRegisterPage() {
        dispose();
        EventQueue.invokeLater(() -> {
            var registerFrame = new RegisterFrame();
            registerFrame.setVisible(true);
        });
    }
    
    // Mở trang quên mật khẩu
    private void openForgotPasswordPage() {
        dispose();
        EventQueue.invokeLater(() -> {
            var forgotPasswordFrame = new ForgotPasswordFrame();
            forgotPasswordFrame.setVisible(true);
        });
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.", 
                "Thông tin thiếu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (checkLogin(username, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose();
            EventQueue.invokeLater(() -> {
                var modeSelectionFrame = new ModeSelectionFrame(username);
                modeSelectionFrame.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, 
                "Tên đăng nhập hoặc mật khẩu không đúng.", 
                "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}