package com.zetcode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ForgotPasswordFrame extends JFrame {

    private JTextField usernameField;
    private JTextField emailField;
    private JButton submitButton;
    private JButton backButton;
    private final Color LIGHT_BLUE = new Color(173, 216, 230);

    public ForgotPasswordFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Tetris - Quên mật khẩu");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(LIGHT_BLUE);
        
        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LIGHT_BLUE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tiêu đề
        JLabel titleLabel = new JLabel("Quên mật khẩu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Panel form
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBackground(LIGHT_BLUE);
        
        formPanel.add(new JLabel("Tên đăng nhập:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);
        
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);
        
        // Panel nút
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(LIGHT_BLUE);
        
        backButton = new JButton("Quay lại");
        submitButton = new JButton("Gửi mật khẩu");
        
        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);
        
        // Thêm vào panel chính
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Thêm vào frame
        add(mainPanel);
        
        // Xử lý sự kiện
        backButton.addActionListener(e -> {
            dispose();
            EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
        });
        
        submitButton.addActionListener(e -> recoverPassword());
        
        getRootPane().setDefaultButton(submitButton);
    }
    
    private void recoverPassword() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        
        // Kiểm tra đầu vào
        if (username.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập đầy đủ tên đăng nhập và email.", 
                "Thông tin thiếu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Kiểm tra người dùng tồn tại
        if (!Database.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, 
                "Tên đăng nhập không tồn tại.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Lấy mật khẩu hiện tại
        String currentPassword = Database.getCurrentPassword(username);
        if (currentPassword == null) {
            JOptionPane.showMessageDialog(this, 
                "Không thể lấy thông tin tài khoản.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Kiểm tra email và cập nhật nếu cần
        boolean emailMatched = Database.checkEmailByUsername(username, email);
        if (!emailMatched) {
            Database.updateUserEmail(username, email);
        }
        
        // Gửi email
        boolean emailSent = EmailSender.sendPasswordEmail(email, username, currentPassword);
        
        if (emailSent) {
            JOptionPane.showMessageDialog(this, 
                "Mật khẩu đã được gửi đến email của bạn.", 
                "Gửi mật khẩu thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Không thể gửi email. Mật khẩu hiện tại của bạn là: " + currentPassword, 
                "Lưu ý", JOptionPane.WARNING_MESSAGE);
        }
        
        // Quay lại màn hình đăng nhập
        dispose();
        EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}