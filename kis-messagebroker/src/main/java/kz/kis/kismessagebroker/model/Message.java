package kz.kis.kismessagebroker.model;

import java.util.*;

public class Message {

    private Map<String, Object> data = new HashMap<>();

    // Используется и заполняется при ЧТЕНИИ из очереди, чтобы название topic-а было в самом сообщении,
    // и не нужно было брать из ConsumerRecord.topic()
    private String topic = null;

    public Message(Map<String, Object> data) {
        if (data != null) {
            this.data.putAll(data);
        }
    }

    public Object get(String key) {
        return data.get(key);
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String toString() {
        return "topic: " + topic + ", data: " + data;
    }

}
