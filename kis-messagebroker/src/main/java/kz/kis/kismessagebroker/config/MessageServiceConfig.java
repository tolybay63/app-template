package kz.kis.kismessagebroker.config;

import com.fasterxml.jackson.databind.*;
import kz.kis.kismessagebroker.service.*;
import kz.kis.kismessagebroker.service.impl.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;

@Configuration
public class MessageServiceConfig {

    @Bean
    MessageService messageService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        return new MessageServiceImpl(kafkaTemplate, objectMapper);
    }

}
