package kz.kis.kismessagebroker.service.impl;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import kz.kis.kiscore.service.*;
import kz.kis.kiscore.utils.*;
import kz.kis.kismessagebroker.model.*;
import kz.kis.kismessagebroker.service.*;
import org.apache.kafka.clients.consumer.*;
import org.slf4j.*;
import org.springframework.kafka.core.*;

import java.security.*;
import java.util.*;

/**
 * Абстракция канала отправки и приёма сообщений
 */
public class MessageServiceImpl implements MessageService {

    public static Logger log = LoggerFactory.getLogger(MessageService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public MessageServiceImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void send(String topic, Message message) {
        if (message == null || message.getData() == null || message.getData().isEmpty()) {
            log.warn("Attempted to send an empty message: {}", message);
            return;
        }

        //
        String jsonData;
        try {
            jsonData = objectMapper.writeValueAsString(message.getData());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize message, topic: %s, data: %s".formatted(topic, message.getData()), e);
        }

        // Умышленно отправляем в синхронном режиме (с .get() на конце),
        // чтобы точно понимать, отправлено сообщение или нет
        try {
            kafkaTemplate.send(topic, jsonData).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //
        log.info("send message, topic: {}, data: {}", topic, message.getData());
    }

    public Message parseMessage(ConsumerRecord<String, String> record) {
        if (record == null || record.value() == null || record.value().isEmpty()) {
            log.warn("Received empty Kafka message, skipping processing.");
            return null;
        }

        try {
            String kafkaMessage = record.value();
            Map<String, Object> data = objectMapper.readValue(kafkaMessage, Map.class);
            Message message = new Message(data);
            //
            String topic = record.topic();
            message.setTopic(topic);
            //
            return message;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize Kafka message", e);
        }
    }

    public void sendTask(String topic, String task, Map<String, Object> messageData) {
        Message message = new Message(messageData);
        String guid = genGuid();
        message.put("guid", guid);
        message.put("task", task);
        topic = topic + "-in";
        send(topic, message);
    }

    public void sendState(String topic, String state, Message sourceMessage) {
        Message message = new Message(sourceMessage.getData());
        message.put("state", state);
        topic = topic + "-out";
        send(topic, message);
    }

    public void sendStateOk(String topic, Message sourceMessage, Map<String, Object> resultData) {
        Message message = new Message(sourceMessage.getData());
        message.put("state", WorkState.OK);
        message.put("result", resultData);
        topic = topic + "-out";
        send(topic, message);
    }

    public void sendStateError(String topic, String error, Message sourceMessage) {
        Message message = new Message(sourceMessage.getData());
        message.put("state", WorkState.ERROR);
        message.put("error", error);
        topic = topic + "-out";
        send(topic, message);
    }

    private final SecureRandom rnd = new SecureRandom();

    private String genGuid() {
        return UtString.toHexString(rnd.nextLong());
    }

}
