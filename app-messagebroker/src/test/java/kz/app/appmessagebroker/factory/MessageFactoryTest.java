package kz.app.appmessagebroker.factory;

import kz.app.appmessagebroker.config.MessageBrokerConfig;
import kz.app.appmessagebroker.model.Message;
import kz.app.appmessagebroker.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Map;

@EmbeddedKafka(partitions = 1, topics = {"test-topic", "test-topic-out"})
@EnableKafka
@SpringBootTest(classes = {MessageBrokerConfig.class})
public class MessageFactoryTest {

    @Autowired
    private MessageService messageService;

    @Test
    public void testSendAndReceiveMessage() throws Exception {
        Message msg = new Message(Map.of("key", "value"));
        messageService.send("MyService1-in", msg);
    }

}
