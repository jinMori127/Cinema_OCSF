package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.Screening;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import org.hibernate.Transaction;
import org.hibernate.Session;
import java.time.ZoneId;
import java.util.Date;

import org.hibernate.query.Query;

import java.util.List;


public class SimpleChatServer
{
	
	private static SimpleServer server;
    public static String password;
    public static String host;
    private static List<Screening> delete_screenings_daily() {
        // Open a new session
        Session session = server.sessionFactory.openSession();

        // Begin a transaction
        Transaction transaction = null;
        List<Screening> screeningsToDelete = new ArrayList<>();  // Initialize the list to avoid potential NPE

        try {
            // Get the date for one week ago
            LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
            Date dateOneWeekAgo = Date.from(oneWeekAgo.atZone(ZoneId.systemDefault()).toInstant());

            // Start the transaction
            transaction = session.beginTransaction();

            // HQL query to select screenings older than one week
            String selectHql = "FROM Screening WHERE date_time < :oneWeekAgo";
            Query<Screening> selectQuery = session.createQuery(selectHql, Screening.class);
            selectQuery.setParameter("oneWeekAgo", dateOneWeekAgo);

            // Execute the query to retrieve the screenings to delete
            screeningsToDelete = selectQuery.list();

            // HQL query to delete screenings older than one week
            String deleteHql = "DELETE FROM Screening WHERE date_time < :oneWeekAgo";
            int rowsDeleted = session.createQuery(deleteHql)
                    .setParameter("oneWeekAgo", dateOneWeekAgo)
                    .executeUpdate();

            // Commit the transaction after deleting the rows
            transaction.commit();
            System.out.println("Deleted " + rowsDeleted + " past screenings.");

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();  // Rollback in case of an error
            }
            e.printStackTrace();  // Log the exception

        } finally {
            // Ensure session is closed in the finally block
            if (session != null) {
                session.close();  // Close the session to avoid resource leaks
            }
        }

        return screeningsToDelete;  // Return the screenings outside the finally block
    }

    private static void  log_out_from_everything()
    {
        Session session = server.sessionFactory.openSession();

        // Begin a transaction
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            // HQL query to delete screenings older than one week
            String hql = "UPDATE Worker SET is_worker_loggedIn = 0";
            int rowsUpdated = session.createQuery(hql)
                    .executeUpdate();
            // Commit the transaction
            hql = "UPDATE IdUser SET isLoggedIn = 0";
             session.createQuery(hql)
                    .executeUpdate();
            transaction.commit();
            //System.out.println("logedOu " + rowsUpdated + " past screenings.");

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();  // Rollback in case of an error
            }
            e.printStackTrace();
        } finally {
            session.close();

        }
    }



    private static void daily_task()
    {
        // Create a ScheduledExecutorService with one thread
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Define the task you want to run every minute
        Runnable minuteTask = () -> {
            System.out.println("Running task at: " + LocalDateTime.now());
            List <Screening> data = delete_screenings_daily();
            for (Screening screening : data) {
                System.out.println(screening.getAuto_number_screening());
            }
            Message m = new Message(10,"#Delete_Past_Screenings");
            m.setObject(data);
            server.sendToAllClients(m);
            // Add your task logic here


            List<Movie> movieList;
            try {
                movieList = SimpleServer.getAllMovies();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(!movieList.isEmpty()) {
                for (Movie movie : movieList) {
                    System.out.println(movie.getAuto_number_movie());
                    System.out.println(movie.getCategory());
                    try {
                        SimpleServer.setMovieAnnouncement(movie);
                        System.out.println();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        // Schedule the task to run every 1 minute, starting now (0 initial delay)
        long initialDelay = 0;  // Start immediately
        long period = 1;  // 1 minute

        scheduler.scheduleAtFixedRate(minuteTask, initialDelay, period, TimeUnit.MINUTES);

        // Optionally, add a shutdown hook to gracefully shut down the scheduler
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down scheduler...");
            log_out_from_everything();
            scheduler.shutdown();
        }));
    }

    public static void main( String[] args ) throws IOException
    {
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter a string
        System.out.print("Enter a my SQL password: \n");

        // Read the input string
        password = scanner.nextLine();
        System.out.print("Enter a host: \n");
        host = scanner.nextLine();
        scanner.close();
        Path currentPath = Paths.get("");
        String currentWorkingDir = currentPath.toAbsolutePath().toString();
        System.out.println("Current working directory: " + currentWorkingDir);

        server = new SimpleServer(3000);
        System.out.println("server is listening");
        server.listen();
        Session session = server.sessionFactory.openSession();
        session.beginTransaction();
        server.create_reports(session);
        session.getTransaction().commit();
        session.close();

        daily_task();
    }
}
