package kz.app.appmain.config;

import kz.app.appmain.model.QueuedMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class QueueConfig {

    @Bean
    public ConcurrentLinkedQueue<QueuedMessage> kafkaQueue() {
        return new ConcurrentLinkedQueue<>();
    }

}
