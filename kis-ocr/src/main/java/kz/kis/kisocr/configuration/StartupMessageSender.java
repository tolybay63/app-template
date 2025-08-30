package kz.kis.kisocr.configuration;

import kz.kis.kiscore.service.*;
import kz.kis.kismessagebroker.model.*;
import kz.kis.kismessagebroker.service.*;
import org.springframework.boot.*;
import org.springframework.context.annotation.*;

import java.util.*;

@Configuration
public class StartupMessageSender {

    private final MessageService messageService;

    public StartupMessageSender(MessageService messageService) {
        this.messageService = messageService;
    }

    @Bean
    public ApplicationRunner sendStartupMessage() {
        return args -> {
            messageService.send(ServiceTaskPoint.TOPIC_KIS_OCR, new Message(Map.of("state", ServiceState.SERVICE_STARTED)));
        };
    }
}