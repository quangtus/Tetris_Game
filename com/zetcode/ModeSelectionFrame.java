package com.zetcode;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModeSelectionFrame extends JFrame {

    // Định nghĩa màu cố định để sử dụng trong toàn bộ lớp
    private final Color LIGHT_BLUE = new Color(173, 216, 230);
    private final Color DARK_BLUE = new Color(0, 102, 153);
    private final Color BUTTON_COLOR = new Color(0, 153, 204);
    private final Color BUTTON_HOVER_COLOR = new Color(0, 170, 230); // Màu sáng hơn khi hover
    private final Color LOGOUT_BUTTON_COLOR = new Color(51, 153, 255);
    private final Color LOGOUT_BUTTON_HOVER_COLOR = new Color(70, 170, 255); // Màu sáng hơn khi hover

    public ModeSelectionFrame(String username) {
        initUI(username);
    }

    private void initUI(String username) {
        setTitle("Tetris - Select Mode");
        setSize(450, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Đặt màu nền xanh nhạt
        getContentPane().setBackground(LIGHT_BLUE);
        
        // Sử dụng BorderLayout để tổ chức giao diện
        setLayout(new BorderLayout());
        
        // Panel tiêu đề ở trên cùng
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(LIGHT_BLUE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Select Game Mode", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        titlePanel.add(titleLabel);
        
        // Panel chứa thông tin người dùng
        JPanel userPanel = new JPanel();
        userPanel.setBackground(LIGHT_BLUE);
        userPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel userLabel = new JLabel("Player: " + username, SwingConstants.CENTER);
        userLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        userLabel.setForeground(DARK_BLUE);
        userPanel.add(userLabel);
        
        // Panel chứa các nút
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 20));
        buttonPanel.setBackground(LIGHT_BLUE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Nút chơi đơn
        JButton singlePlayerButton = createStyledButton("Single Player", "/zetcode/player1_icon.png", BUTTON_COLOR, BUTTON_HOVER_COLOR);
        singlePlayerButton.addActionListener(e -> {
            AudioPlayer startSound = new AudioPlayer();
            startSound.play("resources/start.wav");
            dispose();
            EventQueue.invokeLater(() -> {
                var game = new Tetris(username);
                game.setVisible(true);
            });
        });
        
        // Nút chơi hai người
        JButton twoPlayerButton = createStyledButton("Two Player Mode", "/zetcode/player2_icon.png", BUTTON_COLOR, BUTTON_HOVER_COLOR);
        twoPlayerButton.addActionListener(e -> {
            AudioPlayer startSound = new AudioPlayer();
            startSound.play("resources/start.wav");
            dispose();
            EventQueue.invokeLater(() -> {
                var twoPlayerFrame = new TwoPlayerFrame(username);
                twoPlayerFrame.setVisible(true);
            });
        });
        
        buttonPanel.add(singlePlayerButton);
        buttonPanel.add(twoPlayerButton);
        
        // Panel đăng xuất ở dưới cùng
        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(LIGHT_BLUE);
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        JButton logoutButton = createStyledButton("Logout", null, LOGOUT_BUTTON_COLOR, LOGOUT_BUTTON_HOVER_COLOR);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DARK_BLUE, 1, true),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                EventQueue.invokeLater(() -> {
                    var loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                });
            }
        });
        
        logoutPanel.add(logoutButton);
        
        // Thêm các panel vào frame
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_BLUE);
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(userPanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(logoutPanel, BorderLayout.SOUTH);
    }
    
    private JButton createStyledButton(String text, String iconPath, Color normalColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        
        // Tạo border bo tròn cố định để tránh thay đổi kích thước khi hover
        Border fixedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DARK_BLUE, 2, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
        button.setBorder(fixedBorder);
        
        // Thêm hiệu ứng hover chỉ đổi màu
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Khi chuột di vào nút, chỉ đổi màu nền
                button.setBackground(hoverColor);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Đổi con trỏ chuột thành hình bàn tay
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Khi chuột rời khỏi nút, quay lại màu bình thường
                button.setBackground(normalColor);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                // Khi nhấn chuột, đổi màu tối hơn
                button.setBackground(normalColor.darker());
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // Khi thả chuột
                if (button.contains(e.getPoint())) {
                    button.setBackground(hoverColor);
                } else {
                    button.setBackground(normalColor);
                }
            }
        });
        
        // Thử tải biểu tượng nếu có
        if (iconPath != null) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                if (icon.getIconWidth() > 0) {
                    button.setIcon(icon);
                    button.setHorizontalAlignment(SwingConstants.LEFT);
                    button.setIconTextGap(10);
                }
            } catch (Exception e) {
                // Nếu không tìm thấy biểu tượng, chỉ hiển thị văn bản
            }
        }
        
        return button;
    }
}