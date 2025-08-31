package kz.app.appmessagebroker.config;

import com.fasterxml.jackson.databind.*;
import kz.app.appmessagebroker.service.*;
import kz.app.appmessagebroker.service.impl.*;
import kz.app.appmessagebroker.service.*;
import kz.app.appmessagebroker.service.impl.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;

@Configuration
public class MessageServiceConfig {

    @Bean
    MessageService messageService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        return new MessageServiceImpl(kafkaTemplate, objectMapper);
    }

}
