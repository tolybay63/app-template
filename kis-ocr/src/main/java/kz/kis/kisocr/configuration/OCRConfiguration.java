package kz.kis.kisocr.configuration;

import kz.kis.kismessagebroker.KisMessageBrokerAutoConfiguration;
import kz.kis.kisstorage.config.PageConfiguration;
import kz.kis.kisstorage.config.StorageConfiguration;
import kz.kis.kistempstorage.config.TemporaryStorageConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({KisMessageBrokerAutoConfiguration.class, TemporaryStorageConfig.class, StorageConfiguration.class, PageConfiguration.class})
public class OCRConfiguration {

}