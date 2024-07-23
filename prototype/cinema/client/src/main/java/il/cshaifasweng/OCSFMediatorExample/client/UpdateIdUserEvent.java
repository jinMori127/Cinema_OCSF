package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class UpdateIdUserEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public UpdateIdUserEvent(Message message) {
        this.message = message;
    }
}
