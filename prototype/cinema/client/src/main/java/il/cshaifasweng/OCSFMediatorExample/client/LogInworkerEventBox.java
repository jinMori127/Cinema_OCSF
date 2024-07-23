package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class LogInworkerEventBox {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public LogInworkerEventBox(Message message) {
        this.message = message;
    }
}