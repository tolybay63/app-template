package kz.kis.kismessagebroker.factory;

import kz.kis.kismessagebroker.config.MessageBrokerConfig;
import kz.kis.kismessagebroker.model.Message;
import kz.kis.kismessagebroker.service.*;
import kz.kis.kismessagebroker.service.impl.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;

@EmbeddedKafka(partitions = 1, topics = {"test-topic", "test-topic-out"})
@EnableKafka
@SpringBootTest(classes = {MessageBrokerConfig.class})
public class MessageFactoryTest {

    private static final Logger logger = LoggerFactory.getLogger(MessageFactoryTest.class);

    private MessageService messageService;

//    @Autowired
//    private AdminClient adminClient;
//
//    @BeforeEach
//    void setup() {
//        NewTopic myService1In = new NewTopic("MyService1-in", 1, (short) 1);
//        NewTopic myService1Out = new NewTopic("MyService1-out", 1, (short) 1);
//        NewTopic myService2In = new NewTopic("MyService2-in", 1, (short) 1);
//        NewTopic myService2Out = new NewTopic("MyService2-out", 1, (short) 1);
//        NewTopic myService3In = new NewTopic("MyService2-in", 1, (short) 1);
//        NewTopic myService3Out = new NewTopic("MyService2-out", 1, (short) 1);
//        List<NewTopic> topics = new ArrayList();
//        topics.add(myService1In);
//        topics.add(myService1Out);
//        topics.add(myService2In);
//        topics.add(myService2Out);
//        topics.add(myService3In);
//        topics.add(myService3Out);
//        adminClient.createTopics(topics);
//    }

    @Test
    public void testSendAndReceiveMessage() throws Exception {
        // Send a test message to Kafka
        Message msg = new Message(Map.of("key", "value"));
        messageService.send("MyService1-in", msg);
    }

