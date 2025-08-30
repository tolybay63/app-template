package kz.kis.kisocr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "kz.kis.kisocr",
        "kz.kis.kisstorage",
        "kz.kis.kismessagebroker",
})
public class KisOcrApplication {

	public static void main(String[] args) {
		SpringApplication.run(KisOcrApplication.class, args);
	}

}
