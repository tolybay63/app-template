package kz.app.appmain.model;

import kz.app.appmessagebroker.model.Message;

public class QueuedMessage {
    private final String topic;
    private final Message message;

    public QueuedMessage(String topic, Message message) {
        this.topic = topic;
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public Message getMessage() {
        return message;
    }
}
