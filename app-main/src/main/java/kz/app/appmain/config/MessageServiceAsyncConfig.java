package kz.app.appmain.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.app.appmain.service.MessageServiceAsync;
import kz.app.appmessagebroker.service.MessageService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class MessageServiceAsyncConfig {

    @Bean(name = "messageServiceAsync")
    MessageService messageService(ApplicationEventPublisher eventPublisher, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        return new MessageServiceAsync(eventPublisher, kafkaTemplate, objectMapper);
    }

}
