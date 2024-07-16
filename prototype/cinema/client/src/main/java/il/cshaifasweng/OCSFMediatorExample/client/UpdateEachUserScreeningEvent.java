package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class UpdateEachUserScreeningEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public UpdateEachUserScreeningEvent(Message message) {
        this.message = message;
    }
}
