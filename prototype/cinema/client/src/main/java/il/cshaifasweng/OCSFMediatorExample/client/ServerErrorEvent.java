package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class ServerErrorEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public ServerErrorEvent(Message message) {
        this.message = message;
    }
}
