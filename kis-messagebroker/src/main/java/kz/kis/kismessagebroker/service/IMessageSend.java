package kz.kis.kismessagebroker.service;

import kz.kis.kismessagebroker.model.*;

/**
 * Умение отправлять сообщения в очередь/шину
 */
public interface IMessageSend {

    void send(String topic, Message message);

}
