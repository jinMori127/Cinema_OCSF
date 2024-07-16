package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;

public class UpdateScreeningForMovieEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public UpdateScreeningForMovieEvent(Message message) {
        this.message = message;
    }
}
