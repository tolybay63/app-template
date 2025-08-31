package kz.app.appmessagebroker.service;

import kz.app.appmessagebroker.model.*;
import kz.app.appmessagebroker.model.*;
import org.apache.kafka.clients.consumer.*;

/**
 * Умение парсить сообщения
 */
public interface IMessageParse {

    Message parseMessage(ConsumerRecord<String, String> record);

}
