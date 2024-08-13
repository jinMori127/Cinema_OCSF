package il.cshaifasweng.OCSFMediatorExample.server;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import il.cshaifasweng.OCSFMediatorExample.server.EmailSender;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EmailScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void scheduleEmail(String recipient, String subject, String body, LocalDateTime sendTime) {
        // Calculate delay
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, sendTime);
        long delay = duration.toMillis();

        // Schedule the email task
        scheduler.schedule(() -> {
            try {
                EmailSender emailSender = new EmailSender();
                emailSender.sendEmail(new String[]{recipient}, subject, body); // Pass recipient as a String[]
                System.out.println("Email sent to " + recipient);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}
