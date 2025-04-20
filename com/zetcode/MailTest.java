package com.zetcode;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import javax.swing.JOptionPane;

public class MailTest {
    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("g00glesama@gmail.com", "cqmk rysk zlct nccj");
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("g00glesama@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("bkuptos2@gmail.com"));
            message.setSubject("Test Email");
            message.setText("This is a test email from JavaMail.");

            Transport.send(message);
            JOptionPane.showMessageDialog(null, "Gửi email thành công!", "MailTest", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi gửi email:\n" + e.getMessage(), "MailTest", JOptionPane.ERROR_MESSAGE);
        }
    }
}