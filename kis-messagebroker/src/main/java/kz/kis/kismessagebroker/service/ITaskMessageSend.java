package kz.kis.kismessagebroker.service;

import kz.kis.kismessagebroker.model.*;

import java.util.*;

/**
 * Умение отправлять сообщения о задачах (task) и их состояниях (state).
 * С полями, соответствующими соглашению между сервисами.
 */
public interface ITaskMessageSend {

    void sendTask(String topic, String task, Map<String, Object> messageData);

    void sendState(String topic, String state, Message sourceMessage);

    void sendStateOk(String topic, Message sourceMessage, Map<String, Object> resultData);

    void sendStateError(String topic, String error, Message sourceMessage);

}
