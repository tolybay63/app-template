package kz.kis.kisfilestorage.config;

import kz.kis.kismessagebroker.KisMessageBrokerAutoConfiguration;
import org.springframework.context.annotation.*;

@Configuration
@Import(KisMessageBrokerAutoConfiguration.class)
public class FilestorageConfig {
}
