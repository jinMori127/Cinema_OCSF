package il.cshaifasweng.OCSFMediatorExample.server;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class MovieLink implements HttpHandler{
        private final String movieUrl;
        private final LocalTime startTime;
        private final LocalTime endTime;

        public MovieLink(String movieUrl, LocalTime startTime, LocalTime endTime) {
            this.movieUrl = movieUrl;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LocalTime currentTime = LocalTime.now();
            String response;

            // Check if current time is within the allowed range
            if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
                // Redirect to the movie URL
                exchange.getResponseHeaders().set("Location", movieUrl);
                exchange.sendResponseHeaders(302, 0);
            } else {
                // Serve a message that the link is not available
                response = "This link is only available between " + startTime + " and " + endTime + ".";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

