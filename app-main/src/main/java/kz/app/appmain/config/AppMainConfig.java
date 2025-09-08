package kz.app.appmain.config;

import kz.app.appmessagebroker.AppMessageBrokerConfiguration;
import org.springframework.context.annotation.*;

@Configuration
@Import(AppMessageBrokerConfiguration.class)
public class AppMainConfig {
}
