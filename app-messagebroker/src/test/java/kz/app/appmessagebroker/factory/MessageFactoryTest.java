package kz.app.appmessagebroker.factory;

import kz.app.appmessagebroker.config.*;
import kz.app.appmessagebroker.model.*;
import kz.app.appmessagebroker.service.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.kafka.annotation.*;
import org.springframework.kafka.test.context.*;

import java.util.*;

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
