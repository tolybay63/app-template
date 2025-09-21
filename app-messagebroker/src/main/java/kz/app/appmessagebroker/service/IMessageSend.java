package kz.app.appmessagebroker.service;

import kz.app.appmessagebroker.model.Message;

/**
 * Умение отправлять сообщения в очередь/шину
 */
public interface IMessageSend {

    void send(String topic, Message message);

}
