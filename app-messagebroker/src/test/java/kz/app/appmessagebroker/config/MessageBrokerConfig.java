package kz.app.appmessagebroker.config;

import kz.app.appmessagebroker.AppMessageBrokerAutoConfiguration;
import org.springframework.context.annotation.Import;

@Import(AppMessageBrokerAutoConfiguration.class)
public class MessageBrokerConfig {

}
