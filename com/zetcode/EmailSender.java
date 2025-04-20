package com.zetcode;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

/**
 * Lớp gửi email chứa mật khẩu hiện tại cho người dùng quên mật khẩu
 */
public class EmailSender {
    
    // Thông tin cấu hình SMTP
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";
    private static final String USERNAME = "g00glesama@gmail.com"; 
    private static final String PASSWORD = "cqmk rysk zlct nccj"; 
    
    /**
     * Gửi email chứa mật khẩu hiện tại cho người dùng.
     * 
     * @param recipientEmail Email người nhận
     * @param username Tên đăng nhập người dùng
     * @param currentPassword Mật khẩu hiện tại
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public static boolean sendPasswordEmail(String recipientEmail, String username, String currentPassword) {
        if (recipientEmail == null || recipientEmail.isEmpty() || 
            username == null || username.isEmpty() || 
            currentPassword == null || currentPassword.isEmpty()) {
            System.out.println("Email, username hoặc mật khẩu không được để trống");
            return false;
        }
        
        try {
            // Thiết lập kết nối
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", HOST);
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            
            // Tạo session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });
            
            // Tạo message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Thông tin mật khẩu Tetris Game");
            
            // Tạo nội dung HTML
            String htmlContent = createEmailContent(username, currentPassword);
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            
            // Gửi email
            Transport.send(message);
            System.out.println("Email đã được gửi thành công đến: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.out.println("Lỗi khi gửi email: " + e.getMessage());
            e.printStackTrace();
            
            // Hiển thị mật khẩu trong console để giúp testing
            System.out.println("Mật khẩu hiện tại của " + username + " là: " + currentPassword);
            return false;
        }
    }
    
    /**
     * Tạo nội dung email
     */
    private static String createEmailContent(String username, String password) {
        return String.format(
            "<div style='font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;'>" +
            "<h2 style='color: #0066cc; border-bottom: 1px solid #ccc; padding-bottom: 10px;'>Thông tin mật khẩu Tetris Game</h2>" +
            "<p>Xin chào <b>%s</b>,</p>" +
            "<p>Đây là mật khẩu tài khoản của bạn:</p>" +
            "<div style='background: #f0f0f0; padding: 15px; border-radius: 5px; margin: 15px 0; text-align: center;'>" +
            "<span style='font-family: monospace; font-size: 16px; font-weight: bold; letter-spacing: 1px;'>%s</span>" +
            "</div>" +
            "<p>Vui lòng đăng nhập với mật khẩu này.</p>" +
            "<p>Trân trọng,<br>Tetris Game Team</p>" +
            "</div>",
            username, password
        );
    }
    
    /**
     * Kiểm tra email hợp lệ
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}