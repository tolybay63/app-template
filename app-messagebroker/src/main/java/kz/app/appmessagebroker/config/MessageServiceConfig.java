package kz.app.appmessagebroker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.app.appmessagebroker.service.MessageService;
import kz.app.appmessagebroker.service.impl.MessageServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class MessageServiceConfig {

    @Bean
    MessageService messageService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        return new MessageServiceImpl(kafkaTemplate, objectMapper);
    }

}
