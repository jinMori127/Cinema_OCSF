package il.cshaifasweng.OCSFMediatorExample.server;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EmailScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void scheduleEmail(String recipient, String subject, String body, Date sendTime) {
        LocalDateTime sendLocalDateTime = sendTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime adjustedSendTime = sendLocalDateTime.minusHours(1);

        Date adjustedSendDate = Date.from(adjustedSendTime.atZone(ZoneId.systemDefault()).toInstant());

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, adjustedSendTime);
        long delay = duration.toMillis();

        if (delay < 0) {
            delay = 0;
            System.out.println("Adjusted time is in the past. Sending email immediately.");
        }

        scheduler.schedule(() -> {
            try {
                // Create and start the EmailSender thread
                EmailSender emailSender = new EmailSender(new String[]{recipient}, subject, body);
                Thread emailThread = new Thread(emailSender);
                emailThread.start();  // Start the thread

                System.out.println("Email sent to " + recipient);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}
