package kz.kis.kisocr;

import kz.kis.kisocr.impl.*;
import org.springframework.context.annotation.*;

@Configuration
public class TestConfig {

    @Bean
    OCRProcessorImpl service() throws Exception {
        return new OCRProcessorImpl();
    }

}
