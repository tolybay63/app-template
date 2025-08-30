package kz.kis.kismessagebroker;

import com.fasterxml.jackson.databind.*;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.*;
import org.springframework.kafka.config.*;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;

import java.util.*;

@AutoConfiguration
@EnableKafka
public class KisMessageBrokerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger("config");

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        log.info("=========================");
        log.info("KisMessageBrokerAutoConfiguration.consumerFactory");
        log.info("bootstrapServers: " + bootstrapServers);
        log.info("");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    // Define Kafka topics
    @Bean
    public NewTopic parserInTopic() {
        return new NewTopic("kis-parser-in", 1, (short) 1);
    }

    @Bean
    public NewTopic parserOutTopic() {
        return new NewTopic("kis-parser-out", 1, (short) 1);
    }

    @Bean
    public NewTopic searchInTopic() {
        return new NewTopic("kis-indexer-in", 1, (short) 1);
    }

    @Bean
    public NewTopic searchOutTopic() {
        return new NewTopic("kis-indexer-out", 1, (short) 1);
    }

    @Bean
    public NewTopic rastInTopic() {
        return new NewTopic("kis-rast-in", 1, (short) 1);
    }

    @Bean
    public NewTopic rastOutTopic() {
        return new NewTopic("kis-rast-out", 1, (short) 1);
    }

    @Bean
    public NewTopic ocrInTopic() {
        return new NewTopic("kis-ocr-in", 1, (short) 1);
    }

    @Bean
    public NewTopic ocrOutTopic() {
        return new NewTopic("kis-ocr-out", 1, (short) 1);
    }
}
