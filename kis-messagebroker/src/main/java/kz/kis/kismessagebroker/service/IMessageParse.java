package kz.kis.kismessagebroker.service;

import kz.kis.kismessagebroker.model.*;
import org.apache.kafka.clients.consumer.*;

/**
 * Умение парсить сообщения
 */
public interface IMessageParse {

    Message parseMessage(ConsumerRecord<String, String> record);

}
