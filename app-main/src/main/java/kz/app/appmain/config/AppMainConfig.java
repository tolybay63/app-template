package kz.app.appmain.config;

import kz.app.appmessagebroker.AppMessageBrokerAutoConfiguration;
import org.springframework.context.annotation.*;

@Configuration
@Import(AppMessageBrokerAutoConfiguration.class)
public class AppMainConfig {
}