    ///

/*
    public static class FileListener1 {

        private final MessageService mf;

        private final MyService1 myService;

        public FileListener1(MessageService messageService, MyService1 myService) {
            this.mf = messageService;
            this.myService = myService;
        }

        @KafkaListener(topics = {"MyService1-in"}, groupId = "in-group")
        public void listen(String message, Acknowledgment acknowledgment) {

            try {
                // Читаем (и парсим) входящее сообщение
                Message msgIn = mf.getMessage(message);

                // Сообщаем watcher-у: сообщение прочитано
                Message msgWait = new Message(Map.of("wait", "message waiting for processing"));
                mf.send("MyService1-out", msgWait);

                // Передаем сообщение исполнителю
                onMessage(msgIn);

                // Сообщаем watcher-у: исполнитель отработал (без ошибок)
                Message msgDone = new Message(Map.of("done", "message successfully processed"));
                mf.send("MyService1-out", msgDone);

            } catch (Exception e) {
                logger.error("Error while processing message from Kafka.", e);

                Message msgError = new Message( Map.of("error", e.toString()));
                mf.send("MyService1-out",msgError);

            } finally {
                acknowledgment.acknowledge();

            }
        }

        *//**
         * Handles the message received from Kafka
         *
         * @param msg
         *//*
        public void onMessage(Message msg) throws Exception{
            // Simulate processing the message and working with the data
            // For example, do some work with msg.getData()

            // Send "running" status to indicate the service is processing
            Message msgRunning = new Message(Map.of("running", "service is processing"));
            mf.send("MyService1-out", msgRunning);

            List<Map> result = myService.doSomeWork(msg.getData());

            Map data = new HashMap();
            data.put("MyService1", result);

            // Simulate sending a result message to another topic (e.g., kis-indexer)
            Message msgResponse = new Message(data);
            mf.send("MyService2-in", msgResponse);
        }

    }

    // Service class that uses the MessageFactory to process messages
    public static class MyService1 {

        private final MessageService mf;

        public MyService1(MessageService messageService) {
            this.mf = messageService;
        }

        public List<Map> doSomeWork(Map data) throws Exception {
            if (data.isEmpty()) {
                throw new Exception("Data in service 1 is empty!");
            }
            Map doneData = new HashMap<>();
            doneData.put("key", "value");
            List result = new ArrayList();
            result.add(doneData);
            logger.info("Some work done in service 1");
            return result;
        }
    }

    ///

    public static class FileListener2 {

        private final MessageService mf;

        private final MyService2 myService;

        public FileListener2(MessageService messageService, MyService2 myService) {
            this.mf = messageService;
            this.myService = myService;
        }

        @KafkaListener(topics = {"MyService2-in"}, groupId = "in-group")
        public void listen(String message, Acknowledgment acknowledgment) {

            try {
                // Читаем (и парсим) входящее сообщение
                Message msgIn = mf.getMessage(message);

                // Сообщаем watcher-у: сообщение прочитано
                Message msgWait = new Message(Map.of("wait", "message waiting for processing"));
                mf.send("MyService2-out", msgWait);

                // Передаем сообщение исполнителю
                onMessage(msgIn);

                // Сообщаем watcher-у: исполнитель отработал (без ошибок)
                Message msgDone = new Message(Map.of("done", "message successfully processed"));
                mf.send("MyService2-out", msgDone);

            } catch (Exception e) {
                logger.error("Error while processing message from Kafka.", e);

                Message msgError = new Message(Map.of("error", e.toString()));
                mf.send("MyService1-out", msgError);

            } finally {
                acknowledgment.acknowledge();

            }
        }

        *//**
         * Handles the message received from Kafka
         *
         * @param msg
         *//*
        public void onMessage(Message msg) throws Exception{
            // Simulate processing the message and working with the data
            // For example, do some work with msg.getData()

            // Send "running" status to indicate the service is processing
            Message msgRunning = new Message(Map.of("running", "service is processing"));
            mf.send("MyService2-out", msgRunning);
            String nextIn;
            if (msg.getData().containsKey("MyService1")) {
                myService.doSomeWorkParser(msg.getData());
                nextIn = "MyService3-in";
            } else if (msg.getData().containsKey("MyService3")) {
                myService.doSomeWorkOcr(msg.getData());
                nextIn = "MyService4-in";
            } else {
                nextIn = "MyService4-in";
            }

            String hash = "asd";

            Map data = new HashMap();
            data.put("MyService2", hash);

            // Simulate sending a result message to another topic (e.g., kis-indexer)
            Message msgResponse = new Message(data);
            mf.send(nextIn, msgResponse);
        }
    }

    // Service class that uses the MessageFactory to process messages
    public static class MyService2 {

        private final MessageService mf;

        private final String KIS_PARSER = "kis-parser";
        private final String KIS_OCR = "kis-ocr";

        public MyService2(MessageService messageService) {
            this.mf = messageService;
        }

        public void doSomeWorkParser(Map data) throws Exception {
            if (data.isEmpty()) {
                throw new Exception("Data in service 2 is empty!");
            }
            Map doneData = new HashMap<>();
            doneData.put("key", "value");
            logger.info("Some parser work done in service 2");
        }

        public void doSomeWorkOcr(Map data) throws Exception {
            if (data.isEmpty()) {
                throw new Exception("Data in service 2 is empty!");
            }
            Map doneData = new HashMap<>();
            doneData.put("key", "value");
            logger.info("Some ocr work done in service 2");
        }
    }

    ///

    public static class FileListener3 {

        private final MessageService mf;

        private final MyService3 myService;

        public FileListener3(MessageService messageService, MyService3 myService) {
            this.mf = messageService;
            this.myService = myService;
        }

        @KafkaListener(topics = {"MyService3-in"}, groupId = "in-group")
        public void listen(String message, Acknowledgment acknowledgment) {

            try {
                // Читаем (и парсим) входящее сообщение
                Message msgIn = mf.getMessage(message);

                // Сообщаем watcher-у: сообщение прочитано
                Message msgWait = new Message(Map.of("wait", "message waiting for processing"));
                mf.send("MyService3-out", msgWait);

                // Передаем сообщение исполнителю
                onMessage(msgIn);

                // Сообщаем watcher-у: исполнитель отработал (без ошибок)
                Message msgDone = new Message(Map.of("done", "message successfully processed"));
                mf.send("MyService3-out", msgDone);

            } catch (Exception e) {
                logger.error("Error while processing message from Kafka.", e);

                // Сообщаем watcher-у: исполнитель отработал с ошибкой
                Message msgError = new Message(Map.of("error", "processing failed"));
                mf.send("MyService3-out", msgError);

            } finally {
                acknowledgment.acknowledge();
            }
        }

        *//**
         * Handles the message received from Kafka
         *
         * @param msg
         *//*
        public void onMessage(Message msg) {
            try {
                // Simulate processing the message and working with the data
                // For example, do some work with msg.getData()

                // Send "running" status to indicate the service is processing
                Message msgRunning = new Message(Map.of("running", "service is processing"));
                mf.send("MyService3-out", msgRunning);

                List<HashMap> result = myService.doSomeWork(msg.getData());
                Map data = new HashMap();
                data.put("MyService3", result);
                // Simulate sending a result message to another topic (e.g., kis-indexer)
                Message msgResponse = new Message( data);
                mf.send("MyService2-in",msgResponse);
            } catch (Exception e) {
                // Handle error and send an error message
                Message msgError = new Message(Map.of("error", e.toString()));
                mf.send("MyService3-out", msgError);
            }
        }
    }

    public static class MyService3 {

        private final MessageService mf;

        public MyService3(MessageService messageService) {
            this.mf = messageService;
        }

        public List<HashMap> doSomeWork(Map data) throws Exception {
            if (data.isEmpty()) {
                throw new Exception("Data from service 2 is empty!");
            }
            Map doneData = new HashMap<>();
            doneData.put("key", "value");
            List result = new ArrayList();
            result.add(doneData);
            logger.info("Some work done in service 3");
            return result;
        }
    }
*/
}
