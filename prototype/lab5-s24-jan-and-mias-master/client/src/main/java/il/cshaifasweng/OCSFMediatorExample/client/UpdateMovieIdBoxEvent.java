package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class UpdateMovieIdBoxEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public UpdateMovieIdBoxEvent(Message message) {
        this.message = message;
    }
}
