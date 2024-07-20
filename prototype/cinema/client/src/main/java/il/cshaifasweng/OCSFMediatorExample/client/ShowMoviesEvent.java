package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
public class ShowMoviesEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public ShowMoviesEvent(Message message) {
        this.message = message;
    }
}
