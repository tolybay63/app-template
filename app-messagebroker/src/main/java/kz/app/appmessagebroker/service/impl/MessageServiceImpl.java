package kz.app.appmessagebroker.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.app.appmessagebroker.model.Message;
import kz.app.appmessagebroker.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

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

}
