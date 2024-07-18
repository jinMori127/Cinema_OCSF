package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class ShowPurchasesBoxEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public ShowPurchasesBoxEvent(Message message) {
        this.message = message;
    }
}