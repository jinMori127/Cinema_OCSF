package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class BaseEventBox {
    private Message message;
    private int id;
    public BaseEventBox(int id, Message message) {
        this.message = message;
        this.id = id;
    }
    public Message getMessage() {
        return message;
    }
    public int getId() {
        return id;
    }
}