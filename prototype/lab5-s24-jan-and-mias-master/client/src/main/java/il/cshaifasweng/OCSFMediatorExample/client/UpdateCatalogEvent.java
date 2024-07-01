package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class UpdateCatalogEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public UpdateCatalogEvent(Message message) {
        this.message = message;
    }
}
