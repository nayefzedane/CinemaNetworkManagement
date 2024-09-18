package il.cshaifasweng.OCSFMediatorExample.server;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    public static void sendEmail(String to, String subject, String body) {
        // Sender's email credentials
        String from = "starlightcinema100@gmail.com";
        String password = "kxzqknfutttobupk";

        // SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Add this line


        // Create a session with authenticator
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create a default MimeMessage object
            Message message = new MimeMessage(session);

            // Set From: header
            message.setFrom(new InternetAddress(from));

            // Set To: header
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            // Set Subject: header
            message.setSubject(subject);

            // Now set the actual message body
            message.setText(body);

            // Send message
            Transport.send(message);

            System.out.println("Email Sent Successfully");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

//public static void main(String[] args) {
// Test sending an email
//   sendEmail("recipient-email@gmail.com", "Test Subject", "This is a test email!");
// }
//}
