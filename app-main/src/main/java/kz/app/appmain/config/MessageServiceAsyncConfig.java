package kz.app.appmain.config;

import com.fasterxml.jackson.databind.*;
import kz.app.appmain.service.*;
import kz.app.appmessagebroker.service.*;
import kz.app.appmain.service.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;

@Configuration
public class MessageServiceAsyncConfig {

    @Bean(name = "messageServiceAsync")
    MessageService messageService(ApplicationEventPublisher eventPublisher, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        return new MessageServiceAsync(eventPublisher, kafkaTemplate, objectMapper);
    }

}
