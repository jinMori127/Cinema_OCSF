package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class DeletePurchasesBoxEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public DeletePurchasesBoxEvent(Message message) {
        this.message = message;
    }
}