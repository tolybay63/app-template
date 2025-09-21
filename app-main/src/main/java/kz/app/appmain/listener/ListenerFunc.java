package kz.app.appmain.listener;

import kz.app.appcore.utils.UtCnv;
import kz.app.appmessagebroker.model.Message;
import kz.app.appmessagebroker.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class ListenerFunc {

    private static final Logger log = LoggerFactory.getLogger(ListenerFunc.class);

    private final MessageService messageService;

    public ListenerFunc(
            MessageService messageService
    ) {
        this.messageService = messageService;
    }

    @KafkaListener(topics = {"app-func-in"}, groupId = "default")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        Message msg = messageService.parseMessage(record);
        log.info("ListenerFunc.listen - received message: {}", msg);

        // Независимо от результатов второй раз не читаем задачу
        acknowledgment.acknowledge();

        //
        try {
            String task = UtCnv.toString(msg.get("task"), null);

            log.info("ListenerFunc.listen, got task: {}", task);

            if (task.equals("ping")) {
                // Do some useful work
                log.info("ListenerFunc.listen, exec task: {}", task);

                // Send result
                Message msgResult = new Message(UtCnv.toMap("result", "pong"));
                messageService.send("app-func-in", msgResult);

            } else {
                // Do some useful work
                log.info("ListenerFunc.listen, exec task: {}", task);

                // Send result
                Message msgResult = new Message(UtCnv.toMap("result", "ok"));
                messageService.send("app-func-in", msgResult);
            }

            log.info("ListenerFunc.listen, task exec - ok");

        } catch (Exception e) {
            log.error("Error processing message: {}", record.value(), e);
            Message msgResult = new Message(UtCnv.toMap("result", "error", "error", e.getMessage()));
            messageService.send("app-func-in", msgResult);

        } finally {
            log.info("ListenerFunc.listen - done");
        }
    }

}