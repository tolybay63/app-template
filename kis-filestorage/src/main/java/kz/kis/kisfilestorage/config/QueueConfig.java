package kz.kis.kisfilestorage.config;

import kz.kis.kisfilestorage.model.QueuedMessage;
import kz.kis.kismessagebroker.model.*;
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
