package kz.app.appmessagebroker.service;

import kz.app.appmessagebroker.model.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Умение парсить сообщения
 */
public interface IMessageParse {

    Message parseMessage(ConsumerRecord<String, String> record);

}
