package kz.app.appmain.service;

import kz.app.appcore.utils.UtCnv;
import kz.app.appcore.utils.UtString;
import kz.app.appmessagebroker.model.Message;
import kz.app.appmessagebroker.service.MessageService;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class SendKafkaFunc {

    private final MessageService messageService;

    public SendKafkaFunc(MessageService messageService) {
        this.messageService = messageService;
    }

    public void send(String task){
        String guid = genGuid();
        Message msgResult = new Message(UtCnv.toMap("task", task, "data", guid));
        messageService.send("app-func-in", msgResult);
    }

    private final SecureRandom rnd = new SecureRandom();

    private String genGuid() {
        return UtString.toHexString(rnd.nextLong());
    }

}
