package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class UpdateEachUserCatalogEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public UpdateEachUserCatalogEvent(Message message) {
        this.message = message;
    }
}
