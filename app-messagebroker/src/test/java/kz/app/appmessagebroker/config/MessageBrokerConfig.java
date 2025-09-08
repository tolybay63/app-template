package kz.app.appmessagebroker.config;

import kz.app.appmessagebroker.AppMessageBrokerConfiguration;
import org.springframework.context.annotation.Import;

@Import(AppMessageBrokerConfiguration.class)
public class MessageBrokerConfig {

}
