package kz.kis.kisauth;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {
        "kz.kis.kisauth",
        "kz.kis.kiscore",
        "kz.kis.kisdbtools",
        "kz.kis.kisfile",
        "kz.kis.annotations"
})
public class KisAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(KisAuthApplication.class, args);
    }

}