package il.cshaifasweng.OCSFMediatorExample.server;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender implements Runnable {
    private static final String EMAIL = "LunaAura.cinema@gmail.com";
    private static final String PASSWORD = "sxym ghxu reni jyki";

    private String[] recipients;
    private String subject;
    private String body;

    // Constructor to initialize email details
    public EmailSender(String[] recipients, String subject, String body) {
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
    }

    @Override
    public void run() {
        // Set up the SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a session with an authenticator
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });

        try {
            // Create a message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(String.join(", ", recipients)));
            message.setSubject(subject);

            // Set the content as HTML
            message.setContent(body, "text/html; charset=utf-8");

            // Send the message
            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}