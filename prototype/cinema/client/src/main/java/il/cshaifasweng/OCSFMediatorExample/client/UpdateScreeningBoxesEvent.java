package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class UpdateScreeningBoxesEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public UpdateScreeningBoxesEvent(Message message) {
        this.message = message;
    }
}
