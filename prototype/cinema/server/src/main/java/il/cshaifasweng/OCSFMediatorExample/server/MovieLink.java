package il.cshaifasweng.OCSFMediatorExample.server;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.time.LocalDate;

public class MovieLink implements HttpHandler{
    private final LocalDate startDate;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String movieUrl;

    public MovieLink(LocalDate startDate, LocalTime startTime, LocalTime endTime, String movieUrl) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.movieUrl = movieUrl;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        LocalTime currentTime = LocalTime.now();
        LocalDate currentDate = LocalDate.now();

        String response;

        if (currentDate.equals(startDate)) {
            if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
                exchange.getResponseHeaders().set("Location", movieUrl);
                exchange.sendResponseHeaders(302, 0);
            } else {
                response = "This link is only available between " + startTime + " and " + endTime + " on " + startDate + ".";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } else {
            response = "This link is only available on " + startDate + " between " + startTime + " and " + endTime + ".";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
    }

